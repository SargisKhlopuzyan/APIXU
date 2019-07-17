package com.sargis.kh.apixu.di.component;

import com.sargis.kh.apixu.di.module.FavoriteWeatherPresenterModule;
import com.sargis.kh.apixu.favorite_weather.FavoriteWeatherActivity;
import com.sargis.kh.apixu.favorite_weather.contracts.FavoriteWeatherContract;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = FavoriteWeatherPresenterModule.class)
public interface FavoriteWeatherComponent {

//    void inject(FavoriteWeatherContract.View view);
    void inject(FavoriteWeatherActivity favoriteWeatherActivity);

}
