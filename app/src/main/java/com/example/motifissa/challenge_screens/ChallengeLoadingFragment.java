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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class ChallengeLoadingFragment extends Fragment {

   private ChallengeActivity challengeActivity;


    public ChallengeLoadingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof ChallengeActivity) {
            challengeActivity = (ChallengeActivity) context;
        } else {
            throw new RuntimeException(context.toString() + " must be ChallengeActivity");
        }
        challengeActivity.startChallenge();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_challenge_loading, container, false);

        challengeActivity.getUser(challengeActivity.getSelectedFriend()).setSuccessListener(selectedUser -> {
            TextView title = view.findViewById(R.id.loading_title);
            title.setText(title.getText().toString().replaceAll("username", selectedUser.getName()));
        });

        Button cancelButton = view.findViewById(R.id.loading_cancel_button);
        cancelButton.setOnClickListener(v -> challengeActivity.cancelChallenge());

        return view;
    }
}