package com.example.motifissa.challenge_screens;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.example.motifissa.HelperClasses.UsersArrayAdaptor;
import com.example.motifissa.R;

import java.util.ArrayList;
import java.util.Objects;

public class ChooseFriendFragment extends Fragment {

    ChallengeActivity challengeActivity;
    private ArrayList<String> friends;
    private ArrayList<String> friendsID;
    private UsersArrayAdaptor arrayAdaptor;

    public ChooseFriendFragment() {
        // Required empty public constructor
    }


    public static ChooseFriendFragment newInstance() {
        return new ChooseFriendFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_challenge_choose_friend, container, false);

        populateList(view);

        return view;
    }

    public void populateList(View view){
        // setup the list view for the users
        challengeActivity.getFriends().setSuccessListener(friends -> {
            ListView friendsList = view.findViewById(R.id.C_friends_list);

            arrayAdaptor = new UsersArrayAdaptor(getActivity(), friends, true);
            friendsList.setAdapter(arrayAdaptor);

            friendsList.setOnItemClickListener(friendsListListener);

            // setup the search field:
            EditText searchField = view.findViewById(R.id.C_search_friends);
            searchField.addTextChangedListener(searchTextWatcher);

            // automatically update when a user was changed
            challengeActivity.getUpdateListener().setSuccessListener(updateListener -> updateListener.addListener(value -> {
                if (value){
                    challengeActivity.getFriends().setSuccessListener(updatedFriends -> {
                        if(updatedFriends != arrayAdaptor.getCurrentUsers()) {
                            arrayAdaptor.changeUsers(updatedFriends);
                            arrayAdaptor.getFilter().filter(searchField.getText().toString());
                        }
            });}}));
        });
    }


    private final AdapterView.OnItemClickListener friendsListListener = (parent, view, position, id) -> {
        //String friendID = arrayAdapter.getItem(position).getUID();
            challengeActivity.setSelectedFriend(Objects.requireNonNull(arrayAdaptor.getItem(position)).getUID());
            challengeActivity.moveUpFragment();

    };


    private final TextWatcher searchTextWatcher = new TextWatcher(){

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            arrayAdaptor.getFilter().filter(s);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
}