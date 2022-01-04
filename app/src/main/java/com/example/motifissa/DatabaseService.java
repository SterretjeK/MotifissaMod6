package com.example.motifissa;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class DatabaseService extends Service {

    IBinder mBinder = new LocalBinder();

    // data
    JSONObject[] users;
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


    public JSONObject[] getUsers(){
        return users;
    }

    public String[] getFriendsString() {
        String[] _friends = new String[friends.size()];
        friends.toArray(_friends);
        return _friends;
    }

    public void addFriend(String newFriend) {
        if (!friends.contains(newFriend)) friends.add(newFriend);
    }

    public void toggleFriend(String friend){
        if (friends.contains(friend)) friends.remove(friend);
        else friends.add(friend);
    }

    public void makeUsers() {
        try {
            String[] names = new String[]{"Henkie", "Sterre", "Jelle", "Floor", "Sil", "Frank", "Henkie 2", "Sallie", "Carmine", "Norbert", "Pam", "Deon", "Modesto", "Isaac", "Robert", "Bernie", "Rodrigo", "Yesenia", "Rosalinda", "Mohammed", "Britt", "Candace", "Ginger", "Zelma", "Patricia", "Aurelio", "Carlos", "Emmitt", "Garfield", "Charley", "Blanche", "Efren", "Kay", "Pam", "Robert", "Pearlie", "Imelda", "Daryl", "Latonya", "Jami", "Jere", "Dwain", "Randolph", "Ina", "Karla", "Ellen", "Aimee", "Malcolm", "Antione", "Lana", "Sherrie", "Carlo", "Anastasia", "Tonya", "Harris", "Roslyn"};

            users = new JSONObject[names.length];
            Random random = new Random();

            for (int i = 0; i < users.length; i++) {
                JSONObject userData = new JSONObject();
                userData.put("Name", names[i]);
                if (names[i].equals("Frank"))
                    userData.put("ID", "#666");
                else
                    userData.put("ID", "#" + random.nextInt(1000));
                users[i] = userData;
            }
        } catch (JSONException e){
            Log.e("DATABASE", "faild to make users\n" + e);
        }
    }
}