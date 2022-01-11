package com.example.motifissa;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.motifissa.HelperClasses.ServiceListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class NotificationsActivity extends ServiceListener {

    private static final String TAG = "NotificationFragment";

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
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // setup the action bar
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null; // to make sure that this activity has an action bar
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM); // showing the back button in action bar
        actionBar.setCustomView(R.layout.action_bar); // set our custom action bar
        actionBar.setDisplayHomeAsUpEnabled(true);
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

            ArrayList<String> notifications = new ArrayList<>();

            for (DataSnapshot data : snapshot.getChildren()) {
                String notificationData = data.getValue(String.class);
                if (notificationData == null) continue;
                String[] notificationCode = notificationData.split("\\|");

                getUser(notificationCode[1]).setSuccessListener(otherUser -> {
                    String notification = "";
                    switch (notificationCode[0]) {
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
                });
            }


            ListView notificationsList = findViewById(R.id.NotificationsList);
            ArrayAdapter<String> notificationsAdaptor = new ArrayAdapter<>(NotificationsActivity.this, android.R.layout.simple_list_item_1, notifications);
            notificationsList.setAdapter(notificationsAdaptor);
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