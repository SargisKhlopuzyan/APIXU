package com.sargis.kh.apixu.favorite_weather.presenters;

import com.sargis.kh.apixu.App;
import com.sargis.kh.apixu.R;
import com.sargis.kh.apixu.favorite_weather.contracts.FavoriteWeatherContract;
import com.sargis.kh.apixu.helpers.NetworkHelper;
import com.sargis.kh.apixu.favorite_weather.models.favorite.CurrentWeatherDataModel;
import com.sargis.kh.apixu.favorite_weather.models.search.SearchDataModel;
import com.sargis.kh.apixu.network.calls.Data;
import com.sargis.kh.apixu.network.calls.GetDataCallback;

import okhttp3.ResponseBody;

public class FavoriteWeatherPresenter implements FavoriteWeatherContract.Presenter {

    private FavoriteWeatherContract.View viewCallback;

    private SearchDataModel searchDataModel;

    public FavoriteWeatherPresenter(FavoriteWeatherContract.View viewCallback) {
        this.viewCallback = viewCallback;
    }

    @Override
    public void getFavoriteWeatherData(SearchDataModel searchDataModel, Integer orderIndex) {

        this.searchDataModel = searchDataModel;
        viewCallback.onFavoriteWeatherDataLoadingStarted();

        if (!NetworkHelper.isNetworkActive()) {
            viewCallback.onFavoriteWeatherDataLoadedWithError(App.getAppContext().getString(R.string.no_internet_connection));
            return;
        }

        Data.getCurrentWeatherData(new GetDataCallback<CurrentWeatherDataModel>() {

            @Override
            public void onSuccess(CurrentWeatherDataModel currentWeatherDataModel) {
                viewCallback.onFavoriteWeatherDataLoaded(currentWeatherDataModel, orderIndex);
            }

            @Override
            public void onError(int errorCode, ResponseBody errorResponse) {
                viewCallback.onFavoriteWeatherDataLoadedWithError(errorResponse.toString());
            }

            @Override
            public void onFailure(Throwable failure) {
                viewCallback.onFavoriteWeatherDataLoadedWithError(failure.getMessage());
            }
        }, searchDataModel.name);
    }

    @Override
    public SearchDataModel getSearchDataModel() {
        return searchDataModel;
    }

}
