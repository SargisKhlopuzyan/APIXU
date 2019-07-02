package com.sargis.kh.apixu.favorite_weather.contracts;

import com.sargis.kh.apixu.models.search.SearchDataModel;

import java.util.List;

public interface SearchContract {

    interface View {
        void onSearchDataLoadingStarted();
        void onSearchDataLoaded(List<SearchDataModel> searchDataModels);
        void onSearchDataLoadedWithError(String errorMessage);

        void setSearchEmpty();
        void stopSearchLoadingAndCleanData();
    }

    interface Presenter {
        void getSearchData(String text);
    }

}
