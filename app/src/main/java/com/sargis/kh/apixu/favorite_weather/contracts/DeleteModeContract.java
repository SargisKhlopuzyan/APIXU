package com.sargis.kh.apixu.favorite_weather.contracts;

import com.sargis.kh.apixu.favorite_weather.enums.DeleteModeSelectedState;
import com.sargis.kh.apixu.favorite_weather.models.favorite.CurrentWeatherDataModel;

import java.util.ArrayList;
import java.util.List;

public interface DeleteModeContract {

    interface View {
        void updateView();
        void onFavoriteWeatherItemDeleted(int position);
        void setSelectedItemsCount(DeleteModeSelectedState deleteModeSelectedState, int selectedItemsCount);
    }

    interface Presenter {
        void deleteAllSelectedFavoriteWeatherDataFromDatabase(List<CurrentWeatherDataModel> currentWeatherDataModels);

        void setAllItemsStateSelected(List<CurrentWeatherDataModel> currentWeatherDataModels, DeleteModeSelectedState deleteModeSelectedState);
        void resetSelectedItems(List<CurrentWeatherDataModel> currentWeatherDataModels);

        ArrayList<Integer> getSelectedItemsOrderIndexes();
        void setSelectedItemsOrderIndexes(List<CurrentWeatherDataModel> savedCurrentWeatherDataModels, ArrayList<Integer> selectedItemsOrderIndexes);

        void itemSelectedStateChanged(CurrentWeatherDataModel currentWeatherDataModel, int itemsSize, Boolean isSelected);
    }

}
