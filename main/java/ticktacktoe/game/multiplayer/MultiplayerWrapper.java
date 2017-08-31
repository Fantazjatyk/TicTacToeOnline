/*
 * The MIT License
 *
 * Copyright 2017 Michał Szymański, kontakt: michal.szymanski.aajar@gmail.com.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package ticktacktoe.game.multiplayer;

import ticktacktoe.game.multiplayer.PlayersStartSynchronizer;
import java.lang.ref.WeakReference;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import ticktacktoe.game.GameResultFactory;
import ticktacktoe.game.PlayParticipant;
import ticktacktoe.game.Plays;
import ticktacktoe.game.Wrapper;
import ticktacktoe.transport.Message;
import ticktacktoe.transport.namespace.InfoType;
import ticktacktoe.transport.namespace.MessageType;
import tictactoe.model.Player;
import tictactoe.play.Play;

/**
 *
 * @author Michał Szymański, kontakt: michal.szymanski.aajar@gmail.com
 */
public class MultiplayerWrapper extends Wrapper {

    private PlayParticipants players = new PlayParticipants();
    private AtomicBoolean askedPlayersAreReady = new AtomicBoolean(false);
    private PlayersStartSynchronizer synch = new PlayersStartSynchronizer();

    public MultiplayerWrapper(Play play) {
        super(play);
    }

    public synchronized void register(String username, String key) {
        PlayParticipant p = new PlayParticipant(key, play.getInfo().getId());
        p.setUsername(username);

        if (!players.isFirstPresent()) {
            players.setFirst(p);

        } else if (!players.isSecondPresent()) {
            players.setSecond(p);

        }
    }

    protected Message prepareGameEndMessage() {
        Message message = new Message();
        message.setTitle(MessageType.INFO);
        message.setContent(InfoType.GAME_END);
        message.setAppendix(GameResultFactory.createMutliplayerGameResult(this));
        return message;
    }

    public synchronized void login(String id, String displayName) {
        if (id.equals(players.getFirst().getId())) {
            synch.playerOneLogin();
            play.join(players.getFirst());
        } else if (id.equals(players.getSecond().getId())) {
            synch.playerTwoLogin();
            play.join(players.getSecond());
        }

        if (this.players.isPair() && deliverer.isPresent() && this.askedPlayersAreReady.get() == false && synch.arePlayersLogin()) {
            this.askedPlayersAreReady.set(true);
            deliverer.get().get().askArePlayersReady(play.getInfo().getId(), players.getFirst().getId(), players.getSecond().getId());
        }
    }

    public PlayParticipants getPlayers() {
        return players;
    }

    public synchronized void commitReadyPlayer(String id) {
        if (id.equals(players.getFirst().getId()) && !synch.isPlayerOneReady()) {
            this.synch.playerOneReady();
        } else if (id.equals(players.getSecond().getId()) && !synch.isPlayerTwoReady()) {
            this.synch.playersTwoReady();
        }

        if (synch.arePlayersReady() && super.isGameStarted.get() == false) {
            this.isGameStarted.set(true);
            players.getFirst().setMessagesSender(deliverer.get());
            players.getSecond().setMessagesSender(deliverer.get());
            super.startPlay();

        }
    }

    public synchronized void receiveMessage(Message msg, String username) {
        if (username.equals(players.getFirst().getId())) {
            players.getFirst().receiveMessage(msg);
        } else if (username.equals(players.getSecond().getId())) {
            players.getSecond().receiveMessage(msg);
        }
    }

    @Override
    protected void onGameEnd() {
        Message message = prepareGameEndMessage();
        this.getPlayers().getFirst().sendInfo(message);
        this.getPlayers().getSecond().sendInfo(message);
        worker.interrupt();
    }

    @Override
    protected void onGameStart() {
    }

    @Override
    public void resign(String playerId) {
        Player p = this.players.getPlayerById(playerId).get();
        this.executor.resign(p);
        Logger.getLogger(this.getClass().getCanonicalName()).info(playerId + " resigned from game");
    }

}
