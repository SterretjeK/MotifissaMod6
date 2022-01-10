package com.example.motifissa;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class LoginTabFragment extends Fragment {
    public static final String LOGIN_NAME = "com.example.myfirstapp.LOGIN_NAME";

    ViewGroup root;

    TextView usernameText;
    EditText username;
    TextView passwordText;
    EditText password;
    Button loginButton;
    float v = 0;

    public LoginTabFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = (ViewGroup) inflater.inflate(R.layout.fragment_login_tab, container, false);


        usernameText = root.findViewById(R.id.username_text);
        username = root.findViewById(R.id.username);
        passwordText = root.findViewById(R.id.password_text);
        password = root.findViewById(R.id.password);
        loginButton = root.findViewById(R.id.login_button);

        usernameText.setTranslationX(800);
        username.setTranslationX(800);
        passwordText.setTranslationX(800);
        password.setTranslationX(800);
        loginButton.setTranslationY(300);

        usernameText.setAlpha(v);
        username.setAlpha(v);
        passwordText.setAlpha(v);
        password.setAlpha(v);
        loginButton.setAlpha(v);

        usernameText.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(300).start();
        username.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(350).start();
        passwordText.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(500).start();
        password.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(550).start();
        loginButton.animate().translationY(0).alpha(1).setDuration(800).setStartDelay(700).start();

        passwordText.setOnFocusChangeListener(passwordOnFocusChangeListener);
        loginButton.setOnClickListener(view -> login()); // click handling code

        return root;
    }

    private void login(){

        // get text from username
        EditText usernameText = root.findViewById(R.id.username);
        String usernameMessage = usernameText.getText().toString();

        //get text from password
        EditText passwordText = root.findViewById(R.id.password);
        String passwordMessage = passwordText.getText().toString();


        if(!usernameMessage.matches("") && !passwordMessage.matches("")) { //checks if the username edit text is not empty

            //starts the service
            Intent startServiceIntent = new Intent(getActivity(), DatabaseService.class);
            startServiceIntent.putExtra(LOGIN_NAME, usernameMessage);
            getActivity().startService(startServiceIntent);

            // starts the main screen activity
            Intent mainScreenIntent = new Intent(getActivity(), MainScreen.class);
            getActivity().finish();
            mainScreenIntent.putExtra(LOGIN_NAME, usernameMessage);
            startActivity(mainScreenIntent);
        }
        if(passwordMessage.matches("")) {
            Toast.makeText(getActivity(), "Password is missing", Toast.LENGTH_LONG).show();
        }

        if(usernameMessage.matches("")) {
            Toast.makeText(getActivity(), "Username is missing", Toast.LENGTH_LONG).show();
        }
    }

    private final View.OnFocusChangeListener passwordOnFocusChangeListener = (v, hasFocus) -> {
        if(!hasFocus){ // if the users exits the focus, try to login
            login();
        }
    };
}