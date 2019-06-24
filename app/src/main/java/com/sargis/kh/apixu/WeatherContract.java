package com.sargis.kh.apixu;

import com.sargis.kh.apixu.models.favorite.CurrentWeatherDataModel;
import com.sargis.kh.apixu.models.search.SearchDataModel;

import java.util.ArrayList;
import java.util.List;

public interface WeatherContract {

    interface View {

        void onFavoriteSavedDataLoadingFromDatabaseStarted();
        void onFavoriteSavedDataLoadedFromDatabase(List<CurrentWeatherDataModel> currentWeatherDataModels);

        void onFavoriteSavedDataUpdatingStarted();
        void onFavoriteSavedDataUpdated(CurrentWeatherDataModel currentWeatherDataModel);
        void onFavoriteSavedDataUpdatingFinished();
        void onFavoriteSavedDataUpdatingFinishedWithError(String errorMessage);

        void onFavoriteDataLoadingStarted();
        void onFavoriteDataLoaded(CurrentWeatherDataModel currentWeatherDataModel);
        void onFavoriteDataLoadedWithError(String errorMessage);

        void onSearchDataLoadingStarted();
        void onSearchDataLoaded(ArrayList<SearchDataModel> searchDataModels);
        void onSearchDataLoadedWithError(String errorMessage);

        void setIsSearchMode(boolean isSearchMode);
        void setIsSearchEmpty(boolean isSearchEmpty);
        void stopSearchLoadingAndCleanData();
    }

    interface Presenter {
        void getSearchData(String text);
        void getFavoriteData(String text, Long orderPosition);
        void updateFavoritesData(List<CurrentWeatherDataModel> currentWeatherDataModels);
    }

}
