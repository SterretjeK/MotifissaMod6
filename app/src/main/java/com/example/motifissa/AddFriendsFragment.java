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
import com.example.motifissa.HelperClasses.ListenerVariable;
import com.example.motifissa.HelperClasses.ServiceListener;
import com.example.motifissa.HelperClasses.User;

import java.util.ArrayList;
import java.util.Objects;

public class AddFriendsFragment extends Fragment {

    EditText searchField;
    ListView usersList;
    FriendsListAdaptor friendsListAdaptor;
    View view;
    ListenerVariable<Boolean> viewUpdated;
    ServiceListener serviceListener;
    ListenerVariable<Boolean> updateListener;


    ArrayList<String> friends;

    public AddFriendsFragment() {
        // Required empty public constructor
    }

    public static AddFriendsFragment newInstance() {
        return new AddFriendsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (viewUpdated == null) viewUpdated = new ListenerVariable<>(false);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof ServiceListener) {
            serviceListener = (ServiceListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must be a ServiceListener");
        }

        if (viewUpdated == null) viewUpdated = new ListenerVariable<>(false);
        viewUpdated.addSuccessListener(() -> serviceListener.getUpdateListener().setSuccessListener(updateListenerIn -> {
            updateListener = updateListenerIn;
            updateListener.addListener(usersChangeListener);
        }));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_friends, container, false);
        // setup the list view for the users
        usersList = view.findViewById(R.id.users_list);
        // setup the search field:
        searchField = view.findViewById(R.id.search_friends);
        searchField.addTextChangedListener(searchTextWatcher);

        viewUpdated.set(true);
        return view;
    }

    @Override
    public void onDetach() { // called when the fragment is detached, so when it isn't visible anymore to the user
        super.onDetach();
        if (updateListener != null && usersChangeListener != null)
            updateListener.removeListener(usersChangeListener);
    }

    public void initList(ArrayList<User> users, ArrayList<String> friends) {
        friendsListAdaptor = new FriendsListAdaptor(getActivity(), users, friends);
        usersList.setAdapter(friendsListAdaptor);

        usersList.setOnItemClickListener(usersListListener);
    }

    private final ListenerVariable.ChangeListener<Boolean> usersChangeListener = value -> {
        ArrayList<User> users = new ArrayList<>(serviceListener.getUsersDirect());
        User currentUser = serviceListener.getCurrentUserDirect();
        users.remove(currentUser);
        friends = serviceListener.getFriendsUIDDirect();

        if (usersList == null || friendsListAdaptor == null || usersList.getAdapter() == null) {
            initList(users, friends);
        } else {
            friendsListAdaptor.changeUsers(users);
            friendsListAdaptor.changeFriends(friends);
        }
    };


    private final AdapterView.OnItemClickListener usersListListener = (parent, view, position, id) -> {
        String UID = Objects.requireNonNull(friendsListAdaptor.getItem(position)).getUID();


        serviceListener.toggleFriend(UID).setSuccessListener(task -> task.addOnSuccessListener(success -> {
            friends = serviceListener.getFriendsUIDDirect();

            friendsListAdaptor.changeFriends(friends);
        }).addOnFailureListener(error -> {
            Toast.makeText(getContext(), "Couldn't add friend", Toast.LENGTH_SHORT).show();
            Log.e("FriendsFragment", "Couldn't toggle friend: " + error);
        }));
    };

    private final TextWatcher searchTextWatcher = new TextWatcher() {

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