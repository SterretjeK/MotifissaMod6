package com.example.motifissa;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginScreen extends AppCompatActivity {
    public static final String LOGIN_NAME = "com.example.myfirstapp.LOGIN_NAME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        Button loginButton;
        loginButton = findViewById(R.id.login_button);

        loginButton.setOnClickListener(view -> {
            // click handling code
            // get text from username
            EditText usernameText = findViewById(R.id.username);
            String usernameMessage = usernameText.getText().toString();
            
            //get text from password
            EditText passwordText = findViewById(R.id.password);
            String passwordMessage = passwordText.getText().toString();
            
            
            if(!usernameMessage.matches("") && !passwordMessage.matches("")) { //checks if the username edit text is not empty
                Intent mainScreenIntent = new Intent(LoginScreen.this, MainScreen.class);
                mainScreenIntent.putExtra(LOGIN_NAME, usernameMessage);
                startActivity(mainScreenIntent);
            }
            if(passwordMessage.matches("")) {
                Toast.makeText(this, "Password is missing", Toast.LENGTH_LONG).show();
            }  
                
            if(usernameMessage.matches("")) {
                Toast.makeText(this, "Username is missing", Toast.LENGTH_LONG).show();
            }
            
        });
    }
}