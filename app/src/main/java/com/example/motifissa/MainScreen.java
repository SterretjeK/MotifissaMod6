package com.example.motifissa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.content.Intent;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.Query;

import org.json.JSONObject;

import java.util.ArrayList;

public class MainScreen extends AppCompatActivity {

    // fragments
    DashboardFragment dashboardFragment;
    FriendsFragment friendsFragment;

    // elements
    BottomNavigationView bottomNav;

    // service
    ListenerVariable<Boolean> mBounded = new ListenerVariable<>(false); // a custom type, that allows us to add listeners to variables
    boolean mIsConnecting;
    DatabaseService mDatabaseService;

    // firebase
    private FirebaseAuth mAuth;
    FirebaseUser currentUser;

    String loginName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // get the current user
        currentUser = mAuth.getCurrentUser();
        if (currentUser == null){ //log out if the login isn't valid
            mAuth.signOut();
            finish();
            Intent loginPageIntent = new Intent(this, LoginScreen.class);
            startActivity(loginPageIntent);
        }
        loginName = currentUser.getDisplayName();

//        // get the login name from the intent
//        Intent intent = getIntent();
//        loginName = intent.getStringExtra(LoginScreen.LOGIN_NAME);

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

        // setup logout button
        FloatingActionButton logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(view -> {
            mAuth.signOut();
            finish();
            Intent loginPageIntent = new Intent(this, LoginScreen.class);
            startActivity(loginPageIntent);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        // setup the service that connects to the database (just mock data for now)
        this.connectToService();
    }


    private final BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
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
            Toast.makeText(MainScreen.this, "Service is disconnected", Toast.LENGTH_SHORT).show();
            mBounded.set(false);
            mIsConnecting = false;
            mDatabaseService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Toast.makeText(MainScreen.this, "Service is connected", Toast.LENGTH_SHORT).show();
            DatabaseService.LocalBinder mLocalBinder = (DatabaseService.LocalBinder)service;
            mDatabaseService = mLocalBinder.getServerInstance();

//            mDatabaseService.makeUsers();
//            mDatabaseService.setCurrentUser(loginName);

            mIsConnecting = false;
            mBounded.set(true);
        }
    };

    @Override
    protected void onStop() {
        super.onStop();

        //when the activity is stopped, release the server
        if(mBounded.get()) {
            unbindService(mConnection);
        }
        mBounded = new ListenerVariable<>(false);
        mIsConnecting = false;
    }

    public Query getUsersQuery(){
        if (mBounded.get() && mDatabaseService != null) {
            try {
                return mDatabaseService.getUsersQuery();
            } catch (Exception e) {
                Log.e("MainScreen", "Database not bound, but said it was when trying to access getUsersQuery");
            }
        }

        if (!mIsConnecting) {
            Toast.makeText(this, "Service is disconnected, connecting...", Toast.LENGTH_SHORT).show();
            this.connectToService();
        }
        return null;
    }

    public ArrayList<String> getFriendsUID(){
        if (mBounded.get() && mDatabaseService != null) {
            try {
                return mDatabaseService.getFriendsUIDArray();
            } catch (Exception e) {
                Log.e("MainScreen", "Database not bound, but said it was when trying to access getFriendsUID");
            }
        }

        if (!mIsConnecting) {
            Toast.makeText(this, "Service is disconnected, connecting...", Toast.LENGTH_SHORT).show();
            this.connectToService();
        }
        return null;
    }

    public Task<Void> toggleFriend(String UID){
        if (mBounded.get() && mDatabaseService != null) {
            try {
                return mDatabaseService.toggleFriend(UID);
            } catch (Exception e) {
                Log.e("MainScreen", "Database not bound, but said it was when trying to access getFriendsUID");
            }
        }

        if (!mIsConnecting) {
            Toast.makeText(this, "Service is disconnected, connecting...", Toast.LENGTH_SHORT).show();
            this.connectToService();
        }
        return null;
    }

    public FirebaseUser getCurrentUser(){
        return currentUser;
    }
}