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

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import ticktacktoe.game.Plays;
import ticktacktoe.game.PlayMessageReceiver;
import ticktacktoe.game.multiplayer.MultiplayerWrapper;
import ticktacktoe.exceptions.GameNotFoundException;
import ticktacktoe.game.Wrapper;
import ticktacktoe.transport.Message;
import ticktacktoe.transport.namespace.MessageType;
import ticktacktoe.transport.namespace.RequestType;
import tictactoe.play.Play;

/**
 *
 * @author Michał Szymański, kontakt: michal.szymanski.aajar@gmail.com
 */
@Controller
public class MessagesReceiver implements PlayMessageReceiver {

    @Autowired
    MessagesSender sender;

    @MessageMapping("/play/{playid}/join/{key}")
    public void joinToPlay(@DestinationVariable("key") String key, String username, @DestinationVariable("playid") String gameid) {
        Optional found = Plays.getInstance().getPlay(gameid);

        if (!found.isPresent()) {
            return;
        }
        Wrapper play = (Wrapper) found.get();
        play.setDeliverer(sender);
        play.login(key, username);
    }

    @MessageMapping("/play/{playid}/{userId}/response")
    public void handleResponse(Message message, @DestinationVariable("userId") String userId, @DestinationVariable("playid") String playId) {
        Optional found = Plays.getInstance().getPlay(playId);

        if (!found.isPresent()) {
            throw new GameNotFoundException();
        }
        Wrapper play = (Wrapper) found.get();

        if (play instanceof MultiplayerWrapper && message.getTitle().equals(MessageType.RESPONSE) && message.getContent().equals(RequestType.IS_READY)) {
            MultiplayerWrapper wrapper = (MultiplayerWrapper) play;
            wrapper.commitReadyPlayer(userId);
        } else {
            play.receiveMessage(message, userId);
        }

    }

    @MessageMapping("/play/{playid}/{userId}/resign")
    public void resign(@DestinationVariable("playid") String playId, @DestinationVariable("userId") String userId) {
        Optional found = Plays.getInstance().getPlay(playId);

        if (!found.isPresent()) {
            throw new GameNotFoundException();
        }
        Wrapper play = (Wrapper) found.get();
        play.resign(userId);
    }

}
