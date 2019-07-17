package com.sargis.kh.apixu;

import android.app.Application;
import android.support.v7.app.AppCompatDelegate;

import com.sargis.kh.apixu.di.component.ApplicationComponent;
import com.sargis.kh.apixu.di.component.DaggerApplicationComponent;
import com.sargis.kh.apixu.di.module.ApplicationModule;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

public class DataApplication extends Application {

    protected ApplicationComponent applicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        applicationComponent = DaggerApplicationComponent
                .builder()
                .applicationModule(new ApplicationModule(this))
                .build();

        applicationComponent.inject(this);

        Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttp3Downloader(this, Integer.MAX_VALUE));
        Picasso built = builder.build();
        Picasso.setSingletonInstance(built);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

}
