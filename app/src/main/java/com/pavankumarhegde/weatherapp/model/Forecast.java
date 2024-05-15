package com.pavankumarhegde.weatherapp.model;

public class Forecast {
    private ForecastDay forecastday[];

    public ForecastDay[] getForecastday() {
        return forecastday;
    }

    public void setForecastday(ForecastDay[] forecastday) {
        this.forecastday = forecastday;
    }
}
