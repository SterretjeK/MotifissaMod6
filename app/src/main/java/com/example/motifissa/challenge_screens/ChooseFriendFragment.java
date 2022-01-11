package com.example.motifissa.challenge_screens;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.example.motifissa.ListenerVariable;
import com.example.motifissa.R;
import com.example.motifissa.ServiceListener;

import java.util.ArrayList;

public class ChooseFriendFragment extends Fragment {

    ServiceListener serviceListener;
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
        if (context instanceof ServiceListener) {
            serviceListener = (ServiceListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must be MainScreen");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_choose_friend, container, false);

        populateList(view);

        return view;
    }

    public void populateList(View view){
        // setup the list view for the users
//        friends = challengeActivity.getFriends();
//        friendsID = challengeActivity.getFriendsID();
        // TODO make a custom list adaptor
        serviceListener.getFriendsNames().setSuccessListener(friends -> {
            this.friends = friends;

            ListView friendsList = view.findViewById(R.id.C_friends_list);

            arrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1,friends);
//        friendsListAdaptor = new FriendsListAdaptor(getActivity(), users, friends);
            friendsList.setAdapter(arrayAdapter);

            friendsList.setOnItemClickListener(friendsListListener);

            // setup the search field:
            EditText searchField = view.findViewById(R.id.C_search_friends);
            searchField.addTextChangedListener(searchTextWatcher);
        });
    }


    private final AdapterView.OnItemClickListener friendsListListener = (parent, view, position, id) -> {
        //String friendID = arrayAdapter.getItem(position).getUID();
//            challengeActivity.setSelectedFriend(friendsID.get(position));
//            challengeActivity.moveUpFragment();

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