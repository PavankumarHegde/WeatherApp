package com.pavankumarhegde.weatherapp;

import com.pavankumarhegde.weatherapp.model.WeatherData;
import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WeatherAPI {


    public static final String api_key="d31b6fd3a5f840e3aa3142528200108";//weather api key
    //public static final String api_key="86b0d39444a9e3b98a76995d2c636bb1";//weather api key

    public static final int no_days = 3;// no of days

    public static WeatherData weatherData;

    public static WeatherData getData(double lat,double lon) {
        final CountDownLatch countDownLatch = new CountDownLatch(1);

        OkHttpClient client = new OkHttpClient();

        String url = "http://api.weatherapi.com/v1/forecast.json?key="+ api_key +"&q=" + lat + "," + lon + "&days="+no_days;

       // String url = "https://api.openweathermap.org/data/3.0/onecall?appid="+ api_key +"&lat=" + lat + "&lon=" + lon + "&exclude=hourly,daily";
        //https://api.openweathermap.org/data/3.0/onecall?lat={lat}&lon={lon}&exclude={part}&appid={API key}

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                countDownLatch.countDown();
            }


            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String json = response.body().string();
                    Gson gson = new Gson();
                    weatherData = gson.fromJson(json, WeatherData.class);
                    countDownLatch.countDown();
                }
            }
        });

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return weatherData;
    }


}


