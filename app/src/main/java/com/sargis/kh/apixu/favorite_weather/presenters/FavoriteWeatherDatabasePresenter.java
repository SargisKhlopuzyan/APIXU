package com.sargis.kh.apixu.favorite_weather.presenters;

import android.arch.persistence.room.Room;

import com.sargis.kh.apixu.App;
import com.sargis.kh.apixu.favorite_weather.contracts.FavoriteWeatherDatabaseContract;
import com.sargis.kh.apixu.favorite_weather.database.FavoriteWeatherDatabase;
import com.sargis.kh.apixu.favorite_weather.database.dao.ItemDAO;
import com.sargis.kh.apixu.favorite_weather.database.models.Item;
import com.sargis.kh.apixu.favorite_weather.helpers.DataConverter;
import com.sargis.kh.apixu.favorite_weather.models.favorite.CurrentWeatherDataModel;
import com.sargis.kh.apixu.favorite_weather.models.search.SearchDataModel;
import com.sargis.kh.apixu.network.calls.Data;
import com.sargis.kh.apixu.network.calls.GetDataCallback;

import java.util.List;

import okhttp3.ResponseBody;

public class FavoriteWeatherDatabasePresenter implements FavoriteWeatherDatabaseContract.Presenter {

    private FavoriteWeatherDatabaseContract.View viewCallback;

    private FavoriteWeatherDatabase database;

    public FavoriteWeatherDatabasePresenter(FavoriteWeatherDatabaseContract.View viewCallback) {
        this.viewCallback = viewCallback;
        database = Room.databaseBuilder(App.getAppContext(), FavoriteWeatherDatabase.class, "WeatherDatabaseDb")
                .allowMainThreadQueries()
                .build();
    }

    @Override
    public List<CurrentWeatherDataModel> getSavedFavoriteWeatherDataFromDatabase() {
        ItemDAO itemDAO = database.getItemDAO();
        List<Item> items = itemDAO.getItems();
        return DataConverter.convertItemsToCurrentWeatherDataModels(items);
    }

    @Override
    public Long saveFavoriteDataInDatabase(CurrentWeatherDataModel currentWeatherDataModel) {
        ItemDAO itemDAO = database.getItemDAO();
        Item item = DataConverter.convertCurrentWeatherDataModelToItem(currentWeatherDataModel);
        return itemDAO.insert(item);
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
        ItemDAO itemDAO = database.getItemDAO();
        Item oldItem = itemDAO.getItemByFullName(currentWeatherDataModel.location.name, currentWeatherDataModel.location.region, currentWeatherDataModel.location.country );

        currentWeatherDataModel.id = oldItem.getId();
        currentWeatherDataModel.orderIndex = oldItem.getOrder_index();
        Item item = DataConverter.convertCurrentWeatherDataModelToItem(currentWeatherDataModel);
        itemDAO.update(item);

        viewCallback.onSavedFavoriteWeatherDataUpdated(currentWeatherDataModel);
    }

    @Override
    public boolean isFavoriteWeatherDataExistInDatabase(SearchDataModel searchDataModel) {
        String name = searchDataModel.name.replace(", " + searchDataModel.region + ", " + searchDataModel.country, "");
        ItemDAO itemDAO = database.getItemDAO();
        Item item = itemDAO.getItemByFullName(name, searchDataModel.region, searchDataModel.country);
        if (item == null)
            return false;
        else
            return true;
    }

    @Override
    public boolean isFavoriteWeatherDataExistInDatabase(CurrentWeatherDataModel currentWeatherDataModel) {
        ItemDAO itemDAO = database.getItemDAO();
        Item item = itemDAO.getItemByFullName(currentWeatherDataModel.location.name, currentWeatherDataModel.location.region, currentWeatherDataModel.location.country);
        if (item == null)
            return false;
        else
            return true;
    }

    @Override
    public CurrentWeatherDataModel getSavedFavoriteWeatherDataFromDatabase(SearchDataModel searchDataModel) {
        String name = searchDataModel.name.replace(", " + searchDataModel.region + ", " + searchDataModel.country, "");
        ItemDAO itemDAO = database.getItemDAO();
        return DataConverter.convertItemToCurrentWeatherDataModel(itemDAO.getItemByFullName(name, searchDataModel.region, searchDataModel.country));
    }
}
