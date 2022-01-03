package com.example.motifissa;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import java.util.ArrayList;
import java.util.Arrays;

public class DatabaseService extends Service {

    IBinder mBinder = new LocalBinder();

    // data
    ArrayList<String> friends = new ArrayList<>(Arrays.asList("Sterre", "Jelle", "Floor", "Sil", "Frank"));

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class LocalBinder extends Binder {
        public DatabaseService getServerInstance() {
            return DatabaseService.this;
        }
    }

    public String[] getUsersString() {
        return new String[]{"Henkie", "Sterre", "Jelle", "Floor", "Sil", "Frank", "Henkie 2", "Sallie", "Carmine", "Norbert", "Pam", "Deon", "Modesto", "Isaac", "Robert", "Bernie", "Rodrigo", "Yesenia", "Rosalinda", "Mohammed", "Britt", "Candace", "Ginger", "Zelma", "Patricia", "Aurelio", "Carlos", "Emmitt", "Garfield", "Charley", "Blanche", "Efren", "Frank", "Kay", "Pam", "Robert", "Pearlie", "Imelda", "Daryl", "Latonya", "Jami", "Jere", "Dwain", "Randolph", "Ina", "Karla", "Ellen", "Aimee", "Malcolm", "Antione", "Lana", "Sherrie", "Carlo", "Anastasia", "Tonya", "Harris", "Roslyn"};
    }

    public String[] getFriendsString() {
        return (String[]) friends.toArray();
    }

    public void addFriend(String newFriend) {
        if (!friends.contains(newFriend)) friends.add(newFriend);
    }
}