package com.example.motifissa;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import java.util.Timer;
import java.util.TimerTask;

public class StartupScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup_screen);

        int timeout = 2000; // make the activity visible for 4 seconds 4000

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                finish();
                Intent homepage = new Intent(StartupScreen.this, LoginScreen.class);
                startActivity(homepage);
            }
        }, timeout);
    }
}