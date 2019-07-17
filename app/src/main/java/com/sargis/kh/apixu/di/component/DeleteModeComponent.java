package com.sargis.kh.apixu.di.component;

import com.sargis.kh.apixu.di.module.DeleteModePresenterModule;
import com.sargis.kh.apixu.favorite_weather.FavoriteWeatherActivity;
import com.sargis.kh.apixu.favorite_weather.contracts.DeleteModeContract;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = DeleteModePresenterModule.class)
public interface DeleteModeComponent {

//    void inject(DeleteModeContract.View view);
    void inject(FavoriteWeatherActivity favoriteWeatherActivity);

}
