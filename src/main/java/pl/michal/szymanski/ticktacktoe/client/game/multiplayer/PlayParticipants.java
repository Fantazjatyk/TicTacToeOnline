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
package pl.michal.szymanski.ticktacktoe.client.game.multiplayer;

import pl.michal.szymanski.ticktacktoe.client.game.PlayParticipant;
import java.util.Optional;

/**
 *
 * @author Michał Szymański, kontakt: michal.szymanski.aajar@gmail.com
 */
public class PlayParticipants {

    private PlayParticipant p1;
    private PlayParticipant p2;

    public PlayParticipant getFirst() {
        return this.p1;
    }

    public PlayParticipant getSecond() {
        return this.p2;
    }

    public boolean isFirstPresent() {
        return this.p1 != null;
    }

    public boolean isSecondPresent() {
        return this.p2 != null;
    }

    public void setFirst(PlayParticipant p1) {
        this.p1 = p1;
    }

    public void setSecond(PlayParticipant p2) {
        this.p2 = p2;
    }

    public Optional<PlayParticipant> getPlayerById(String id) {
        return p1.getId().equals(id) ? Optional.of(p1) : (p2.getId().equals(id) ? Optional.of(p2) : Optional.empty());
    }

    public Optional<PlayParticipant> getPlayerByName(String name) {
        return p1.getUsername().equals(name) ? Optional.of(p1) : (p2.getUsername().equals(name) ? Optional.of(p2) : Optional.empty());
    }

    public boolean isPair() {
        return this.p1 != null && this.p2 != null;
    }
}
