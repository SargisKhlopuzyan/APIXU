package com.sargis.kh.apixu.favorite_weather.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.sargis.kh.apixu.favorite_weather.database.dao.ItemDAO;
import com.sargis.kh.apixu.favorite_weather.database.models.Item;

@Database(entities = {Item.class}, version = 1, exportSchema = false)
public abstract class FavoriteWeatherDatabase extends RoomDatabase {
    public abstract ItemDAO getItemDAO();
}