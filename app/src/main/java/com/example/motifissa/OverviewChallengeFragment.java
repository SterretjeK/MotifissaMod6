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
            TextView username = v.findViewById(R.id.Username_field);
            try {
                username.setText(selectedUser.getString("Name"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return v;
    }
}