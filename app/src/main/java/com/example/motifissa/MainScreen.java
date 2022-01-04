package com.example.motifissa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.content.Intent;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONObject;

public class MainScreen extends AppCompatActivity {

    // fragments
    DashboardFragment dashboardFragment;
    FriendsFragment friendsFragment;

    // elements
    BottomNavigationView bottomNav;

    // service
    boolean mBounded;
    boolean mIsConnecting;
    DatabaseService mDatabaseService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        // get the login name from the intent
        Intent intent = getIntent();
        String loginName = intent.getStringExtra(LoginScreen.LOGIN_NAME);

        // setup the dashboard fragment
        dashboardFragment = new DashboardFragment();
        Bundle bundle = new Bundle();
        bundle.putString("LOGIN_NAME", loginName);
        dashboardFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, dashboardFragment).commit();

        // setup the friends fragment
        friendsFragment = new FriendsFragment();

        // setup the bottom navigation
        bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // setup the service that connects to the database (just mock data for now)
        this.connectToService();
    }


    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;

            switch (item.getItemId()){
                case R.id.dashboard:
                    selectedFragment = dashboardFragment;
                    break;
                case R.id.friends:
                    selectedFragment = friendsFragment;
                    break;
//                case R.id.profile:
//                    selectedFragment = ;
//                    break;
            }
            if (selectedFragment == null){
                Toast.makeText(MainScreen.this, "SelectedFragment in navListener was unknown", Toast.LENGTH_SHORT).show();
                return false;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, selectedFragment).commit();
            return true;
        }
    };

    private void connectToService(){
        mIsConnecting = true;
        Intent serviceIntent = new Intent(this, DatabaseService.class);
        bindService(serviceIntent, mConnection, BIND_AUTO_CREATE);
    }

    ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Toast.makeText(MainScreen.this, "Service is disconnected", Toast.LENGTH_SHORT).show();
            mBounded = false;
            mIsConnecting = false;
            mDatabaseService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Toast.makeText(MainScreen.this, "Service is connected", Toast.LENGTH_SHORT).show();
            mBounded = true;
            mIsConnecting = false;
            DatabaseService.LocalBinder mLocalBinder = (DatabaseService.LocalBinder)service;
            mDatabaseService = mLocalBinder.getServerInstance();

            mDatabaseService.makeUsers();

            if(friendsFragment.mainscreen != null)
                friendsFragment.populateList();
        }
    };

    @Override
    protected void onStop() {
        super.onStop();

        //when the activity is stopped, release the server
        if(mBounded) {
            unbindService(mConnection);
            mBounded = false;
        }
        mIsConnecting = false;
    }

    public JSONObject[] getUsers(){
        if (mBounded){
            return mDatabaseService.getUsers();
        } else{
            if (!mIsConnecting) {
                Toast.makeText(this, "Service is disconnected, connecting...", Toast.LENGTH_SHORT).show();
                this.connectToService();
            }
            return null;
        }
    }

    public String[] getFriends(){
        if (mBounded){
            return mDatabaseService.getFriendsString();
        } else{
            if (!mIsConnecting) {
                Toast.makeText(this, "Service is disconnected, connecting...", Toast.LENGTH_SHORT).show();
                this.connectToService();
            }
            return null;
        }
    }
}