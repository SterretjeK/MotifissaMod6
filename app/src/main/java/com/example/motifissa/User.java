package com.example.motifissa;

public class User {

    private String Name;
    private String Password;
    private String ID;
    private int Score;
//    private boolean isOnline;

    public User(){}
    public User(String name, String password){
        this.Name = name;
        this.Password = password;
        this.ID = "666";
        this.Score = 0;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public int getScore() {
        return Score;
    }

    public void setScore(int score) {
        Score = score;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }
}
