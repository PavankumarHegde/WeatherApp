package com.pavankumarhegde.weatherapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import com.pavankumarhegde.weatherapp.adapters.LocationManagerAdapter;
import com.pavankumarhegde.weatherapp.model.Location;
import com.pavankumarhegde.weatherapp.model.LocationHandler;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;

public class LocationManagerActivity extends AppCompatActivity implements View.OnLongClickListener {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private TextView itemCount;

    LocationHandler locationHandler;
    SharedPreferences sharedpreferences;

    List<Location> locationList;
    LocationManagerAdapter adapter;

    public boolean isContextualOpen = false;
    int counter = 0;

    List<Location> selectedList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_manager);
        toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);

        itemCount = findViewById(R.id.itemCounter);
        recyclerView = findViewById(R.id.locationList);
        itemCount.setText("Manage Locations");
        sharedpreferences = getSharedPreferences("MyLocationData", Context.MODE_PRIVATE);

        selectedList = new ArrayList<>();

        populateList();

    }

    public void populateList(){
        Gson gson = new Gson();
        String json = sharedpreferences.getString("LOCATION_LIST", "");
        locationHandler = gson.fromJson(json, LocationHandler.class);

        try {
            locationList = locationHandler.getLocationList();
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            adapter = new LocationManagerAdapter(this, locationList);
            recyclerView.setAdapter(adapter);
        }catch(NullPointerException e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.normal_menu,menu);
        return true;
    }

    @Override
    public boolean onLongClick(View v){
        enableContextualMenu();
        return true;
    }

    public void enableContextualMenu(){
        isContextualOpen = true;
        toolbar.getMenu().clear();
        toolbar.inflateMenu(R.menu.contextual_menu);

        getSupportActionBar().setTitle("0 Items Selected");
        itemCount.setText("0 Items Selected");
        adapter.notifyDataSetChanged();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void MakeSelection(View v,int adapterPosition){
        if(((CheckBox)v).isChecked()){
            selectedList.add(locationList.get(adapterPosition));
            counter++;
        }
        else{
            selectedList.remove(locationList.get(adapterPosition));
            counter--;
        }
        itemCount.setText(counter + " Items Selected");
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.delete){
            removeItem();
            removeContextualMode();
        }else if(item.getItemId()== android.R.id.home){
            removeContextualMode();
            adapter.notifyDataSetChanged();
        }else if(item.getItemId()==R.id.add_location){
            startActivity(new Intent(this,SearchLocationActivity.class));
        }else if(item.getItemId()==R.id.edit_location){
            enableContextualMenu();
        }

        return true;
    }

    private void removeContextualMode() {
        isContextualOpen = false;
        itemCount.setText("Manage Locations");
        toolbar.getMenu().clear();
        toolbar.inflateMenu(R.menu.normal_menu);
        counter = 0;
        selectedList.clear();
        adapter.notifyDataSetChanged();
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    public void removeItem(){
        for(int i = 0; i < selectedList.size(); i++){
            locationList.remove(selectedList.get(i));
        }
        SharedPreferences.Editor editor = sharedpreferences.edit();
        locationHandler.setLocationList(locationList);
        Gson gson = new Gson();
        String json = gson.toJson(locationHandler);
        editor.putString("LOCATION_LIST", json);
        editor.apply();
    }


}
