package com.sargis.kh.apixu.favorite_weather.helpers;

import com.sargis.kh.apixu.favorite_weather.database.models.Item;
import com.sargis.kh.apixu.favorite_weather.models.favorite.Condition;
import com.sargis.kh.apixu.favorite_weather.models.favorite.Current;
import com.sargis.kh.apixu.favorite_weather.models.favorite.CurrentWeatherDataModel;
import com.sargis.kh.apixu.favorite_weather.models.favorite.Location;

import java.util.ArrayList;
import java.util.List;

public class DataConverter {

    public static List<CurrentWeatherDataModel> convertItemsToCurrentWeatherDataModels(List<Item> items) {
        List<CurrentWeatherDataModel> currentWeatherDataModels = new ArrayList<>();
        for (Item item: items) {
            currentWeatherDataModels.add(convertItemToCurrentWeatherDataModel(item));
        }
        return currentWeatherDataModels;
    }

    public static CurrentWeatherDataModel convertItemToCurrentWeatherDataModel(Item item) {
        CurrentWeatherDataModel currentWeatherDataModel = new CurrentWeatherDataModel();
        currentWeatherDataModel.id = item.getId();
        currentWeatherDataModel.orderIndex = item.getOrder_index();
        currentWeatherDataModel.current = new Current();
        currentWeatherDataModel.location = new Location();
        currentWeatherDataModel.location.name = item.getName();
        currentWeatherDataModel.location.region = item.getRegion();
        currentWeatherDataModel.location.country = item.getCountry();
        currentWeatherDataModel.current.condition = new Condition();
        currentWeatherDataModel.current.condition.text = item.getCondition_text();
        currentWeatherDataModel.current.temp_c = item.getTemp_c();
        currentWeatherDataModel.current.wind_kph = item.getWind_kph();
        currentWeatherDataModel.current.wind_dir = item.getWind_dir();
        currentWeatherDataModel.current.condition.icon = item.getIcon();
        return currentWeatherDataModel;
    }

    public static Item convertCurrentWeatherDataModelToItem(CurrentWeatherDataModel currentWeatherDataModel) {
        Item item = new Item();
        item.setId(currentWeatherDataModel.id);
        item.setOrder_index(currentWeatherDataModel.orderIndex);
        item.setName(currentWeatherDataModel.location.name);
        item.setRegion(currentWeatherDataModel.location.region);
        item.setCountry(currentWeatherDataModel.location.country);
        item.setCondition_text(currentWeatherDataModel.current.condition.text);
        item.setTemp_c(currentWeatherDataModel.current.temp_c);
        item.setWind_kph(currentWeatherDataModel.current.wind_kph);
        item.setWind_dir(currentWeatherDataModel.current.wind_dir);
        item.setIcon(currentWeatherDataModel.current.condition.icon);
        return item;
    }

}
