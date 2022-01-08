package com.example.motifissa;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

public class FriendsFragment extends Fragment {

    EditText searchField;
    ListView usersList;
    FriendsListAdaptor friendsListAdaptor;
    View view;
    JSONObject[] users;
    MainScreen mainscreen;

    public FriendsFragment() {
        // Required empty public constructor
    }

    public static FriendsFragment newInstance() {
        return new FriendsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof MainScreen) {
            mainscreen = (MainScreen) context;
        } else {
            throw new RuntimeException(context.toString() + " must be MainScreen");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_friends, container, false);

        // setup the list view for the users
        if (mainscreen.mBounded.get())
            this.populateList();
        else{
            mainscreen.mBounded.setListener(value -> {
             if(value) populateList();
            });
        }

        return view;
    }

    public void populateList(){
        // setup the list view for the users
        users = mainscreen.getUsers();
        String[] friends = mainscreen.getFriends();

        usersList = view.findViewById(R.id.users_list);

        friendsListAdaptor = new FriendsListAdaptor(getActivity(), users, friends);
        usersList.setAdapter(friendsListAdaptor);

        usersList.setOnItemClickListener(usersListListener);

        // setup the search field:
        searchField = view.findViewById(R.id.search_friends);
        searchField.addTextChangedListener(searchTextWatcher);
    }

    private AdapterView.OnItemClickListener usersListListener = new AdapterView.OnItemClickListener(){

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            try {
                mainscreen.mDatabaseService.toggleFriend(friendsListAdaptor.getItem(position).getString("ID"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String[] friends = mainscreen.mDatabaseService.getFriendsNameArray();
            friendsListAdaptor.changeFriends(friends);
        }
    };

    private TextWatcher searchTextWatcher = new TextWatcher(){

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            friendsListAdaptor.getFilter().filter(s);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
}