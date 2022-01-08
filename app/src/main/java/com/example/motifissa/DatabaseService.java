package com.example.motifissa;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class DatabaseService extends Service {

    IBinder mBinder = new LocalBinder();

    // data
    JSONObject[] usersArray;
    JSONObject users;
    JSONObject currentUser;
    ArrayList<String> friendsNameArray;
    ArrayList<String> friendsIDArray;
//    ArrayList<String> friends = new ArrayList<>(Arrays.asList("Sterre", "Jelle", "Floor", "Sil", "Frank"));
    JSONObject friends;

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class LocalBinder extends Binder {
        public DatabaseService getServerInstance() {
            return DatabaseService.this;
        }
    }


    public JSONObject getUsers(){
        return users;
    }

    public JSONObject getUser(String ID){
        try{
            return users.getJSONObject(ID);
        } catch (JSONException e) {
            Log.e("Database/getUsers", String.format("Can't find a user by the ID: %s, in getUser()", ID));
            return null;
        }
    }

    public JSONObject[] getUsersArray(){
        return usersArray;
    }

    public String[] getFriendsNameArray() {
       return toArray(friendsNameArray);
    }
    public String[] getFriendsIDArray(){
        return toArray(friendsIDArray);
    }

    // TODO rework adding and deleting users
    public void toggleFriend(String friendsID){
        if (friends.has(friendsID)) friends.remove(friendsID); // remove friend
        else { // add friend
            try {
                friends.put(friendsID, users.get(friendsID));
            } catch (JSONException e){
                Log.e("DatabaseService", "couldn't find the user by the ID " + friendsID);
            }
        }

        splitFriendsToArray();
    }

    public JSONObject getCurrentUser(){
        return currentUser;
    }

    // temp, for mock data:
    public void setCurrentUser(String name){
        currentUser = new JSONObject();
        try {
            Random random = new Random();
            currentUser.put("Name", name);
            currentUser.put("Online", true);
            currentUser.put("Score", random.nextInt(25));

            String id = "";
            while (users.has(id))
                id = "" + random.nextInt(1000);

            currentUser.put("ID", id);

        } catch (JSONException e){
            Log.e("DatabaseService/setUser", "Failed to set user: \n" + e);
        }
    }

    // mock data:
    public void makeUsers() {
        String[] names = new String[]{"Henkie", "Sterre", "Jelle", "Floor", "Sil", "Frank", "Henkie 2", "Sallie", "Carmine", "Norbert", "Pam", "Deon", "Modesto", "Isaac", "Robert", "Bernie", "Rodrigo", "Yesenia", "Rosalinda", "Mohammed", "Britt", "Candace", "Ginger", "Zelma", "Patricia", "Aurelio", "Carlos", "Emmitt", "Garfield", "Charley", "Blanche", "Efren", "Kay", "Pam", "Robert", "Pearlie", "Imelda", "Daryl", "Latonya", "Jami", "Jere", "Dwain", "Randolph", "Ina", "Karla", "Ellen", "Aimee", "Malcolm", "Antione", "Lana", "Sherrie", "Carlo", "Anastasia", "Tonya", "Harris", "Roslyn"};

        usersArray = new JSONObject[names.length];
        users = new JSONObject();
        friends = new JSONObject();

        Random random = new Random();

        for (int i = 0; i < usersArray.length; i++) {
            try {
                // data of the user
                JSONObject userData = new JSONObject();
                userData.put("Name", names[i]);
                userData.put("Online", random.nextInt(3) == 0);
                userData.put("Score", random.nextInt(25));

                // ID of the user
                String id = "";
                if (names[i].equals("Frank"))
                    id = "666";
                else
                    while (users.has(id) || id.equals("666"))
                        id = "" + random.nextInt(1000);

                // put it al together in an array and JSONObject (the JSONObject is how we will get it from the database)
                userData.put("ID", id);
                usersArray[i] = userData;
                users.put(id, userData);

                // if they are the current users friend
                if (i > 0 && i < 6)
                    friends.put(id, userData);
            } catch (Exception e){
                Log.e("DatabaseServie/makeUser", "failed to create user " + names[i] + "\n" + e);
            }
        }

        splitFriendsToArray();
//            Log.e("Test", users.toString());
//            Log.e("test", friends.toString());
    }

    private void splitFriendsToArray(){
        friendsNameArray = new ArrayList<>();
        friendsIDArray = new ArrayList<>();
        Iterator<String> keys = friends.keys();
        while (keys.hasNext()){
            String key = (String) keys.next();
            friendsIDArray.add(key);
            try{
                friendsNameArray.add(users.getJSONObject(key).getString("Name"));
            }catch (JSONException e){
                Log.e("DatabaseService/MakeUsers", "whiles making user, users doesn't got an name " + key);
            }
        }
//        Log.e("test", friends.toString());
    }

    private String[] toArray(ArrayList<String> arrayListIn){
        String[] _array = new String[arrayListIn.size()];
        arrayListIn.toArray(_array);
        return _array;
    }
}