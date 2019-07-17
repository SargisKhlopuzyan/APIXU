package com.sargis.kh.apixu.di.module;

import com.sargis.kh.apixu.favorite_weather.contracts.SearchContract;

import dagger.Module;
import dagger.Provides;

@Module
public class SearchPresenterModule {

    private final SearchContract.View view;

    public SearchPresenterModule(SearchContract.View view) {
        this.view = view;
    }

    @Provides
    SearchContract.View provideSearchContractView() {
        return view;
    }
}
