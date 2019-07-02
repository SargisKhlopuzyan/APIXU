package com.sargis.kh.apixu.favorite_weather.contracts;

import com.sargis.kh.apixu.models.favorite.CurrentWeatherDataModel;
import com.sargis.kh.apixu.models.search.SearchDataModel;

public interface FavoriteWeatherContract {

    interface View {
        void onFavoriteWeatherDataLoadingStarted();
        void onFavoriteWeatherDataLoaded(CurrentWeatherDataModel currentWeatherDataModel, Integer orderIndex);
        void onFavoriteWeatherDataLoadedWithError(String errorMessage);
        void onFavoriteWeatherDataFoundInDatabase();
        void onFavoriteWeatherDataWasAddedInDatabaseInBackground(CurrentWeatherDataModel currentWeatherDataModel);
    }

    interface Presenter {
        void getFavoriteWeatherData(SearchDataModel searchDataModel, Integer orderPosition);
        SearchDataModel getSearchDataModel();
    }

}
