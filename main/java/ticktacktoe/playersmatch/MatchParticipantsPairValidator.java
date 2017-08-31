/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ticktacktoe.playersmatch;

import java.lang.ref.WeakReference;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import ticktacktoe.playersmatch.MatchParticipant;
import ticktacktoe.playersmatch.MatchParticipantsPair;
import pl.michal.szymanski.tictactoe.transport.EventProxyResponse;
import ticktacktoe.playersmatch.MatcherMessagesDeliverer;

/**
 *
 * @author Michał Szymański, kontakt: michal.szymanski.aajar@gmail.com
 */
public class MatchParticipantsPairValidator {

    private Optional<Consumer<Result>> onSuccess;
    private Optional<Consumer<Result>> onFailure;
    private Optional<Consumer<Result>> onEnd;
    private EventProxyResponse firstResponse;
    private EventProxyResponse secondResponse;
    private Lock lock;
    private WeakReference<MatcherMessagesDeliverer> deliverer;
    private static final int MAX_WAIT_TIME_MILLIS = 5000;

    public void setOnValidationSuccess(Consumer<Result> callback) {
        this.onSuccess = Optional.of(callback);
    }

    public void setOnConfirmationFailure(Consumer<Result> callback) {
        this.onFailure = Optional.of(callback);
    }

    public void setOnEnd(Consumer<Result> callback) {
        this.onEnd = Optional.of(callback);
    }

    private void attempToWakeUp() {
        if (firstResponse.getReal().isPresent() && secondResponse.getReal().isPresent()) {

            synchronized (lock) {
                this.lock.notifyAll();
            }
        }
    }

    public void validate(MatchParticipantsPair pair) {
        if (!pair.areTwoPresent()) {
            return;
        }

        new Thread() {
            @Override
            public void run() {
                if (!pair.areTwoPresent()) {
                    throw new InvalidMatchParticipantsPair();
                }
                lock = new ReentrantLock();
                firstResponse = new EventProxyResponse();
                firstResponse.setAfterCallback(()
                        -> attempToWakeUp());
                secondResponse = new EventProxyResponse();
                secondResponse.setAfterCallback(()
                        -> attempToWakeUp());

                deliverer.get().isPresent(pair.getFirst().get(), firstResponse);
                deliverer.get().isPresent(pair.getSecond().get(), secondResponse);

                try {
                    synchronized (lock) {
                        lock.wait(MAX_WAIT_TIME_MILLIS);
                    }
                } catch (InterruptedException ex) {
                    Logger.getLogger(MatchParticipantsPairValidator.class.getName()).log(Level.SEVERE, null, ex);
                }
                checkResult(pair);
            }
        }.start();

    }

    private void checkResult(MatchParticipantsPair pair) {
        boolean isFirstConfirmed = firstResponse.getReal().isPresent();
        boolean isSecondConfirmed = secondResponse.getReal().isPresent();
        Result r = prepareResult(pair, isFirstConfirmed, isSecondConfirmed);

        onEnd.ifPresent(el -> el.accept(r));
        if (isFirstConfirmed && isSecondConfirmed) {
            onSuccess.ifPresent(el -> el.accept(r));
        } else {
            onFailure.ifPresent(el -> el.accept(r));
        }
    }

    private Result prepareResult(MatchParticipantsPair pair, boolean isFirstConfirmed, boolean isSecondConfirmed) {
        Result result = new Result();
        result.p1 = pair.getFirst().get();
        result.p2 = pair.getSecond().get();
        result.isFirstConfirmed = isFirstConfirmed;
        result.isSecondConfirmed = isSecondConfirmed;
        return result;
    }

    public void setValidationMessagesDeliverer(MatcherMessagesDeliverer d) {
        this.deliverer = new WeakReference(d);
    }

    public class Result {

        private boolean isFirstConfirmed;
        private boolean isSecondConfirmed;
        private MatchParticipant p1;
        private MatchParticipant p2;

        public boolean isIsFirstConfirmed() {
            return isFirstConfirmed;
        }

        public boolean isIsSecondConfirmed() {
            return isSecondConfirmed;
        }

        public MatchParticipant getP1() {
            return p1;
        }

        public void setP1(MatchParticipant p1) {
            this.p1 = p1;
        }

        public MatchParticipant getP2() {
            return p2;
        }

        public void setP2(MatchParticipant p2) {
            this.p2 = p2;
        }

    }
}
