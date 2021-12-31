package com.example.motifissa;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

public class DashboardScreen extends AppCompatActivity {

    ScoreboardFragment scoreboardFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_screen);

        // get the login name from the intent
        Intent intent = getIntent();
        String message = intent.getStringExtra(LoginScreen.LOGIN_NAME);
//        Toast.makeText(this, message, Toast.LENGTH_LONG).show();

        // set the login name to the screen's textview
        TextView Username_welcome = (TextView) findViewById(R.id.Username_welcome);
        Username_welcome.setText(message);

        // setup the scoreboard fragment
        scoreboardFragment = new ScoreboardFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.scoreboard_container, scoreboardFragment).commit();
    }
}