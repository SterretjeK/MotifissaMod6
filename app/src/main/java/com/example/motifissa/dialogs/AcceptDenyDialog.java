package com.example.motifissa.dialogs;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
//import androidx.fragment.app.DialogFragment;
import android.app.DialogFragment;
import android.widget.TextView;

import com.example.motifissa.DashboardFragment;
import com.example.motifissa.R;

import java.util.Objects;

public class AcceptDenyDialog extends DialogFragment {
    private static final String TAG = "AcceptDenyDialog";
    public static final String TITLE = "TITLE";
    public static final String SUBTITLE = "SUBTITLE";

    AcceptDenyListener listener;

    String title;
    String subtitle;

    public AcceptDenyDialog() {
        super();
    }

    public static DashboardFragment newInstance(String title, String subtitle) {
        DashboardFragment fragment = new DashboardFragment();
        Bundle args = new Bundle();
        args.putString(TITLE, title);
        args.putString(SUBTITLE, subtitle);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title = getArguments().getString(TITLE);
            subtitle = getArguments().getString(SUBTITLE);
        }
    }

    public void setListener(AcceptDenyListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.dialog_accept_deny, container, false);

        TextView titleTxt = view.findViewById(R.id.dialog_title);
        TextView subtitleTxt = view.findViewById(R.id.dialog_subtitle);
        titleTxt.setText(title);
        subtitleTxt.setText(subtitle);

       Button acceptButton = view.findViewById(R.id.dialog_accept);
       Button denyButton = view.findViewById(R.id.dialog_deny);

        if (listener == null){
            Log.e(TAG, "-------------------------------------------------------\nlistener was null");
        }

        acceptButton.setOnClickListener(btnView -> {
            listener.onAccept();
            Objects.requireNonNull(getDialog()).dismiss();
        });
       denyButton.setOnClickListener(btnView -> {
           listener.onDeny();
           Objects.requireNonNull(getDialog()).dismiss();
       });

       return view;
    }

    public interface AcceptDenyListener{
        void onAccept();
        void onDeny();
    }
}
