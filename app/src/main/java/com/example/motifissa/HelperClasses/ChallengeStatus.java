package com.example.motifissa.HelperClasses;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

public class ChallengeStatus {
    private String opponent;
    private ChallengeState challengeState;
    private boolean master;
    private double latitude;
    private double longitude;

    public ChallengeStatus() { // required by firebase
    }

    public ChallengeStatus(String opponent, ChallengeState challengeState) {
        this(opponent, challengeState, false);
    }
    public ChallengeStatus(String opponent, ChallengeState challengeState, boolean master) {
        this.opponent = opponent;
        this.challengeState = challengeState;
        this.master = master;
    }

    public enum ChallengeState{
        WAITING,
        PICK_LOCATION,
        PICK_LOCATION_WAITING,
        PICK_LOCATION_DONE,
        FINDING_EACH_OTHER,
        COUNTDOWN,
        CHALLENGE_START
    }

    public void moveToSecondPhase(){
        if (master)
            challengeState = ChallengeState.PICK_LOCATION;
        else
            challengeState = ChallengeState.PICK_LOCATION_WAITING;
    }

    /**
     * called on the opponents state to see if the *current* user should be in the second phase i.e. map fragment
     * @return returns a boolean value if the current user should be in the second phase i.e. map fragment
     */
    public boolean shouldBeInSecondPhase(){
        return getChallengeState() == ChallengeStatus.ChallengeState.WAITING || getChallengeState() == ChallengeStatus.ChallengeState.PICK_LOCATION || getChallengeState() == ChallengeStatus.ChallengeState.PICK_LOCATION_WAITING;
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

    public boolean isMaster() {
        return master;
    }

    public void setMaster(boolean master) {
        this.master = master;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
