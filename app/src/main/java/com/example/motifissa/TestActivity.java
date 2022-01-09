package com.example.motifissa;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class TestActivity extends AppCompatActivity {

    // service
    ListenerVariable<Boolean> mBounded = new ListenerVariable<>(false); // a custom type, that allows us to add listeners to variables
    boolean mIsConnecting;
    DatabaseService mDatabaseService;

    // firebase auth
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null){ //log out if the login isn't valid
            mAuth.signOut();
            finish();
            Intent loginPageIntent = new Intent(this, LoginScreen.class);
            startActivity(loginPageIntent);
        }

        TextView helloMsg = findViewById(R.id.HelloMsg);
        assert currentUser != null;
        helloMsg.setText("Hello " +  currentUser.getDisplayName());


        EditText edit_name = findViewById(R.id.NameTxt);
        EditText edit_password = findViewById(R.id.PasswordTxt);
        Button button = findViewById(R.id.btn_submit);

//        button.setOnClickListener(v->{
//            User user = new User(edit_name.getText().toString(), edit_password.getText().toString());
//            // TODO change listenerVariable to a queue.
//            if(mBounded.get()){
//                addUser(user);
//            } else {
//                mBounded.setListener(value -> {
//                    if (value){
//                        addUser(user);
//                    }
//                });
//            }
//        });

        Button logout = findViewById(R.id.btn_logout);
        logout.setOnClickListener(v -> {
            mAuth.signOut();
            Intent loginPageIntent = new Intent(this, LoginScreen.class);
            startActivity(loginPageIntent);
        });
    }

    private void addUser(User user){
       mDatabaseService.addUser(user).addOnSuccessListener(success -> Toast.makeText(this, "User successfully added", Toast.LENGTH_SHORT).show())
               .addOnFailureListener(error -> Toast.makeText(this, "" + error, Toast.LENGTH_SHORT).show());
    }

    @Override
    protected void onStart() {
        super.onStart();

        // setup the service that connects to the database (just mock data for now)
        this.connectToService();
    }


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
            Toast.makeText(TestActivity.this, "Service is disconnected", Toast.LENGTH_SHORT).show();
            mBounded.set(false);
            mIsConnecting = false;
            mDatabaseService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Toast.makeText(TestActivity.this, "Service is connected", Toast.LENGTH_SHORT).show();
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
}