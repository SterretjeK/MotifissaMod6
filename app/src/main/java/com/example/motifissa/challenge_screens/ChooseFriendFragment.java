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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.example.motifissa.ListenerVariable;
import com.example.motifissa.R;

import java.util.ArrayList;

public class ChooseFriendFragment extends Fragment {

    ChallengeActivity challengeActivity;
    ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> friends;
    private ArrayList<String> friendsID;

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
            throw new RuntimeException(context.toString() + " must be MainScreen");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_choose_friend, container, false);

        // setup the list view for the users
        if (challengeActivity.mBounded.get()) { // TODO think of something better
            this.populateList(view);
        } else{
            challengeActivity.mBounded.addListener((ListenerVariable.ChangeListener<Boolean>) value -> {
                if(value) populateList(view);
            });
        }

        return view;
    }

    public void populateList(View view){
        // setup the list view for the users
        friends = challengeActivity.getFriends();
        friendsID = challengeActivity.getFriendsID();

        ListView friendslist = view.findViewById(R.id.C_friends_list);

        arrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1,friends);
//        friendsListAdaptor = new FriendsListAdaptor(getActivity(), users, friends);
        friendslist.setAdapter(arrayAdapter);

        friendslist.setOnItemClickListener(friendsListListener);

        // setup the search field:
        EditText searchField = view.findViewById(R.id.C_search_friends);
        searchField.addTextChangedListener(searchTextWatcher);
    }


    private final AdapterView.OnItemClickListener friendsListListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // TODO make a new intent to go to the next fragment and send with the username of the friend
            challengeActivity.setSelectedFriend(friendsID.get(position));
            challengeActivity.moveUpFragment();

        }
    };


    private final TextWatcher searchTextWatcher = new TextWatcher(){

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            arrayAdapter.getFilter().filter(s);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
}