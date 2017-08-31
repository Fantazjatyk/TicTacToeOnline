/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ticktacktoe.game;

import java.util.concurrent.TimeUnit;
import tictactoe.ai.AILevel;
import tictactoe.play.MultiplayerPlay;
import tictactoe.play.Play;
import tictactoe.play.SingleplayerPlay;

/**
 *
 * @author Michał Szymański, kontakt: michal.szymanski.aajar@gmail.com
 */
public interface PlayFactory {

    public static Play createDefaultMultiplayerPlay(String player1, String player2) {
        MultiplayerPlay play = new MultiplayerPlay();
        play.settings()
                .moveTimeLimit(15, TimeUnit.SECONDS)
                .gameTimeLimit(5, TimeUnit.MINUTES)
                .beginOnAllPlayersJoined();

        return play;
    }

    public static SingleplayerPlay createDefaultSingleplayerPlay(AILevel level) {
        SingleplayerPlay play = new SingleplayerPlay();
        play.setAILevel(level);
        play.settings().moveTimeLimit(15, TimeUnit.SECONDS)
                .gameTimeLimit(5, TimeUnit.MINUTES);
        return play;
    }
}
