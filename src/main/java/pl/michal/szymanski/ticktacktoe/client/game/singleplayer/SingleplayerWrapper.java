/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.michal.szymanski.ticktacktoe.client.game.singleplayer;


import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;
import pl.michal.szymanski.ticktacktoe.client.game.GameResultFactory;
import pl.michal.szymanski.ticktacktoe.client.game.PlayParticipant;
import pl.michal.szymanski.ticktacktoe.client.game.Plays;
import pl.michal.szymanski.ticktacktoe.client.game.Wrapper;
import pl.michal.szymanski.ticktacktoe.client.transport.Message;
import pl.michal.szymanski.ticktacktoe.client.transport.namespace.InfoType;
import pl.michal.szymanski.ticktacktoe.client.transport.namespace.MessageType;
import pl.michal.szymanski.tictactoe.model.Player;
import pl.michal.szymanski.tictactoe.play.SingleplayerPlay;

/**
 *
 * @author Michał Szymański, kontakt: michal.szymanski.aajar@gmail.com
 */
public class SingleplayerWrapper extends Wrapper {

    private AtomicBoolean isPlayerRegistered = new AtomicBoolean(false);
    private AtomicBoolean isPlayerLogged = new AtomicBoolean(false);
    private PlayParticipant player;

    public SingleplayerWrapper(SingleplayerPlay play) {
        super(play);

    }

    public Player getAi() {
        return ((SingleplayerPlay) super.play).getAiPlayer();
    }

    public PlayParticipant getPlayer() {
        return player;
    }

    @Override
    public void register(String username, String key) {
        this.player = new PlayParticipant(key, super.play.getInfo().getId());
        this.player.setUsername(username);
        this.isPlayerRegistered.set(true);
    }

    @Override
    public void login(String key, String username) {
        this.isPlayerLogged.set(true);
        this.play.join(player);
        this.player.setMessagesSender(super.deliverer.get());
        super.startPlay();
    }

    @Override
    public void receiveMessage(Message msg, String key) {
        if (this.player.getId().equals(key)) {
            this.player.receiveMessage(msg);
        }
    }

    protected Message prepareGameEndMessage() {
        Message message = new Message();
        message.setTitle(MessageType.INFO);
        message.setContent(InfoType.GAME_END);
        message.setAppendix(GameResultFactory.createSingleplayerGameResult(this));
        return message;
    }

    @Override
    protected void onGameEnd() {
        Message message = prepareGameEndMessage();
        this.player.sendInfo(message);
        worker.interrupt();
    }

    @Override
    protected void onGameStart() {
    }

    @Override
    public void resign(String playerId) {
        this.executor.resign(player);
        Logger.getLogger(this.getClass().getCanonicalName()).info(playerId + " resigned from game");
    }

}
