package com.example.motifissa;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class SignupTabFragment extends Fragment {

    private LoginScreen loginScreen;
    private ViewGroup root;

    public SignupTabFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = (ViewGroup) inflater.inflate(R.layout.fragment_signup_tab, container, false);

        // add onclick listener on sign up button
        Button signupButton = root.findViewById(R.id.signup_button);
        signupButton.setOnClickListener(view -> signUp()); // click handling code

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

    private void signUp(){
        // get input from username
        EditText usernameInput = root.findViewById(R.id.username);
        String username = usernameInput.getText().toString();

        //get input from email
        EditText emailInput = root.findViewById(R.id.email);
        String email = emailInput.getText().toString();

        //get input from password
        EditText passwordInput = root.findViewById(R.id.password);
        String password = passwordInput.getText().toString();

        //get input from confirm password
        EditText confirmPasswordInput = root.findViewById(R.id.confirm_password);
        String confirm_password = confirmPasswordInput.getText().toString();

        // TODO check all fields if  empty
        if (username.matches("")) {
            Toast.makeText(getActivity(), "Username is missing", Toast.LENGTH_LONG).show();
        } else if(email.matches("")) {
            Toast.makeText(getActivity(), "Email is missing", Toast.LENGTH_LONG).show();
        } else if(!email.contains("@")) {
            Toast.makeText(getActivity(), "Email is not valid", Toast.LENGTH_LONG).show();
        } else if(password.matches("")) {
            Toast.makeText(getActivity(), "Password is missing", Toast.LENGTH_LONG).show();
        } else if(confirm_password.matches("")) {
            Toast.makeText(getActivity(), "Confirm password is missing", Toast.LENGTH_LONG).show();
        } else if(!confirm_password.matches(password)) {
            Toast.makeText(getActivity(), "Passwords do not match", Toast.LENGTH_LONG).show();
        } else if (password.length() < 6){  // if firebase considers it a weak password
            Toast.makeText(getActivity(), "Password has to be at least 6 characters", Toast.LENGTH_SHORT).show();
                    } else {
            loginScreen.signUp(username, email, password);
        }
    }

}