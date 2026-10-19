package com.example.smartpantrymanager;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitle("Settings");

        SharedPreferences prefs = getSharedPreferences("settings", Context.MODE_PRIVATE);
        SwitchCompat switchExpiry = findViewById(R.id.switchExpiry);

        // Load the saved value (default: on)
        switchExpiry.setChecked(prefs.getBoolean("expiry_alerts", true));

        // Save whenever the user flips the switch
        switchExpiry.setOnCheckedChangeListener((button, isChecked) ->
                prefs.edit().putBoolean("expiry_alerts", isChecked).apply());
    }
}