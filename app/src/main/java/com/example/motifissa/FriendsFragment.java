package com.example.motifissa;

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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.motifissa.HelperClasses.FriendsListAdaptor;
import com.example.motifissa.HelperClasses.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class FriendsFragment extends Fragment {

    EditText searchField;
    ListView usersList;
    FriendsListAdaptor friendsListAdaptor;
    View view;
    MainScreen mainscreen;
    Query userQuery;

    ArrayList<String> friends;

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

        mainscreen.getUsersQuery().setSuccessListener(queryIn -> {
            userQuery = queryIn;
            userQuery.addValueEventListener(usersChangeListener);
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_friends, container, false);

        return view;
    }

    @Override
    public void onDetach() { // called when the fragment is detached, so when it isn't visible anymore to the user
        super.onDetach();
        if (userQuery != null && usersChangeListener != null)
            userQuery.removeEventListener(usersChangeListener);
    }

    public void populateList(ArrayList<User> users, ArrayList<String> friends){
        // setup the list view for the users
        usersList = view.findViewById(R.id.users_list);

        friendsListAdaptor = new FriendsListAdaptor(getActivity(), users, friends);
        usersList.setAdapter(friendsListAdaptor);

        usersList.setOnItemClickListener(usersListListener);

        // setup the search field:
        searchField = view.findViewById(R.id.search_friends);
        searchField.addTextChangedListener(searchTextWatcher);
    }

    private final ValueEventListener usersChangeListener =  new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            ArrayList<User> users = new ArrayList<>();

            for (DataSnapshot data : snapshot.getChildren()){
                User user = data.getValue(User.class);
                users.add(user);
            }


            mainscreen.getFriendsUID().setSuccessListener(friendsIn ->{
                friends = friendsIn;
                populateList(users, friends);
            });
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };


    private final AdapterView.OnItemClickListener usersListListener = (parent, view, position, id) -> {
        String UID = Objects.requireNonNull(friendsListAdaptor.getItem(position)).getUID();


        mainscreen.toggleFriend(UID).setSuccessListener(task -> task.addOnSuccessListener(succes ->{
                if(friends.contains(UID))
                    friends.remove(UID);
                else friends.add(UID);

                friendsListAdaptor.changeFriends(friends);
            }).addOnFailureListener(error -> {
                Toast.makeText(getContext(), "Couldn't add friend", Toast.LENGTH_SHORT).show();
                Log.e("FriendsFragment", "Couldn't toggle friend: " + error);
            }));
    };

    private final TextWatcher searchTextWatcher = new TextWatcher(){

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