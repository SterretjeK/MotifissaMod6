package com.example.motifissa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.MenuItem;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // set the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // setup the action bar
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null; // to make sure that this activity has an action bar
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM); // showing the back button in action bar
        actionBar.setCustomView(R.layout.action_bar); // set our custom action bar
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

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