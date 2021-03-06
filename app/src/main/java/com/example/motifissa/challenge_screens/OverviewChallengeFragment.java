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
import android.widget.Toast;

import com.example.motifissa.R;
import com.example.motifissa.HelperClasses.User;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OverviewChallengeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OverviewChallengeFragment extends Fragment {

    private ChallengeActivity challengeActivity;
    private User selectedUser;
    private User currentUser;

    public OverviewChallengeFragment() {
        // Required empty public constructor
    }


    public static OverviewChallengeFragment newInstance() {
        return new OverviewChallengeFragment();
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
            throw new RuntimeException(context.toString() + " must be challengeActivity");
        }

        challengeActivity.getUser(challengeActivity.getSelectedFriend()).setSuccessListener(result -> selectedUser = result);
        challengeActivity.getCurrentUser().setSuccessListener(result -> currentUser = result);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_challenge_overview, container, false);

        if (selectedUser != null) { // if the user is selected replace the texts so it's about the current user and his opponent
            TextView introTxtField = v.findViewById(R.id.Challenge_IntroTxtField);
            TextView subTxtField = v.findViewById(R.id.challenge_subText);
            String introTxt = getResources().getString(R.string.Challenge_IntroTxt);
            String username = selectedUser.getName();
            introTxt = introTxt.replaceAll("username", username);
            introTxtField.setText(introTxt);

            String subTxt = getResources().getString(R.string.challenge_subText);
            subTxt = subTxt.replaceAll("username", username);

            int scoreDiff = selectedUser.getScore() - currentUser.getScore(); // the amount the opponent (selected user is ahead of you)
            subTxt = subTxt.replaceAll("scoreDiff", (scoreDiff > 0 ? "+" : "") + scoreDiff);
            subTxt = subTxt.replaceAll("aheadOf/behind", (scoreDiff >= 0 ? "ahead of" : "behind"));

            subTxtField.setText(subTxt);

        }

        Button challengeFriend_button = v.findViewById(R.id.ChallengeFriendButton);
        challengeFriend_button.setOnClickListener(view ->
                challengeActivity.challengeFriend().setSuccessListener(task ->
                        task.addOnSuccessListener(success -> challengeActivity.moveUpFragment())
                                .addOnFailureListener(error -> Toast.makeText(getContext(), "Couldn't sent notification", Toast.LENGTH_SHORT).show())
                ));

        return v;
    }
}