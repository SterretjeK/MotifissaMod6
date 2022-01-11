package com.example.motifissa;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class NotificationsFragment extends Fragment {
    MainScreen mainScreen;

    private static final String TAG = "NotificationFragment";

    public NotificationsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof MainScreen) {
            mainScreen = (MainScreen) context;
        } else {
            throw new RuntimeException(context.toString() + " must be MainScreen");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);



        Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                if(mainScreen.mBounded.get()) {
                    mainScreen.getNotifications().addValueEventListener(valueEventListener);
                }
                else {
                    mainScreen.mBounded.addListener(value -> {
                        if (value) {
                            mainScreen.getNotifications().addValueEventListener(valueEventListener);
                        }
                    });
                }
            }
        }, 1000);

        return view;
    }

    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {

            ArrayList<String> notifications = new ArrayList<>();

            for(DataSnapshot data : snapshot.getChildren()){
                String notificationData = data.getValue(String.class);
                if (notificationData == null) continue;
                String[] notificationCode = notificationData.split("\\|");

                User otherUser = mainScreen.getUser(notificationCode[1]);
                String notification = "";
                switch(notificationCode[0]){
                    case "Challenge":
                        notification = "You have been challenged by username.";
                        break;
                    case "Friends":
                        notification = "username asked to be your friend";
                        break;
                }

                // TODO make custom adaptor
                notification = notification.replaceAll("username", otherUser.getName());
                notifications.add(notification);
            }


            ListView notificationsList = Objects.requireNonNull(getView()).findViewById(R.id.NotificationsList);
            ArrayAdapter<String> notificationsAdaptor = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, notifications);
            notificationsList.setAdapter(notificationsAdaptor);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };
}