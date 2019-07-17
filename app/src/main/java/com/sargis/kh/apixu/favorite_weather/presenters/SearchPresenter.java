package com.sargis.kh.apixu.favorite_weather.presenters;

import android.os.CountDownTimer;

import com.sargis.kh.apixu.R;
import com.sargis.kh.apixu.favorite_weather.contracts.SearchContract;
import com.sargis.kh.apixu.favorite_weather.data.DataManager;
import com.sargis.kh.apixu.favorite_weather.models.search.SearchDataModel;
import com.sargis.kh.apixu.helpers.NetworkHelper;
import com.sargis.kh.apixu.network.calls.Data;
import com.sargis.kh.apixu.network.calls.GetDataCallback;

import java.util.List;

import javax.inject.Inject;

import okhttp3.ResponseBody;

public class SearchPresenter implements SearchContract.Presenter {

    private SearchContract.View viewCallback;
    private CountDownTimer countDownTimer;

    @Inject
    DataManager dataManager;

    @Inject
    public SearchPresenter(SearchContract.View viewCallback) {
        this.viewCallback = viewCallback;
    }

    @Override
    public void getSearchData(String text) {

        if (text.isEmpty()) {
            viewCallback.setSearchEmpty();
            destroyTimer();
            return;
        } else if (text.length() < 3) {
            viewCallback.stopSearchLoadingAndCleanData();
            destroyTimer();
            return;
        }

        viewCallback.onSearchDataLoadingStarted();

        if (!NetworkHelper.isNetworkActive(dataManager.getContext())) {
            viewCallback.onSearchDataLoadedWithError(dataManager.getString(R.string.no_internet_connection));
            destroyTimer();
            return;
        }

        createTimer(text,500);
    }

    public void createTimer(String text, int screenTimeoutMillis) {
        destroyTimer();
        countDownTimer = new CountDownTimer(screenTimeoutMillis, 100) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                Data.getSearchData(new GetDataCallback<List<SearchDataModel>>() {
                    @Override
                    public void onSuccess(List<SearchDataModel> searchDataModels) {
                        viewCallback.onSearchDataLoaded(searchDataModels);
                    }

                    @Override
                    public void onError(int errorCode, ResponseBody errorResponse) {
                        viewCallback.onSearchDataLoadedWithError(errorResponse.toString());
                    }

                    @Override
                    public void onFailure(Throwable failure) {
                        viewCallback.onSearchDataLoadedWithError(failure.getMessage());
                    }
                }, text);
            }
        };

        countDownTimer.start();
    }

    private void destroyTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }

}
