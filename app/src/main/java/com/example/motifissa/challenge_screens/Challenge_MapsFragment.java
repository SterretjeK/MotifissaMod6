package com.example.motifissa.challenge_screens;

import android.content.Context;
import android.os.Bundle;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Objects;

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

    Button confirmButton;
    Button changeButton;
    TextView titleTxt;
    ChallengeActivity challengeActivity;

    Marker chosenMarker;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof ChallengeActivity) {
            challengeActivity = (ChallengeActivity) context;
        } else {
            throw new RuntimeException(context.toString() + " must be ChallengeActivity");
        }
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
            // TODO change this to the users current location:
            LatLng userLatLng = new LatLng(52.24655176852505, 6.847529082501974);
            googleMap.addMarker(new MarkerOptions().position(userLatLng).title("Your location"));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(userLatLng));


            chosenMarker = googleMap.addMarker(new MarkerOptions().position(userLatLng).title("Place to meet").visible(false));

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
        if (!pickingLocation()){
            changeButton.setVisibility(View.VISIBLE);
            changeButton.setEnabled(false);

            challengeActivity.getUser(challengeActivity.getChallengeStatus().getOpponent()).setSuccessListener(opponent ->{
                titleTxt.setText(getResources().getString(R.string.challenge_maps_title_waiting).replaceAll("username", opponent.getName()));
            });

            // TODO confirmButton.setOnClickListener();
        } else {
            confirmButton.setOnClickListener(v -> {
                ChallengeStatus challengeStatus = challengeActivity.getChallengeStatus();
                challengeStatus.setLatitude(chosenMarker.getPosition().latitude);
                challengeStatus.setLongitude(chosenMarker.getPosition().longitude);

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


}