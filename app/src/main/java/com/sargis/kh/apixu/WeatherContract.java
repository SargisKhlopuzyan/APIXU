package com.sargis.kh.apixu;

import com.sargis.kh.apixu.enums.SelectedState;
import com.sargis.kh.apixu.models.favorite.CurrentWeatherDataModel;
import com.sargis.kh.apixu.models.search.SearchDataModel;

import java.util.List;

public interface WeatherContract {

    interface View {

        void onSavedFavoriteWeatherDataLoadingFromDatabaseStarted();
        void onSavedFavoriteWeatherDataLoadedFromDatabase(List<CurrentWeatherDataModel> currentWeatherDataModels);

        void onSavedFavoriteWeatherDataUpdatingStarted();
        void onSavedFavoriteWeatherDataUpdated(CurrentWeatherDataModel currentWeatherDataModel);
        void onSavedFavoriteWeatherDataUpdatingFinished();
        void onSavedFavoriteWeatherDataUpdatingFinishedWithError(String errorMessage);

        void onFavoriteWeatherDataLoadingStarted();
        void onFavoriteWeatherDataLoadedFromDatabase(CurrentWeatherDataModel currentWeatherDataModel);
        void onFavoriteWeatherDataFoundInDatabase();
        void onFavoriteWeatherDataLoadedWithError(String errorMessage);

        void onSearchDataLoadingStarted();
        void onSearchDataLoaded(List<SearchDataModel> searchDataModels);
        void onSearchDataLoadedWithError(String errorMessage);

        void setIsSearchEmpty(boolean isSearchEmpty);
        void stopSearchLoadingAndCleanData();

        void setSelectedItemsCount(SelectedState selectedState, int selectedItemsCount);

        void updateView();

        void onFavoriteItemDeleted(int position);
    }

    interface Presenter {
        void getSearchData(String text);
        void getFavoriteData(SearchDataModel searchDataModel, Long orderPosition);
        void updateFavoritesData(List<CurrentWeatherDataModel> currentWeatherDataModels);

        void deleteFavoriteDataFromDatabase(List<CurrentWeatherDataModel> currentWeatherDataModels, CurrentWeatherDataModel currentWeatherDataModel, int position);
        void deleteAllSelectedFavoriteDatesFromDatabase(List<CurrentWeatherDataModel> currentWeatherDataModels);

        void setAllItemsStateSelected(List<CurrentWeatherDataModel> currentWeatherDataModels, SelectedState selectedState);
        void resetSelectedItems(List<CurrentWeatherDataModel> currentWeatherDataModels);
        void itemSelectedStateChanged(int itemsSize, Boolean isSelected);

        void getFavoriteSavedDataFromDatabase();
        void onFavoriteItemMoved(List<CurrentWeatherDataModel> currentWeatherDataModels, int fromPosition, int toPosition);

        int getSelectedItemsCount();
    }

}
