package com.example.motifissa;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

import com.example.motifissa.HelperClasses.ListenerVariable;
import com.example.motifissa.HelperClasses.ScoreboardListAdapter;
import com.example.motifissa.HelperClasses.ServiceListener;
import com.example.motifissa.HelperClasses.User;
import com.example.motifissa.HelperClasses.UsersArrayAdaptor;
import com.example.motifissa.challenge_screens.ChallengeActivity;

import java.util.ArrayList;


public class ScoreboardFragment extends Fragment {
    private static final String TAG = "ScoreboardFragment";

    // communication with service/database
    ServiceListener serviceListener;
    ListenerVariable<Boolean> updateListener;

    ListView scoreboardList;

    private ScoreboardListAdapter arrayAdaptor;

    public ScoreboardFragment() {
        // Required empty public constructor
    }

    public static ScoreboardFragment newInstance() {
        return new ScoreboardFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof ServiceListener) {
            serviceListener = (ServiceListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must be ServiceListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_scoreboard, container, false);

        populateList(view);
        return view;
    }

    public void populateList(View view) {
        // setup the list view for the users
        scoreboardList = view.findViewById(R.id.scoreboard_listview);

        // add a change listener so the scoreboard data is loaded and updated when the data is available
        serviceListener.getUpdateListener().setSuccessListener(updateListenerIn -> {
            updateListener = updateListenerIn;
            updateListener.addListener(changeListener);
        });
    }

    private final ListenerVariable.ChangeListener<Boolean> changeListener = value -> {
        // an change listener is called when it's attached and when the value changes so it's important to check it it has the value we want
        if (value) { // so this is a boolean if it has been updated, which should be TRUE
            // get the data we want:
            ArrayList<User> friends = serviceListener.getFriendsDirect();
            User currentUser = serviceListener.getCurrentUserDirect();

            // check if the data is valid
            if (currentUser != null)
                friends.add(currentUser);
            else
                Log.e(TAG, "current User is null");

            if (getActivity() == null) {
                Log.e(TAG, "No Activity active");
                return;
            } else if (friends == null) {
                Log.e(TAG, "friends was null in changeListener");
                return;
            }

            // if the arrayAdaptor wasn't made yet make it
            if (arrayAdaptor == null) {
                arrayAdaptor = new ScoreboardListAdapter(getActivity(), friends);
                scoreboardList.setAdapter(arrayAdaptor);
            } else if (friends != arrayAdaptor.getCurrentUsers()) { // otherwise just update it
                arrayAdaptor.changeUsers(friends);
            }

        }
    };
}