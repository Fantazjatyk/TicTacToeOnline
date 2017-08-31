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
package ticktacktoe.game;

import java.awt.Point;
import java.lang.ref.WeakReference;
import java.util.function.Consumer;
import ticktacktoe.controllers.MessagesSender;
import ticktacktoe.game.APIRequestHandler;
import ticktacktoe.game.MessageHandler;
import ticktacktoe.game.PlayMessageSender;
import ticktacktoe.transport.Message;
import ticktacktoe.transport.namespace.InfoType;
import ticktacktoe.transport.namespace.MessageType;
import ticktacktoe.transport.namespace.RequestType;
import pl.michal.szymanski.tictactoe.model.Board;
import pl.michal.szymanski.tictactoe.model.GameResult;
import pl.michal.szymanski.tictactoe.model.IntPoint;
import pl.michal.szymanski.tictactoe.model.Player;
import pl.michal.szymanski.tictactoe.play.PlayInfo;
import pl.michal.szymanski.tictactoe.play.PlaySettings;
import pl.michal.szymanski.tictactoe.transport.ProxyResponse;

/**
 *
 * @author Michał Szymański, kontakt: michal.szymanski.aajar@gmail.com
 */
public class PlayParticipant extends Player {

    private String gameid;
    private MessageHandler handler;
    private APIRequestHandler rqHandler;
    private WeakReference<PlayMessageSender> sender;

    public PlayParticipant(String id, String gameid) {
        super(id);
        this.gameid = gameid;
        this.handler = new MessageHandler();
        this.rqHandler = new APIRequestHandler();
    }

    public void setMessagesSender(WeakReference<PlayMessageSender> sender) {
        this.sender = sender;
    }

    @Override
    public void getMoveField(ProxyResponse<IntPoint> prs) {
        sendRequest(new Message(MessageType.REQUEST, RequestType.GET_FIELD), prs);
    }

    @Override
    public void receiveBoard(Board board) {
        sendInfo(new Message(MessageType.INFO, InfoType.SHOW_BOARD, board.getSelector().getSimplified()));
    }

    @Override
    public void onTurnTimeout() {
        sendInfo(new Message(MessageType.INFO, InfoType.TURN_TIMEOUT));
    }

    @Override
    public void isConnected(ProxyResponse<Boolean> prs) {
        sendRequest(new Message(MessageType.REQUEST, RequestType.IS_CONNECTED), prs);
    }

    public void sendInfo(Message msg) {
        sender.get().send(msg, super.getId(), gameid);
    }

    public void sendRequest(Message msg, ProxyResponse prs) {
        handler.requests.put(msg.getContent(), prs);
        sendInfo(msg);
    }

    public void receiveMessage(Message message) {
        this.handler.receiveMessage(message);
    }

    @Override
    public void onGameEnd(PlayInfo pi, PlaySettings.PlaySettingsGetters psg) {
    }

    @Override
    public void receiveGameResult(GameResult gr) {
    }

}
