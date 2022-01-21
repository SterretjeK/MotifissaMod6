package com.example.motifissa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.MenuItem;
import android.content.Intent;
import android.widget.Toast;

import com.example.motifissa.HelperClasses.ListenerTask;
import com.example.motifissa.HelperClasses.ListenerVariable;
import com.example.motifissa.HelperClasses.ServiceListener;
import com.example.motifissa.HelperClasses.User;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.Query;

import java.util.ArrayList;

public class MainScreen extends ServiceListener {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // get the current user
        currentUser = mAuth.getCurrentUser();
        if (currentUser == null) { //log out if the login isn't valid
            logout();
        }

        // setup the dashboard fragment
        dashboardFragment = new DashboardFragment();
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

    public void logout() {
        mAuth.signOut();
        finish();
        Intent loginPageIntent = new Intent(this, LoginScreen.class);
        startActivity(loginPageIntent);
    }

    private final BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;

            switch (item.getItemId()) {
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
            if (selectedFragment == null) {
                Toast.makeText(MainScreen.this, "SelectedFragment in navListener was unknown", Toast.LENGTH_SHORT).show();
                return false;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, selectedFragment).commit();
            return true;
        }
    };

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//
//        //when the activity is stopped, release the server
//        if (mBounded.get()) {
//            // set the user as offline, might disable this if it cost too much MB upload
//            mDatabaseService.toggleOnlineUser(currentUser.getUid(), false);
//            unbindService(mConnection);
//        }
//        mBounded = new ListenerVariable<>(false);
//        mIsConnecting = false;
//    }

    public void showNotifications() {
        Intent notificationsIntent = new Intent(MainScreen.this, NotificationsActivity.class);
        startActivity(notificationsIntent);
    }

}
