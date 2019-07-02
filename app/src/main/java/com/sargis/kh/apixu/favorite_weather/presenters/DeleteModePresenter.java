package com.sargis.kh.apixu.favorite_weather.presenters;

import android.arch.persistence.room.Room;

import com.sargis.kh.apixu.App;
import com.sargis.kh.apixu.favorite_weather.enums.DeleteModeSelectedState;
import com.sargis.kh.apixu.favorite_weather.contracts.DeleteModeContract;
import com.sargis.kh.apixu.favorite_weather.database.FavoriteWeatherDatabase;
import com.sargis.kh.apixu.favorite_weather.database.dao.ItemDAO;
import com.sargis.kh.apixu.favorite_weather.helpers.DataConverter;
import com.sargis.kh.apixu.favorite_weather.models.favorite.CurrentWeatherDataModel;

import java.util.ArrayList;
import java.util.List;

public class DeleteModePresenter implements DeleteModeContract.Presenter {

    private DeleteModeContract.View viewCallback;

    private FavoriteWeatherDatabase database;

    private ArrayList<Integer> selectedItemsOrderIndexes = new ArrayList<>();

    public DeleteModePresenter(DeleteModeContract.View viewCallback) {
        this.viewCallback = viewCallback;
        database = Room.databaseBuilder(App.getAppContext(), FavoriteWeatherDatabase.class, "WeatherDatabaseDb")
                .allowMainThreadQueries()
                .build();
    }

    @Override
    public ArrayList<Integer> getSelectedItemsOrderIndexes() {
        return selectedItemsOrderIndexes;
    }

    @Override
    public void setSelectedItemsOrderIndexes(List<CurrentWeatherDataModel> savedCurrentWeatherDataModels, ArrayList<Integer> selectedItemsOrderIndexes) {
        this.selectedItemsOrderIndexes = selectedItemsOrderIndexes;

        for (Integer orderIndex : selectedItemsOrderIndexes) {
            savedCurrentWeatherDataModels.get(savedCurrentWeatherDataModels.size() - orderIndex - 1).isSelected = true;
        }
    }

    @Override
    public void itemSelectedStateChanged(CurrentWeatherDataModel currentWeatherDataModel, int allItemsCount, Boolean isSelected) {
        if (isSelected && !selectedItemsOrderIndexes.contains(currentWeatherDataModel.orderIndex)) {
            selectedItemsOrderIndexes.add(currentWeatherDataModel.orderIndex);
        } else if (selectedItemsOrderIndexes.contains(currentWeatherDataModel.orderIndex)) {
            selectedItemsOrderIndexes.remove(currentWeatherDataModel.orderIndex);
        }

        DeleteModeSelectedState deleteModeSelectedState = selectedItemsOrderIndexes.isEmpty() ? DeleteModeSelectedState.Unselected :
                (selectedItemsOrderIndexes.size() == allItemsCount ? DeleteModeSelectedState.AllSelected : DeleteModeSelectedState.Selected);

        viewCallback.setSelectedItemsCount(deleteModeSelectedState, selectedItemsOrderIndexes.size());
    }

    @Override
    public void resetSelectedItems(List<CurrentWeatherDataModel> currentWeatherDataModels) {
        selectedItemsOrderIndexes.clear();

        for (CurrentWeatherDataModel currentWeatherDataModel : currentWeatherDataModels) {
            currentWeatherDataModel.isSelected = false;
        }
        viewCallback.setSelectedItemsCount(DeleteModeSelectedState.Unselected, selectedItemsOrderIndexes.size());
    }

    @Override
    public void setAllItemsStateSelected(List<CurrentWeatherDataModel> currentWeatherDataModels, DeleteModeSelectedState deleteModeSelectedState) {

        selectedItemsOrderIndexes.clear();

        for (CurrentWeatherDataModel currentWeatherDataModel : currentWeatherDataModels) {
            currentWeatherDataModel.isSelected = deleteModeSelectedState == DeleteModeSelectedState.AllSelected;

            if (deleteModeSelectedState == DeleteModeSelectedState.AllSelected) {
                selectedItemsOrderIndexes.add(currentWeatherDataModel.orderIndex);
            }
        }

        viewCallback.setSelectedItemsCount(deleteModeSelectedState, selectedItemsOrderIndexes.size());
        viewCallback.updateView();
    }

    @Override
    public void deleteAllSelectedFavoriteWeatherDataFromDatabase(List<CurrentWeatherDataModel> currentWeatherDataModels) {

        selectedItemsOrderIndexes.clear();

        ItemDAO itemDAO = database.getItemDAO();

        for (int i = currentWeatherDataModels.size() - 1; i >= 0 && i < currentWeatherDataModels.size(); i--) {

            if (currentWeatherDataModels.get(i).isSelected) {

                itemDAO.delete(currentWeatherDataModels.get(i).id);

                if (i > 0 && i < currentWeatherDataModels.size()) {
                    currentWeatherDataModels.get(i-1).orderIndex = currentWeatherDataModels.get(i).orderIndex;
                    if (!currentWeatherDataModels.get(i-1).isSelected) {
                        itemDAO.update(DataConverter.convertCurrentWeatherDataModelToItem(currentWeatherDataModels.get(i-1)));
                    }
                }

                viewCallback.setSelectedItemsCount(DeleteModeSelectedState.Unselected, selectedItemsOrderIndexes.size());
                viewCallback.onFavoriteWeatherItemDeleted(currentWeatherDataModels.indexOf(currentWeatherDataModels.get(i)));

            } else if((i > 0 && i < currentWeatherDataModels.size()) &&
                    (currentWeatherDataModels.get(i-1).orderIndex != currentWeatherDataModels.get(i).orderIndex + 1)) {

                currentWeatherDataModels.get(i-1).orderIndex = currentWeatherDataModels.get(i).orderIndex + 1;

                if (!currentWeatherDataModels.get(i-1).isSelected) {
                    itemDAO.update(DataConverter.convertCurrentWeatherDataModelToItem(currentWeatherDataModels.get(i-1)));
                }
            }
        }

    }
}
