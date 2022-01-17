package com.example.motifissa.HelperClasses;

public class ChallengeStatus {
    private String opponent;
    private ChallengeState challengeState;

    public ChallengeStatus() { // required by firebase
    }

    public ChallengeStatus(String opponent, ChallengeState challengeState) {
        this.opponent = opponent;
        this.challengeState = challengeState;
    }

    public enum ChallengeState{
        WAITING,
        CONNECTED
    }

    public String getOpponent() {
        return opponent;
    }

    public void setOpponent(String opponent) {
        this.opponent = opponent;
    }

    public ChallengeState getChallengeState() {
        return challengeState;
    }

    public void setChallengeState(ChallengeState challengeState) {
        this.challengeState = challengeState;
    }
}
