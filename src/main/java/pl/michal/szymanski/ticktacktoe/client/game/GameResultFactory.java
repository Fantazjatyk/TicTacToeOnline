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
package pl.michal.szymanski.ticktacktoe.client.game;

import pl.michal.szymanski.ticktacktoe.client.game.multiplayer.MultiplayerWrapper;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import pl.michal.szymanski.ticktacktoe.client.game.singleplayer.SingleplayerWrapper;
import pl.michal.szymanski.ticktacktoe.client.transport.GameResult;

import pl.michal.szymanski.ticktacktoe.client.transport.GameResult.Builder;
import pl.michal.szymanski.ticktacktoe.client.transport.namespace.GameResultState;
import pl.michal.szymanski.tictactoe.model.BoardField;
import pl.michal.szymanski.tictactoe.model.Player;
import pl.michal.szymanski.tictactoe.play.GameMaster;

/**
 *
 * @author Michał Szymański, kontakt: michal.szymanski.aajar@gmail.com
 */
public interface GameResultFactory {

    public static GameResult createMutliplayerGameResult(MultiplayerWrapper play) {
        Optional<Player> winner = play.getPlay().getInfo().getWinner();
        GameResult.Builder builder = new GameResult.Builder();

        if (winner.isPresent()) {
            String winnerId = winner.get().getId();
            String winnerName = play.getPlayers().getPlayerById(winnerId).get().getUsername();
            appendWinnerInfo(winnerId, winnerName, builder);
        }

        appendWinCombination(play, builder);
        builder.playerOneName(play.getPlayers().getFirst().getUsername());
        builder.playerTwoName(play.getPlayers().getSecond().getUsername());
        builder.totalMoves(play.getPlay().getHistory().getMoves().size());
        builder.totalTime(play.getPlay().getInfo().getTotalTimeInMilis());
        return builder.build();
    }

    public static GameResult createSingleplayerGameResult(SingleplayerWrapper play) {
        Optional<Player> winner = play.getPlay().getInfo().getWinner();
        GameResult.Builder builder = new GameResult.Builder();

        if (winner.isPresent()) {
            String winnerId = winner.get().getId();
            String winnerName = null;
            if (winner.get().getId().equals(play.getPlayer().getId())) {
                winnerName = play.getPlayer().getUsername();
            } else if (!winnerId.isEmpty() && winnerId.equals(play.getAi().getId())) {
                winnerName = "Computer";
            }

            appendWinnerInfo(winnerId, winnerName, builder);
        }
        appendWinCombination(play, builder);

        builder.playerOneName(play.getPlayer().getUsername());
        builder.playerTwoName("Computer");
        builder.totalMoves(play.getPlay().getHistory().getMoves().size());
        builder.totalTime(play.getPlay().getInfo().getTotalTimeInMilis());
        return builder.build();
    }

    public static void appendWinCombination(Wrapper play, Builder builder) {
        List<BoardField[]> combinations = play.getPlay().getInfo().getBoard().getSelector().getAllPossibleWinningLines();
        List<BoardField[]> winCombinations = GameMaster.getWinCombinations(combinations);
        builder.winningLine(winCombinations.size() == 1 ? Arrays.asList(winCombinations.get(0)) : null);

    }

    public static void appendWinnerInfo(String winnerId, String winnerName, Builder builder) {
        builder.winnerId(winnerId);
        builder.winnerName(winnerName);
    }

}
