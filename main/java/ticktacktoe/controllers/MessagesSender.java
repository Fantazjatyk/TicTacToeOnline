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
package ticktacktoe.controllers;

import java.text.MessageFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import ticktacktoe.game.PlayMessageSender;
import ticktacktoe.transport.Message;
import ticktacktoe.transport.namespace.MessageType;
import ticktacktoe.transport.namespace.RequestType;

/**
 *
 * @author Michał Szymański, kontakt: michal.szymanski.aajar@gmail.com
 */
@Controller
public class MessagesSender implements PlayMessageSender {

    @Autowired
    SimpMessageSendingOperations operations;

    @Override
    public void askArePlayersReady(String gameid, String player1id, String player2id) {
        Message message = new Message(MessageType.REQUEST, RequestType.IS_READY);
        operations.convertAndSend(MessageFormat.format("/queue/play/{0}/{1}", gameid, player1id), message);
        operations.convertAndSend(MessageFormat.format("/queue/play/{0}/{1}", gameid, player2id), message);
    }

    @Override
    public void send(Message message, String userId, String gameId) {
        operations.convertAndSend(MessageFormat.format("/queue/play/{0}/{1}", gameId, userId), message);
    }

}
