package com.example.motifissa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.content.Intent;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainScreen extends AppCompatActivity {

    // fragments
    DashboardFragment dashboardFragment;
    FriendsFragment friendsFragment;

    //elements
    BottomNavigationView bottomNav;

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
//                case R.id.nav_dashboard:
//                    selectedFragment = dashboardFragment;
//                    break;
            }
            if (selectedFragment == null){
                Toast.makeText(MainScreen.this, "SelectedFragment in navListener was null", Toast.LENGTH_SHORT).show();
                return false;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, selectedFragment).commit();
            return true;
        }
    };
}