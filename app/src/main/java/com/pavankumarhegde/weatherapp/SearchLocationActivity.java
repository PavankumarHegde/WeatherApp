package com.pavankumarhegde.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;


import com.pavankumarhegde.weatherapp.adapters.SearchLocationListAdapter;
import com.pavankumarhegde.weatherapp.model.Location;
import com.pavankumarhegde.weatherapp.model.LocationHandler;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class SearchLocationActivity extends AppCompatActivity {

    SearchView searchView;
    ListView listView;
    JSONArray data;
    ArrayList<Location> list = new ArrayList<>();
    SharedPreferences sharedpreferences;
    LocationHandler locationHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_search);

        sharedpreferences = getSharedPreferences("MyLocationData", Context.MODE_PRIVATE);
        searchView = findViewById(R.id.searchView);
        listView = findViewById(R.id.listview);

        Gson gson = new Gson();
        String json = sharedpreferences.getString("LOCATION_LIST", "");
        locationHandler = gson.fromJson(json, LocationHandler.class);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(!list.isEmpty())
                    list.clear();
                getJSON(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(!list.isEmpty())
                    list.clear();
                getJSON(newText);
                return false;
            }
        });


    }

    @SuppressLint("StaticFieldLeak")
    public void getJSON(final String city) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

            }

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    URL url = new URL("http://api.weatherapi.com/v1/search.json?key=d31b6fd3a5f840e3aa3142528200108&q="+city);

                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                    StringBuffer json = new StringBuffer(1024);
                    String tmp;

                    while ((tmp = reader.readLine()) != null)
                        json.append(tmp).append("\n");
                    reader.close();

                    data = new JSONArray(json.toString());

                } catch (Exception e) {
                    System.out.println("Exception " + e.getMessage());
                    return null;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void Void) {
                if (data != null) {

                    try {

                        for(int i = 0;i < data.length(); i++){
                            JSONObject obj = data.getJSONObject(i);
                            Location loc = new Location();
                            loc.setLon(obj.getDouble("lon"));
                            loc.setLat(obj.getDouble("lat"));
                            loc.setName(obj.getString("name"));
                            loc.setRegion(obj.getString("region"));
                            loc.setCountry(obj.getString("country"));
                            list.add(loc);
                        }

                        final SearchLocationListAdapter adapter = new SearchLocationListAdapter(SearchLocationActivity.this,R.layout.listview_item,list);
                        listView.setAdapter(adapter);
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Location loc = (Location) adapter.getItem(position);

                                if(locationHandler==null)
                                    locationHandler = new LocationHandler();

                                locationHandler.addLocation(loc);
                                SharedPreferences.Editor editor = sharedpreferences.edit();
                                Gson gson = new Gson();
                                String json = gson.toJson(locationHandler);
                                editor.putString("LOCATION_LIST", json);
                                editor.apply();

                                Intent intent = new Intent(SearchLocationActivity.this,MainActivity.class);

                                startActivity(intent);
                            }
                        });

                    }catch(Exception e){
                        e.printStackTrace();
                    }

                }
            }
        }.execute();

    }
}
