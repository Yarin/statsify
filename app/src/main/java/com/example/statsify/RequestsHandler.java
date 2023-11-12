package com.example.statsify;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;


import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class RequestsHandler extends AsyncTask<String, Void, String> {

    public final String BASE_ENDPOINT = "https://api.spotify.com/v1/me";
    private SharedPreferences sharedPreferences;

    /**
     * Constructs a RequestsHandler object.
     *
     * @param context The context of the application.
     */
    public RequestsHandler(Context context) {
        super(); // Call the constructor of the superclass if needed
        this.sharedPreferences = context.getSharedPreferences("details", Context.MODE_PRIVATE);
    }

    /**
     * Executes the request in the background.
     *
     * @param strings An array of strings containing the token, item, and time_range.
     * @return The display name of the user.
     */
    @Override
    protected String doInBackground(String... strings) { // token, item, time_range
        // https://api.spotify.com/v1/me/top/artists?time_range=short_term
        String displayName = getUsernameAndId(strings[0])[1];
        String userId = getUsernameAndId(strings[0])[0];
        this.sharedPreferences.edit().putString("user_id", userId).commit();
        try {
            saveToDatabase(userId, displayName, strings[0]);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return displayName;
    }


    /**
     * Retrieves the username and ID of the user.
     *
     * @param token The Spotify access token.
     * @return An array of strings containing the user ID and display name.
     */
    protected String[] getUsernameAndId(String token) {
        String url = BASE_ENDPOINT;

        URL obj = null;
        try {
            obj = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        HttpURLConnection con = null;
        try {
            con = (HttpURLConnection) obj.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //add request header
        con.setRequestProperty("Authorization", "Bearer " + token);
        con.setRequestProperty("Content-Type", "application/json");
        System.out.println("\nSending 'GET' request to URL : " + url);


        BufferedReader in = null;
        try {
            in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        String inputLine = null;
        StringBuffer response = new StringBuffer();

        while (true) {
            try {
                if (!((inputLine = in.readLine()) != null)) break;
            } catch (IOException e) {
                e.printStackTrace();
            }
            response.append(inputLine);

        }

        JSONArray items = null;
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String result = response.toString();
        String displayName = "";
        String userId = "";
        try {

            JSONObject json = new JSONObject(result);
            displayName = json.getString("display_name");
            userId = json.getString("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String[] arr = {userId, displayName};
        return arr;
    }


    /**
     * Retrieves the user's top tracks.
     *
     * @param token      The Spotify access token.
     * @param time_range The time range for the top tracks.
     * @return An ArrayList of strings containing the names of the top tracks.
     */
        protected List<String> getTopTracks(String token, String time_range) throws JSONException {
        String url = BASE_ENDPOINT + "/top/" + "tracks" + "?limit=20&time_range=" + time_range;

        URL obj = null;
        try {
            obj = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        HttpURLConnection con = null;
        try {
            con = (HttpURLConnection) obj.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //add request header
        con.setRequestProperty("Authorization", "Bearer " + token);
        con.setRequestProperty("Content-Type", "application/json");
        // Toast.makeText(c, url, Toast.LENGTH_LONG).show();
        // int responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);



        BufferedReader in = null;
        try {
            in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        String inputLine = null;
        StringBuffer response = new StringBuffer();

        while (true) {
            try {
                if (!((inputLine = in.readLine()) != null)) break;
            } catch (IOException e) {
                e.printStackTrace();
            }
            response.append(inputLine);

        }

        JSONArray items = null;
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String result = response.toString();
        String name = "";
        try {

            JSONObject json = new JSONObject(result);
            items = json.getJSONArray("items");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ArrayList<String> songs = new ArrayList<>();
        System.out.println(items.length());
        for (int i = 0; i < items.length(); i++) {
            JSONObject jsonObj = items.getJSONObject(i);
            System.out.println(jsonObj);
            String songArtist = jsonObj.getJSONArray("artists").getJSONObject(0).getString("name");
            String songName = jsonObj.getString("name");
            System.out.println(songArtist);
            System.out.println(songName);
            songs.add(songArtist + " - " + songName);

        }

        return songs;


    }


    /**
     * Retrieves the user's top artists.
     *
     * @param token      The Spotify access token.
     * @param time_range The time range for the top artists.
     * @return An ArrayList of strings containing the names of the top artists.
     */
    protected List<String> getTopArtists(String token, String time_range) throws JSONException {
        String url = BASE_ENDPOINT + "/top/" + "artists" + "?limit=20&time_range=" + time_range;

        URL obj = null;
        try {
            obj = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        HttpURLConnection con = null;
        try {
            con = (HttpURLConnection) obj.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //add request header
        con.setRequestProperty("Authorization", "Bearer " + token);
        con.setRequestProperty("Content-Type", "application/json");
        // Toast.makeText(c, url, Toast.LENGTH_LONG).show();
        // int responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);


        BufferedReader in = null;
        try {
            in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        String inputLine = null;
        StringBuffer response = new StringBuffer();

        while (true) {
            try {
                if (!((inputLine = in.readLine()) != null)) break;
            } catch (IOException e) {
                e.printStackTrace();
            }
            response.append(inputLine);

        }

        JSONArray items = null;
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String result = response.toString();
        String name = "";
        try {

            JSONObject json = new JSONObject(result);
            items = json.getJSONArray("items");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ArrayList<String> names = new ArrayList<>();
        for (int i = 0; i < items.length(); i++) {
            JSONObject jsonObj = items.getJSONObject(i);
            String name1 = jsonObj.getString("name");
            names.add(name1);
            System.out.println(name1);
        }

        return names;



    }

    /**
     * Saves user data to the Firebase database.
     *
     * @param user_id      The user's ID.
     * @param display_name The user's display name.
     * @param token        The Spotify access token.
     */
    public void saveToDatabase(String user_id, String display_name, String token) throws JSONException {
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://statsify-ab389-default-rtdb.europe-west1.firebasedatabase.app/");

        DatabaseReference usersRef = database.getReference("users");

        usersRef.child(user_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // User exists in the database
                    // Perform necessary actions
                    Log.i("Check User", "User exists in the database");
                } else {
                    // User does not exist in the database
                    DatabaseReference myRef = database.getReference("users/" + user_id);
                    User user = new User(display_name, user_id, new HashMap<String, String[]>());
                    myRef.setValue(user, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                            if (error != null) {
                                Log.e("Save to Database", "Failed to save data to database", error.toException());
                            } else {
                                Log.i("Save to Database", "Data saved to database successfully");
                            }
                        }
                    });
                    Log.i("Check User", "User does not exist in the database");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error
                Log.e("Check User", "Error checking user existence: " + databaseError.getMessage());
            }
        });



        // Get the current date
        Calendar calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd_MM_yyyy", Locale.getDefault());
        String formattedDate = dateFormat.format(currentDate);

        DatabaseReference rootRef = database.getReference();

        // Create a reference to the "stats" node
        DatabaseReference statsRef = database.getReference("users/" + user_id + "/stats");

        // Create a reference to the new date node under the "stats" node
        DatabaseReference newDateRef = statsRef.child(formattedDate);

        // Create a reference to the "artists" child under the new date node
        DatabaseReference newArtistsRef = newDateRef.child("artists");

        // Set the value of the "artists" child to the new artists list
        newArtistsRef.setValue(getTopArtists(token, "short_term"));

        // Create a reference to the "songs" child under the new date node
        DatabaseReference newSongsRef = newDateRef.child("songs");

        // Set the value of the "songs" child to the new tracks list
        newSongsRef.setValue(getTopTracks(token, "short_term"));

    }

}
