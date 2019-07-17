package com.sargis.kh.apixu.di.module;

import com.sargis.kh.apixu.favorite_weather.contracts.FavoriteWeatherDatabaseContract;

import dagger.Module;
import dagger.Provides;

@Module
public class FavoriteWeatherDatabasePresenterModule {

    private final FavoriteWeatherDatabaseContract.View view;

    public FavoriteWeatherDatabasePresenterModule(FavoriteWeatherDatabaseContract.View view) {
        this.view = view;
    }

    @Provides
    FavoriteWeatherDatabaseContract.View provideFavoriteWeatherDatabaseContractView() {
        return view;
    }
}
