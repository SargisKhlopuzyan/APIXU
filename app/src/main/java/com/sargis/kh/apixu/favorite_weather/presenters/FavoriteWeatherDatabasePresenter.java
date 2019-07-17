package com.sargis.kh.apixu.favorite_weather.presenters;

import android.util.Log;

import com.sargis.kh.apixu.favorite_weather.contracts.FavoriteWeatherDatabaseContract;
import com.sargis.kh.apixu.favorite_weather.data.DataManager;
import com.sargis.kh.apixu.favorite_weather.database.models.Item;
import com.sargis.kh.apixu.favorite_weather.helpers.DataConverter;
import com.sargis.kh.apixu.favorite_weather.models.favorite.CurrentWeatherDataModel;
import com.sargis.kh.apixu.favorite_weather.models.search.SearchDataModel;
import com.sargis.kh.apixu.network.calls.Data;
import com.sargis.kh.apixu.network.calls.GetDataCallback;

import java.util.List;

import javax.inject.Inject;

import okhttp3.ResponseBody;

public class FavoriteWeatherDatabasePresenter implements FavoriteWeatherDatabaseContract.Presenter {

    private FavoriteWeatherDatabaseContract.View viewCallback;

    @Inject
    DataManager dataManager;

    @Inject
    public FavoriteWeatherDatabasePresenter(FavoriteWeatherDatabaseContract.View viewCallback) {
        this.viewCallback = viewCallback;
    }

    @Override
    public List<CurrentWeatherDataModel> getSavedFavoriteWeatherDataFromDatabase() {
        Log.e("LOG_TAG", "getSavedFavoriteWeatherDataFromDatabase() : dataManager : " + dataManager);
        List<Item> items = dataManager.getItems();
        return DataConverter.convertItemsToCurrentWeatherDataModels(items);
    }

    @Override
    public Long saveFavoriteDataInDatabase(CurrentWeatherDataModel currentWeatherDataModel) {
        Item item = DataConverter.convertCurrentWeatherDataModelToItem(currentWeatherDataModel);
        return dataManager.insert(item);
    }

    @Override
    public void updateFavoriteWeatherDataList(List<CurrentWeatherDataModel> currentWeatherDataModels) {
        if (currentWeatherDataModels.isEmpty()){
            viewCallback.onSavedFavoriteWeatherDataUpdated();
            return;
        }
        viewCallback.onSavedFavoriteWeatherDataUpdatingStarted();
        updateFavoriteWeatherData(currentWeatherDataModels, 0);
    }

    private void updateFavoriteWeatherData(List<CurrentWeatherDataModel> currentWeatherDataModels, int position) {

        if (!currentWeatherDataModels.isEmpty() && position < currentWeatherDataModels.size()) {

            String searchQuery = currentWeatherDataModels.get(position).location.name + ", "
                    + currentWeatherDataModels.get(position).location.region + ", "
                    + currentWeatherDataModels.get(position).location.country;

            Data.getCurrentWeatherData(new GetDataCallback<CurrentWeatherDataModel>() {

                @Override
                public void onSuccess(CurrentWeatherDataModel currentWeatherDataModel) {
                    updateFavoritesDataInDatabase(currentWeatherDataModel);

                    if (position == currentWeatherDataModels.size() - 1) {
                        viewCallback.onSavedFavoriteWeatherDataUpdated();
                    } else {
                        updateFavoriteWeatherData(currentWeatherDataModels,position + 1);
                    }
                }

                @Override
                public void onError(int errorCode, ResponseBody errorResponse) {
                    if (position == currentWeatherDataModels.size() - 1) {
                        viewCallback.onSavedFavoriteWeatherDataUpdatingFinishedWithError(errorResponse.toString());
                    } else {
                        updateFavoriteWeatherData(currentWeatherDataModels,position + 1);
                    }
                }

                @Override
                public void onFailure(Throwable failure) {
                    if (position == currentWeatherDataModels.size() - 1) {
                        viewCallback.onSavedFavoriteWeatherDataUpdatingFinishedWithError(failure.getMessage());
                    } else {
                        updateFavoriteWeatherData(currentWeatherDataModels,position + 1);
                    }
                }
            }, searchQuery);
        }
    }

    private void updateFavoritesDataInDatabase(CurrentWeatherDataModel currentWeatherDataModel) {
        Item oldItem = dataManager.getItemByFullName(currentWeatherDataModel.location.name, currentWeatherDataModel.location.region, currentWeatherDataModel.location.country );

        currentWeatherDataModel.id = oldItem.getId();
        currentWeatherDataModel.orderIndex = oldItem.getOrder_index();
        Item item = DataConverter.convertCurrentWeatherDataModelToItem(currentWeatherDataModel);
        dataManager.update(item);

        viewCallback.onSavedFavoriteWeatherDataUpdated(currentWeatherDataModel);
    }

    @Override
    public boolean isFavoriteWeatherDataExistInDatabase(SearchDataModel searchDataModel) {
        String name = searchDataModel.name.replace(", " + searchDataModel.region + ", " + searchDataModel.country, "");
        Item item = dataManager.getItemByFullName(name, searchDataModel.region, searchDataModel.country);
        if (item == null)
            return false;
        else
            return true;
    }

    @Override
    public boolean isFavoriteWeatherDataExistInDatabase(CurrentWeatherDataModel currentWeatherDataModel) {
        Item item = dataManager.getItemByFullName(currentWeatherDataModel.location.name, currentWeatherDataModel.location.region, currentWeatherDataModel.location.country);
        if (item == null)
            return false;
        else
            return true;
    }

    @Override
    public CurrentWeatherDataModel getSavedFavoriteWeatherDataFromDatabase(SearchDataModel searchDataModel) {
        String name = searchDataModel.name.replace(", " + searchDataModel.region + ", " + searchDataModel.country, "");
        return DataConverter.convertItemToCurrentWeatherDataModel(dataManager.getItemByFullName(name, searchDataModel.region, searchDataModel.country));
    }
}
