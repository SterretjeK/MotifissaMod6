package com.example.motifissa.HelperClasses;

import com.google.android.gms.maps.model.LatLng;

public class ChallengeStatus {
    private String opponent;
    private ChallengeState challengeState;
    private boolean master;
    private Position ownPos;
    private Position chosenPos;

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

    public boolean chosenPosIsEmpty() {
        if (chosenPos == null) return false;
        return chosenPos.empty();
    }

    public boolean ownPosIsEmpty() {
        if (ownPos == null) return true;
        return ownPos.empty();
    }

    public enum ChallengeState{
        WAITING,
        PICK_LOCATION,
        PICK_LOCATION_WAITING,
        PICK_LOCATION_DONE,
        FINDING_EACH_OTHER,
        FOUND,
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

    public Position getOwnPos() {
        return ownPos;
    }

    public void setOwnPos(Position ownPos) {
        this.ownPos = ownPos;
    }

    public Position getChosenPos() {
        return chosenPos;
    }

    public void setChosenPos(Position chosenPos) {
        this.chosenPos = chosenPos;
    }

    public static class Position{
        private double longitude;
        private double latitude;

        public Position() {}

        public Position(LatLng latLng){
            this.latitude = latLng.latitude;
            this.longitude = latLng.longitude;
        }

        public Position(double latitude, double longitude){
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public LatLng changeToLatLng(){
            return new LatLng(latitude, longitude);
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

        public boolean empty() {
            return longitude == 0 && latitude == 0;
        }
    }
}
