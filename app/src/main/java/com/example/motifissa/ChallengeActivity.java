package com.example.motifissa;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import org.json.JSONObject;

import java.util.HashMap;

public class ChallengeActivity extends AppCompatActivity {

    // service
    ListenerVariable<Boolean> mBounded = new ListenerVariable<>(false); // a custom type, that allows us to add listeners to variables
    boolean mIsConnecting;
    DatabaseService mDatabaseService;

    // fragments
    int currentFragment = 1;
    ChooseFriendFragment chooseFriendFragment;
    OverviewChallengeFragment overviewChallengeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge);

        chooseFriendFragment = new ChooseFriendFragment();
        overviewChallengeFragment = new OverviewChallengeFragment();

        // This callback will change what happens when the user clicks back
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                if (currentFragment > 1)
                    changeFragment(currentFragment-1);
                else
                    finish();
            }
        };
        this.getOnBackPressedDispatcher().addCallback(this, callback);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // calling the action bar
        ActionBar actionBar = getSupportActionBar();
        // showing the back button in action bar
        assert actionBar != null; // to make sure that this activity has an action bar, idk wou die graag
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Challenge");
    }

    @Override
    protected void onStart() {
        super.onStart();

        // setup the service that connects to the database (just mock data for now)
        this.connectToService();
    }

    private void changeFragment(int changeToFragment){
        Fragment selectedFragment = null;

        switch (changeToFragment){
            case 1:
                selectedFragment = chooseFriendFragment;
                break;
            case 2:
                selectedFragment = overviewChallengeFragment;
                break;

            default:
                Toast.makeText(this, "SelectedFragment in navListener was unknown", Toast.LENGTH_SHORT).show();
                return;
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerChallenge, selectedFragment).commit();
        this.currentFragment = changeToFragment;
    }

    // this event will enable the back
    // function to the button on press
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (currentFragment > 1)
                    changeFragment(currentFragment-1);
                else
                    finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void connectToService(){
        mIsConnecting  = true;
        mBounded = new ListenerVariable<>(false);
        Intent serviceIntent = new Intent(this, DatabaseService.class);
        bindService(serviceIntent, mConnection, BIND_AUTO_CREATE);
    }

    ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBounded.set(false);
            mIsConnecting = false;
            mDatabaseService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBounded.set(true);
            mIsConnecting = false;
            DatabaseService.LocalBinder mLocalBinder = (DatabaseService.LocalBinder)service;
            mDatabaseService = mLocalBinder.getServerInstance();

            mDatabaseService.makeUsers();
        }
    };

    @Override
    protected void onStop() {
        super.onStop();

        //when the activity is stopped, release the server
        if(mBounded.get()) {
            unbindService(mConnection);
            mBounded.set(false);
        }
        mIsConnecting = false;
    }

    public JSONObject[] getUsers(){
        if (mBounded.get() && mDatabaseService != null) {
            try {
                return mDatabaseService.getUsers();
            } catch (Exception e) {
                Log.e("ChallengeScreen", "Database not bound, but said it was when trying to access getFriends");
            }
        }

        if (!mIsConnecting) {
            this.connectToService();
        }
        return null;
    }

    public String[] getFriends(){
        if (mBounded.get()) {
            try {
                return mDatabaseService.getFriendsString();
            } catch (Exception e){
                Log.e("ChallengeScreen", "Database not bound, but said it was when trying to access getFriends");
            }
        }

        if (!mIsConnecting) {
            this.connectToService();
        }
        return null;

    }
}