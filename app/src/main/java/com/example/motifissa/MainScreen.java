package com.example.motifissa;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;
import android.content.Intent;

public class MainScreen extends AppCompatActivity {

    DashboardFragment dashboardFragment;

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
        getSupportFragmentManager().beginTransaction().replace(R.id.dashboard_container, dashboardFragment).commit();

    }
}