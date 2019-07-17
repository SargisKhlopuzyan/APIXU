package com.sargis.kh.apixu.di.module;

import com.sargis.kh.apixu.favorite_weather.contracts.FavoriteWeatherContract;

import dagger.Module;
import dagger.Provides;

@Module
public class FavoriteWeatherPresenterModule {

    private final FavoriteWeatherContract.View view;

    public FavoriteWeatherPresenterModule(FavoriteWeatherContract.View view) {
        this.view = view;
    }

    @Provides
    FavoriteWeatherContract.View provideFavoriteWeatherContractView() {
        return view;
    }
}
