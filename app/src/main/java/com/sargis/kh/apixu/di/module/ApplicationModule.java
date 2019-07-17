package com.sargis.kh.apixu.di.module;

import android.app.Application;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.sargis.kh.apixu.di.ApplicationContext;
import com.sargis.kh.apixu.favorite_weather.database.FavoriteWeatherDatabase;
import com.sargis.kh.apixu.favorite_weather.database.dao.ItemDAO;

import dagger.Module;
import dagger.Provides;

@Module
public class ApplicationModule {

    private final Application application;

    public ApplicationModule(Application application) {
        this.application = application;
    }

    @Provides
    @ApplicationContext
    Context provideContext() {
        return application;
    }

    @Provides
    Application provideApplication() {
        return application;
    }

    @Provides
    SharedPreferences provideSharedPrefs() {
        return application.getSharedPreferences("weather-database-prefs", Context.MODE_PRIVATE);
    }

    @Provides
    ItemDAO provideItemDAO() {
        ItemDAO itemDAO = Room.databaseBuilder(application, FavoriteWeatherDatabase.class, "weather-database")
                .allowMainThreadQueries()
                .build().getItemDAO();

        Log.e("LOG_TAG", "provideItemDAO() : itemDAO : " + itemDAO);
        return itemDAO;
    }

}
