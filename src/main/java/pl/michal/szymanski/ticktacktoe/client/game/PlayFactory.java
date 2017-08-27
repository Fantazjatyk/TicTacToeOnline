/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.michal.szymanski.ticktacktoe.client.game;

import java.util.concurrent.TimeUnit;
import pl.michal.szymanski.tictactoe.ai.AILevel;
import pl.michal.szymanski.tictactoe.play.MultiplayerPlay;
import pl.michal.szymanski.tictactoe.play.Play;
import pl.michal.szymanski.tictactoe.play.SingleplayerPlay;

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
