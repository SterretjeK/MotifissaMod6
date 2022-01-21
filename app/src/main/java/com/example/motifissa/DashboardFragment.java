package com.example.motifissa;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.motifissa.challenge_screens.ChallengeActivity;

import java.util.Objects;

public class DashboardFragment extends Fragment {
    View view;
    private static final String TAG = "DashboardFragment";
    ScoreboardFragment scoreboardFragment;

    private MainScreen mainscreen;

    public DashboardFragment() {
        // Required empty public constructor
    }


    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof MainScreen) {
            mainscreen = (MainScreen) context;
        } else {
            throw new RuntimeException(context.toString() + " must be MainScreen");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        // update each time the username was updated
        mainscreen.getUpdateListener().setSuccessListener(listener -> listener.addListener(value ->{
            if(value){ // only update it when the service is connected to firebase
                mainscreen.getCurrentUser().setSuccessListener(user ->{
                    TextView Username_welcome = view.findViewById(R.id.username_welcome);
                    Username_welcome.setText(user.getName());
                });
            }
        }));


         // setup the scoreboard fragment
//        scoreboardFragment = new ScoreboardFragment();
//        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.scoreboard_container, scoreboardFragment).commit();

        CardView challengeButton = view.findViewById(R.id.dashboard_tile_challenge);
        challengeButton.setOnClickListener(challengeOnClickListener);

        ImageButton menuButton = view.findViewById(R.id.moreMenuButton);
        menuButton.setOnClickListener(v -> {
            //Creating the instance of PopupMenu
            PopupMenu popup = new PopupMenu(getContext(), menuButton);
            //Inflating the Popup using xml file
            popup.getMenuInflater().inflate(R.menu.dashboard_menu, popup.getMenu());

            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                public boolean onMenuItemClick(MenuItem item) {
//                    Toast.makeText(getContext(),"You Clicked : " + item.getTitle(), Toast.LENGTH_SHORT).show();
                    if (item.getTitle().equals(getResources().getString(R.string.logout))) {
                        mainscreen.logout();
                    } else if (item.getTitle().equals(getResources().getString(R.string.Notifications))){
                        mainscreen.showNotifications();
                    }
                    return true;
                }
            });

            popup.show();
        });

        return view;
    }

    private View.OnClickListener challengeOnClickListener = v -> {
        Intent mainScreenIntent = new Intent(getContext(), ChallengeActivity.class);
        startActivity(mainScreenIntent);
    };

}