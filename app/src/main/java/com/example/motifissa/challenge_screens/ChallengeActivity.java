package com.example.motifissa.challenge_screens;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.motifissa.HelperClasses.ChallengeStatus;
import com.example.motifissa.HelperClasses.ListenerTask;
import com.example.motifissa.HelperClasses.Notification;
import com.example.motifissa.R;
import com.example.motifissa.HelperClasses.ServiceListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ChallengeActivity extends ServiceListener {
    /*
    Screens
        -1 Choose Friend
        -2 Overview Friend
        -3 Sent
        - -------
        -4 loading/waiting on other user??
        -5 choose location
        -6 countdown/challengeScreen
     */

    private static final String TAG = "ChallengeActivity";
    public static final String START_FRAGMENT = "START_FRAGMENT";
    public static final String START_SELECTED_FRIEND_UID = "START_SELECTED_FRIEND_UID";


    // fragments
    int currentFragment = 1;
    ChooseFriendFragment chooseFriendFragment;
    OverviewChallengeFragment overviewChallengeFragment;
    ChallengeSentFragment challengeSentFragment;
    ChallengeLoadingFragment challengeLoadingFragment;
    Challenge_MapsFragment challengeMapsFragment;

    // the actual challenge:
    ChallengeStatus challengeStatus;
    ChallengeStatus challengeStatusOpponent;

    private String selectedFriend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge);

        // wait until the service is bounded
        isBounded().setSuccessListener(bounded ->{
            chooseFriendFragment = new ChooseFriendFragment();
            overviewChallengeFragment = new OverviewChallengeFragment();
            challengeSentFragment = new ChallengeSentFragment();
            challengeLoadingFragment = new ChallengeLoadingFragment();
            challengeMapsFragment = new Challenge_MapsFragment();


            // This callback will change what happens when the user clicks back
            OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
                @Override
                public void handleOnBackPressed() {
                    onBackPress();
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

            if (getIntent().hasExtra(START_FRAGMENT)){
                currentFragment = getIntent().getIntExtra(START_FRAGMENT, 1);
            } else {
                currentFragment = 1;
            }

            if (getIntent().hasExtra(START_SELECTED_FRIEND_UID)){
                selectedFriend = getIntent().getStringExtra(START_SELECTED_FRIEND_UID);
            }

            changeFragment(currentFragment);
        });
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
            case 4:
                selectedFragment = challengeLoadingFragment;
                break;
            case 5:
                selectedFragment = challengeMapsFragment;
                break;

            default:
                Toast.makeText(this, "changeToFragment in changeFragment was unknown, " +  changeToFragment, Toast.LENGTH_SHORT).show();
                return;
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerChallenge, selectedFragment).commit();
        this.currentFragment = changeToFragment;
    }

    // this event will handle the bck arrow on the action bar
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPress();
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
                onBackPress();
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

    public ListenerTask<Task<Void>> challengeFriend() {
        return sendNotification(Notification.NotificationType.CHALLENGE.toString(), selectedFriend);
    }

    public String getSelectedFriend() {
        return selectedFriend;
    }

    public void setSelectedFriend(String newID) {
        selectedFriend = newID;
    }

    public void startChallenge(){
        challengeStatus = new ChallengeStatus(selectedFriend, ChallengeStatus.ChallengeState.WAITING);

        getOpponentsChallengeQuery(getSelectedFriend()).setSuccessListener(query -> query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                challengeStatusOpponent = snapshot.getValue(ChallengeStatus.class);
                if (challengeStatus == null){
                    query.removeEventListener(this);
                    return;
                }
                if (challengeStatusOpponent == null && challengeStatus.getChallengeState() != ChallengeStatus.ChallengeState.WAITING){
                    if (challengeStatus != null)
                        getUser(getSelectedFriend()).setSuccessListener(friend -> Toast.makeText(ChallengeActivity.this, "username disconnected".replaceAll("username", friend.getName()), Toast.LENGTH_SHORT).show());
                    cancelChallenge();
                    query.removeEventListener(this);
                    return;
                } else if (challengeStatusOpponent == null){
                    return;
                }
                if ((challengeStatusOpponent.getChallengeState() == ChallengeStatus.ChallengeState.WAITING || challengeStatusOpponent.getChallengeState() == ChallengeStatus.ChallengeState.CONNECTED)  && currentFragment < 5){
                    changeFragment(5);
                    challengeStatus.setChallengeState(ChallengeStatus.ChallengeState.CONNECTED);
                    changeChallengeStatus(challengeStatus);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        }));
    }

    public void cancelChallenge(){
        challengeStatus = null;
        removeChallengeStatus().setSuccessListener(task -> task.addOnSuccessListener(success -> finish())
                .addOnFailureListener(error -> {
                    Toast.makeText(this, "Couldn't cancel challenge", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Couldn't cancel challenge error: " + error);
                }));
    }

    public void onBackPress(){
        if (currentFragment > 1 && currentFragment < 4)
            changeFragment(currentFragment - 1);
        else if (currentFragment > 4)
            cancelChallenge();
        else finish();
    }
}