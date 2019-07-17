package com.sargis.kh.apixu.favorite_weather.presenters;

import com.sargis.kh.apixu.favorite_weather.contracts.DeleteModeContract;
import com.sargis.kh.apixu.favorite_weather.data.DataManager;
import com.sargis.kh.apixu.favorite_weather.enums.DeleteModeSelectedState;
import com.sargis.kh.apixu.favorite_weather.helpers.DataConverter;
import com.sargis.kh.apixu.favorite_weather.models.favorite.CurrentWeatherDataModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class DeleteModePresenter implements DeleteModeContract.Presenter {

    private DeleteModeContract.View viewCallback;

    private ArrayList<Integer> selectedItemsOrderIndexes = new ArrayList<>();

    @Inject
    DataManager dataManager;

    @Inject
    public DeleteModePresenter(DeleteModeContract.View viewCallback) {
        this.viewCallback = viewCallback;
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

        for (int i = currentWeatherDataModels.size() - 1; i >= 0 && i < currentWeatherDataModels.size(); i--) {

            if (currentWeatherDataModels.get(i).isSelected) {

                dataManager.delete(currentWeatherDataModels.get(i).id);

                if (i > 0 && i < currentWeatherDataModels.size()) {
                    currentWeatherDataModels.get(i-1).orderIndex = currentWeatherDataModels.get(i).orderIndex;
                    if (!currentWeatherDataModels.get(i-1).isSelected) {
                        dataManager.update(DataConverter.convertCurrentWeatherDataModelToItem(currentWeatherDataModels.get(i-1)));
                    }
                }

                viewCallback.setSelectedItemsCount(DeleteModeSelectedState.Unselected, selectedItemsOrderIndexes.size());
                viewCallback.onFavoriteWeatherItemDeleted(currentWeatherDataModels.indexOf(currentWeatherDataModels.get(i)));

            } else if((i > 0 && i < currentWeatherDataModels.size()) &&
                    (currentWeatherDataModels.get(i-1).orderIndex != currentWeatherDataModels.get(i).orderIndex + 1)) {

                currentWeatherDataModels.get(i-1).orderIndex = currentWeatherDataModels.get(i).orderIndex + 1;

                if (!currentWeatherDataModels.get(i-1).isSelected) {
                    dataManager.update(DataConverter.convertCurrentWeatherDataModelToItem(currentWeatherDataModels.get(i-1)));
                }
            }
        }

    }
}
