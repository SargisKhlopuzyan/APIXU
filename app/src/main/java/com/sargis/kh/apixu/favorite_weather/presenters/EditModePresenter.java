package com.sargis.kh.apixu.favorite_weather.presenters;

import android.arch.persistence.room.Room;

import com.sargis.kh.apixu.App;
import com.sargis.kh.apixu.favorite_weather.contracts.EditModeContract;
import com.sargis.kh.apixu.favorite_weather.database.FavoriteWeatherDatabase;
import com.sargis.kh.apixu.favorite_weather.database.dao.ItemDAO;
import com.sargis.kh.apixu.helpers.DataConverter;
import com.sargis.kh.apixu.models.favorite.CurrentWeatherDataModel;

import java.util.List;

public class EditModePresenter implements EditModeContract.Presenter {

    private EditModeContract.View viewCallback;

    private FavoriteWeatherDatabase database;

    public EditModePresenter(EditModeContract.View viewCallback) {
        this.viewCallback = viewCallback;
        database = Room.databaseBuilder(App.getAppContext(), FavoriteWeatherDatabase.class, "WeatherDatabaseDb")
                .allowMainThreadQueries()
                .build();
    }

    @Override
    public void onFavoriteWeatherItemMoved(List<CurrentWeatherDataModel> currentWeatherDataModels, int fromPosition, int toPosition) {
        ItemDAO itemDAO = database.getItemDAO();
        itemDAO.update(DataConverter.convertCurrentWeatherDataModelToItem(currentWeatherDataModels.get(fromPosition)));
        itemDAO.update(DataConverter.convertCurrentWeatherDataModelToItem(currentWeatherDataModels.get(toPosition)));
    }

    @Override
    public void deleteFavoriteWeatherDataFromDatabase(List<CurrentWeatherDataModel> currentWeatherDataModels, CurrentWeatherDataModel currentWeatherDataModel, int position) {
        ItemDAO itemDAO = database.getItemDAO();
        itemDAO.delete(currentWeatherDataModel.id);

        for (int i = 0; i < position; i++) {
            currentWeatherDataModels.get(i).orderIndex = currentWeatherDataModels.get(i+1).orderIndex;
            itemDAO.update(DataConverter.convertCurrentWeatherDataModelToItem(currentWeatherDataModels.get(i)));
        }

        viewCallback.onFavoriteWeatherItemDeleted(position);
    }

}
