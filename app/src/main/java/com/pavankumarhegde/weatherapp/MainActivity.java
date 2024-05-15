package com.pavankumarhegde.weatherapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.pavankumarhegde.weatherapp.adapters.DailyListAdapter;
import com.pavankumarhegde.weatherapp.adapters.HourlyListAdapter;
import com.pavankumarhegde.weatherapp.model.Current;
import com.pavankumarhegde.weatherapp.model.ForecastDay;
import com.pavankumarhegde.weatherapp.model.Hour;
import com.pavankumarhegde.weatherapp.model.Location;
import com.pavankumarhegde.weatherapp.model.LocationHandler;
import com.pavankumarhegde.weatherapp.model.WeatherData;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    /**
     * class attributes
     */
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;
    private boolean requestingLocationUpdates = true;


    TextView tvLocation, tvDate, tvMainTemp, tvMinMaxTemp, tvCondition, tvLastUpdate;
    TextView tvHumidity, tvPressure, tvVisibility, tvWindSpeed, tvWindDirection, tvUVIndex, tvPrecipitation;
    TextView tvSunrise, tvSunset, tvMoonset, tvMoonrise;
    TextView tvWillSnow, tvWillRain, tvChanceRain, tvChanceSnow;
    ImageView imgMain;
    ImageButton btnLeft, btnRight, btnSetting;
    CoordinatorLayout drawerLayout;

    WeatherData weatherData;
    List<Location> list;
    Location loc;

    RecyclerView recyclerView, recyclerView2;
    RecyclerView.LayoutManager layoutManager, layoutManager2;
    RecyclerView.Adapter adapter, adapter2;

    SharedPreferences sharedpreferences;
    LocationHandler locationHandler;

    String UNIT;
    SharedPreferences settings;

    int size;
    //endregion

    @SuppressLint({"WrongConstant", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        this.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.actionbar_layout);






        Gson gson = new Gson();

        //get settings preferences
        settings = PreferenceManager.getDefaultSharedPreferences(this);
        UNIT = settings.getString("UNIT", "C°");

        //get location list
        sharedpreferences = getSharedPreferences("MyLocationData", Context.MODE_PRIVATE);
        String json = sharedpreferences.getString("LOCATION_LIST", "");

        //region======================== variable initializing =====================================
        btnSetting = findViewById(R.id.btnSetting);

        //=================== main area elements initializing ============================
        tvLocation = findViewById(R.id.tvLocation);
        tvDate = findViewById(R.id.tvDate);
        tvCondition = findViewById(R.id.tvCondition);
        tvMainTemp = findViewById(R.id.tvMainTemp);
        tvMinMaxTemp = findViewById(R.id.tvMinMaxTemp);
        imgMain = findViewById(R.id.imgMain);
        tvLastUpdate = findViewById(R.id.tvLastupdated);

        //=================== details card element initializing =========================
        tvHumidity = findViewById(R.id.tvMainHumidity);
        tvPressure = findViewById(R.id.tvPressure);
        tvPrecipitation = findViewById(R.id.tvPrecipitation);
        tvWindDirection = findViewById(R.id.tvWindDirection);
        tvWindSpeed = findViewById(R.id.tvWindSpeed);
        tvVisibility = findViewById(R.id.tvVisibility);
        tvUVIndex = findViewById(R.id.tvUVIndex);

        //================== astrology card element initializing ========================
        tvSunrise = findViewById(R.id.tvSunrise);
        tvSunset = findViewById(R.id.tvSunset);
        tvMoonrise = findViewById(R.id.tvMoonrise);
        tvMoonset = findViewById(R.id.tvMoonset);

        //================== tips card element initializing =============================
        tvChanceRain = findViewById(R.id.tvChanceRain);
        tvChanceSnow = findViewById(R.id.tvChanceSnow);
        tvWillRain = findViewById(R.id.tvWillRain);
        tvWillSnow = findViewById(R.id.tvWillSnow);

        //================== left/right location change btn initialize ==================
        btnLeft = findViewById(R.id.imgBtnLeft);
        btnRight = findViewById(R.id.imgBtnRight);

        drawerLayout = findViewById(R.id.coordinator_layout);

        //endregion=========

        locationHandler = gson.fromJson(json, LocationHandler.class);

        //assign location list to list
        final int[] position = {0};
        try {
            list = locationHandler.getLocationList();
            if (list.size() == 1 || list.size() == 0)
                btnRight.setVisibility(View.GONE);
            size = list.size();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        //region======================== left/right buttons click listeners ========================
        btnLeft.setVisibility(View.GONE);

        //button right
        btnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                position[0]++;
                if (position[0] < size) {
                    loc = list.get(position[0]);
                    setAppData(loc.getLat(), loc.getLon());
                    btnLeft.setVisibility(View.VISIBLE);
                }
                if (position[0] == size - 1) {
                    btnRight.setVisibility(View.GONE);
                }
            }
        });

        //button left
        btnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                position[0]--;
                if (position[0] >= 0) {
                    loc = list.get(position[0]);
                    setAppData(loc.getLat(), loc.getLon());
                    if (size > 1) {
                        btnRight.setVisibility(View.VISIBLE);
                    }
                }
                if (position[0] == 0) {
                    btnLeft.setVisibility(View.GONE);
                }

            }
        });
        //endregion===

        //get first location from the list
        try {
            loc = list.get(0);
        } catch (IndexOutOfBoundsException | NullPointerException e) {
            e.printStackTrace();
        }

        //if a location available set data
        if (loc != null) {
            setAppData(loc.getLat(), loc.getLon());
            SharedPreferences.Editor editor = sharedpreferences.edit();
            Gson gson1 = new Gson();
            String json1 = gson1.toJson(weatherData);
            editor.putString("WEATHER_DATA", json1);
            editor.apply();
            System.out.println(gson1.toJson(loc));
        } else {
            LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            android.location.Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();
            setAppData(latitude,longitude);//if no location set static location data
        }

        //settings button click listeners
        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent intent = new Intent(MainActivity.this,SettingsActivity.class);
               startActivity(intent);
            }
        });

    }

    /**
     * set main UI data method
     */
    public void setAppData(double lat,double lon){

        //check internet connection
        if (isNetworkAvailable()){
            weatherData = WeatherAPI.getData(lat,lon);
        }else{
            //if no internet connection get previously saved data
            Toast.makeText(this, "Network unavailable", Toast.LENGTH_SHORT).show();
            Gson gson = new Gson();
            String json = sharedpreferences.getString("WEATHER_DATA", "");
            weatherData = gson.fromJson(json, WeatherData.class);
        }

        if(weatherData!=null) {
            //---------------- card-1 hourly adapter -------------------------------
            ForecastDay day[] = weatherData.getForecast().getForecastday();

            Hour hour[] = day[0].getHour();
            List<Hour> hoursList = Arrays.asList(hour);

            recyclerView = findViewById(R.id.recyclerView);
            recyclerView.setHasFixedSize(true);
            layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            recyclerView.setLayoutManager(layoutManager);
            adapter = new HourlyListAdapter(MainActivity.this, hoursList);
            recyclerView.setAdapter(adapter);

            //---------------- card-2 daily adapter ---------------------------------
            List<ForecastDay> forecastDayList = Arrays.asList(day);

            recyclerView2 = findViewById(R.id.recyclerView1);
            recyclerView2.setHasFixedSize(true);
            layoutManager2 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            recyclerView2.setLayoutManager(layoutManager2);
            adapter2 = new DailyListAdapter(MainActivity.this, forecastDayList);
            recyclerView2.setAdapter(adapter2);

            //----------------- main top area ----------------------------------
            Current current = weatherData.getCurrent();
            Location location = weatherData.getLocation();

            tvLocation.setText(location.getName());
            tvCondition.setText(current.getCondition().getText());
            Picasso.get().load("http:" + current.getCondition().getIcon()).into(imgMain);

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date mDate = null;
            Date lDate = null;
            try {
                mDate = format.parse(location.getLocaltime());
                lDate = format.parse(current.getLast_updated());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            format = new SimpleDateFormat("EEE, dd MMMM ");
            String date1 = format.format(mDate);
            tvDate.setText(date1);

            format = new SimpleDateFormat("dd/MM HH:mm");
            String date2 = format.format(lDate);
            tvLastUpdate.setText("Updated " + date2);

            if (UNIT.equals("C°")) {
                tvMainTemp.setText((int) current.getTemp_c() + "°");
                tvMinMaxTemp.setText((int) day[0].getDay().getMintemp_c() + "°/" + (int) day[0].getDay().getMaxtemp_c() + "° Feels like " + (int) current.getFeelslike_c() + "°");
            } else if (UNIT.equals("F°")) {
                tvMainTemp.setText((int) current.getTemp_f() + "°");
                tvMinMaxTemp.setText((int) day[0].getDay().getMintemp_f() + "°/" + (int) day[0].getDay().getMaxtemp_f() + "° Feels like " + (int) current.getFeelslike_f() + "°");
            }

            //-------------------------- card-3 details -------------------------------------
            tvHumidity.setText(current.getHumidity() + "%");
            tvPressure.setText(current.getPressure_mb() + " mb");
            tvWindSpeed.setText(current.getWind_kph() + " kph");
            tvWindDirection.setText(current.getWind_dir());
            tvPrecipitation.setText(current.getPrecip_mm() + " mm");
            tvVisibility.setText(current.getVis_km() + " km");
            tvUVIndex.setText(String.valueOf(current.getUv()));

            //-------------------------- card-4 astrology ------------------------------------
            tvSunrise.setText(day[0].getAstro().getSunrise());
            tvSunset.setText(day[0].getAstro().getSunset());
            tvMoonrise.setText(day[0].getAstro().getMoonrise());
            tvMoonset.setText(day[0].getAstro().getMoonset());

            //-------------------------- card-5 tips -----------------------------------------
            String rain = "No";
            String snow = "No";
            if (day[0].getDay().getDaily_will_it_rain() == 1)
                rain = "Yes";
            if (day[0].getDay().getDaily_will_it_snow() == 1)
                snow = "Yes";
            tvWillRain.setText(rain);
            tvWillSnow.setText(snow);
            tvChanceRain.setText(day[0].getDay().getDaily_chance_of_rain() + "%");
            tvChanceSnow.setText(day[0].getDay().getDaily_chance_of_snow() + "%");

        }else{
            finish();
        }

    }


    /**
     * create option menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_right_menu,menu);
        return true;
    }

    /**
     * option selection handler
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.btnAdd) {
            Intent intent = new Intent(this, SearchLocationActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * check for internet connection
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


}
