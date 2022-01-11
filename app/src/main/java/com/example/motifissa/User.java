package com.example.motifissa;

import java.util.ArrayList;
import java.util.List;

public class User {

    private String Name;
    private String ID;
    private String UID;
    private int Score;
    private boolean isOnline;
    private List<String> Friends;

    public User(){}
    public User(String name, String UID, String ID){
        this.Name = name;
        this.UID = UID;
        this.ID = ID;
        this.Score = 0;
        this.isOnline = true;
        this.Friends = new ArrayList<>();
        this.Friends.add("rKjspnNlU7OWs4BOGeQR59g5VQ23"); // add the TestAccount to their friends
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public int getScore() {
        return Score;
    }

    public void setScore(int score) {
        Score = score;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public List<String> getFriends() {
        return Friends;
    }

    public void toggleFriend(String UID){
        if (Friends == null) Friends = new ArrayList<>();
        if (Friends.contains(UID))
            Friends.remove(UID);
        else
            Friends.add(UID);
    }

    public void setFriends(List<String> friends) {
        Friends = friends;
    }
}

