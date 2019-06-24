package com.sargis.kh.apixu.network.calls;

import com.sargis.kh.apixu.helpers.UrlHelper;
import com.sargis.kh.apixu.models.favorite.CurrentWeatherDataModel;
import com.sargis.kh.apixu.models.search.SearchDataModel;
import com.sargis.kh.apixu.network.APIService;
import com.sargis.kh.apixu.network.RetrofitClientInstance;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;

public class AsynchronousRequests {

    public static void getSearchData(GetDataCallback<ArrayList<SearchDataModel>> dataCallback, String query) {
        APIService service = RetrofitClientInstance.getRetrofitInstance().create(APIService.class);
        Call<ArrayList<SearchDataModel>> call = service.getSearchData(UrlHelper.getDataBySearchQuery(query));
        call.enqueue(new Callback<ArrayList<SearchDataModel>>() {
            @Override
            public void onResponse(Call<ArrayList<SearchDataModel>> call, retrofit2.Response<ArrayList<SearchDataModel>> dataResponse) {
                if (dataResponse.isSuccessful()) {
                    dataCallback.onSuccess(dataResponse.body());
                } else {
                    dataCallback.onError(dataResponse.code(), dataResponse.errorBody());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<SearchDataModel>> call, Throwable t) {
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
