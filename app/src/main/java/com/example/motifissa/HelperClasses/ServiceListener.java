package com.example.motifissa.HelperClasses;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.motifissa.DatabaseService;
import com.example.motifissa.MainScreen;
import com.example.motifissa.challenge_screens.ChallengeActivity;
import com.example.motifissa.dialogs.AcceptDenyDialog;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.Query;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class ServiceListener extends AppCompatActivity {
    private static final String TAG = "ServiceListener";
    // service
    protected ListenerVariable<Boolean> mBounded; // a custom type, that allows us to add listeners to variables
    protected boolean mIsConnecting = true;
    protected DatabaseService mDatabaseService;
    private ListenerVariable<Notification> notificationListener;

    public ServiceListener() {
        mBounded = new ListenerVariable<>(false);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // reset al the listeners
        mBounded = new ListenerVariable<>(false);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // setup the service that connects to the database
        this.connectToService();
    }


    protected void onServiceConnect() {
        // when a message is received show a accept deny dialog.
        setupNotificationListener();
    }

    // ----------------  Connect to service  ----------------
    public void connectToService() {
        if (!mBounded.get()) {
            mIsConnecting = true;
//            Log.e(TAG, "-----------------------------------------\n Change: " + mBounded.getChangeListeners().toString() + "\nSuccess: " + mBounded.getSuccessListeners().toString());
//            mBounded = new ListenerVariable<>(false);
            mBounded.set(false);
            Intent serviceIntent = new Intent(this, DatabaseService.class);
            bindService(serviceIntent, mConnection, BIND_AUTO_CREATE);
        }
    }

    protected ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
//            Toast.makeText(getBaseContext(), "Service is disconnected", Toast.LENGTH_SHORT).show();
            mBounded.set(false);
            mIsConnecting = false;
            mDatabaseService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
//            Toast.makeText(getBaseContext(), "Service is connected", Toast.LENGTH_SHORT).show();
            DatabaseService.LocalBinder mLocalBinder = (DatabaseService.LocalBinder) service;
            mDatabaseService = mLocalBinder.getServerInstance();

            mIsConnecting = false;
            mBounded.set(true);

            onServiceConnect();
        }
    };

    @Override
    protected void onStop() {
        super.onStop();

        //when the activity is stopped, release the server
        if (mBounded.get()) {
//            if (this instanceof MainScreen) {
//                // set the user as offline, might disable this if it cost too much MB upload
//                mDatabaseService.toggleOnlineUser(mDatabaseService.getCurrentUser().getUID(), false);
//            }
            unbindService(mConnection);
        }
        // delete all bound listeners:
        mBounded = new ListenerVariable<>(false);
        mIsConnecting = false;
        if (notificationListener != null) //notificationChangeListener
            notificationListener.removeListener(notificationChangeListener);
    }

    // ---------------- Service functions ----------------

    // Users:
    public ListenerTask<User> getUser(String UID) {
        return new ListenerTask<>(this, () -> mDatabaseService.getUser(UID));
    }
    public ListenerTask<Query> getUsersQuery(){
        return new ListenerTask<>(this, () -> mDatabaseService.getUsersQuery());
    }

    // Current user:
    public ListenerTask<User> getCurrentUser() {
        return new ListenerTask<>(this, () -> mDatabaseService.getCurrentUser());
    }

    // Friends:
    public ListenerTask<ArrayList<User>> getFriends() {
        return new ListenerTask<>(this, () -> mDatabaseService.getFriendsData());
    }
    public ListenerTask<Query> getCurrentUserFriendsQuery() {
        return new ListenerTask<>(this, () -> mDatabaseService.getCurrentUserFriendsQuery());
    }
    public ListenerTask<ArrayList<String>> getFriendsNames() {
        return new ListenerTask<>(this, () -> mDatabaseService.getFriendsNameArray());
    }
    public ListenerTask<ArrayList<String>> getFriendsUID() {
        return new ListenerTask<>(this, () -> mDatabaseService.getFriendsUIDArray());
    }
    public ListenerTask<Task<Void>> toggleFriend(String UID){
        return new ListenerTask<>(this, () -> mDatabaseService.toggleFriend(UID));
    }

    // Notifications:
    public ListenerTask<Query> getNotifications() {
        return new ListenerTask<>(this, () -> mDatabaseService.getNotifications());
    }
    public ListenerTask<Task<Void>> sendNotification(String msg, String toUID) {
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        String formattedDate = df.format(c);

        return new ListenerTask<>(this, () -> mDatabaseService.sendNotification(msg, toUID, formattedDate));
    }
    public ListenerTask<Task<Void>> removeNotification(Notification notification) {
        return new ListenerTask<>(this, () -> mDatabaseService.removeNotification(notification));
    }
    public ListenerTask<ListenerVariable<Notification>> getNotificationListener() {
        return new ListenerTask<>(this, () -> mDatabaseService.getNotificationListener());
    }

    // Challenges:
    public ListenerTask<Query> getOpponentsChallengeQuery(String UID) {
        return new ListenerTask<>(this, () -> mDatabaseService.getOpponentsChallengeQuery(UID));
    }
    public ListenerTask<Task<Void>> changeChallengeStatus(ChallengeStatus challengeStatus) {
        return new ListenerTask<>(this, () -> mDatabaseService.changeChallengeStatus(challengeStatus));
    }
    public ListenerTask<Task<Void>> removeChallengeStatus() {
        return new ListenerTask<>(this, () -> mDatabaseService.removeChallengeStatus());
    }

    // this function can be used if you want to get more values at the same time, because in this success listener you could call the direct functions
    public ListenerTask<Boolean> isBounded() {
        return new ListenerTask<>(this, () -> mBounded.get());
    }
    public ListenerTask<ListenerVariable<Boolean>> getUpdateListener() {
        return new ListenerTask<>(this, () -> mDatabaseService.getUpdateListener());
    }

    // ----------- Direct service functions  -----------

    // only use these direct functions in a successListener of isBounded or getUpdateListener
    public User getUserDirect(String UID) {
        return mDatabaseService.getUser(UID);
    }
    public ArrayList<User> getUsersDirect(){
        return mDatabaseService.getUsersArray();
    }
    public HashMap<String, User> getUsersHashMapDirect(){
        return mDatabaseService.getUsers();
    }
    public ArrayList<User> getFriendsDirect(){
        return mDatabaseService.getFriendsData();
    }
    public User getCurrentUserDirect(){
        return mDatabaseService.getCurrentUser();
    }

    protected void acceptChallenge(Notification notification) {
        Intent challengeIntent = new Intent(getApplicationContext(), ChallengeActivity.class);
        challengeIntent.putExtra(ChallengeActivity.START_FRAGMENT, 4);
        challengeIntent.putExtra(ChallengeActivity.START_SELECTED_FRIEND_UID, notification.getSentBy());
        startActivity(challengeIntent);

        ChallengeStatus challengeStatus = new ChallengeStatus(notification.getSentBy(), ChallengeStatus.ChallengeState.WAITING, true);
        changeChallengeStatus(challengeStatus);
    }


    private void setupNotificationListener() {
        getNotificationListener().setSuccessListener(notificationListenerIn -> {
                    notificationListener = notificationListenerIn;
                    notificationListener.addListener(notificationChangeListener);
                });
    }

    ListenerVariable.ChangeListener<Notification> notificationChangeListener = notification -> {
        // if the new notification is a challenge, show a dialog for it:
        if (notification.getType() == Notification.NotificationType.CHALLENGE || notification.getType() == Notification.NotificationType.ACCEPTED_CHALLENGE) {
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
                    removeNotification(notification);
                    if (notification.getType() == Notification.NotificationType.CHALLENGE)
                        sendNotification(Notification.NotificationType.ACCEPTED_CHALLENGE.toString(), notification.getSentBy());

                    acceptChallenge(notification);

                }

                @Override
                public void onDeny() {
                    removeNotification(notification);
                    Toast.makeText(getApplicationContext(), "Denied " + notification.getTitle().toLowerCase(), Toast.LENGTH_SHORT).show();
                }
            });

            // show the dialog
//            android.support.v4.app.FragmentActivity.getSupportFragmentManager()
            try {
                dialog.show(getFragmentManager(), notification.getTitle());
            } catch (Exception ignored){}
        }

        // if the new notification is a challenge Accept, show a dialog for that:
        // Todo, make a separate dialog for this
    };

}


