package com.example.motifissa.HelperClasses;

public class Notification {
    
    private String title;
    private String sentBy;
    private NotificationType type;
    private String message;
    private boolean read;
    private String date;

    public Notification() {}

    public Notification(String type, String sentBy) {
        this.sentBy = sentBy;
        read = false;
        message = "";
        date = "-";

        if(type.equals(NotificationType.CHALLENGE.toString())){
            this.type = NotificationType.CHALLENGE;
        } else if(type.equals(NotificationType.FRIEND_REQUEST.toString())){
            this.type = NotificationType.FRIEND_REQUEST;
        } else {
            this.type = NotificationType.MESSAGE;
        }


        switch (this.type){
            case CHALLENGE:
                message = "You have been challenged by username.";
                break;
            case FRIEND_REQUEST:
                message =  "username would like to be your friend";
                break;
        }

        title = message;
    }

    public Notification(String type, String sentBy, String date) {
        this.sentBy = sentBy;
        this.date = date;
        read = false;
        message = "";

        if(type.equals(NotificationType.CHALLENGE.toString())){
            this.type = NotificationType.CHALLENGE;
        } else if(type.equals(NotificationType.FRIEND_REQUEST.toString())){
            this.type = NotificationType.FRIEND_REQUEST;
        } else {
            this.type = NotificationType.MESSAGE;
        }


        switch (this.type){
            case CHALLENGE:
                message = "You have been challenged by username.";
                break;
            case FRIEND_REQUEST:
                message =  "username would like to be your friend";
                break;
        }

        title = message;
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
}