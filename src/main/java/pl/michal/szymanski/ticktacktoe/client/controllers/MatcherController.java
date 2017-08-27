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
package pl.michal.szymanski.ticktacktoe.client.controllers;

import java.text.MessageFormat;
import java.util.HashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.michal.szymanski.ticktacktoe.client.game.Plays;
import pl.michal.szymanski.ticktacktoe.client.game.multiplayer.MultiplayerWrapper;
import pl.michal.szymanski.ticktacktoe.client.playersmatch.Matchers;
import pl.michal.szymanski.ticktacktoe.client.playersmatch.MatchParticipant;
import pl.michal.szymanski.ticktacktoe.client.playersmatch.MatchResult;
import pl.michal.szymanski.ticktacktoe.client.transport.Message;
import pl.michal.szymanski.ticktacktoe.client.transport.namespace.MessageType;
import pl.michal.szymanski.ticktacktoe.client.transport.namespace.RequestType;
import pl.michal.szymanski.tictactoe.transport.ProxyResponse;
import pl.michal.szymanski.ticktacktoe.client.playersmatch.MatcherMessagesDeliverer;

/**
 *
 * @author Michał Szymański, kontakt: michal.szymanski.aajar@gmail.com
 */
@Controller
public class MatcherController implements MatcherMessagesDeliverer {

    @Autowired
    SimpMessagingTemplate template;

    @Autowired
    SimpMessageSendingOperations operations;

    private HashMap<String, ProxyResponse> proxyResponses = new HashMap();

    public MatcherController() {
        Matchers.getMultiplayerMatcher().setResultDeliverer(this);
    }


    @Override
    public void deliverResult(MatchResult result) {
        MultiplayerWrapper wrapper = (MultiplayerWrapper) Plays.getInstance().getPlay(result.getGameId()).get();
        wrapper.register(result.getFirstParticipant().getUsername(), result.getFirstParticipant().getKey());
        wrapper.register(result.getSecondParticipant().getUsername(), result.getSecondParticipant().getKey());

        operations.convertAndSend("/queue/matching/" + result.getFirstParticipant().getKey(), result);
        operations.convertAndSend("/queue/matching/" + result.getSecondParticipant().getKey(), result);
    }

    public void isPresent(MatchParticipant p, ProxyResponse pr) {
        proxyResponses.put(p.getKey(), pr);
        operations.convertAndSend("/queue/matching/" + p.getKey(), new Message(MessageType.REQUEST, RequestType.IS_CONNECTED));
    }

    @MessageMapping("/{key}/connected")
    public void handleConnected(@DestinationVariable String key) {
        if (!key.isEmpty()) {
            ProxyResponse r = proxyResponses.get(key);
            if (r != null) {
                r.setReal(true);
            }
        }
    }


    @MessageMapping("/{key}/join")
    public String join(@DestinationVariable String key, String username) {
        if (username.isEmpty()) {
            username = "Anonymous";
        }

        Matchers.getMultiplayerMatcher().registerMatcher(new MatchParticipant(key, username));
        return "joined to matching";
    }

}
