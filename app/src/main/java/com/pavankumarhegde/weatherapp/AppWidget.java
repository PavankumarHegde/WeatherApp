package com.pavankumarhegde.weatherapp;


import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.RemoteViews;
import android.widget.Toast;
import androidx.preference.PreferenceManager;
import com.pavankumarhegde.weatherapp.model.ForecastDay;
import com.pavankumarhegde.weatherapp.model.Location;
import com.pavankumarhegde.weatherapp.model.WeatherData;
import com.google.gson.Gson;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link AppWidgetConfigureActivity AppWidget3ConfigureActivity}
 */
public class AppWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {

        String code = AppWidgetConfigureActivity.loadTitlePref(context,appWidgetId);

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.app_widget3);

        SharedPreferences sharedpreferences = context.getSharedPreferences("MyLocationData", Context.MODE_PRIVATE);
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        String UNIT = settings.getString("UNIT","C°");

        Gson gson = new Gson();
        Location location = gson.fromJson(code, Location.class);

        WeatherData weatherData = null;

        if (isNetworkAvailable(context)) {
            weatherData = WeatherAPI.getData(location.getLat(), location.getLon());
            SharedPreferences.Editor editor = sharedpreferences.edit();

            Date date = new Date(System.currentTimeMillis());;
            SimpleDateFormat format = new SimpleDateFormat("dd/MM HH:mm");
            String date_text = format.format(date);

            weatherData.getCurrent().setLast_updated(date_text);
            String json = gson.toJson(weatherData);
            editor.putString("WIDGET3_WEATHER_DATA", json);
            editor.apply();
        }else {
            Toast.makeText(context, "Network unavailable", Toast.LENGTH_SHORT).show();
            String json = sharedpreferences.getString("WIDGET3_WEATHER_DATA", "");
            weatherData = gson.fromJson(json, WeatherData.class);
        }



        if (weatherData!=null) {
            ForecastDay forecastDay[] = weatherData.getForecast().getForecastday();

            if(UNIT.equals("C°")) {
                views.setTextViewText(R.id.widget3_main_temp, String.valueOf((int) weatherData.getCurrent().getTemp_c()));
                views.setTextViewText(R.id.widget3_temp_desc, (int) forecastDay[0].getDay().getMintemp_c() + "°/" + (int) forecastDay[0].getDay().getMaxtemp_c() + "° Feels like " + (int) weatherData.getCurrent().getFeelslike_c() + "°");
            }else if(UNIT.equals("F°")){
                views.setTextViewText(R.id.widget3_main_temp, String.valueOf((int) weatherData.getCurrent().getTemp_f()));
                views.setTextViewText(R.id.widget3_temp_desc, (int) forecastDay[0].getDay().getMintemp_f() + "°/" + (int) forecastDay[0].getDay().getMaxtemp_f() + "° Feels like " + (int) weatherData.getCurrent().getFeelslike_f() + "°");
            }


            views.setTextViewText(R.id.widget3_location, String.valueOf(weatherData.getLocation().getName()));

            views.setTextViewText(R.id.widget3_last_update, "Updated " + weatherData.getCurrent().getLast_updated());

            if (isNetworkAvailable(context)) {
                final String url_main = "http:" + weatherData.getCurrent().getCondition().getIcon();
                final String url_1 = "http:" + forecastDay[0].getDay().getCondition().getIcon();
                final String url_2 = "http:" + forecastDay[1].getDay().getCondition().getIcon();
                final String url_3 = "http:" + forecastDay[2].getDay().getCondition().getIcon();

                final ImageDownloader downloader = new ImageDownloader();
                final ImageDownloader downloader1 = new ImageDownloader();
                final ImageDownloader downloader2 = new ImageDownloader();
                final ImageDownloader downloader3 = new ImageDownloader();
                try {
                    downloader.execute(url_main).get();
                    downloader1.execute(url_1).get();
                    downloader2.execute(url_2).get();
                    downloader3.execute(url_3).get();
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }

                Bitmap bitmap_main = downloader.getBmImg();
                Bitmap bitmap_1 = downloader1.getBmImg();
                Bitmap bitmap_2 = downloader2.getBmImg();
                Bitmap bitmap_3 = downloader3.getBmImg();

                views.setImageViewBitmap(R.id.widget3_main_img, bitmap_main);
                views.setImageViewBitmap(R.id.widget3_1_img, bitmap_1);
                views.setImageViewBitmap(R.id.widget3_2_img, bitmap_2);
                views.setImageViewBitmap(R.id.widget3_3_img, bitmap_3);

            }

            views.setString(R.id.widget3_date, "setTimeZone", String.valueOf(TimeZone.getDefault().getID()));

            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
            Date day1 = null;
            Date day2 = null;
            Date day3 = null;
            try {
                day1 = format1.parse(forecastDay[0].getDate());
                day2 = format1.parse(forecastDay[1].getDate());
                day3 = format1.parse(forecastDay[2].getDate());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            format1 = new SimpleDateFormat("EEE");
            views.setTextViewText(R.id.widget3_1_day, format1.format(day1));
            views.setTextViewText(R.id.widget3_2_day, format1.format(day2));
            views.setTextViewText(R.id.widget3_3_day, format1.format(day3));
        }else{
            views.setTextViewText(R.id.widget3_last_update,"Network Error");
            Toast.makeText(context, "Network Error", Toast.LENGTH_SHORT).show();
        }

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        for (int appWidgetId : appWidgetIds) {
            AppWidgetConfigureActivity.deleteTitlePref(context, appWidgetId);
        }
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}

