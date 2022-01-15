package com.example.motifissa.challenge_screens;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.motifissa.HelperClasses.ListenerTask;
import com.example.motifissa.HelperClasses.Notification;
import com.example.motifissa.R;
import com.example.motifissa.HelperClasses.ServiceListener;
import com.google.android.gms.tasks.Task;

public class ChallengeActivity extends ServiceListener {
    /*
    Screens
        - Choose Friend
        - Overview Friend
        - Sent
        - -------
        - loading/waiting on other user??
        - choose location
        - countdown/challengeScreen
     */

    public static final String START_FRAGMENT = "START_FRAGMENT";
    public static final String START_SELECTED_FRIEND_UID = "START_SELECTED_FRIEND_UID";


    // fragments
    int currentFragment = 1;
    ChooseFriendFragment chooseFriendFragment;
    OverviewChallengeFragment overviewChallengeFragment;
    ChallengeSentFragment challengeSentFragment;
    ChallengeLoadingFragment challengeLoadingFragment;
    Challenge_MapsFragment challengeMapsFragment;

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
                    if (currentFragment > 1 && currentFragment < 4)
                        changeFragment(currentFragment - 1);
                    else if (currentFragment > 4)
                        changeFragment(currentFragment - 1);
                    else
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
            if (currentFragment > 1 && currentFragment < 4)
                changeFragment(currentFragment - 1);
            else if (currentFragment > 4)
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

    public ListenerTask<Task<Void>> challengeFriend() {
        return sendNotification(Notification.NotificationType.CHALLENGE.toString(), selectedFriend);
    }

    public String getSelectedFriend() {
        return selectedFriend;
    }

    public void setSelectedFriend(String newID) {
        selectedFriend = newID;
    }
}