package com.sargis.kh.apixu.di.component;

import com.sargis.kh.apixu.di.module.SearchPresenterModule;
import com.sargis.kh.apixu.favorite_weather.FavoriteWeatherActivity;
import com.sargis.kh.apixu.favorite_weather.contracts.SearchContract;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = SearchPresenterModule.class)
public interface SearchComponent {

//    void inject(SearchContract.View view);
    void inject(FavoriteWeatherActivity favoriteWeatherActivity);

}
