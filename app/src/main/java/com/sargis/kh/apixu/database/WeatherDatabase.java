package com.sargis.kh.apixu.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.sargis.kh.apixu.database.dao.ItemDAO;
import com.sargis.kh.apixu.database.models.Item;

@Database(entities = {Item.class}, version = 1, exportSchema = false)
public abstract class WeatherDatabase extends RoomDatabase {
    public abstract ItemDAO getItemDAO();
}