package com.sargis.kh.apixu.network;

import com.sargis.kh.apixu.models.favorite.CurrentWeatherDataModel;
import com.sargis.kh.apixu.models.search.SearchDataModel;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface APIService {

    @GET
    Call<ArrayList<SearchDataModel>> getSearchData(@Url String url);

    @GET
    Call<CurrentWeatherDataModel> getCurrentWeatherData(@Url String url);

}