package com.example.motifissa;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OverviewChallengeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OverviewChallengeFragment extends Fragment {

    private ChallengeActivity challengeActivity;
    private JSONObject selectedUser;

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
            throw new RuntimeException(context.toString() + " must be MainScreen");
        }

        selectedUser = challengeActivity.getUser(challengeActivity.getSelectedFriend());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_overview_challenge, container, false);

        if(selectedUser != null){
            TextView introTxtField = v.findViewById(R.id.Challenge_IntroTxtField);
            TextView subTxtField = v.findViewById(R.id.challenge_subText);
            try {
                String introTxt = getResources().getString(R.string.Challenge_IntroTxt);
                String username =  selectedUser.getString("Name");
                introTxt = introTxt.replaceAll("username", username);
                introTxtField.setText(introTxt);

                // TODO get the actual current user
                // TODO change scoreDiff and ahead/below of the string.
                String subTxt = getResources().getString(R.string.challenge_subText);
                subTxt = subTxt.replaceAll("username", username);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return v;
    }
}