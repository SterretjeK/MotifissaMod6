package com.example.motifissa.challenge_screens;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.motifissa.HelperClasses.ListenerTask;
import com.example.motifissa.R;
import com.example.motifissa.HelperClasses.ServiceListener;
import com.google.android.gms.tasks.Task;

public class ChallengeActivity extends ServiceListener {

    // service
//    ListenerVariable<Boolean> mBounded = new ListenerVariable<>(false); // a custom type, that allows us to add listeners to variables
//    boolean mIsConnecting;
//    DatabaseService mDatabaseService;

    // fragments
    int currentFragment = 1;
    ChooseFriendFragment chooseFriendFragment;
    OverviewChallengeFragment overviewChallengeFragment;
    ChallengeSentFragment challengeSentFragment;

    private String selectedFriend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge);

        chooseFriendFragment = new ChooseFriendFragment();
        overviewChallengeFragment = new OverviewChallengeFragment();
        challengeSentFragment = new ChallengeSentFragment();

        // This callback will change what happens when the user clicks back
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                if (currentFragment > 1)
                    changeFragment(currentFragment - 1);
                else
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

        changeFragment(1);
    }


    public void changeFragment(int changeToFragment) {
        Fragment selectedFragment;

        switch (changeToFragment) {
            case 1:
                selectedFragment = chooseFriendFragment;
                break;
            case 2:
                selectedFragment = overviewChallengeFragment;
                break;
            case 3:
                selectedFragment = challengeSentFragment;
                break;

            default:
//                Toast.makeText(this, "SelectedFragment in navListener was unknown", Toast.LENGTH_SHORT).show();
                return;
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerChallenge, selectedFragment).commit();
        this.currentFragment = changeToFragment;
    }

    // this event will handle the bck arrow on the action bar
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (currentFragment > 1)
                changeFragment(currentFragment - 1);
            else
                finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void moveUpFragment() {
        changeFragment(currentFragment + 1);
        // This callback will change what happens when the user clicks back
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                if (currentFragment > 1)
                    changeFragment(currentFragment - 1);
                else
                    finish();
            }
        };
        this.getOnBackPressedDispatcher().addCallback(this, callback);
    }

    public void moveBackFragment() {
        if (currentFragment > 1)
            changeFragment(currentFragment - 1);
        else
            finish();
    }

    public void moveBackFragment(int amount) {
        if (currentFragment - amount > 0)
            changeFragment(currentFragment - amount);
        else
            finish();
    }


//    public void connectToService(){
//        mIsConnecting  = true;
//        mBounded.set(false);
//        Intent serviceIntent = new Intent(this, DatabaseService.class);
//        bindService(serviceIntent, mConnection, BIND_AUTO_CREATE);
//    }
//
//    ServiceConnection mConnection = new ServiceConnection() {
//        @Override
//        public void onServiceDisconnected(ComponentName name) {
//            mBounded.set(false);
//            mIsConnecting = false;
//            mDatabaseService = null;
//        }
//
//        @Override
//        public void onServiceConnected(ComponentName name, IBinder service) {
//            DatabaseService.LocalBinder mLocalBinder = (DatabaseService.LocalBinder)service;
//            mDatabaseService = mLocalBinder.getServerInstance();
//            mBounded.set(true);
//            mIsConnecting = false;
//
////            Toast.makeText(ChallengeActivity.this, "Service is connected Challenge screen", Toast.LENGTH_SHORT).show();
//        }
//    };

//    @Override
//    protected void onStop() {
//        super.onStop();
//
//        //when the activity is stopped, release the server
//        if(mBounded.get()) {
//            unbindService(mConnection);
//            mBounded.set(false);
//        }
//        mIsConnecting = false;
//    }

//    public ArrayList<User> getUsersArray(){
//        if (mBounded.get() && mDatabaseService != null) {
//            try {
//                return mDatabaseService.getUsersArray();
//            } catch (Exception e) {
//                Log.e("ChallengeScreen", "Database not bound, but said it was when trying to access getFriends");
//            }
//        }
//
//        if (!mIsConnecting) {
//            this.connectToService();
//        }
//        return null;
//    }

//    public User getUser(String ID){
//        if (mBounded.get()) {
//            try {
//                return mDatabaseService.getUser(ID);
//            } catch (Exception e){
//                Log.e("ChallengeScreen", "Database not bound, but said it was when trying to access getUser");
//            }
//        }
//
//        if (!mIsConnecting) {
//            this.connectToService();
//        }
//        return null;
//    }

//    public ArrayList<String> getFriends(){
//        if (mBounded.get()) {
//            try {
//                return mDatabaseService.getFriendsNameArray();
//            } catch (Exception e){
//                Log.e("ChallengeScreen", "Database not bound, but said it was when trying to access getFriends");
//            }
//        }
//
//        if (!mIsConnecting) {
//            this.connectToService();
//        }
//        return null;
//    }

//    public ArrayList<String> getFriendsID(){
//        if (mBounded.get()) {
//            try {
//                return mDatabaseService.getFriendsUIDArray();
//            } catch (Exception e){
//                Log.e("ChallengeScreen", "Database not bound, but said it was when trying to access getFriendsID");
//            }
//        }
//
//        if (!mIsConnecting) {
//            this.connectToService();
//        }
//        return null;
//    }

//    public User getCurrentUser() {
//        if (mBounded.get()) {
//            try {
//                return mDatabaseService.getCurrentUserData();
//            } catch (Exception e){
//                Log.e("ChallengeScreen", "Database not bound, but said it was when trying to access getFriendsID");
//            }
//        }
//
//        if (!mIsConnecting) {
//            this.connectToService();
//        }
//        return null;
//    }

    public ListenerTask<Task<Void>> challengeFriend() {
        return sendNotification("Challenge", selectedFriend);
    }

    public String getSelectedFriend() {
        return selectedFriend;
    }

    public void setSelectedFriend(String newID) {
        selectedFriend = newID;
    }
}