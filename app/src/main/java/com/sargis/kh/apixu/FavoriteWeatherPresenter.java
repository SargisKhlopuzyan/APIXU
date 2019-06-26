package com.sargis.kh.apixu;

import android.arch.persistence.room.Room;

import com.sargis.kh.apixu.database.WeatherDatabase;
import com.sargis.kh.apixu.database.dao.ItemDAO;
import com.sargis.kh.apixu.database.models.Item;
import com.sargis.kh.apixu.enums.SelectedState;
import com.sargis.kh.apixu.helpers.DataConverter;
import com.sargis.kh.apixu.models.favorite.CurrentWeatherDataModel;
import com.sargis.kh.apixu.models.search.SearchDataModel;
import com.sargis.kh.apixu.network.calls.Data;
import com.sargis.kh.apixu.network.calls.GetDataCallback;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;

public class FavoriteWeatherPresenter implements WeatherContract.Presenter {

    private WeatherContract.View viewCallback;

    private WeatherDatabase database;

    private int selectedItemsCount;

    public FavoriteWeatherPresenter(WeatherContract.View viewCallback) {
        this.viewCallback = viewCallback;
        database = Room.databaseBuilder(App.getAppContext(), WeatherDatabase.class, "WeatherDatabaseDb")
                .allowMainThreadQueries()
                .build();
    }

    @Override
    public void getFavoriteData(SearchDataModel searchDataModel, Long orderIndex) {
        viewCallback.onFavoriteDataLoadingStarted();
        String name = searchDataModel.name.replace(", " + searchDataModel.region + ", " + searchDataModel.country, "");

        ItemDAO itemDAO = database.getItemDAO();
        Item item = itemDAO.getItemByFullName(name, searchDataModel.region, searchDataModel.country);

        if (item != null) {
            viewCallback.onFavoriteDataLoadedWithError("");
            return;
        }

        Data.getCurrentWeatherData(new GetDataCallback<CurrentWeatherDataModel>() {

            @Override
            public void onSuccess(CurrentWeatherDataModel currentWeatherDataModel) {
                currentWeatherDataModel.orderIndex = orderIndex;
                Long id = saveFavoriteDataInDatabase(currentWeatherDataModel);
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
        }, searchDataModel.name);
    }

    @Override
    public void getSearchData(String text) {
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

            String searchQuery = currentWeatherDataModels.get(position).location.name + ", "
                    + currentWeatherDataModels.get(position).location.region + ", "
                    + currentWeatherDataModels.get(position).location.country;

            Data.getCurrentWeatherData(new GetDataCallback<CurrentWeatherDataModel>() {

                @Override
                public void onSuccess(CurrentWeatherDataModel currentWeatherDataModel) {
                    updateFavoritesDataInDatabase(currentWeatherDataModel);

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

        viewCallback.onFavoriteSavedDataUpdated(currentWeatherDataModel);
    }

    @Override
    public void getFavoriteSavedDataFromDatabase() {
        viewCallback.onFavoriteSavedDataLoadingFromDatabaseStarted();
        ItemDAO itemDAO = database.getItemDAO();
        List<Item> items = itemDAO.getItems();
        viewCallback.onFavoriteSavedDataLoadedFromDatabase(DataConverter.convertItemsToCurrentWeatherDataModels(items));
    }

    private Long saveFavoriteDataInDatabase(CurrentWeatherDataModel currentWeatherDataModel) {
        ItemDAO itemDAO = database.getItemDAO();
        Item item = DataConverter.convertCurrentWeatherDataModelToItem(currentWeatherDataModel);
        return itemDAO.insert(item);
    }

    @Override
    public void onFavoriteItemMoved(List<CurrentWeatherDataModel> currentWeatherDataModels, int fromPosition, int toPosition) {
        ItemDAO itemDAO = database.getItemDAO();
        itemDAO.update(DataConverter.convertCurrentWeatherDataModelToItem(currentWeatherDataModels.get(fromPosition)));
        itemDAO.update(DataConverter.convertCurrentWeatherDataModelToItem(currentWeatherDataModels.get(toPosition)));
    }

    @Override
    public void itemSelectedStateChanged(int itemsSize, Boolean isSelected) {
        selectedItemsCount = isSelected ? selectedItemsCount + 1 : selectedItemsCount - 1;
        SelectedState selectedState = selectedItemsCount == 0 ? SelectedState.Unselected : (selectedItemsCount == itemsSize ? SelectedState.AllSelected : SelectedState.Selected);

        viewCallback.setSelectedItemsCount(selectedState, selectedItemsCount);
    }

    @Override
    public void resetSelectedItems(List<CurrentWeatherDataModel> currentWeatherDataModels) {
        selectedItemsCount = 0;
        for (CurrentWeatherDataModel currentWeatherDataModel : currentWeatherDataModels) {
            currentWeatherDataModel.isSelected = false;
        }
        viewCallback.setSelectedItemsCount(SelectedState.Unselected, selectedItemsCount);
    }

    @Override
    public void setAllItemsStateSelected(List<CurrentWeatherDataModel> currentWeatherDataModels, SelectedState selectedState) {

        for (CurrentWeatherDataModel currentWeatherDataModel : currentWeatherDataModels) {
            currentWeatherDataModel.isSelected = selectedState == SelectedState.AllSelected;
        }

        selectedItemsCount = selectedState == SelectedState.AllSelected ? currentWeatherDataModels.size() : 0;
        viewCallback.setSelectedItemsCount(selectedState, selectedItemsCount);
        viewCallback.updateView();
    }

    @Override
    public void deleteFavoriteDataFromDatabase(List<CurrentWeatherDataModel> currentWeatherDataModels, CurrentWeatherDataModel currentWeatherDataModel, int position) {
        ItemDAO itemDAO = database.getItemDAO();
        itemDAO.delete(currentWeatherDataModel.id);

        SelectedState selectedState = selectedItemsCount == 0 ? SelectedState.Unselected : (selectedItemsCount == currentWeatherDataModels.size() ? SelectedState.AllSelected : SelectedState.Selected);

        for (int i = 0; i < position; i++) {
            currentWeatherDataModels.get(i).orderIndex = currentWeatherDataModels.get(i+1).orderIndex;
            itemDAO.update(DataConverter.convertCurrentWeatherDataModelToItem(currentWeatherDataModels.get(i)));
        }

        viewCallback.setSelectedItemsCount(selectedState, selectedItemsCount);
        viewCallback.onFavoriteItemDeleted(position);
    }

    @Override
    public void deleteAllSelectedFavoriteDatesFromDatabase(List<CurrentWeatherDataModel> currentWeatherDataModels) {

        ItemDAO itemDAO = database.getItemDAO();

        for (int i = currentWeatherDataModels.size() - 1; i >= 0 && i < currentWeatherDataModels.size(); i--) {

            if (currentWeatherDataModels.get(i).isSelected) {

                itemDAO.delete(currentWeatherDataModels.get(i).id);
                --selectedItemsCount;
                SelectedState selectedState = selectedItemsCount == 0 ? SelectedState.Unselected : (selectedItemsCount == currentWeatherDataModels.size() ? SelectedState.AllSelected : SelectedState.Selected);

                if (i > 0 && i < currentWeatherDataModels.size()) {
                    currentWeatherDataModels.get(i-1).orderIndex = currentWeatherDataModels.get(i).orderIndex;
                    if (!currentWeatherDataModels.get(i-1).isSelected) {
                        itemDAO.update(DataConverter.convertCurrentWeatherDataModelToItem(currentWeatherDataModels.get(i-1)));
                    }
                }

                viewCallback.setSelectedItemsCount(selectedState, selectedItemsCount);
                viewCallback.onFavoriteItemDeleted(currentWeatherDataModels.indexOf(currentWeatherDataModels.get(i)));

            } else if((i > 0 && i < currentWeatherDataModels.size())
                    && (currentWeatherDataModels.get(i-1).orderIndex != currentWeatherDataModels.get(i).orderIndex + 1)) {

                currentWeatherDataModels.get(i-1).orderIndex = currentWeatherDataModels.get(i).orderIndex + 1;

                if (!currentWeatherDataModels.get(i-1).isSelected) {
                    itemDAO.update(DataConverter.convertCurrentWeatherDataModelToItem(currentWeatherDataModels.get(i-1)));
                }
            }
        }

    }
}
