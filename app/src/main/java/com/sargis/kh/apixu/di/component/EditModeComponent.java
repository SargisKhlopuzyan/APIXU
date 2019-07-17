package com.sargis.kh.apixu.di.component;

import com.sargis.kh.apixu.di.module.EditModePresenterModule;
import com.sargis.kh.apixu.favorite_weather.FavoriteWeatherActivity;
import com.sargis.kh.apixu.favorite_weather.contracts.EditModeContract;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = EditModePresenterModule.class)
public interface EditModeComponent {

//    void inject(EditModeContract.View view);
    void inject(FavoriteWeatherActivity favoriteWeatherActivity);

}
