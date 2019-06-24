package com.sargis.kh.apixu.network.calls;

import com.sargis.kh.apixu.models.favorite.CurrentWeatherDataModel;
import com.sargis.kh.apixu.models.search.SearchDataModel;

import java.util.ArrayList;

public class Data {

    public static void getSearchData(GetDataCallback<ArrayList<SearchDataModel>> dataCallback, String query) {
        AsynchronousRequests.getSearchData(dataCallback, query);
    }

    public static void getCurrentWeatherData(GetDataCallback<CurrentWeatherDataModel> dataCallback, String query) {
        AsynchronousRequests.getCurrentWeatherData(dataCallback, query);
    }
}