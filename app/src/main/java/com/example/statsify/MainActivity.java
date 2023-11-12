package com.example.statsify;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;


import com.google.android.material.snackbar.Snackbar;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;
import com.spotify.sdk.android.auth.BuildConfig;
//import com.spotify.sdk.android.authentication.sample.R;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;
import java.util.concurrent.ExecutionException;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // Request code will be used to verify if result comes from the login activity. Can be set to any integer.
    final int REQUEST_CODE = 1337;
    final String REDIRECT_URI = "https://oauth.pstmn.io/v1/browser-callback";
    final String CLIENT_ID = "5f86cd44ae094e4e8dfe069ec355cbb1";
    final String SCOPES = "user-top-read,user-read-recently-played";

    AuthenticationHandler authHandler = new AuthenticationHandler(CLIENT_ID, REDIRECT_URI, SCOPES, REQUEST_CODE);
    Animation animFadeIn;
    TextView textFadeIn;
    Button myButton;
    SharedPreferences sp;
    private SharedPreferences sharedPreferences;
    private static final String CHANNEL_ID = "notification_channel";


    /**
     * Called when the activity is created. Initializes the UI components and sets click listeners.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sp = getSharedPreferences("details", 0);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        checkLastOpenDate();

        myButton = findViewById((R.id.loginbtn));
        textFadeIn = findViewById(R.id.knowyourmusic);
        animFadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        textFadeIn.startAnimation(animFadeIn);
        myButton.setOnClickListener(this);

    }

    private void checkLastOpenDate() {
        long lastOpenTime = sharedPreferences.getLong("LAST_OPEN_TIME", 0);
        long currentTime = new Date().getTime();

        // Calculate the difference in milliseconds between the current time and the last open time
        long timeDifference = currentTime - lastOpenTime;
        long weekInMillis = 7 * 24 * 60 * 60 * 1000; // 1 week in milliseconds

        if (timeDifference >= weekInMillis) {
            // If the difference is equal to or greater than 1 week, show the notification
            showNotification();
        }

        // Save the current time as the last open time
        sharedPreferences.edit().putLong("LAST_OPEN_TIME", currentTime).apply();
    }

    private void showNotification() {
        // Create a notification channel for Android Oreo and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "App Notification",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        // Create an intent to launch the app when the notification is clicked
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        // Create the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle("App Reminder")
                .setContentText("You haven't opened the app for a week!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        // Show the notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(1, builder.build());
    }

    /**
     * Creates the options menu for the activity.
     *
     * @param menu The menu to inflate.
     * @return true if the menu was successfully inflated, false otherwise.
     */
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mainmenu, menu);
        return true;
    }

    /**
     * Handles menu item selection.
     *
     * @param item The selected menu item.
     * @return true if the menu item was handled, false otherwise.
     */
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id==R.id.info) {
            Intent intent = new Intent(this, Info.class);
            startActivity(intent);
        }

        if(id==R.id.settings) {
            Intent intent = new Intent(this, Settings.class);
            startActivity(intent);
        }
        return true;
    }

    /**
     * Handles button click events.
     *
     * @param v The clicked view.
     */
    @Override
    public void onClick(View v) {
        if (v == myButton) {
            authHandler.openAuthentication(this);
        }
    }

    /**
     * Handles the result of the authentication flow.
     *
     * @param requestCode The request code.
     * @param resultCode  The result code.
     * @param intent      The result intent.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, intent);

            switch (response.getType()) {
                // Response was successful and contains auth token
                case TOKEN:
                    SharedPreferences.Editor editor = sp.edit();
                    String token = response.getAccessToken();
                    //System.out.println(token);
                    Log.d("STARTING", "GOT AUTH TOKEN");
                    editor.putString("token", token); // store the token in the SharedPreferences
                    editor.commit();

                    Intent loggedIn = new Intent(this, LoggedIn.class);
                    startActivity(loggedIn);
                    break;

                // Auth flow returned an error
                case ERROR:
                    // Handle error response
                    break;

                // Most likely auth flow was cancelled
                default:
                    // Handle other cases
            }
        }
    }

}
