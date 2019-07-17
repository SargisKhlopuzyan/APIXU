package com.sargis.kh.apixu.di.component;

import android.app.Application;
import android.content.Context;

import com.sargis.kh.apixu.DataApplication;
import com.sargis.kh.apixu.di.ApplicationContext;
import com.sargis.kh.apixu.di.module.ApplicationModule;
import com.sargis.kh.apixu.favorite_weather.data.DataManager;
import com.sargis.kh.apixu.favorite_weather.data.SharedPrefsHelper;
import com.sargis.kh.apixu.favorite_weather.database.dao.ItemDAO;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {

    void inject(DataApplication dataApplication);

    DataManager getDataManager();

    @ApplicationContext
    Context getContext();

    Application getApplication();

    SharedPrefsHelper getPreferenceHelper();

    ItemDAO getItemDAO();
}
