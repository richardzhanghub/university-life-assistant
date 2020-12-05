package com.cs446.group18.timetracker.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.cs446.group18.timetracker.entity.Geolocation;

import java.util.List;

@Dao
public interface GeolocationDao {
    @Query("SELECT * FROM geolocation_table")
    List<Geolocation> getGeolocations();

    @Query("SELECT * FROM geolocation_table WHERE geolocation_id = :geolocation_id")
    Geolocation getGeolocationById(long geolocation_id);

    @Insert
    void insert(Geolocation geolocation);
}
