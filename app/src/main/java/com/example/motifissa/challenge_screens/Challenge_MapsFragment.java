package com.example.motifissa.challenge_screens;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.motifissa.HelperClasses.ChallengeStatus;
import com.example.motifissa.MainScreen;
import com.example.motifissa.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Random;

// TODO rework entire gps: https://www.youtube.com/watch?v=77BO6UiWt4A
//                         https://www.youtube.com/watch?v=uTxItxrDVSk
//  BUG: when the one who proposed the pos clicked on the arrived button something happes :(


/**
 * <h2>Description of how this fragment works:</h2>
 * <p>
 * One user (the master) the one who started the challenge chooses a location on the map and then confirms              <br>
 * Then the other user is shown this location and can choose to accept or to change it                                  <br>
 * If accepted he chances his state and continues on then the other uses sees that state change and continues with him  <br>
 * If he didn't accept and he can change it and sent it trough to the other user where he can do the same again         <br>
 * </p>
 */

public class Challenge_MapsFragment extends Fragment {
    // initializing FusedLocationProviderClient
    //https://techenum.com/how-to-get-current-gps-location-in-android/
    private static final String TAG = "Challenge_MapsFragment";
    private static final int ZOOM_LEVEL = 15;
    private static final int MAPS_ZOOM_PADDING = 200;
    public static final int DEFAULT_GPS_INTERFAL = 60000;
    public static final int FASTEST_GPS_INTERVAL = 10000;
    private static final int PERMISSION_FINE_LOCATION = 69;

    Button confirmButton;
    Button changeButton;
    TextView titleTxt;
    ChallengeActivity challengeActivity;

    // maps
    GoogleMap maps;
    Marker userMarker;
    Marker chosenMarker;
    Marker opponentMarker;
    ChallengeStatus.Position userPos;
    ChallengeStatus.Position opponentsPos;

    // GPS location
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof ChallengeActivity) {
            challengeActivity = (ChallengeActivity) context;
        } else {
            throw new RuntimeException(context.toString() + " must be ChallengeActivity");
        }

        // TODO change this to the users current location:
        // get the location of the user
//        if (Build.FINGERPRINT.contains("generic")) { // if run on an emulator:
            Random random = new Random();
            userPos = new ChallengeStatus.Position(52.24655176852505 + (0.5 - random.nextDouble()) * 0.01, 6.847529082501974 + (0.5 - random.nextDouble()) * 0.01);
            challengeActivity.setOwnPos(userPos); // the the users location to the opponents
//        } else {
            // setup the setting for the gps:
//            locationRequest = new LocationRequest();
//            locationRequest.setInterval(DEFAULT_GPS_INTERFAL); // 1 min when the phone is in energy saving modes
//            locationRequest.setFastestInterval(FASTEST_GPS_INTERVAL); // 10 sec on fastest
//            locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY); // TODO might change this to high accuracy mode, idk
//            setup_GPS();
//        }
    }

//    private void setup_GPS(){
//        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
//
//        if(ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){ // if the permission is granted
//            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location ->{
//               //TODO this
//                if (location != null){
//                    Log.e(TAG, location.toString());
//                    userPos = new ChallengeStatus.Position(location.getLatitude(), location.getLongitude());
//                    if (userMarker != null){
//                        userMarker.setPosition(userPos.changeToLatLng());
//                    }
//                    challengeActivity.setOwnPos(userPos);
//                    centerCamera();
//                } else
//                    Log.e(TAG, "null");
//            });
//        } else {
//            requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_FINE_LOCATION);
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//
//        if (requestCode == PERMISSION_FINE_LOCATION) {
//            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
//                setup_GPS();
//            else {
//                Toast.makeText(getContext(), "Can't challenge without location permission", Toast.LENGTH_LONG).show();
//                challengeActivity.cancelChallenge();
//            }
//        }
//    }

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */

        // https://developers.google.com/maps/documentation/android-sdk/events
        @Override
        public void onMapReady(GoogleMap googleMap) {
            maps = googleMap;

            LatLng userLatLng = userPos.changeToLatLng();
            userMarker = googleMap.addMarker(new MarkerOptions().position(userLatLng).title("Your location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
            assert userMarker != null;
            userMarker.showInfoWindow();

            chosenMarker = googleMap.addMarker(new MarkerOptions().position(userLatLng).title("Place to meet").visible(false));
            opponentMarker = googleMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Opponents location").visible(false).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
            if (opponentsPos != null && opponentMarker != null) {
                opponentMarker.setPosition(opponentsPos.changeToLatLng());
                opponentMarker.setVisible(true);
            }

            // set the start zoom and place
            if (opponentsPos != null) {
                centerCamera();
            } else {
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, ZOOM_LEVEL));
            }

            googleMap.setOnMapLongClickListener(latLng -> {
                if (pickingLocation()) {
                    chosenMarker.setPosition(latLng);
                    chosenMarker.setVisible(true);
                    confirmButton.setEnabled(true);
                }
            });
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_challenge__maps, container, false);

        confirmButton = view.findViewById(R.id.confirm_button);
        changeButton = view.findViewById(R.id.change_button);
        titleTxt = view.findViewById(R.id.maps_title);

        setupView();

        return view;
    }

    private boolean pickingLocation() {
        return challengeActivity.getChallengeStatus().getChallengeState() == ChallengeStatus.ChallengeState.PICK_LOCATION;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

    public void otherChooseLocation(ChallengeStatus.Position pos) {
        if (maps == null) {
            Log.e(TAG, "Maps not yet initialized");
        }
        // set marker on the chosen spot
        chosenMarker.setPosition(pos.changeToLatLng());
        chosenMarker.setVisible(true);

        //zoom to the chosen marker
        maps.animateCamera(CameraUpdateFactory.newLatLngZoom(pos.changeToLatLng(), ZOOM_LEVEL));

        confirmButton.setEnabled(true);
        changeButton.setEnabled(true);

        titleTxt.setText(R.string.challenge_maps_title_chosen);
        centerCamera();
    }

    public void updateOpponentsPos(ChallengeStatus.Position opponentsPos) {
        if (opponentMarker != null && opponentsPos != this.opponentsPos) {
            opponentMarker.setPosition(opponentsPos.changeToLatLng());
            opponentMarker.setVisible(true);
            this.opponentsPos = opponentsPos;
            centerCamera();
        }
        this.opponentsPos = opponentsPos;
    }

    private void centerCamera() {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(userPos.changeToLatLng());
        if (opponentMarker != null && opponentMarker.isVisible() && opponentsPos != null) {
            builder.include(opponentsPos.changeToLatLng());
        }

        if (chosenMarker != null && chosenMarker.isVisible()){
            builder.include(chosenMarker.getPosition());
        }

        LatLngBounds bounds = builder.build();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, MAPS_ZOOM_PADDING);

        maps.animateCamera(cameraUpdate);
    }

    private void centerCameraChosenPos() {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(userPos.changeToLatLng());
        builder.include(opponentsPos.changeToLatLng());
        builder.include(chosenMarker.getPosition());

        LatLngBounds bounds = builder.build();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, MAPS_ZOOM_PADDING);

        maps.animateCamera(cameraUpdate);
    }

    public void acceptedLocation() {
        changeButton.setVisibility(View.GONE);
        confirmButton.setEnabled(true);
        confirmButton.setVisibility(View.VISIBLE);

        // TODO Temp, should be changed with a position check or with box if they are connected.
        confirmButton.setText(R.string.action_arrived_temp);
        titleTxt.setText(R.string.challenge_maps_title_find);

        ChallengeStatus challengeStatus = challengeActivity.getChallengeStatus();
        challengeStatus.setChallengeState(ChallengeStatus.ChallengeState.FINDING_EACH_OTHER);
        challengeActivity.setChallengeStatus(challengeStatus);
        challengeActivity.changeChallengeStatus(challengeStatus).setSuccessListener(task -> task.addOnSuccessListener(success -> {
            //TODO this
            // and this
        }).addOnFailureListener(error -> {
            Toast.makeText(getContext(), "Couldn't accept challenge, " + error, Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Couldn't accept challenge, " + error);
        }));

        centerCamera();
    }

    public void setupView() {

        confirmButton.setEnabled(false);
        confirmButton.setVisibility(View.VISIBLE);

        // show different screens to the master and slave
        if (!pickingLocation()) { // slave, the one waiting for the other user
            changeButton.setVisibility(View.VISIBLE);
            changeButton.setEnabled(false);

            challengeActivity.getUser(challengeActivity.getChallengeStatus().getOpponent()).setSuccessListener(opponent ->
                    titleTxt.setText(getResources().getString(R.string.challenge_maps_title_waiting).replaceAll("username", opponent.getName())));

            confirmButton.setOnClickListener(v -> acceptedLocation());

            changeButton.setOnClickListener(v -> {
                changeButton.setEnabled(false);
                ChallengeStatus challengeStatus = challengeActivity.getChallengeStatus();
                challengeStatus.setChosenPos(null);
                challengeStatus.setChallengeState(ChallengeStatus.ChallengeState.PICK_LOCATION);
                challengeActivity.setChallengeStatus(challengeStatus);
                challengeActivity.changeChallengeStatus(challengeStatus).setSuccessListener(task -> task.addOnSuccessListener(success -> setupView())
                        .addOnFailureListener(error -> {
                            Toast.makeText(getContext(), "Couldn't deny location, " + error, Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "Couldn't deny location, " + error);
                        }));
            });
        } else { // master, the one first picking a location
            titleTxt.setText(R.string.Challenge_maps_title);
            changeButton.setVisibility(View.GONE);

            confirmButton.setOnClickListener(v -> {
                ChallengeStatus challengeStatus = challengeActivity.getChallengeStatus();
                challengeStatus.setChosenPos(new ChallengeStatus.Position(chosenMarker.getPosition()));

                challengeActivity.setChallengeStatus(challengeStatus);
                challengeStatus.setChallengeState(ChallengeStatus.ChallengeState.PICK_LOCATION_DONE);
                challengeActivity.changeChallengeStatus(challengeStatus).setSuccessListener(task -> task.addOnSuccessListener(success -> {
                    //TODO this
                    challengeActivity.getUser(challengeActivity.getChallengeStatus().getOpponent()).setSuccessListener(opponent -> {
                        // set the title and hide the confirm button
                        titleTxt.setText(getResources().getString(R.string.challenge_maps_title_locationChosen).replaceAll("username", opponent.getName()));
                        confirmButton.setVisibility(View.INVISIBLE);
                    });
                }).addOnFailureListener(error -> {
                    Toast.makeText(getContext(), "Couldn't send location, " + error, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Couldn't send location, " + error);
                }));

                confirmButton.setEnabled(false);
            });
        }
    }
}