package com.sargis.kh.apixu.di.module;

import com.sargis.kh.apixu.favorite_weather.contracts.EditModeContract;

import dagger.Module;
import dagger.Provides;

@Module
public class EditModePresenterModule {

    private final EditModeContract.View view;

    public EditModePresenterModule(EditModeContract.View view) {
        this.view = view;
    }

    @Provides
    EditModeContract.View provideEditModeContractView() {
        return view;
    }
}
