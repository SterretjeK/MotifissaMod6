package com.example.motifissa;

import android.app.Activity;
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
import android.widget.Toast;

public class FriendsFragment extends Fragment {

    EditText searchField;
    ListView usersList;
    ArrayAdapter<String> listUsers_Adapter;
    FriendsListAdaptor friendsListAdaptor;
    View view;
    String[] users;
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
        users = mainscreen.mDatabaseService.getUsersString();
        usersList = view.findViewById(R.id.users_list);

        String[] usersIDs = mainscreen.mDatabaseService.getUsersIDString();
        String[] friends = mainscreen.mDatabaseService.getFriendsString();
        friendsListAdaptor = new FriendsListAdaptor(getActivity(), users, usersIDs, friends);
        usersList.setAdapter(friendsListAdaptor);

//        listUsers_Adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, users);// sets the users list in an adaptor
//        usersList.setAdapter(listUsers_Adapter); //add the adaptor to the list view

        // more complex adaptor for the list view (future improvement): https://www.tutlane.com/tutorial/android/android-listview-with-examples

//        usersList.setOnItemClickListener(usersListListener);

        // setup the search field:
//        searchField = view.findViewById(R.id.search_friends);
//        searchField.addTextChangedListener(searchTextWatcher);

        return view;
    }

//    private AdapterView.OnItemClickListener usersListListener = new AdapterView.OnItemClickListener(){
//
//        @Override
//        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//            mainscreen.mDatabaseService.addFriend(users[position]);
//            Toast.makeText(getContext(), users[position], Toast.LENGTH_SHORT).show();
//        }
//    };
//
//    private TextWatcher searchTextWatcher = new TextWatcher(){
//
//        @Override
//        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//        }
//
//        @Override
//        public void onTextChanged(CharSequence s, int start, int before, int count) {
//            listUsers_Adapter.getFilter().filter(s);
//        }
//
//        @Override
//        public void afterTextChanged(Editable s) {
//
//        }
//    };
}