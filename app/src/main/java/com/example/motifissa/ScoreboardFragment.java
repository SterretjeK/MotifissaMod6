package com.example.motifissa;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

import com.example.motifissa.HelperClasses.ScoreboardListAdapter;
import com.example.motifissa.HelperClasses.ServiceListener;
import com.example.motifissa.HelperClasses.User;
import com.example.motifissa.HelperClasses.UsersArrayAdaptor;
import com.example.motifissa.challenge_screens.ChallengeActivity;

import java.util.ArrayList;


public class ScoreboardFragment extends Fragment {

    ServiceListener serviceListener;
    private ArrayList<User> friends;
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
        ListView scoreboardList = view.findViewById(R.id.scoreboard_listview);

        serviceListener.getFriends().setSuccessListener(friendsIn -> {
            serviceListener.getCurrentUser().setSuccessListener(currentUser -> {
                friends = new ArrayList<>(friendsIn);
                friends.add(currentUser);
                arrayAdaptor = new ScoreboardListAdapter(getActivity(), friends);
                scoreboardList.setAdapter(arrayAdaptor);

                // automatically update when a user was changed
                serviceListener.getUpdateListener().setSuccessListener(updateListener -> updateListener.addListener(value -> {
                    if (value) {
                        serviceListener.getFriends().setSuccessListener(updatedFriends -> {
                            serviceListener.getCurrentUser().setSuccessListener(updatedCurrentUser -> {
                                friends = new ArrayList<>(updatedFriends);
                                friends.add(updatedCurrentUser);
                                if (friends != arrayAdaptor.getCurrentUsers()) {
                                    arrayAdaptor.changeUsers(friends);
                                }
                            });
                        });
                    }
                }));
            });
        });
    }
}