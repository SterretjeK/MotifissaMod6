package com.example.motifissa.challenge_screens;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.motifissa.R;
import com.example.motifissa.HelperClasses.User;

public class ChallengeSentFragment extends Fragment {

    private ChallengeActivity challengeActivity;
    private User selectedUser;
    private User currentUser;

    public ChallengeSentFragment() {
        // Required empty public constructor
    }

    public static ChallengeSentFragment newInstance() {
        return new ChallengeSentFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof ChallengeActivity) {
            challengeActivity = (ChallengeActivity) context;
        } else {
            throw new RuntimeException(context.toString() + " must be challengeActivity");
        }

        challengeActivity.getCurrentUser().setSuccessListener(result -> currentUser = result);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_challenge_sent, container, false);
        challengeActivity.getUser(challengeActivity.getSelectedFriend()).setSuccessListener(result -> {
            selectedUser = result;
            if (selectedUser == null)
                challengeActivity.moveBackFragment(2);

            TextView introTxt = v.findViewById(R.id.Challenge_sent_IntroTxtField);
            String txt = getResources().getString(R.string.Challenge_sent_IntroTxt);
            txt = txt.replaceAll("username", selectedUser.getName());
            introTxt.setText(txt);

            Button list = v.findViewById(R.id.button_back);
            Button home = v.findViewById(R.id.button_home);

            list.setOnClickListener(view -> challengeActivity.moveBackFragment(2));
            home.setOnClickListener(view -> challengeActivity.moveBackFragment(3));
        });
        return v;
    }
}