package com.sargis.kh.apixu.di.component;

import com.sargis.kh.apixu.di.module.FavoriteWeatherDatabasePresenterModule;
import com.sargis.kh.apixu.favorite_weather.FavoriteWeatherActivity;
import com.sargis.kh.apixu.favorite_weather.contracts.FavoriteWeatherDatabaseContract;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = FavoriteWeatherDatabasePresenterModule.class)
public interface FavoriteWeatherDatabaseComponent {

//    void inject(FavoriteWeatherDatabaseContract.View view);
    void inject(FavoriteWeatherActivity favoriteWeatherActivity);

}
