package com.paad.earthquake;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class Earthquake extends Activity {
    static final private int QUAKE_DIALOG = 1;
    static final private int MENU_UPDATE = Menu.FIRST;
    ListView earthquakeListView;
    ArrayAdapter<Quake> aa;
    ArrayList<Quake> earthquakes = new ArrayList<Quake>();

    // Override the onCreate method to store an ArrayList
    // of Quake objects and bind that to the ListView
    // using an ArrayAdapter
    Quake selectedQuake;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        earthquakeListView = (ListView) this.findViewById(
                R.id.earthquakeListView);

        earthquakeListView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView _av, View _v,
                                            int _index, long arg3) {
                        selectedQuake = earthquakes.get(_index);
                        showDialog(QUAKE_DIALOG);
                    }
                });

        int layoutID = android.R.layout.simple_list_item_1;
        aa = new ArrayAdapter<Quake>(this, layoutID, earthquakes);
        earthquakeListView.setAdapter(aa);

        refreshEarthquakes();
    }

    private void refreshEarthquakes() {
        // Get the JSON url
        try {

            Log.d("Earthquake Logger", "Start refreshEarthquakes ");

            // Step 1: connect to the earthquake research center JSON url

            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet("http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/2.5_day.geojson");
            HttpResponse response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            // Step 2: convert the entity to object
            Object content = EntityUtils.toString(entity);

            //Step 3: convert to JSONObject
            JSONObject jsonObj = new JSONObject(content.toString());
            JSONArray jsonArray = jsonObj.getJSONArray("features");

            //Step 4: start fetching required tags from JSONObject
            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject featureObj = jsonArray.getJSONObject(i);
                JSONObject propertyObj = featureObj.getJSONObject("properties");
                String details = propertyObj.getString("title");
                String time = propertyObj.getString("time");
                String link = propertyObj.getString("url");

                JSONObject geometryObj = featureObj.getJSONObject("geometry");
                JSONArray coordiArray = geometryObj.getJSONArray("coordinates");


                long parsetime = Long.parseLong(time);
                Date qdate = new Date(parsetime);

                Location l = new Location("dummyGPS");
                l.setLatitude(Double.parseDouble(coordiArray.getString(0)));
                l.setLongitude(Double.parseDouble(coordiArray.getString(1)));

                String magnitudeString = details.split(" ")[1];
                int end = magnitudeString.length() - 1;
                double magnitude = Double.parseDouble(
                        magnitudeString.substring(0, end));

                if (details.contains(",")) {
                    details = details.split(",")[1].trim();
                } else {
                    details = details.split("-")[1].trim();
                }

                // Step 4.2: Create a new Quake object
                Quake quake = new Quake(qdate, details, l,
                        magnitude, link);
                // Step 4.3: Add the newly found earthquake
                addNewQuake(quake);
            }

            Log.d("Earthquake Logger", "Complete refreshEarthquakes");

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            Log.d("Earthquake Logger", "Reached Finally");
        }

    }

    // Update the addNewQuake method so that it takes each newly
    // processed quake and adds it to the earthquake Array List.
    // It should also notify the Array Adapter that the underlying
    // data has changed.
    private void addNewQuake(Quake _quake) {
        // Add the new quake to our list of earthquakes.
        earthquakes.add(_quake);

        // Notify the array adapter of a change.
        aa.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        menu.add(0, MENU_UPDATE, Menu.NONE, R.string.menu_update);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case (MENU_UPDATE): {
                refreshEarthquakes();
                return true;
            }
        }
        return false;
    }

    @Override
    public Dialog onCreateDialog(int id) {
        switch (id) {
            case (QUAKE_DIALOG):
                LayoutInflater li = LayoutInflater.from(this);
                View quakeDetailsView = li.inflate(R.layout.quake_details,
                        null);

                AlertDialog.Builder quakeDialog = new AlertDialog.Builder(this);
                quakeDialog.setTitle("Quake Time");
                quakeDialog.setView(quakeDetailsView);
                return quakeDialog.create();
        }
        return null;
    }

    @Override
    public void onPrepareDialog(int id, Dialog dialog) {
        switch (id) {
            case (QUAKE_DIALOG):
                SimpleDateFormat sdf = new SimpleDateFormat(
                        "dd/MM/yyyy HH:mm:ss");
                String dateString = sdf.format(selectedQuake.getDate());
                String quakeText = "Magnitude " + selectedQuake.getMagnitude() +
                        "\n" + selectedQuake.getDetails() + "\n" +
                        selectedQuake.getLink();

                AlertDialog quakeDialog = (AlertDialog) dialog;
                quakeDialog.setTitle(dateString);
                TextView tv = (TextView) quakeDialog.findViewById(
                        R.id.quakeDetailsTextView);
                tv.setText(quakeText);

                break;
        }
    }


}