package com.example.motifissa;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class LoginScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        Button loginButton;
        loginButton = findViewById(R.id.login_button);

        loginButton.setOnClickListener(view -> {
            // click handling code
            Intent dashboard = new Intent(LoginScreen.this, DashboardScreen.class);
            startActivity(dashboard);

        });
    }
}