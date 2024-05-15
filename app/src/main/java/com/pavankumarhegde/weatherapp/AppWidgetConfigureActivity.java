package com.pavankumarhegde.weatherapp;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.pavankumarhegde.weatherapp.adapters.WidgetLocationListAdapter;
import com.pavankumarhegde.weatherapp.model.Location;
import com.pavankumarhegde.weatherapp.model.LocationHandler;
import com.google.gson.Gson;

import java.util.List;

/**
 * The configuration screen for the {@link AppWidget AppWidget} AppWidget.
 */
public class AppWidgetConfigureActivity extends Activity {

    private static final String PREFS_NAME = "com.pavankumarhegde.weatherapp.AppWidget";
    private static final String PREF_PREFIX_KEY = "appwidget_";
    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    RecyclerView recyclerView;
    LocationHandler locationHandler;
    SharedPreferences sharedpreferences;

    List<Location> locationList;
    WidgetLocationListAdapter adapter;

    String LOCATION;


    public AppWidgetConfigureActivity() {
        super();
    }

    // Write the prefix to the SharedPreferences object for this widget
    static void saveTitlePref(Context context, int appWidgetId, String text) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(PREF_PREFIX_KEY + appWidgetId, text);
        prefs.apply();
    }

    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    static String loadTitlePref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String titleValue = prefs.getString(PREF_PREFIX_KEY + appWidgetId, null);
        if (titleValue != null) {
            return titleValue;
        } else {
            return "{\"country\":\"India\",\"lat\":14.0045519,\"lon\":74.558034,\"name\":\"Honavar, Karnataka, India\",\"region\":\"India\"}";
        }
    }

    static void deleteTitlePref(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_PREFIX_KEY + appWidgetId);
        prefs.apply();
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);

        setContentView(R.layout.app_widget_configure);

        recyclerView = findViewById(R.id.widget_conf_listView);


        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }

        sharedpreferences = getSharedPreferences("MyLocationData", Context.MODE_PRIVATE);

        Gson gson = new Gson();
        String json = sharedpreferences.getString("LOCATION_LIST", "");
        locationHandler = gson.fromJson(json, LocationHandler.class);

        try {
            locationList = locationHandler.getLocationList();
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

            adapter = new WidgetLocationListAdapter(this, locationList);
            recyclerView.setAdapter(adapter);

        }catch(NullPointerException e){
            e.printStackTrace();
        }

    }

    public void MakeSelection(View view,int position){

        final Context context = AppWidgetConfigureActivity.this;

        Gson gson = new Gson();
        LOCATION = gson.toJson(locationList.get(position));
        saveTitlePref(context,mAppWidgetId,LOCATION);

        // It is the responsibility of the configuration activity to update the app widget
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        AppWidget.updateAppWidget(context, appWidgetManager, mAppWidgetId);

        // Make sure we pass back the original appWidgetId
        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        setResult(RESULT_OK, resultValue);
        finish();
    }
}

