package com.sargis.kh.apixu;

import android.arch.persistence.room.Room;
import android.util.Log;

import com.sargis.kh.apixu.database.WeatherDatabase;
import com.sargis.kh.apixu.database.dao.ItemDAO;
import com.sargis.kh.apixu.database.models.Item;
import com.sargis.kh.apixu.helpers.DataConverter;
import com.sargis.kh.apixu.models.favorite.CurrentWeatherDataModel;
import com.sargis.kh.apixu.models.search.SearchDataModel;
import com.sargis.kh.apixu.network.calls.Data;
import com.sargis.kh.apixu.network.calls.GetDataCallback;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;

public class WeatherPresenter implements WeatherContract.Presenter {

    private WeatherContract.View viewCallback;

    private WeatherDatabase database;

    public WeatherPresenter(WeatherContract.View viewCallback) {
        this.viewCallback = viewCallback;
        database = Room.databaseBuilder(App.getAppContext(), WeatherDatabase.class, "WeatherDatabaseDb")
                .allowMainThreadQueries()
                .build();
    }

    @Override
    public void getFavoriteData(String name, Long orderPosition) {

        viewCallback.onFavoriteDataLoadingStarted();

        Data.getCurrentWeatherData(new GetDataCallback<CurrentWeatherDataModel>() {

            @Override
            public void onSuccess(CurrentWeatherDataModel currentWeatherDataModel) {

                ItemDAO itemDAO = database.getItemDAO();
                Item item = itemDAO.getItemByName(currentWeatherDataModel.location.name);

                if (item == null) {
                    Log.e("LOG_TAG", "item ====== null => currentWeatherDataModel.location.name: " + currentWeatherDataModel.location.name);
                } else {
                    Log.e("LOG_TAG", "item != null => currentWeatherDataModel.location.name: " + currentWeatherDataModel.location.name);
                    viewCallback.onFavoriteDataLoadedWithError("");
                    return;
                }

                currentWeatherDataModel.orderPosition = orderPosition;
                Long id = saveFavoriteDataInDatabase(currentWeatherDataModel);
                Log.e("LOG_TAG", "id: " + id);
                currentWeatherDataModel.id = id;

                viewCallback.onFavoriteDataLoaded(currentWeatherDataModel);
            }

            @Override
            public void onError(int errorCode, ResponseBody errorResponse) {
                viewCallback.onFavoriteDataLoadedWithError(errorResponse.toString());
            }

            @Override
            public void onFailure(Throwable failure) {
                viewCallback.onFavoriteDataLoadedWithError(failure.getMessage());
            }
        }, name);
    }

    @Override
    public void getSearchData(String text) {
        viewCallback.setIsSearchMode(true);

        if (text.isEmpty())
            viewCallback.setIsSearchEmpty(true);
        else
            viewCallback.setIsSearchEmpty(false);

        if (text.length() < 3) {
            viewCallback.stopSearchLoadingAndCleanData();
            return;
        }

        viewCallback.onSearchDataLoadingStarted();

        Data.getSearchData(new GetDataCallback<ArrayList<SearchDataModel>>() {
            @Override
            public void onSuccess(ArrayList<SearchDataModel> searchDataModels) {
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

    @Override
    public void updateFavoritesData(List<CurrentWeatherDataModel> currentWeatherDataModels) {
        if (currentWeatherDataModels.isEmpty()){
            viewCallback.onFavoriteSavedDataUpdatingFinished();
            return;
        }
        viewCallback.onFavoriteSavedDataUpdatingStarted();
        updateFavoritesData(currentWeatherDataModels, 0);
    }

    private void updateFavoritesData(List<CurrentWeatherDataModel> currentWeatherDataModels, int position) {

        if (!currentWeatherDataModels.isEmpty() && position < currentWeatherDataModels.size()) {

            Log.e("LOG_TAG", "currentWeatherDataModels: " + currentWeatherDataModels.get(position).location.name);

            Data.getCurrentWeatherData(new GetDataCallback<CurrentWeatherDataModel>() {

                @Override
                public void onSuccess(CurrentWeatherDataModel currentWeatherDataModel) {

                    updateFavoritesDataInDatabase(currentWeatherDataModel);
//                    viewCallback.onFavoriteSavedDataUpdated(currentWeatherDataModel);

                    if (position == currentWeatherDataModels.size() - 1) {
                        viewCallback.onFavoriteSavedDataUpdatingFinished();
                    } else {
                        updateFavoritesData(currentWeatherDataModels,position + 1);
                    }
                }

                @Override
                public void onError(int errorCode, ResponseBody errorResponse) {
                    if (position == currentWeatherDataModels.size() - 1) {
                        viewCallback.onFavoriteSavedDataUpdatingFinishedWithError(errorResponse.toString());
                    } else {
                        updateFavoritesData(currentWeatherDataModels,position + 1);
                    }
                }

                @Override
                public void onFailure(Throwable failure) {
                    if (position == currentWeatherDataModels.size() - 1) {
                        viewCallback.onFavoriteSavedDataUpdatingFinishedWithError(failure.getMessage());
                    } else {
                        updateFavoritesData(currentWeatherDataModels,position + 1);
                    }
                }
            }, currentWeatherDataModels.get(position).location.name);
        }
    }

    public void deleteFavoriteDataFromDatabase(String name) {
        ItemDAO itemDAO = database.getItemDAO();
        itemDAO.delete(name);
    }

    private void updateFavoritesDataInDatabase(CurrentWeatherDataModel currentWeatherDataModel) {
        ItemDAO itemDAO = database.getItemDAO();
        Log.e("LOG_TAG", "updateFavoritesDataInDatabase: currentWeatherDataModel.location.name: " + currentWeatherDataModel.location.name);
        Item oldItem = itemDAO.getItemByName(currentWeatherDataModel.location.name);
        if (oldItem == null) {
            Log.e("LOG_TAG", "updateFavoritesDataInDatabase: oldItem == null");
        }
        currentWeatherDataModel.id = oldItem.getId();
        currentWeatherDataModel.orderPosition = oldItem.getOrder_position();
        Item item = DataConverter.convertCurrentWeatherDataModelToItem(currentWeatherDataModel);
        int isUpdate = itemDAO.update(item);

        Log.e("LOG_TAG", "id: " + currentWeatherDataModel.id + " ; isUpdate: " + isUpdate);
        viewCallback.onFavoriteSavedDataUpdated(currentWeatherDataModel);
    }

    public void getFavoriteSavedDataFromDatabase() {
        viewCallback.onFavoriteSavedDataLoadingFromDatabaseStarted();
        ItemDAO itemDAO = database.getItemDAO();
        List<Item> items = itemDAO.getItems();
        viewCallback.onFavoriteSavedDataLoadedFromDatabase(DataConverter.convertItemsToCurrentWeatherDataModels(items));
    }

    public Long saveFavoriteDataInDatabase(CurrentWeatherDataModel currentWeatherDataModel) {
        ItemDAO itemDAO = database.getItemDAO();
        Item item = DataConverter.convertCurrentWeatherDataModelToItem(currentWeatherDataModel);
        return itemDAO.insert(item);
    }

}
