package com.example.motifissa;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.motifissa.HelperClasses.Notification;
import com.example.motifissa.HelperClasses.NotificationsArrayAdaptor;
import com.example.motifissa.HelperClasses.ServiceListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

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

        TabLayout tabBar = findViewById(R.id.NotificationsTabs);

        for(int i = 0; i < tabBar.getTabCount(); i++){
            TabLayout.Tab currentTab = tabBar.getTabAt(i);
            String tabName = currentTab.getText().toString();


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

        tabBar.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (notificationsAdaptor != null){
                    notificationsAdaptor.getFilter().filter(tab.getContentDescription().toString());
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onServiceConnect() {
        getNotifications().setSuccessListener(result ->result.addValueEventListener(valueEventListener));
    }

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
                            notification = new Notification(notificationCode[0], notificationCode[1]);
                        } else {
                            notification = new Notification(notificationCode[0], notificationCode[1], notificationCode[2]);
                        }
                        notification.setMessage(notification.getMessage().replaceAll("username", otherUser.getName()));

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