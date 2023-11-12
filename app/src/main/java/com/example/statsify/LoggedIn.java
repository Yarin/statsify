package com.example.statsify;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;

import org.json.JSONException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class LoggedIn extends AppCompatActivity {

    SharedPreferences sp;
    SharedPreferences.Editor editor;
    private Spinner dateSpinner;
    private String selectedDate;

    private GridView gridView1;
    private GridView gridView2;
    private ArrayAdapter<String> adapter1;
    private ArrayAdapter<String> adapter2;
    private List<String> topItems1;
    private List<String> topItems2;

    private DatabaseReference databaseReference;


    /**
     * Pushes the data to Firebase using the provided token.
     *
     * @param token The token to use for authentication.
     * @return The result of the data push.
     */
    public String pushDataToFirebase(String token) {
        String res = null;
        try {
            RequestsHandler requestsHandler = new RequestsHandler(this);
            res = requestsHandler.execute(token, "short_term").get();
            Toast.makeText(getApplicationContext(), res, Toast.LENGTH_LONG).show();
            System.out.println(token);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * Formats a list of dates from "_"-formatted to "/"-formatted.
     *
     * @param dates The list of dates to format.
     * @return The formatted list of dates.
     */
    public static List<String> formatDates(List<String> dates) {  // --> from _ to / list of dates
        List<String> formattedDates = new ArrayList<>();
        SimpleDateFormat inputFormat = new SimpleDateFormat("dd_MM_yyyy", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        for (String date : dates) {
            try {
                Date inputDate = inputFormat.parse(date);
                String formattedDate = outputFormat.format(inputDate);
                formattedDates.add(formattedDate);
            } catch (ParseException e) {
                // Handle the exception if the date parsing fails
                e.printStackTrace();
                // Alternatively, you can skip the invalid date and continue with the loop
                // continue;
            }
        }

        return formattedDates;
    }

    /**
     * Formats a date from "/"-formatted to "_"-formatted.
     *
     * @param date The date to format.
     * @return The formatted date.
     */
    public static String formatDate(String date) { // --> From / to _ one date
        String formattedDate = null;
        SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd_MM_yyyy", Locale.getDefault());

        try {
            Date inputDate = inputFormat.parse(date);
            formattedDate = outputFormat.format(inputDate);
        } catch (ParseException e) {
            // Handle the exception if the date parsing fails
            e.printStackTrace();
            // Alternatively, you can return null or an error message indicating the parsing failure
        }

        return formattedDate;
    }

    /**
     * Filters a list of dates to include only dates with at least one week between them.
     *
     * @param dates The list of dates to filter.
     * @return The filtered list of dates.
     */
    public List<String> filterDates(List<String> dates) {
        // Convert the dates from string to Date objects
        List<Date> parsedDates = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd_MM_yyyy");
        for (String dateString : dates) {
            try {
                Date date = dateFormat.parse(dateString);
                parsedDates.add(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        // Sort the dates in ascending order using a custom comparator
        Collections.sort(parsedDates, new Comparator<Date>() {
            @Override
            public int compare(Date date1, Date date2) {
                return date1.compareTo(date2);
            }
        });

        // Filter the dates with at least one week between them
        List<String> filteredDates = new ArrayList<>();
        if (parsedDates.size() > 0) {
            Date previousDate = parsedDates.get(0);
            filteredDates.add(dateFormat.format(previousDate));
            for (int i = 1; i < parsedDates.size(); i++) {
                Date currentDate = parsedDates.get(i);
                long differenceInMilliseconds = currentDate.getTime() - previousDate.getTime();
                long differenceInDays = differenceInMilliseconds / (24 * 60 * 60 * 1000);
                if (differenceInDays >= 7) {
                    filteredDates.add(dateFormat.format(currentDate));
                    previousDate = currentDate;
                }
            }
        }
        System.out.println(filteredDates);
        return formatDates(filteredDates);
    }

    /**
     * Sets up the spinner with the provided list of dates.
     *
     * @param dates The list of dates to populate the spinner with.
     */
    public void putInSpinner(List<String> dates) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date currentDate = new Date();
            dates.add(0, dateFormat.format(currentDate));

        // Create an ArrayAdapter for the spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, dates);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dateSpinner.setAdapter(adapter);

        // Set a listener for date selection
        dateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected date
                selectedDate = dates.get(position);

                // Call your custom function whenever a different date is selected
                onDateSelected(selectedDate);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    /**
     * Handles the selection of a date from the spinner.
     *
     * @param date The selected date.
     */
    private void onDateSelected(String date) {
        // Do something with the selected date
        Toast.makeText(this, "Selected Date: " + date, Toast.LENGTH_SHORT).show();
        sp = getSharedPreferences("details", MODE_PRIVATE);
        String userId = sp.getString("user_id", "NO USER ID");
        updateGrids(userId, formatDate(date)); // date is now formatted as DD_MM_YYYY to access database

    }

    /**
     * Updates the grid views with data for the specified user ID and date.
     *
     * @param userId The user ID.
     * @param date   The date.
     */
    private void updateGrids(String userId, String date) {
        CompletableFuture<List<String>[]> futureData = getDataByDate(userId, date);
        futureData.thenAccept(result -> {
            result = cutLists(result);
            List<String> artistsList = formatList(result[1]);
            List<String> songsList = formatList(result[0]);
            // Process the retrieved artistsList and songsList
            // Print the lists
            System.out.println("Artists: " + artistsList);
            System.out.println("Songs: " + songsList);

            // Set up GridView 1
            adapter1 = new ArrayAdapter<>(this, R.layout.custom_grid_item, artistsList);
            gridView1.setAdapter(adapter1);

            // Set up GridView 2
            adapter2 = new ArrayAdapter<>(this, R.layout.custom_grid_item, songsList);
            gridView2.setAdapter(adapter2);

        }).exceptionally(ex -> {
            // Handle exception if there was an error retrieving data
            ex.printStackTrace();
            return null;
        });
    }

    public void updateGridsBasicMode(String token, String userId, String timeRange) throws JSONException {

        System.out.println("TIME RANGE IS " + timeRange);

        if (timeRange.equals("Last 3 months")) {
            System.out.println("SHORT TERM SELECTED?");
            timeRange = "short_term";
        }

        else if (timeRange.equals("Last 6 months")) {
            timeRange = "medium_term";
        }

        else {
            timeRange = "long_term";
        }
        RequestsHandler requestsHandler = new RequestsHandler(this);
        List<String> topArtists = requestsHandler.getTopArtists(token, timeRange);
        List<String> topSongs = requestsHandler.getTopTracks(token, timeRange);

        topArtists = topArtists.subList(0, 5);
        topSongs = topSongs.subList(0, 5);
        topArtists = formatList(topArtists);
        topSongs = formatList(topSongs);

        // Set up GridView 1
        adapter1 = new ArrayAdapter<>(this, R.layout.custom_grid_item, topArtists);
        gridView1.setAdapter(adapter1);

        // Set up GridView 2
        adapter2 = new ArrayAdapter<>(this, R.layout.custom_grid_item, topSongs);
        gridView2.setAdapter(adapter2);


    }



    /**
     * Returns the current date formatted as "dd_MM_yyyy".
     *
     * @return The current date.
     */
    public static String getCurrentDate() {
        Date currentDate = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd_MM_yyyy");
        String formattedDate = formatter.format(currentDate);
        return formattedDate;
    }

    /**
     * Retrieves data from the database for the specified user ID and date.
     *
     * @param userId The user ID.
     * @param date   The date.
     * @return A CompletableFuture with the retrieved data.
     */
    public CompletableFuture<List<String>[]> getDataByDate(String userId, String date) {
        CompletableFuture<List<String>[]> futureResult = new CompletableFuture<>();

        final List<String> artistsList = new ArrayList<>();
        final List<String> songsList = new ArrayList<>();

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://statsify-ab389-default-rtdb.europe-west1.firebasedatabase.app/");
        System.out.println("USER ID " + userId);
        databaseReference = database.getReference("users/" + userId + "/stats");
        databaseReference.child(date).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    DataSnapshot artistsSnapshot = dataSnapshot.child("artists");
                    DataSnapshot songsSnapshot = dataSnapshot.child("songs");

                    for (DataSnapshot artist : artistsSnapshot.getChildren()) {
                        String artistName = artist.getValue(String.class);
                        artistsList.add(artistName);
                    }

                    for (DataSnapshot song : songsSnapshot.getChildren()) {
                        String songName = song.getValue(String.class);
                        songsList.add(songName);
                    }
                }

                List<String>[] resultArray = new ArrayList[2];
                resultArray[0] = artistsList;
                resultArray[1] = songsList;
                futureResult.complete(resultArray);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error if needed
                System.out.println("ERROR PULLING STATS FROM FIREBASE !");
                futureResult.completeExceptionally(databaseError.toException());
            }
        });

        return futureResult;
    }

    /**
     * Cuts the lists to contain only the first 5 elements.
     *
     * @param lists The lists to cut.
     * @return The cut lists.
     */
    public static List<String>[] cutLists(List<String>[] lists) {
        List<String>[] cutLists = new List[lists.length];

        for (int i = 0; i < lists.length; i++) {
            List<String> originalList = lists[i];
            List<String> cutList = new ArrayList<>();

            // Copy the first 5 elements from the original list
            for (int j = 0; j < Math.min(originalList.size(), 5); j++) {
                cutList.add(originalList.get(j));
            }

            cutLists[i] = cutList;
        }

        return cutLists;
    }

    /**
     * Formats a list by adding numbers and a dot in front of each element.
     *
     * @param inputList The input list.
     * @return The formatted list.
     */
    public List<String> formatList(List<String> inputList) {
        List<String> outputList = new ArrayList<>();

        for (int i = 0; i < inputList.size(); i++) {
            String rewrittenString = (i + 1) + ". " + inputList.get(i);
            outputList.add(rewrittenString);
        }

        return outputList;
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
        if (id == R.id.info) {
            Intent intent = new Intent(this, Info.class);
            startActivity(intent);
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_in);

        sp = getSharedPreferences("details", MODE_PRIVATE);
        String token = sp.getString("token", "NO TOKEN");
        String userId = sp.getString("user_id", "NO USER ID");
        Boolean switchState = sp.getBoolean("SWITCH_STATE", false);

        gridView1 = findViewById(R.id.gridView1);
        gridView2 = findViewById(R.id.gridView2);

        CompletableFuture<List<String>[]> futureData = getDataByDate(userId, getCurrentDate());
        futureData.thenAccept(result -> {
            result = cutLists(result);
            List<String> artistsList = formatList(result[1]);
            List<String> songsList = formatList(result[0]);
            // Process the retrieved artistsList and songsList
            // Print the lists
            System.out.println("Artists: " + artistsList);
            System.out.println("Songs: " + songsList);

            // Set up GridView 1
            adapter1 = new ArrayAdapter<>(this, R.layout.custom_grid_item, artistsList);
            gridView1.setAdapter(adapter1);

            // Set up GridView 2
            adapter2 = new ArrayAdapter<>(this, R.layout.custom_grid_item, songsList);
            gridView2.setAdapter(adapter2);

        }).exceptionally(ex -> {
            // Handle exception if there was an error retrieving data
            ex.printStackTrace();
            return null;
        });

        String res = pushDataToFirebase(token);
        TextView welcomeText = findViewById(R.id.welcome);
        dateSpinner = findViewById(R.id.dateSpinner);

        welcomeText.setText("Welcome " + res);

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://statsify-ab389-default-rtdb.europe-west1.firebasedatabase.app/");
        DatabaseReference usersRef = database.getReference("users");

        if (switchState == true) {

            // under development

            List<String> timePeriods = new ArrayList<>();
            timePeriods.add("Last 3 months");
            timePeriods.add("Last 6 months");
            timePeriods.add("All-time");

            // Create a custom ArrayAdapter
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.custom_spinner_item, timePeriods) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    TextView textView = (TextView) view.findViewById(android.R.id.text1);
                    textView.setTextColor(getResources().getColor(R.color.spinner_text_color));
                    return view;
                }

                @Override
                public View getDropDownView(int position, View convertView, ViewGroup parent) {
                    View view = super.getDropDownView(position, convertView, parent);
                    TextView textView = (TextView) view.findViewById(android.R.id.text1);
                    textView.setTextColor(getResources().getColor(R.color.spinner_text_color));
                    return view;
                }
            };

            // Create an ArrayAdapter for the spinner
            ArrayAdapter<String> adapter4 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, timePeriods);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            dateSpinner.setAdapter(adapter);

            // Set a listener for date selection
            dateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    // Get the selected date
                    selectedDate = timePeriods.get(position);

                    // Call your custom function whenever a different date is selected
                    Toast.makeText(getApplicationContext(), selectedDate ,Toast.LENGTH_SHORT).show();
                    sp = getSharedPreferences("details", MODE_PRIVATE);
                    String token = sp.getString("token", "NO TOKEN");
                    String userId = sp.getString("user_id", "NO USER ID");

                   try {
                        updateGridsBasicMode(token, userId, selectedDate);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //onDateSelected(selectedDate);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // Do nothing
                }
            });
            dateSpinner.setAdapter(adapter);

        }

        else {

            usersRef.child(userId).child("stats").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    List<String> dates = new ArrayList<>();
                    for (DataSnapshot dateSnapshot : dataSnapshot.getChildren()) {
                        String date = dateSnapshot.getKey();
                        dates.add(date);
                    }

                    // Process the list of dates here
                    System.out.println(dates);
                    List<String> filteredDates = filterDates(dates);

                    // Create a custom ArrayAdapter
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.custom_spinner_item, filteredDates) {
                        @Override
                        public View getView(int position, View convertView, ViewGroup parent) {
                            View view = super.getView(position, convertView, parent);
                            TextView textView = (TextView) view.findViewById(android.R.id.text1);
                            textView.setTextColor(getResources().getColor(R.color.spinner_text_color));
                            return view;
                        }

                        @Override
                        public View getDropDownView(int position, View convertView, ViewGroup parent) {
                            View view = super.getDropDownView(position, convertView, parent);
                            TextView textView = (TextView) view.findViewById(android.R.id.text1);
                            textView.setTextColor(getResources().getColor(R.color.spinner_text_color));
                            return view;
                        }
                    };

                    putInSpinner(filteredDates);
                    dateSpinner.setAdapter(adapter);
                }


                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle database error
                }
            });


        }


    }
}