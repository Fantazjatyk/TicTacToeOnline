/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.michal.szymanski.ticktacktoe.client.playersmatch;

import java.text.MessageFormat;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Michał Szymański, kontakt: michal.szymanski.aajar@gmail.com
 */
public class MultiplayerMatcher extends Matcher {

    private static ConcurrentLinkedQueue<String> validatingMatchersIds = new ConcurrentLinkedQueue();
    private static final ReentrantLock lock = new ReentrantLock();

    public synchronized void registerMatcher(MatchParticipant matcher) {
        super.registerMatcher(matcher);

        if (queue.size() >= 2) {
            synchronized (lock) {
                lock.notifyAll();
            }
        }

    }

    @Override
    protected boolean isMatcherValid(MatchParticipant p) {
        return !validatingMatchersIds.contains(p.getKey());
    }

    @Override
    protected void match() {
        if (queue.size() >= 2) {
            MatchParticipant matcher1 = queue.pollLastEntry().getValue();
            MatchParticipant matcher2 = queue.pollLastEntry().getValue();

            if (validatingMatchersIds.contains(matcher1.getKey()) || validatingMatchersIds.contains(matcher2.getKey())) {
                if (!validatingMatchersIds.contains(matcher1.getKey())) {
                    queue.put(matcher1.getKey(), matcher1);
                }
                if (!validatingMatchersIds.contains(matcher2.getKey())) {
                    queue.put(matcher1.getKey(), matcher2);
                }
                return;
            }

            validatingMatchersIds.add(matcher1.getKey());
            validatingMatchersIds.add(matcher2.getKey());

            MatchParticipantsPairValidator p = new MatchParticipantsPairValidator();

            p.setOnEnd((r) -> {
                validatingMatchersIds.remove(matcher1.getKey());
                validatingMatchersIds.remove(matcher2.getKey());
            });
            p.setOnValidationSuccess((validationResult) -> {
                String gameId = MatcherHelper.createMultiplayerPlayAndGetItsId(matcher1.getKey(), matcher2.getKey());
                MatchResult result = new MatchResult(gameId, matcher1, matcher2);

                deliverer.ifPresent(el -> el.deliverResult(result));
            });
            p.setOnConfirmationFailure((validationResult) -> {
                if (validationResult.isIsFirstConfirmed()) {
                    registerMatcher(validationResult.getP1());
                }
                if (validationResult.isIsSecondConfirmed()) {
                    registerMatcher(validationResult.getP2());
                }
            });

            p.setValidationMessagesDeliverer(deliverer.get());
            p.validate(new MatchParticipantsPair(matcher1, matcher2));

        }

    }

    @Override
    protected void cycle() {
        LOG.log(Level.SEVERE, queue.size() + " partipants in queue");
        try {
            match();
            if (queue.size() >= 2) {
                this.setSleepTimeInMilis(500);
                LOG.log(Level.SEVERE, MessageFormat.format("Sleep time set on: {0} seconds", this.sleepTimeInMilis));
                Thread.sleep(sleepTimeInMilis);
            } else {
                synchronized (lock) {
                    LOG.log(Level.SEVERE, "Thread waiting mode");
                    lock.wait();
                }
            }
        } catch (InterruptedException ex) {
            LOG.log(Level.SEVERE, ex.getMessage());
        }
    }

}
