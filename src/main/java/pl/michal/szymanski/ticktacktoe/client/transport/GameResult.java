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
package pl.michal.szymanski.ticktacktoe.client.transport;

import java.util.List;
import pl.michal.szymanski.tictactoe.model.BoardField;

/**
 *
 * @author Michał Szymański, kontakt: michal.szymanski.aajar@gmail.com
 */
public class GameResult {

    private String winnerName;
    private String winnerId;
    private String playerOneName;
    private String playerTwoName;
    private String state;
    private List<BoardField> winningLine;
    private int totalTime;
    private int totalMoves;

    public GameResult(GameResult.Builder b) {
        this.winnerId = b.winnerId;
        this.winnerName = b.winnerName;
        this.playerOneName = b.playerOneName;
        this.playerTwoName = b.playerTwoName;
        this.totalTime = b.totalTime;
        this.totalMoves = b.totalMoves;
        this.winningLine = b.winningLine;
        this.state = b.state;
    }

    public String getWinnerName() {
        return winnerName;
    }

    public String getWinnerId() {
        return winnerId;
    }

    public void setState(String state) {
        this.state = state;
    }

    public List<BoardField> getWinningLine() {
        return winningLine;
    }

    public String getPlayerOneName() {
        return playerOneName;
    }

    public String getPlayerTwoName() {
        return playerTwoName;
    }

    public int getTotalTime() {
        return totalTime;
    }

    public int getTotalMoves() {
        return totalMoves;
    }

    public static class Builder {

        private String winnerId;
        private String winnerName;
        private String playerOneName;
        private String playerTwoName;
        private String state;
        private int totalTime;
        private int totalMoves;
        private List<BoardField> winningLine;

        public Builder winnerName(String name) {
            this.winnerName = name;
            return this;
        }

        public Builder winnerId(String id) {
            this.winnerId = id;
            return this;
        }

        public Builder state(String state) {
            this.state = state;
            return this;
        }

        public Builder playerOneName(String name) {
            this.playerOneName = name;
            return this;
        }

        public Builder playerTwoName(String name) {
            this.playerTwoName = name;
            return this;
        }

        public Builder totalTime(int value) {
            this.totalTime = value;
            return this;
        }

        public Builder totalMoves(int moves) {
            this.totalMoves = moves;
            return this;
        }

        public Builder winningLine(List<BoardField> line) {
            this.winningLine = line;
            return this;
        }

        public GameResult build() {
            return new GameResult(this);
        }
    }
}
