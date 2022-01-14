package com.example.motifissa.HelperClasses;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.motifissa.DatabaseService;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.Query;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ServiceListener extends AppCompatActivity {
    private static final String TAG = "ServiceListener";
    // service
    protected ListenerVariable<Boolean> mBounded; // a custom type, that allows us to add listeners to variables
    protected boolean mIsConnecting = true;
    protected DatabaseService mDatabaseService;

    public ServiceListener() {
        mBounded = new ListenerVariable<>(false);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // reset al the listeners
        mBounded = new ListenerVariable<>(false);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // setup the service that connects to the database
        this.connectToService();
    }


    protected void onServiceConnect(){}

    // ----------------  Connect to service  ----------------
    public void connectToService(){
        if (!mBounded.get()) {
            mIsConnecting = true;
//            Log.e(TAG, "-----------------------------------------\n Change: " + mBounded.getChangeListeners().toString() + "\nSuccess: " + mBounded.getSuccessListeners().toString());
//            mBounded = new ListenerVariable<>(false);
            mBounded.set(false);
            Intent serviceIntent = new Intent(this, DatabaseService.class);
            bindService(serviceIntent, mConnection, BIND_AUTO_CREATE);
        }
    }

    protected ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
//            Toast.makeText(getBaseContext(), "Service is disconnected", Toast.LENGTH_SHORT).show();
            mBounded.set(false);
            mIsConnecting = false;
            mDatabaseService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
//            Toast.makeText(getBaseContext(), "Service is connected", Toast.LENGTH_SHORT).show();
            DatabaseService.LocalBinder mLocalBinder = (DatabaseService.LocalBinder)service;
            mDatabaseService = mLocalBinder.getServerInstance();

            mIsConnecting = false;
            mBounded.set(true);

            onServiceConnect();
        }
    };

    @Override
    protected void onStop() {
        super.onStop();

        //when the activity is stopped, release the server
        if(mBounded.get()) {
            unbindService(mConnection);
        }
        // delete all bound listeners:
        mBounded = new ListenerVariable<>(false);
        mIsConnecting = false;
    }

    // ---------------- Service functions ----------------
    public ListenerTask<Query> getNotifications(){
        return new ListenerTask<>(this, () -> mDatabaseService.getNotifications());
    }

    public ListenerTask<User> getUser(String UID){
        return new ListenerTask<>(this, () -> mDatabaseService.getUser(UID));
    }

    public ListenerTask<User> getCurrentUser(){
        return new ListenerTask<>(this, () -> mDatabaseService.getCurrentUser());
    }

    public ListenerTask<Task<Void>> sendNotification(String msg, String toUID){
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        String formattedDate = df.format(c);

        return new ListenerTask<>(this, () -> mDatabaseService.sendNotification(msg, toUID, formattedDate));
    }

    public ListenerTask<ArrayList<User>> getFriends(){
        return new ListenerTask<>(this, () -> mDatabaseService.getFriendsData());
    }

    public ListenerTask<Query> getCurrentUserQuery(){
        return new ListenerTask<>(this, () -> mDatabaseService.getCurrentUserQuery());
    }

    public ListenerTask<ArrayList<String>> getFriendsNames(){
        return new ListenerTask<>(this, () -> mDatabaseService.getFriendsNameArray());
    }

    public ListenerTask<ListenerVariable<Boolean>> getUpdateListener(){
        return new ListenerTask<>(this, () -> mDatabaseService.getUpdateListener());
    }

    // this function can be used if you want to get more values at the same time, because in this success listener you could call the direct functions
    public ListenerTask<Boolean> isBounded(){
        return new ListenerTask<>(this, () -> mBounded.get());
    }

    // ----------- Direct service functions  -----------
    // DON't USE THESE AS THEY COULD TROW A NULLPOINTER ERROR because the connection isn't checked
    public User getUserDirect(String UID){
        return mDatabaseService.getUser(UID);
    }

}


