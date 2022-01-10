package com.example.motifissa;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

public class DatabaseService extends Service {

    IBinder mBinder = new LocalBinder();

    // data OLD
//    JSONObject[] usersArray;
//    JSONObject users;
//    JSONObject currentUser;
//    ArrayList<String> friendsNameArray;
//    ArrayList<String> friendsIDArray;
//    JSONObject friends;

    // Firebase
    private DatabaseReference databaseReferenceUsers;
    private FirebaseUser currentUser;

    // Data
    private User currentUserData;
    private ArrayList<User> usersArray = new ArrayList<>();
    private HashMap<String, User> users = new HashMap<>();
    private ArrayList<String> friendsUIDArray = new ArrayList<>();
    private ArrayList<String> friendsNameArray = new ArrayList<>();
    private ArrayList<User> friendsData = new ArrayList<>();


    // ------------ Setup functions ------------
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // firebase setup
        FirebaseDatabase database = FirebaseDatabase.getInstance(getResources().getString(R.string.databaseURL));
        databaseReferenceUsers = database.getReference(getResources().getString(R.string.DatabaseUsersRoot));

         currentUser = (FirebaseUser) intent.getExtras().getParcelable("CurrentUser");

         setup();
//        makeUsers();
//        String username = intent.getExtras().getString("LOGIN_NAME");
//        setCurrentUser(username);
        return START_STICKY;
    }

    public void setup(){
        databaseReferenceUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // get/update users
                usersArray = new ArrayList<>();
                users = new HashMap<>();

                for (DataSnapshot data : snapshot.getChildren()){
                    User user = data.getValue(User.class);
                    usersArray.add(user);

                    users.put(user.getUID(), user);
                }

                currentUserData = users.get(currentUser.getUid());

                // add current users friends
                friendsUIDArray = new ArrayList<>();
                friendsNameArray = new ArrayList<>();
                friendsData = new ArrayList<>();

                try {
                    for (String friendUID : currentUserData.getFriends()) {
                        friendsUIDArray.add(friendUID);
                        User friend = users.get(friendUID);
                        friendsData.add(friend);
                        friendsNameArray.add(friend.getName());
                    }
                } catch(NullPointerException ignored){

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class LocalBinder extends Binder {
        public DatabaseService getServerInstance() {
            return DatabaseService.this;
        }
    }

    // ------------ Firebase functions ------------

    // Firebase get data functions
    public Task<Void> addUser(User user){
        // TODO validate data
        //  if (user == null) throw ....;

        return databaseReferenceUsers.push().setValue(user);
    }

    public Query getUsersQuery(){
        return databaseReferenceUsers.orderByKey();
    }

    public User getCurrentUser(){
        return currentUserData;
    }

    public FirebaseUser getCurrentFirebaseUser(){
        return currentUser;
    }

    public User getCurrentUserData() {
        return currentUserData;
    }

    public ArrayList<User> getUsersArray() {
        return usersArray;
    }

    public HashMap<String, User> getUsers() {
        return users;
    }

    public ArrayList<String> getFriendsUIDArray() {
        return friendsUIDArray;
    }

    public ArrayList<String> getFriendsNameArray() {
        return friendsNameArray;
    }

    public ArrayList<User> getFriendsData() {
        return friendsData;
    }

    public User getUser(String UID){
        return users.get(UID);
    }

    public Task<Void> toggleFriend(String UID){
        currentUserData.toggleFriend(UID);

        return databaseReferenceUsers.child(currentUserData.getUID()).setValue(currentUserData);
    }

    public Task<Void> sendNotification(String msg, String UID){
        User tempUser = users.get(UID);
        tempUser.addNotification(msg + "|" + currentUserData.getUID());
        return databaseReferenceUsers.child(UID).setValue(tempUser);
    }
}