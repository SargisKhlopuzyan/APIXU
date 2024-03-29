package com.sargis.kh.apixu.network;

import com.sargis.kh.apixu.favorite_weather.models.favorite.CurrentWeatherDataModel;
import com.sargis.kh.apixu.favorite_weather.models.search.SearchDataModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface APIService {

    @GET
    Call<List<SearchDataModel>> getSearchData(@Url String url);

    @GET
    Call<CurrentWeatherDataModel> getCurrentWeatherData(@Url String url);

}