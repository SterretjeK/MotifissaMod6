package com.example.motifissa;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;


public class LoginTabFragment extends Fragment {
    public static final String LOGIN_NAME = "com.example.myfirstapp.LOGIN_NAME";

    private LoginScreen loginScreen;

    private ViewGroup root;

    public LoginTabFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = (ViewGroup) inflater.inflate(R.layout.fragment_login_tab, container, false);

        // animation
        TextView emailText = root.findViewById(R.id.email_text);
        EditText email = root.findViewById(R.id.email);
        TextView passwordText = root.findViewById(R.id.password_text);
        EditText password = root.findViewById(R.id.password);
        TextView forgot_password = root.findViewById(R.id.forgot_password);
        Button loginButton = root.findViewById(R.id.login_button);

        emailText.setTranslationX(800);
        email.setTranslationX(800);
        passwordText.setTranslationX(800);
        password.setTranslationX(800);
        forgot_password.setTranslationX(800);
        loginButton.setTranslationY(300);

        emailText.setAlpha(0f);
        email.setAlpha(0f);
        passwordText.setAlpha(0f);
        password.setAlpha(0f);
        forgot_password.setAlpha(0f);
        loginButton.setAlpha(0f);

        emailText.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(3000).start();
        email.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(3050).start();
        passwordText.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(3200).start();
        password.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(3250).start();
        forgot_password.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(3300).start();
        loginButton.animate().translationY(0).alpha(1).setDuration(800).setStartDelay(3450).start();

        // add onclick listener on login button
        passwordText.setOnFocusChangeListener(passwordOnFocusChangeListener);
        loginButton.setOnClickListener(view -> login()); // click handling code

        forgot_password.setOnClickListener(view -> {
            if (!email.getText().toString().matches(""))
                loginScreen.forgotPassword(email.getText().toString());
            else
                Toast.makeText(getActivity(), "Enter email", Toast.LENGTH_LONG).show();
        });

        return root;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof LoginScreen) {
            loginScreen = (LoginScreen) context;
        } else {
            throw new RuntimeException(context.toString() + " must be LoginScreen");
        }
    }

    private void login(){
        // get input from email
        EditText emailInput = root.findViewById(R.id.email);
        String username = emailInput.getText().toString();

        //get input from password
        EditText passwordInput = root.findViewById(R.id.password);
        String password = passwordInput.getText().toString();

        if(password.matches("")) {
            Toast.makeText(getActivity(), "Password is missing", Toast.LENGTH_LONG).show();
        } else if(username.matches("")) {
            Toast.makeText(getActivity(), "Username is missing", Toast.LENGTH_LONG).show();
        } else {
            loginScreen.login(username, password);
        }
    }

    private final View.OnFocusChangeListener passwordOnFocusChangeListener = (v, hasFocus) -> {
        if(!hasFocus){ // if the users exits the focus, try to login
            login();
        }
    };
}