package com.sargis.kh.apixu.di.component;

import com.sargis.kh.apixu.di.module.ApplicationModule;
import com.sargis.kh.apixu.di.module.DeleteModePresenterModule;
import com.sargis.kh.apixu.di.module.EditModePresenterModule;
import com.sargis.kh.apixu.di.module.FavoriteWeatherDatabasePresenterModule;
import com.sargis.kh.apixu.di.module.FavoriteWeatherPresenterModule;
import com.sargis.kh.apixu.di.module.SearchPresenterModule;
import com.sargis.kh.apixu.favorite_weather.FavoriteWeatherActivity;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {FavoriteWeatherPresenterModule.class, ApplicationModule.class,
        FavoriteWeatherDatabasePresenterModule.class, SearchPresenterModule.class,
        EditModePresenterModule.class, DeleteModePresenterModule.class})
public interface WeatherActivityComponent {

    void inject(FavoriteWeatherActivity favoriteWeatherActivity);

}
