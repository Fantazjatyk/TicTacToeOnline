/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ticktacktoe.playersmatch;

import java.text.MessageFormat;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Michał Szymański, kontakt: michal.szymanski.aajar@gmail.com
 */
public abstract class Matcher extends Thread {

    protected Optional<MatcherMessagesDeliverer> deliverer = Optional.empty();
    protected int sleepTimeInMilis = 500;
    protected ConcurrentSkipListMap<String, MatchParticipant> queue = new ConcurrentSkipListMap();
    protected final Logger LOG = Logger.getLogger(Matcher.class.getName());

    public synchronized void setResultDeliverer(MatcherMessagesDeliverer deliverer) {
        this.deliverer = Optional.ofNullable(deliverer);
    }

    public synchronized void setSleepTimeInMilis(int time) {
        this.sleepTimeInMilis = time;
    }

    abstract protected void match();

    abstract protected void cycle();

    protected synchronized void registerMatcher(MatchParticipant matcher) {
        if (!queue.containsKey(matcher.getKey()) && isMatcherValid(matcher)) {
            queue.put(matcher.getKey(), matcher);
            LOG.info(MessageFormat.format("- Registered new participant with id {0}", matcher.getKey()));
        }
    }

    abstract protected boolean isMatcherValid(MatchParticipant p);

    public synchronized void unregisterMatcher(MatchParticipant matcher) {
        queue.remove(matcher);
        LOG.info(MessageFormat.format("- Registered participant with id {0}", matcher.getKey()));
    }

    public synchronized MatchParticipant getMatchParticipant(String id) {
        return queue.get(id);
    }

    public synchronized Map getAllMatchers() {
        return queue;
    }

    @Override
    public void run() {
        while (!this.isInterrupted()) {
            cycle();
        }
    }

}
