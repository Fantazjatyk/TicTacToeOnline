/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ticktacktoe.game;

import java.lang.ref.WeakReference;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import ticktacktoe.transport.Message;
import ticktacktoe.transport.namespace.InfoType;
import ticktacktoe.transport.namespace.MessageType;
import tictactoe.play.Play;
import tictactoe.play.PlayExecutor;

/**
 *
 * @author Michał Szymański, kontakt: michal.szymanski.aajar@gmail.com
 */
public abstract class Wrapper {

    protected transient Play play;
    protected transient PlayExecutor executor;
    protected transient Optional<WeakReference<PlayMessageSender>> deliverer;
    protected transient Thread worker;

    protected AtomicBoolean isGameStarted = new AtomicBoolean(false);

    public Wrapper(Play play) {
        this.play = play;
        this.executor = new PlayExecutor(play);
        executor.setCallbacks().addOnStartEvent(() -> onGameStart());
        executor.setCallbacks().addOnEndEvent(() -> Plays.getInstance().removePlay(this.play.getInfo().getId()));
        executor.setCallbacks().addOnEndEvent(() -> onGameEnd());
        this.worker = new Thread() {
            @Override
            public void run() {
                Logger.getLogger("worker").log(Level.WARNING, "starting game with id " + play.getInfo().getId());
                executor.execute();
            }
        };
    }

    public Play getPlay() {
        return play;
    }

    public void setDeliverer(PlayMessageSender deliverer) {
        this.deliverer = Optional.of(new WeakReference(deliverer));
    }

    public Optional<WeakReference<PlayMessageSender>> getDeliverer() {
        return deliverer;
    }

    public abstract void register(String username, String key);

    public abstract void login(String username, String key);

    public abstract void receiveMessage(Message msg, String username);

    protected abstract void onGameEnd();

    protected abstract void onGameStart();

    protected void startPlay() {
        worker.start();
    }

    public abstract void resign(String playerId);
}
