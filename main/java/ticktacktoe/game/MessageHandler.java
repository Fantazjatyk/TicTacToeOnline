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

import java.util.HashMap;
import java.util.Map;
import ticktacktoe.transport.Message;
import ticktacktoe.transport.namespace.MessageType;
import ticktacktoe.transport.namespace.RequestType;
import tictactoe.model.IntPoint;
import tictactoe.transport.ProxyResponse;

/**
 *
 * @author Michał Szymański, kontakt: michal.szymanski.aajar@gmail.com
 */
public class MessageHandler {

    protected HashMap<String, ProxyResponse> requests = new HashMap();

    public void receiveMessage(Message msg) {

        if (requests.containsKey(msg.getContent()) && msg.getTitle().equals(MessageType.RESPONSE)) {
            handleResponse(msg);
        } else if (msg.getTitle().equals(MessageType.REQUEST)) {
            handleRequest(msg);
        }
    }

    private void handleResponse(Message msg) {

        switch (msg.getContent()) {
            case RequestType.GET_FIELD:
                handleGetField(msg);
                break;
            case RequestType.IS_CONNECTED:
                handleIsConnected(msg);
                break;
            default:
                throw new UnsupportedOperationException();

        }
    }

    private void handleGetField(Message msg) {

        Map<String, String> map = ((Map<String, String>) (msg.getAppendix()));
        if (map.containsKey("x") && map.containsKey("y")) {
            IntPoint point = new IntPoint(Integer.parseInt(map.get("x")), Integer.parseInt(map.get("y")));
            requests.get(msg.getContent()).setReal(point);
        }
    }

    private void handleIsConnected(Message msg) {
        requests.get(msg.getContent()).setReal(true);
    }

    private void handleRequest(Message msg) {
        throw new UnsupportedOperationException();
    }

}
