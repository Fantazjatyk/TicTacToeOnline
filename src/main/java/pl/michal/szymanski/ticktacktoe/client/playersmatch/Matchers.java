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
package pl.michal.szymanski.ticktacktoe.client.playersmatch;

/**
 *
 * @author Michał Szymański, kontakt: michal.szymanski.aajar@gmail.com
 */
public class Matchers {

    private static MultiplayerMatcher multiplayer;

    public static synchronized MultiplayerMatcher getMultiplayerMatcher() {
        if (multiplayer == null || multiplayer.getState().equals(Thread.State.TERMINATED)) {
            multiplayer = new MultiplayerMatcher();
            multiplayer.setDaemon(true);
            multiplayer.setName("Matcher");
        }
        return multiplayer;
    }

    public static void startMultiplayerMatcher() {
        MultiplayerMatcher m = getMultiplayerMatcher();
        if (m.getState().equals(Thread.State.NEW)) {
            m.start();
        }
    }

    public static void stopMultiplayerMatcher() {
        if (multiplayer == null) {
            return;
        }
        multiplayer.interrupt();
        multiplayer.getAllMatchers().clear();
    }
}
