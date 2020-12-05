package com.cs446.group18.timetracker.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.cs446.group18.timetracker.entity.Event;

import java.util.List;

@Dao
public interface EventDao {
    @Query("SELECT * FROM event_table")
    LiveData<List<Event>> getEvents();

    @Query("SELECT * FROM event_table WHERE event_id = :event_id")
    Event getEventById(long event_id);

    @Insert
    void insert(Event event);

    @Delete
    void delete(Event event);

    @Update
    void update(Event event);

    @Query("DELETE FROM event_table")
    void deleteAllEvents();
}
