package com.example.motifissa;

import android.os.Bundle;

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
    View view;
    String[] users;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_friends, container, false);

        // setup the list view for the users
        users = new String[]{"Henkie", "Sterre", "Frank", "Jelle", "Floor", "Sil", "Henkie 2", "Sallie","Carmine","Norbert","Pam","Deon","Modesto","Isaac","Robert","Bernie","Rodrigo","Yesenia","Rosalinda","Mohammed","Britt","Candace","Ginger","Zelma","Patricia","Aurelio","Carlos","Emmitt","Garfield","Charley","Blanche","Efren","Frank","Kay","Pam","Robert","Pearlie","Imelda","Daryl","Latonya","Jami","Jere","Dwain","Randolph","Ina","Karla","Ellen","Aimee","Malcolm","Antione","Lana","Sherrie","Carlo","Anastasia","Tonya","Harris","Roslyn"};

        usersList = view.findViewById(R.id.users_list);
        listUsers_Adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, users);// sets the bluetooth devices in an adaptor
        usersList.setAdapter(listUsers_Adapter); //add the bluetooth devices to the list view

        // more complex adaptor for the list view (future improvement): https://www.tutlane.com/tutorial/android/android-listview-with-examples

        usersList.setOnItemClickListener(usersListListener);

        // setup the search field:
        searchField = view.findViewById(R.id.search_friends);
        searchField.addTextChangedListener(searchTextWatcher);

        return view;
    }

    private AdapterView.OnItemClickListener usersListListener = new AdapterView.OnItemClickListener(){

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Toast.makeText(getContext(), users[position], Toast.LENGTH_SHORT).show();
        }
    };

    private TextWatcher searchTextWatcher = new TextWatcher(){

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            listUsers_Adapter.getFilter().filter(s);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
}