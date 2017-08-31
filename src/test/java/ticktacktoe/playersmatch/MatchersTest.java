/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ticktacktoe.playersmatch;

import ticktacktoe.playersmatch.Matchers;
import ticktacktoe.playersmatch.MultiplayerMatcher;
import ticktacktoe.playersmatch.MatchParticipant;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Michał Szymański, kontakt: michal.szymanski.aajar@gmail.com
 */
public class MatchersTest {

    MatchParticipant matcher1;
    MatchParticipant matcher2;
    MultiplayerMatcher connector;

    @Before
    public void setUp() {
        matcher1 = new MatchParticipant("abcd", "1234");
        matcher2 = new MatchParticipant("efgh", "5431");
        connector = Matchers.getMultiplayerMatcher();
        Matchers.startMultiplayerMatcher();
    }

    @After
    public void clean() {
        Matchers.getMultiplayerMatcher().getAllMatchers().clear();
    }

    @Test
    public void testMatchingTwoPlayers() {

        assertTrue(Matchers.getMultiplayerMatcher().getAllMatchers().isEmpty());
        Matchers.getMultiplayerMatcher().registerMatcher(matcher1);
        assertEquals(1, Matchers.getMultiplayerMatcher().getAllMatchers().size());
        Matchers.getMultiplayerMatcher().registerMatcher(matcher2);
        assertEquals(2, Matchers.getMultiplayerMatcher().getAllMatchers().size());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger("fgdfg").log(Level.SEVERE, null, ex);
        }

        assertEquals(0, Matchers.getMultiplayerMatcher().getAllMatchers().size());

    }
}
