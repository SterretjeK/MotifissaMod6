package com.example.motifissa;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.motifissa.HelperClasses.Notification;
import com.example.motifissa.HelperClasses.NotificationsArrayAdaptor;
import com.example.motifissa.HelperClasses.ServiceListener;
import com.example.motifissa.challenge_screens.ChallengeActivity;
import com.example.motifissa.dialogs.AcceptDenyDialog;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class NotificationsActivity extends ServiceListener {

    private static final String TAG = "NotificationFragment";
    private NotificationsArrayAdaptor notificationsAdaptor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        // This callback will change what happens when the user clicks back
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        };
        this.getOnBackPressedDispatcher().addCallback(this, callback);

        // set the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // setup the action bar
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null; // to make sure that this activity has an action bar
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM); // showing the back button in action bar
        actionBar.setCustomView(R.layout.action_bar); // set our custom action bar
        actionBar.setDisplayHomeAsUpEnabled(true);

        // setup the tab bar
        TabLayout tabBar = findViewById(R.id.NotificationsTabs);

        //setup the content description of the tab bar, will be used to filter on
        for(int i = 0; i < tabBar.getTabCount(); i++){
            TabLayout.Tab currentTab = tabBar.getTabAt(i);
            assert currentTab != null;
            String tabName = Objects.requireNonNull(currentTab.getText()).toString();


            if (tabName.equals(getResources().getString(R.string.all_Tab))){
                currentTab.setContentDescription("ALL");
            } else if (tabName.equals(getResources().getString(R.string.challenges_Tab))){
                currentTab.setContentDescription(Notification.NotificationType.CHALLENGE.toString());
            } else if (tabName.equals(getResources().getString(R.string.friends_Tab))){
                currentTab.setContentDescription(Notification.NotificationType.FRIEND_REQUEST.toString());
            } else {
                currentTab.setContentDescription(" ");
            }

        }

        // filter the data when a tab is clicked
        tabBar.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (notificationsAdaptor != null){
                    notificationsAdaptor.getFilter().filter(Objects.requireNonNull(tab.getContentDescription()).toString());
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        //add onclick listener to the listView
        ListView notificationsList = findViewById(R.id.NotificationsList);

        notificationsList.setOnItemClickListener((parent, view, position, id) -> {
            Notification notification = notificationsAdaptor.getItem(position); // get the selected notification
            AcceptDenyDialog dialog = new AcceptDenyDialog();                   // create a new dialog

            // set its arguments
            Bundle bundle = new Bundle();
            bundle.putString(AcceptDenyDialog.TITLE, notification.getTitle());
            bundle.putString(AcceptDenyDialog.SUBTITLE, notification.getMessage());
            dialog.setArguments(bundle);

            // set its onclick listeners
            dialog.setListener(new AcceptDenyDialog.AcceptDenyListener() {
                @Override
                public void onAccept() {
                    if (notification.getType() == Notification.NotificationType.CHALLENGE)
                        sendNotification(Notification.NotificationType.ACCEPTED_CHALLENGE.toString(), notification.getSentBy());
                    if (notification.getType() == Notification.NotificationType.CHALLENGE || notification.getType() == Notification.NotificationType.ACCEPTED_CHALLENGE)
                    acceptChallenge(notification);
                }

                @Override
                public void onDeny() {
                    removeNotification(notification);
                    Toast.makeText(getApplicationContext(), "Denied " + notification.getTitle().toLowerCase(), Toast.LENGTH_SHORT).show();
                }
            });

            // show the dialog
            dialog.show(getFragmentManager(), notification.getTitle());
        });
    }


    @Override
    protected void onServiceConnect() { // when the service is connected load the data in the listView
        super.onServiceConnect();
        getNotifications().setSuccessListener(result ->result.addValueEventListener(valueEventListener));
    }

    // the listener that loads the notifications in the listView
    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            isBounded().setSuccessListener(isBounded ->{
                ArrayList<Notification> notifications = new ArrayList<>();

                for (DataSnapshot data : snapshot.getChildren()) {
                    String notificationData = data.getValue(String.class);
                    if (notificationData == null) continue;
                    String[] notificationCode = notificationData.split("\\|");

                    getUser(notificationCode[1]).setSuccessListener(otherUser -> {
                        Notification notification;
                        if (notificationCode.length <= 2) {
                            notification = new Notification(notificationCode[0], notificationCode[1], otherUser.getName());
                        } else {
                            notification = new Notification(notificationCode[0], notificationCode[1], notificationCode[2], otherUser.getName());
                        }
//                        notification.setMessage(notification.getMessage().replaceAll("username", otherUser.getName()));

                        notifications.add(notification);
                    });
                }

                Collections.reverse(notifications);
                ListView notificationsList = findViewById(R.id.NotificationsList);
                if (notificationsAdaptor == null){ // if this is the first time make a new arrayAdaptor otherwise just update the existing one.
                    notificationsAdaptor = new NotificationsArrayAdaptor(NotificationsActivity.this, notifications);
                    notificationsList.setAdapter(notificationsAdaptor);
                } else {
                    notificationsAdaptor.updateNotifications(notifications);
                }
            });
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };



        // this event will handle the back arrow on the action bar
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}