package com.example.motifissa;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.motifissa.challenge_screens.ChallengeActivity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DashboardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DashboardFragment extends Fragment {
    View view;

    ScoreboardFragment scoreboardFragment;

    public static final String LOGIN_NAME = "LOGIN_NAME";

    private String Login_name;

    public DashboardFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param Login_name Parameter 1.
     * @return A new instance of fragment DashboardFragment.
     */
    public static DashboardFragment newInstance(String Login_name) {
        DashboardFragment fragment = new DashboardFragment();
        Bundle args = new Bundle();
        args.putString(LOGIN_NAME, Login_name);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Login_name = getArguments().getString(LOGIN_NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_dashboard, container, false);

         // set the login name to the screen's textview
         TextView Username_welcome = (TextView) view.findViewById(R.id.Username_welcome);
         Username_welcome.setText(Login_name);

         // setup the scoreboard fragment
        scoreboardFragment = new ScoreboardFragment();
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.scoreboard_container, scoreboardFragment).commit();

        Button challengeButton = view.findViewById(R.id.dashboard_tile_challenge);
        challengeButton.setOnClickListener(challengeOnClickListener);

        return view;
    }


    private View.OnClickListener challengeOnClickListener = v -> {
        Intent mainScreenIntent = new Intent(getContext(), ChallengeActivity.class);
        startActivity(mainScreenIntent);
    };

}