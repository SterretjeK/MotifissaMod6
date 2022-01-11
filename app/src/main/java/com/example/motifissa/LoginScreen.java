package com.example.motifissa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Random;

public class LoginScreen extends AppCompatActivity {
    public static final String LOGIN_NAME = "com.example.myfirstapp.LOGIN_NAME";
    private static final String TAG = "LoginScreen";

    // firebase
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReferenceUsers;

    @Override
    public void onStart() {
        super.onStart();

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){ // user is still signed in
            loginSuccess(currentUser);
        }
    }

    TabLayout tabLayout;
    ViewPager viewPager;
    float v = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // init the database access
        FirebaseDatabase database = FirebaseDatabase.getInstance(getResources().getString(R.string.databaseURL));
        databaseReferenceUsers = database.getReference(getResources().getString(R.string.DatabaseUsersRoot));

        Button loginButton = findViewById(R.id.login_button);
        loginButton.setOnClickListener(view -> login()); // click handling code

        tabLayout.addTab(tabLayout.newTab().setText("Login"));
        tabLayout.addTab(tabLayout.newTab().setText("Signup"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final LoginAdapter adapter = new LoginAdapter(getSupportFragmentManager(), this, tabLayout.getTabCount());
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        // from the internet lol
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                Log.i("TAG", "onTabSelected: " + tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                Log.i("TAG", "onTabUnselected: " + tab.getPosition());
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                Log.i("TAG", "onTabReselected: " + tab.getPosition());
            }
        });

    }

    private void signUp() {
        // get text from username
        EditText usernameText = findViewById(R.id.username);
        String username = usernameText.getText().toString();
        // TODO think of a better way, like searching of the corresponding email to the username as this method doesn't allow duplicate names
        String email = username + "@temp.com";

        //get text from password
        EditText passwordText = findViewById(R.id.password);
        String password = passwordText.getText().toString();

        if(username.matches("") || password.matches("")) { //checks if the username edit text is not empty
            Toast.makeText(LoginScreen.this, "Please fill in username or password to sign up", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length() < 6){  // if firebase considers it a weak password
            Toast.makeText(LoginScreen.this, "Password has to be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        activateLoadingAnimation();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        assert user != null;

                        // create new user profile:
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(username).build();

                        user.updateProfile(profileUpdates)
                                .addOnCompleteListener(updateProfileTask -> {
                                    if (updateProfileTask.isSuccessful()) {
                                        Log.d(TAG, "User profile updated.");
                                    }
                                });

                        // create new User in the data base
//                            User userProfile = new User(username, user.getUid(), "0");
                        Random random = new Random();

                        String id = "" + random.nextInt(1000);

                        User userProfile = new User(username, user.getUid(), id);
                        databaseReferenceUsers.child(user.getUid()).setValue(userProfile).addOnSuccessListener(success -> {
                            loginSuccess(user);
                            disableLoadingAnimation();
                        }).addOnFailureListener(error -> {
                            disableLoadingAnimation();
                            user.delete();
                            Toast.makeText(LoginScreen.this, "Couldn't make a new user, try another username", Toast.LENGTH_SHORT).show();
                        });
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());

                        Toast.makeText(LoginScreen.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                        disableLoadingAnimation();

                        // TODO show error for example red lines on the input boxes.
//                            updateUI(null);
                    }
                });
    }

    private void login(){
        // get text from username
        EditText usernameText = findViewById(R.id.username);
        String username = usernameText.getText().toString();

        //get text from password
        EditText passwordText = findViewById(R.id.password);
        String password = passwordText.getText().toString();

        if(username.matches("")) {  // checks if the username or password edit text is not empty
            Toast.makeText(this, "Username is missing", Toast.LENGTH_LONG).show();
        } else if(password.matches("")) {
            Toast.makeText(this, "Password is missing", Toast.LENGTH_LONG).show();
        } else {
            activateLoadingAnimation();

            // TODO think of a better way, like searching of the corresponding email to the username as this method doesn't allow duplicate names
            String email = username + "@temp.com";

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            disableLoadingAnimation();

                            loginSuccess(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginScreen.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                            // TODO show error for example red lines on the input boxes.
//                                updateUI(null);

                            disableLoadingAnimation();
                        }
                    });
        }
    }

    private void loginSuccess(FirebaseUser user){
        //starts the service
        Intent startServiceIntent = new Intent(this, DatabaseService.class);
//        startServiceIntent.putExtra(LOGIN_NAME, "Henk");
        startServiceIntent.putExtra("CurrentUser", mAuth.getCurrentUser());
        startService(startServiceIntent);


        // starts the main screen activity
        Intent mainScreenIntent = new Intent(LoginScreen.this, MainScreen.class);
        finish();
        startActivity(mainScreenIntent);

        // testing
//        Intent testIntent = new Intent(LoginScreen.this, TestActivity.class);
//        finish();
//        startActivity(testIntent);
    }


//    private void activateLoadingAnimation(){
//        ProgressBar progressBar = findViewById(R.id.progressBar);
//        progressBar.setVisibility(View.VISIBLE);
//
//        Button loginButton = findViewById(R.id.login_button);
//        loginButton.setActivated(false);
//    }
//
//    private void disableLoadingAnimation(){
//        ProgressBar progressBar = findViewById(R.id.progressBar);
//        progressBar.setVisibility(View.INVISIBLE);
//        Button loginButton = findViewById(R.id.login_button);
//        loginButton.setActivated(true);
//    }
}