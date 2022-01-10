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

                Log.e("DatabaseService", snapshot.toString());
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

                for (String friendUID : currentUserData.getFriends()){
                    friendsUIDArray.add(friendUID);
                    User friend = users.get(friendUID);
                    friendsData.add(friend);
                    friendsNameArray.add(friend.getName());
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

    // ------------ Mock data ------------
    // mock Data TODO remove this:
//    public JSONObject getUsers(){
//        return users;
//    }
//
//    public JSONObject getUser(String ID){
//        try{
//            return users.getJSONObject(ID);
//        } catch (JSONException e) {
//            Log.e("Database/getUsers", String.format("Can't find a user by the ID: %s, in getUser()", ID));
//            return null;
//        }
//    }
//
//    public JSONObject[] getUsersArray(){
//        return usersArray;
//    }
//
//    public String[] getFriendsNameArray() {
//       return toArray(friendsNameArray);
//    }
//    public String[] getFriendsIDArray(){
//        return toArray(friendsIDArray);
//    }
//
//    // TODO rework adding and deleting users
//    public void toggleFriend(String friendsID){
//        if (friends.has(friendsID)) friends.remove(friendsID); // remove friend
//        else { // add friend
//            try {
//                friends.put(friendsID, users.get(friendsID));
//            } catch (JSONException e){
//                Log.e("DatabaseService", "couldn't find the user by the ID " + friendsID);
//            }
//        }
//
//        splitFriendsToArray();
//    }
//
////    public JSONObject getCurrentUser(){
////        return currentUser;
////    }
//
//    // temp, for mock data:
//    public void setCurrentUser(String name){
//        currentUser = new JSONObject();
//        try {
//            Random random = new Random();
//            currentUser.put("Name", name);
//            currentUser.put("Online", true);
//            currentUser.put("Score", random.nextInt(25));
//
//            String id = "";
//            while (users.has(id))
//                id = "" + random.nextInt(1000);
//
//            currentUser.put("ID", id);
//
//        } catch (JSONException e){
//            Log.e("DatabaseService/setUser", "Failed to set user: \n" + e);
//        }
//    }
//
//    // mock data:
//    public void makeUsers() {
//        String[] names = new String[]{"Henkie", "Sterre", "Jelle", "Floor", "Sil", "Frank", "Henkie 2", "Sallie", "Carmine", "Norbert", "Pam", "Deon", "Modesto", "Isaac", "Robert", "Bernie", "Rodrigo", "Yesenia", "Rosalinda", "Mohammed", "Britt", "Candace", "Ginger", "Zelma", "Patricia", "Aurelio", "Carlos", "Emmitt", "Garfield", "Charley", "Blanche", "Efren", "Kay", "Pam", "Robert", "Pearlie", "Imelda", "Daryl", "Latonya", "Jami", "Jere", "Dwain", "Randolph", "Ina", "Karla", "Ellen", "Aimee", "Malcolm", "Antione", "Lana", "Sherrie", "Carlo", "Anastasia", "Tonya", "Harris", "Roslyn"};
//
//        usersArray = new JSONObject[names.length];
//        users = new JSONObject();
//        friends = new JSONObject();
//
//        Random random = new Random();
//
//        for (int i = 0; i < usersArray.length; i++) {
//            try {
//                // data of the user
//                JSONObject userData = new JSONObject();
//                userData.put("Name", names[i]);
//                userData.put("Online", random.nextInt(3) == 0);
//                userData.put("Score", random.nextInt(25));
//
//                // ID of the user
//                String id = "";
//                if (names[i].equals("Frank"))
//                    id = "666";
//                else
//                    while (users.has(id) || id.equals("666"))
//                        id = "" + random.nextInt(1000);
//
//                // put it al together in an array and JSONObject (the JSONObject is how we will get it from the database)
//                userData.put("ID", id);
//                usersArray[i] = userData;
//                users.put(id, userData);
//
//                // if they are the current users friend
//                if (i > 0 && i < 6)
//                    friends.put(id, userData);
//            } catch (Exception e){
//                Log.e("DatabaseServie/makeUser", "failed to create user " + names[i] + "\n" + e);
//            }
//        }
//
//        splitFriendsToArray();
////            Log.e("Test", users.toString());
////            Log.e("test", friends.toString());
//    }
//
//    private void splitFriendsToArray(){
//        friendsNameArray = new ArrayList<>();
//        friendsIDArray = new ArrayList<>();
//        Iterator<String> keys = friends.keys();
//        while (keys.hasNext()){
//            String key = keys.next();
//            friendsIDArray.add(key);
//            try{
//                friendsNameArray.add(users.getJSONObject(key).getString("Name"));
//            }catch (JSONException e){
//                Log.e("DatabaseService/MakeUsers", "whiles making user, users doesn't got an name " + key);
//            }
//        }
////        Log.e("test", friends.toString());
//    }
//
//    private String[] toArray(ArrayList<String> arrayListIn){
//        String[] _array = new String[arrayListIn.size()];
//        arrayListIn.toArray(_array);
//        return _array;
//    }
}