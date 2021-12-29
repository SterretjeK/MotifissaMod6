package com.example.motifissa;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class DashboardScreen extends AppCompatActivity {

    ScoreboardFragment scoreboardFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_screen);

        scoreboardFragment = new ScoreboardFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.scoreboard_container, scoreboardFragment).commit();
    }
}