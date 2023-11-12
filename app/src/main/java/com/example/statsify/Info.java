package com.example.statsify;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class Info extends AppCompatActivity {

    LinearLayout q1Layout;
    Button q1btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
    }

    /**
     * Inflates the menu resource file to display the options menu.
     *
     * @param menu The menu instance to inflate.
     * @return true to display the menu, false otherwise.
     */
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mainmenu, menu);
        return true;
    }

    /**
     * Handles the selection of an item from the options menu.
     *
     * @param item The selected menu item.
     * @return true to indicate that the selection has been handled, false otherwise.
     */
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        // Check what item ID and intent to new screen, once I have settings screen
        return true;
    }
}