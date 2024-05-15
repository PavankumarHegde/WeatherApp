package com.pavankumarhegde.weatherapp.model;

public class ForecastDay {
    private String date;
    private Day day;
    private Hour hour[];
    private Astro astro;

    public ForecastDay(String date, Day day, Astro astro) {
        this.date = date;
        this.day = day;
        this.astro = astro;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Day getDay() {
        return day;
    }

    public void setDay(Day day) {
        this.day = day;
    }

    public Astro getAstro() {
        return astro;
    }

    public void setAstro(Astro astro) {
        this.astro = astro;
    }

    public Hour[] getHour() {
        return hour;
    }

    public void setHour(Hour[] hour) {
        this.hour = hour;
    }
}
