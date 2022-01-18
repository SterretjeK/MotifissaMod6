package com.example.motifissa.challenge_screens;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.motifissa.HelperClasses.ChallengeStatus;
import com.example.motifissa.R;
import com.google.android.gms.location.FusedLocationProviderClient;
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

import java.util.Objects;
import java.util.Random;

/**
 * Description of how this fragment works:
 *
 * One user (the master) the one who started the challenge chooses a location on the map and then confirms
 * Then the other user is shown this location and can choose to accept or to change it
 * If accepted he chances his state and continues on then the other uses sees that state change and continues with him
 * If he didn't accept and he can change it and sent it trough to the other user where he can do the same again
 *
 */

public class Challenge_MapsFragment extends Fragment {
    // initializing FusedLocationProviderClient
    //https://techenum.com/how-to-get-current-gps-location-in-android/
    private static final String TAG = "Challenge_MapsFragment";
    private static final int ZOOM_LEVEL = 15;
    private static final int MAPS_ZOOM_PADDING = 100;

    Button confirmButton;
    Button changeButton;
    TextView titleTxt;
    ChallengeActivity challengeActivity;

    // maps
    GoogleMap maps;
    Marker chosenMarker;
    Marker opponentMarker;
    ChallengeStatus.Position userPos;
    ChallengeStatus.Position opponentsPos;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof ChallengeActivity) {
            challengeActivity = (ChallengeActivity) context;
        } else {
            throw new RuntimeException(context.toString() + " must be ChallengeActivity");
        }

        // TODO change this to the users current location:
        Random random = new Random();
        userPos = new ChallengeStatus.Position(52.24655176852505 + (0.5 - random.nextDouble())*0.01, 6.847529082501974 + (0.5 - random.nextDouble())*0.01);
        challengeActivity.setOwnPos(userPos);
    }

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

//            LatLng userLatLng = new LatLng(52.24655176852505, 6.847529082501974);
            LatLng userLatLng = userPos.getPos();
            googleMap.addMarker(new MarkerOptions().position(userLatLng).title("Your location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

            chosenMarker = googleMap.addMarker(new MarkerOptions().position(userLatLng).title("Place to meet").visible(false));
            opponentMarker = googleMap.addMarker(new MarkerOptions().position(new LatLng(0,0)).title("Opponents location").visible(false).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
            if (opponentsPos != null){
                opponentMarker.setPosition(opponentsPos.getPos());
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

        confirmButton.setEnabled(false);

        // show different screens to the master and slave
        if (!pickingLocation()){ // slave, the one waiting for the other user
            changeButton.setVisibility(View.VISIBLE);
            changeButton.setEnabled(false);

            challengeActivity.getUser(challengeActivity.getChallengeStatus().getOpponent()).setSuccessListener(opponent ->{
                titleTxt.setText(getResources().getString(R.string.challenge_maps_title_waiting).replaceAll("username", opponent.getName()));
            });

            // TODO confirmButton.setOnClickListener();
        } else { // master, the one first picking a location
            confirmButton.setOnClickListener(v -> {
                ChallengeStatus challengeStatus = challengeActivity.getChallengeStatus();
                challengeStatus.setChosenPos(new ChallengeStatus.Position(chosenMarker.getPosition()));

                challengeActivity.setChallengeStatus(challengeStatus);
                challengeStatus.setChallengeState(ChallengeStatus.ChallengeState.PICK_LOCATION_DONE);
                challengeActivity.changeChallengeStatus(challengeStatus).setSuccessListener(task -> {
                    task.addOnSuccessListener(success ->{
                        //TODO this
                    }).addOnFailureListener(error -> {
                        //TODO this
                    });
                });

                confirmButton.setEnabled(false);
            });
        }

        return view;
    }

    private boolean pickingLocation(){
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

    public void otherChooseLocation(ChallengeStatus.Position pos){
        if (maps == null) {
            Log.e(TAG, "Maps not yet initialized");
        }
        // set marker on the chosen spot
        chosenMarker.setPosition(pos.getPos());
        chosenMarker.setVisible(true);

        //zoom to the chosen marker
        maps.animateCamera(CameraUpdateFactory.newLatLngZoom(pos.getPos(), ZOOM_LEVEL));

        confirmButton.setEnabled(true);
        changeButton.setEnabled(true);

        titleTxt.setText(R.string.challenge_maps_title_chosen);
        centerCameraChosenPos();
    }

    public void updateOpponentsPos(ChallengeStatus.Position opponentsPos) {
        if (opponentMarker != null && opponentsPos != this.opponentsPos){
            opponentMarker.setPosition(opponentsPos.getPos());
            opponentMarker.setVisible(true);
            centerCamera();
        }
        this.opponentsPos = opponentsPos;
    }

    private void centerCamera(){
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(userPos.getPos());
        builder.include(opponentsPos.getPos());

        LatLngBounds bounds = builder.build();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, MAPS_ZOOM_PADDING);

        maps.animateCamera(cameraUpdate);
    }

    private void centerCameraChosenPos(){
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(userPos.getPos());
        builder.include(opponentsPos.getPos());
        builder.include(chosenMarker.getPosition());

        LatLngBounds bounds = builder.build();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, MAPS_ZOOM_PADDING);

        maps.animateCamera(cameraUpdate);
    }

    public void acceptedLocation(){
        changeButton.setVisibility(View.GONE);
        confirmButton.setEnabled(true);
        // TODO Temp, should be changed with a position check or with box if they are connected.
        confirmButton.setText("Arrived");
        titleTxt.setText(R.string.challenge_maps_title_find);


        //TODO rewrite this NOW
        ChallengeStatus challengeStatus = challengeActivity.getChallengeStatus();
        challengeStatus.setChallengeState(ChallengeStatus.ChallengeState.FINDING_EACH_OTHER);
        challengeActivity.changeChallengeStatus(challengeStatus);
    }
}