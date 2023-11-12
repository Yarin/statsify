package com.example.statsify;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

public class Settings extends AppCompatActivity {

    private Switch switchButton;
    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        switchButton = findViewById(R.id.switchButton);
        sharedPreferences = getSharedPreferences("details", MODE_PRIVATE);

        boolean savedSwitchState = sharedPreferences.getBoolean("SWITCH_STATE", false);
        switchButton.setChecked(savedSwitchState);


        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                updateSwitchState(isChecked);
                if (isChecked) {
                    Toast.makeText(getApplicationContext(), "BASIC MODE WILL CRASH THE APP", Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    /**
     * Update the switch state in SharedPreferences
     *
     * @param isChecked The new switch state
     */
    private void updateSwitchState(boolean isChecked) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("SWITCH_STATE", isChecked);
        editor.apply();

    }
}
