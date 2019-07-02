package com.sargis.kh.apixu.favorite_weather.contracts;

import com.sargis.kh.apixu.favorite_weather.models.favorite.CurrentWeatherDataModel;
import com.sargis.kh.apixu.favorite_weather.models.search.SearchDataModel;

import java.util.List;

public interface FavoriteWeatherDatabaseContract {

    interface View {
        void onSavedFavoriteWeatherDataUpdatingStarted();
        void onSavedFavoriteWeatherDataUpdated(CurrentWeatherDataModel currentWeatherDataModel);
        void onSavedFavoriteWeatherDataUpdated();
        void onSavedFavoriteWeatherDataUpdatingFinishedWithError(String errorMessage);
    }

    interface Presenter {
        List<CurrentWeatherDataModel> getSavedFavoriteWeatherDataFromDatabase();
        Long saveFavoriteDataInDatabase(CurrentWeatherDataModel currentWeatherDataModel);
        void updateFavoriteWeatherDataList(List<CurrentWeatherDataModel> currentWeatherDataModels);
        boolean isFavoriteWeatherDataExistInDatabase(SearchDataModel searchDataModel);

        boolean isFavoriteWeatherDataExistInDatabase(CurrentWeatherDataModel currentWeatherDataModel);

        CurrentWeatherDataModel getSavedFavoriteWeatherDataFromDatabase(SearchDataModel searchDataModel);
    }

}
