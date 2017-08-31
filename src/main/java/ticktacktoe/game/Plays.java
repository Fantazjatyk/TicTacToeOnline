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

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;



/**
 *
 * @author Michał Szymański, kontakt: michal.szymanski.aajar@gmail.com
 */
public class Plays {

    private Plays() {

    }
    private ConcurrentSkipListMap<String, Wrapper> registry = new ConcurrentSkipListMap();

    public synchronized void submitWrapper(Wrapper play) {
        registry.put(play.getPlay().getInfo().getId(), play);
        printStatus();
    }

    public synchronized Optional<Wrapper> getPlay(String id) {
        return Optional.ofNullable(registry.get(id));
    }

    public synchronized Collection<Wrapper> getAll() {
        return registry.values();
    }

    public synchronized void removePlay(String id) {
        registry.remove(id);
        printStatus();
    }

    public synchronized void printStatus() {
        Logger.getLogger(Plays.class.getName()).log(Level.SEVERE, "Active plays: " + registry.size());
    }

    private static Plays instance;

    public synchronized static Plays getInstance() {
        if (instance == null) {
            instance = new Plays();
        }
        return instance;
    }

}
