package com.sargis.kh.apixu.di.module;

import com.sargis.kh.apixu.favorite_weather.contracts.DeleteModeContract;
import com.sargis.kh.apixu.favorite_weather.contracts.EditModeContract;
import com.sargis.kh.apixu.favorite_weather.contracts.FavoriteWeatherDatabaseContract;

import dagger.Module;
import dagger.Provides;

@Module
public class DeleteModePresenterModule {

    private final DeleteModeContract.View view;

    public DeleteModePresenterModule(DeleteModeContract.View view) {
        this.view = view;
    }

    @Provides
    DeleteModeContract.View provideDeleteModeContractView() {
        return view;
    }

}
