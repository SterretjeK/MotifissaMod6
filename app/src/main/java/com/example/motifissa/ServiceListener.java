package com.example.motifissa;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.Query;

import java.util.ArrayList;

public class ServiceListener extends AppCompatActivity {
    // service
    ListenerVariable<Boolean> mBounded = new ListenerVariable<>(false); // a custom type, that allows us to add listeners to variables
    boolean mIsConnecting;
    DatabaseService mDatabaseService;

    public ServiceListener() {
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // setup the service that connects to the database
        this.connectToService();
    }


    protected void onServiceConnect(){}

    // -------- connect to service --------
    private void connectToService(){
        if (!mBounded.get()) {
            mIsConnecting = true;
            mBounded = new ListenerVariable<>(false);
            Intent serviceIntent = new Intent(this, DatabaseService.class);
            bindService(serviceIntent, mConnection, BIND_AUTO_CREATE);
        }
    }

    ServiceConnection mConnection = new ServiceConnection() {
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

    // -------- service functions  --------
//    public QueueItem<Query> getNotifications(){
//        QueueItem<Query> queueItem = new QueueItem<>();
//        return queueItem;
//    }

    public ListenerTask<Query> getNotifications(){
        return new ListenerTask<>(mBounded, () -> mDatabaseService.getNotifications());
    }

    public ListenerTask<User> getUser(String UID){
        return new ListenerTask<>(mBounded, () -> mDatabaseService.getUser(UID));
    }

}


