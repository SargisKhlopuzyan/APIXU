package com.sargis.kh.apixu.favorite_weather.presenters;

import com.sargis.kh.apixu.favorite_weather.contracts.EditModeContract;
import com.sargis.kh.apixu.favorite_weather.data.DataManager;
import com.sargis.kh.apixu.favorite_weather.helpers.DataConverter;
import com.sargis.kh.apixu.favorite_weather.models.favorite.CurrentWeatherDataModel;

import java.util.List;

import javax.inject.Inject;

public class EditModePresenter implements EditModeContract.Presenter {

    private EditModeContract.View viewCallback;

    @Inject
    DataManager dataManager;

    @Inject
    public EditModePresenter(EditModeContract.View viewCallback) {
        this.viewCallback = viewCallback;
    }

    @Override
    public void onFavoriteWeatherItemMoved(List<CurrentWeatherDataModel> currentWeatherDataModels, int fromPosition, int toPosition) {
        dataManager.update(DataConverter.convertCurrentWeatherDataModelToItem(currentWeatherDataModels.get(fromPosition)));
        dataManager.update(DataConverter.convertCurrentWeatherDataModelToItem(currentWeatherDataModels.get(toPosition)));
    }

    @Override
    public void deleteFavoriteWeatherDataFromDatabase(List<CurrentWeatherDataModel> currentWeatherDataModels, CurrentWeatherDataModel currentWeatherDataModel, int position) {
        dataManager.delete(currentWeatherDataModel.id);

        for (int i = 0; i < position; i++) {
            currentWeatherDataModels.get(i).orderIndex = currentWeatherDataModels.get(i+1).orderIndex;
            dataManager.update(DataConverter.convertCurrentWeatherDataModelToItem(currentWeatherDataModels.get(i)));
        }

        viewCallback.onFavoriteWeatherItemDeleted(position);
    }

}
