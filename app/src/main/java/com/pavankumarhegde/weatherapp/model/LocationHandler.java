package com.pavankumarhegde.weatherapp.model;

import java.util.ArrayList;
import java.util.List;

public class LocationHandler {
    public List<Location> locationList = new ArrayList<>();

    public LocationHandler() { }

    public void addLocation(Location loc){
        locationList.add(0,loc);
    }

    public void setLocationList(List<Location> list){
        this.locationList = list;
    }

    public List<Location> getLocationList(){
        return locationList;
    }


}
