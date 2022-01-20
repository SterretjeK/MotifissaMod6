package com.example.motifissa.HelperClasses;

public class Notification {
    
    private String title;
    private String sentBy;
    private NotificationType type;
    private String message;
    private boolean read;
    private String date;
    private String username;

    public Notification() {}

    public Notification(String type, String sentBy, String username) {
        this(type, sentBy, "-", username);
    }

    public Notification(String type, String sentBy, String date, String username) {
        this.sentBy = sentBy;
        this.date = date;
        this.username = username;
        read = false;
        message = "";

        if(type.equals(NotificationType.CHALLENGE.toString())){
            this.type = NotificationType.CHALLENGE;
            title = "Challenge";
            message = "You have been challenged by username.".replace("username", username);
        } else if(type.equals(NotificationType.FRIEND_REQUEST.toString())){
            this.type = NotificationType.FRIEND_REQUEST;
            title = "Friend Request";
            message =  "username would like to be your friend".replace("username", username);
        } else if(type.equals(NotificationType.ACCEPTED_CHALLENGE.toString())){
          this.type = NotificationType.ACCEPTED_CHALLENGE;
          title = "Challenge Accepted";
          message = "username accepted your challenge".replace("username", username);
        } else {
            this.type = NotificationType.MESSAGE;
        }
    }

    public Notification(NotificationType type, String sentBy, String message, String title) {
        this.type = type;
        this.sentBy = sentBy;
        this.message = message;
        this.title = title;
        read = false;
    }

    public enum NotificationType{
        CHALLENGE,
        ACCEPTED_CHALLENGE,
        FRIEND_REQUEST,
        MESSAGE
    }

    // getters and setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSentBy() {
        return sentBy;
    }

    public void setSentBy(String sentBy) {
        this.sentBy = sentBy;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String sendData() {
        return type.toString() + "|" + sentBy + (date.equals("-") ? "": "|" + date);
    }
}