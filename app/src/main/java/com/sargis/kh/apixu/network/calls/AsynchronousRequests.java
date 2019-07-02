package com.sargis.kh.apixu.network.calls;

import com.sargis.kh.apixu.helpers.UrlHelper;
import com.sargis.kh.apixu.favorite_weather.models.favorite.CurrentWeatherDataModel;
import com.sargis.kh.apixu.favorite_weather.models.search.SearchDataModel;
import com.sargis.kh.apixu.network.APIService;
import com.sargis.kh.apixu.network.RetrofitClientInstance;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class AsynchronousRequests {

    public static void getSearchData(GetDataCallback<List<SearchDataModel>> dataCallback, String query) {
        APIService service = RetrofitClientInstance.getRetrofitInstance().create(APIService.class);
        Call<List<SearchDataModel>> call = service.getSearchData(UrlHelper.getDataBySearchQuery(query));
        call.enqueue(new Callback<List<SearchDataModel>>() {
            @Override
            public void onResponse(Call<List<SearchDataModel>> call, retrofit2.Response<List<SearchDataModel>> dataResponse) {
                if (dataResponse.isSuccessful()) {
                    dataCallback.onSuccess(dataResponse.body());
                } else {
                    dataCallback.onError(dataResponse.code(), dataResponse.errorBody());
                }
            }

            @Override
            public void onFailure(Call<List<SearchDataModel>> call, Throwable t) {
                dataCallback.onFailure(t);
            }
        });
    }

    public static void getCurrentWeatherData(GetDataCallback<CurrentWeatherDataModel> dataCallback, String query) {
        APIService service = RetrofitClientInstance.getRetrofitInstance().create(APIService.class);
        Call<CurrentWeatherDataModel> call = service.getCurrentWeatherData(UrlHelper.getDataByCurrentWeatherQuery(query));
        call.enqueue(new Callback<CurrentWeatherDataModel>() {
            @Override
            public void onResponse(Call<CurrentWeatherDataModel> call, retrofit2.Response<CurrentWeatherDataModel> dataResponse) {
                if (dataResponse.isSuccessful()) {
                    dataCallback.onSuccess(dataResponse.body());
                } else {
                    dataCallback.onError(dataResponse.code(), dataResponse.errorBody());
                }
            }

            @Override
            public void onFailure(Call<CurrentWeatherDataModel> call, Throwable t) {
                dataCallback.onFailure(t);
            }
        });
    }

}
