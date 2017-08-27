/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.michal.szymanski.ticktacktoe.client.playersmatch;

import java.util.Optional;

/**
 *
 * @author Michał Szymański, kontakt: michal.szymanski.aajar@gmail.com
 */
public class MatchParticipantsPair {

    private Optional<MatchParticipant> first = Optional.empty();
    private Optional<MatchParticipant> second = Optional.empty();

    public Optional<MatchParticipant> getFirst() {
        return first;
    }

    public MatchParticipantsPair(MatchParticipant first, MatchParticipant second) {
        this.first = Optional.of(first);
        this.second = Optional.of(second);
    }

    public void setFirst(Optional<MatchParticipant> first) {
        this.first = first;
    }

    public Optional<MatchParticipant> getSecond() {
        return second;
    }

    public void setSecond(Optional<MatchParticipant> second) {
        this.second = second;
    }

    public boolean areTwoPresent() {
        return this.first.isPresent() && this.second.isPresent();
    }

}
