package com.example.motifissa.challenge_screens;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.motifissa.AddFriendsFragment;
import com.example.motifissa.HelperClasses.ChallengeStatus;
import com.example.motifissa.HelperClasses.ListenerTask;
import com.example.motifissa.HelperClasses.Notification;
import com.example.motifissa.R;
import com.example.motifissa.HelperClasses.ServiceListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

public class ChallengeActivity extends ServiceListener {
    /*
    Screens
        - -1 add Friend
        -1 Choose Friend
        -2 Overview Friend
        -3 Sent
        - -------
        -4 loading/waiting on other user??
        -5 choose location
        -6 countdown/challengeScreen
     */
    // TODO add friends in choose friends ding iets

    private static final String TAG = "ChallengeActivity";
    public static final String START_FRAGMENT = "START_FRAGMENT";
    public static final String START_SELECTED_FRIEND_UID = "START_SELECTED_FRIEND_UID";


    // fragments
    int currentFragment = 1;
    AddFriendsFragment addFriendsFragment;
    ChooseFriendFragment chooseFriendFragment;
    OverviewChallengeFragment overviewChallengeFragment;
    ChallengeSentFragment challengeSentFragment;
    ChallengeLoadingFragment challengeLoadingFragment;
    Challenge_MapsFragment challengeMapsFragment;
    Challenge_ChallengesFragment challengesFragment;

    // the actual challenge:
    ChallengeStatus challengeStatus;
    ChallengeStatus challengeStatusOpponent;

    // GPS location
    final static String[] PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    private static final int PERMISSION_ALL = 69;
    public static final int DEFAULT_GPS_INTERVAL = 60000;
    public static final int DEFAULT_GPS_DISTANCE = 50;

    public static final int FASTEST_GPS_INTERVAL = 10000;
    LocationManager locationManager;


    private String selectedFriend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge);

        // wait until the service is bounded
        isBounded().setSuccessListener(bounded -> {
            addFriendsFragment = new AddFriendsFragment();
            chooseFriendFragment = new ChooseFriendFragment();
            overviewChallengeFragment = new OverviewChallengeFragment();
            challengeSentFragment = new ChallengeSentFragment();
            challengeLoadingFragment = new ChallengeLoadingFragment();
            challengeMapsFragment = new Challenge_MapsFragment();
            challengesFragment = new Challenge_ChallengesFragment();


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

            if (getIntent().hasExtra(START_FRAGMENT)) {
                currentFragment = getIntent().getIntExtra(START_FRAGMENT, 1);
            } else {
                currentFragment = 1;
            }

            if (getIntent().hasExtra(START_SELECTED_FRIEND_UID)) {
                selectedFriend = getIntent().getStringExtra(START_SELECTED_FRIEND_UID);
            }

            changeFragment(currentFragment);
        });
    }

    // ---------------------------------- GPS ----------------------------------
    public void setupGPS() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        requestPermissions(PERMISSIONS, PERMISSION_ALL);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // if the permission was accepted
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            requestLocation();
        }
    }

    public void requestLocation() {
        if (locationManager == null) { //stupid shouldn't happen
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        } else {
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, DEFAULT_GPS_DISTANCE, DEFAULT_GPS_DISTANCE, locationListener);
                }
            }
        }
    }

    private final LocationListener locationListener = location -> {
        if (currentFragment == 5){
            ChallengeStatus.Position newPos = new ChallengeStatus.Position(location.getLatitude(), location.getLongitude());
            // if run on an emulator change it's position:
            if (Build.FINGERPRINT.contains("generic")) {
                Random random = new Random();
                newPos = new ChallengeStatus.Position(52.24655176852505 + (0.5 - random.nextDouble()) * 0.01, 6.847529082501974 + (0.5 - random.nextDouble()) * 0.01);
            }
            challengeMapsFragment.updatePos(newPos);
            setOwnPos(newPos);
        }
        else{
            // stop gps
            locationManager.removeUpdates(this.locationListener);
        }
        Log.d(TAG, "got location: " + location.getLatitude() + "," + location.getLongitude());
    };

    public void changeFragment(int changeToFragment) {
        Fragment selectedFragment;

        switch (changeToFragment) {
            case -1:
                selectedFragment = addFriendsFragment;
                break;
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
            case 6:
                selectedFragment = challengesFragment;
                break;

            default:
                Toast.makeText(this, "changeToFragment in changeFragment was unknown, " + changeToFragment, Toast.LENGTH_SHORT).show();
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

    public void moveBackFragment(int amount) {
        if (currentFragment == -1) {
            changeFragment(1);
        } else if (currentFragment - amount > 0 && currentFragment < 4)
            changeFragment(currentFragment - amount);
        else if (currentFragment > 4)
            cancelChallenge();
        else finish();
    }

    public void onBackPress() {
        moveBackFragment(1);
    }

    public void showAddFriendsFragment(){
        changeFragment(-1);
        // This callback will change what happens when the user clicks back
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                onBackPress();
            }
        };
        this.getOnBackPressedDispatcher().addCallback(this, callback);
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

    public void startChallenge() {
        challengeStatus = new ChallengeStatus(selectedFriend, ChallengeStatus.ChallengeState.WAITING);

        getOpponentsChallengeQuery(getSelectedFriend()).setSuccessListener(query -> query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                challengeStatusOpponent = snapshot.getValue(ChallengeStatus.class);

                // if canceled remove the event listener
                if (challengeStatus == null) {
                    query.removeEventListener(this);
                    return;
                }
                // if the other user canceled cancel this one as well
                if (challengeStatusOpponent == null && challengeStatus.getChallengeState() != ChallengeStatus.ChallengeState.WAITING) {
                    if (challengeStatus != null)
                        getUser(getSelectedFriend()).setSuccessListener(friend -> Toast.makeText(ChallengeActivity.this, "username disconnected".replaceAll("username", friend.getName()), Toast.LENGTH_SHORT).show());
                    cancelChallenge();
                    query.removeEventListener(this);
                    return;

                    // if the other user isn't connected yet then make this user the master
                } else if (challengeStatusOpponent == null && challengeStatus.getChallengeState() == ChallengeStatus.ChallengeState.WAITING) {
                    challengeStatus.setMaster(true);
                    changeChallengeStatus(challengeStatus);
                    return;
                    // if the other user isn't connected yet and, wait this one is never called :) , just keep it in to fix unknown errors
                } else if (challengeStatusOpponent == null) {
                    Log.e(TAG, "this shouldn't be called :) , startChallenge() in challenge activity");
                    return;
                }
                // it the other user connected and this one is still loading go to the second phase.
                if (challengeStatusOpponent.shouldBeInSecondPhase() && currentFragment < 5) {
                    challengeStatus.moveToSecondPhase();

                    changeFragment(5);
                    changeChallengeStatus(challengeStatus);
                }
                // if the other shared their location and the user is still in the maps fragment
                if (currentFragment == 5 && !challengeStatusOpponent.ownPosIsEmpty()) {
                    challengeMapsFragment.updateOpponentsPos(challengeStatusOpponent.getOwnPos());
                }

                // if the other user choose a new location sent that trough to the maps fragment
                if (currentFragment == 5 && !challengeStatusOpponent.chosenPosIsEmpty() && challengeStatusOpponent.getChallengeState() == ChallengeStatus.ChallengeState.PICK_LOCATION_DONE && challengeStatus.getChallengeState() == ChallengeStatus.ChallengeState.PICK_LOCATION_WAITING) {
                    challengeMapsFragment.otherChooseLocation(challengeStatusOpponent.getChosenPos());
                }

                // if the other user accepted the location
                if (currentFragment == 5 && challengeStatusOpponent.getChallengeState() == ChallengeStatus.ChallengeState.FINDING_EACH_OTHER) {
                    challengeMapsFragment.acceptedLocation();
                }

                // if the other user didn't accept the location and pressed change:
                if (currentFragment == 5 && challengeStatus.getChallengeState() == ChallengeStatus.ChallengeState.PICK_LOCATION_DONE && challengeStatusOpponent.getChallengeState() == ChallengeStatus.ChallengeState.PICK_LOCATION) {
                    challengeStatus.setChallengeState(ChallengeStatus.ChallengeState.PICK_LOCATION_WAITING);
                    changeChallengeStatus(challengeStatus).setSuccessListener(task -> task.addOnSuccessListener(success -> challengeMapsFragment.setupView()).addOnFailureListener(error -> {
                        Log.e(TAG, "couldn't update challengeState to reflect the other users denial of the proposed location");
                        // TODO this, add error something
                    }));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        }));
    }

    public ChallengeStatus getChallengeStatus() {
        return challengeStatus;
    }

    public void setChallengeStatus(ChallengeStatus challengeStatus) {
        this.challengeStatus = challengeStatus;
    }

    public void setOwnPos(ChallengeStatus.Position pos) {
        challengeStatus.setOwnPos(pos);
        changeChallengeStatus(challengeStatus);
    }

    public ChallengeStatus getChallengeStatusOpponent() {
        return challengeStatusOpponent;
    }

    public void cancelChallenge() {
        challengeStatus = null;
        removeChallengeStatus().setSuccessListener(task -> task.addOnSuccessListener(success -> finish())
                .addOnFailureListener(error -> {
                    Toast.makeText(this, "Couldn't cancel challenge", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Couldn't cancel challenge error: " + error);
                }));
    }

    public enum ChallengeFragmentState {
        Choose_Friend,
        Overview_Friend,
        Sent,
        waiting_on_other_user,
        choose_location,
        countdown_challengeScreen,
    }
}