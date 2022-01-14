package com.example.motifissa;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.motifissa.HelperClasses.ListenerVariable;
import com.example.motifissa.HelperClasses.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class DatabaseService extends Service {

    private static final String TAG = "DatabaseService";
    IBinder mBinder = new LocalBinder();

    // Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReferenceUsers;
    private FirebaseUser currentUser;

    // Data
    private User currentUserData;
    private ArrayList<User> usersArray = new ArrayList<>();
    private HashMap<String, User> users = new HashMap<>();
    private ArrayList<String> friendsUIDArray = new ArrayList<>();
    private ArrayList<String> friendsNameArray = new ArrayList<>();
    private ArrayList<User> friendsData = new ArrayList<>();
    ListenerVariable<Boolean> updateListener = new ListenerVariable<>(false);


    // ------------ Setup functions ------------
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // firebase setup
        FirebaseDatabase database = FirebaseDatabase.getInstance(getResources().getString(R.string.databaseURL));
        databaseReferenceUsers = database.getReference(getResources().getString(R.string.DatabaseUsersRoot));

        // get the current user, OLD
//         currentUser = intent.getExtras().getParcelable("CurrentUser");

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // get the current user
        currentUser = mAuth.getCurrentUser();
        if (currentUser == null){ //log out if the login isn't valid
//            logout();
            Log.e(TAG, "CURRENT USER WAS NULL!!!!!!!!!!!!!!!!!!!!");
        }

        // set the user to online
        databaseReferenceUsers.child(currentUser.getUid()).child("online").setValue(true);

         setup();
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

                    assert user != null;
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
                        assert friend != null;
                        friendsNameArray.add(friend.getName());
                    }
                } catch(NullPointerException ignored){

                }

                updateListener.set(true);
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
    public Query getCurrentUserQuery(){
        return databaseReferenceUsers.child(currentUser.getUid()).child("friends");
    }

    public Query getNotifications(){
        return databaseReferenceUsers.child(currentUser.getUid()).child("notifications");
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

    public Task<Void> sendNotification(String msg, String UID, String date){
        User tempUser = users.get(UID);
        if (tempUser != null) {
            tempUser.addNotification(msg + "|" + currentUserData.getUID() + "|" + date);
        } else{
            Toast.makeText(getBaseContext(), "User doesn't exists, trying to remove friend..", Toast.LENGTH_SHORT).show();
            // remove the UID from the users friend list if it doesn't exist anymore
            if (friendsUIDArray.contains(UID)){
                User user = currentUserData;
                if (user.removeFriend(UID)){ // if the friend could be removed
                    databaseReferenceUsers.child(currentUserData.getUID()).setValue(currentUserData)
                            .addOnSuccessListener(success ->  Toast.makeText(getBaseContext(), "successfully removed unknown friend", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(error ->{
                                Toast.makeText(getBaseContext(), "Couldn't remove friend", Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "Couldn't remove unknown friend, error: " + error);
                            });
                }
            }
        }
        return databaseReferenceUsers.child(UID).setValue(tempUser);
    }

    public Task<Void> toggleOnlineUser(String UID, boolean state){
        // set the user to offline or offline
        return databaseReferenceUsers.child(currentUser.getUid()).child("online").setValue(state);
    }

    public ListenerVariable<Boolean> getUpdateListener(){
        return updateListener;
    }
}