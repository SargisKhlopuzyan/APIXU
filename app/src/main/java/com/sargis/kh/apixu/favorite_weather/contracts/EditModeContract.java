package com.sargis.kh.apixu.favorite_weather.contracts;

import com.sargis.kh.apixu.enums.DeleteModeSelectedState;
import com.sargis.kh.apixu.models.favorite.CurrentWeatherDataModel;

import java.util.List;

public interface EditModeContract {

    interface View {
        void onFavoriteWeatherItemDeleted(int position);
        void setSelectedItemsCount(DeleteModeSelectedState deleteModeSelectedState, int selectedItemsCount);
    }

    interface Presenter {
        void deleteFavoriteWeatherDataFromDatabase(List<CurrentWeatherDataModel> currentWeatherDataModels, CurrentWeatherDataModel currentWeatherDataModel, int position);
        void onFavoriteWeatherItemMoved(List<CurrentWeatherDataModel> currentWeatherDataModels, int fromPosition, int toPosition);
    }

}
