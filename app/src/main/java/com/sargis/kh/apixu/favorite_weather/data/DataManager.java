package com.sargis.kh.apixu.favorite_weather.data;

import android.content.Context;

import com.sargis.kh.apixu.di.ApplicationContext;
import com.sargis.kh.apixu.favorite_weather.database.dao.ItemDAO;
import com.sargis.kh.apixu.favorite_weather.database.models.Item;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class DataManager {

    private Context context;
    private ItemDAO itemDAO;
    private SharedPrefsHelper sharedPrefsHelper;

    @Inject
    public DataManager(@ApplicationContext Context context, ItemDAO itemDAO, SharedPrefsHelper sharedPrefsHelper) {
        this.context = context;
        this.itemDAO = itemDAO;
        this.sharedPrefsHelper = sharedPrefsHelper;
    }

    //DataBase
    public Long insert(Item item) {
        return itemDAO.insert(item);
    }

    public int update(Item... items) {
        return itemDAO.update(items);
    }

    public int delete(Long id) {
        return itemDAO.delete(id);
    }

    public List<Item> getItems() {
        return itemDAO.getItems();
    }

    public Item getItemById(Long id) {
        return itemDAO.getItemById(id);
    }

    public Item getItemByFullName(String name, String region, String country) {
        return itemDAO.getItemByFullName(name, region, country);
    }

    //Resources
    public String getString(int id) {
        return context.getString(id);
    }

    //Context
    public Context getContext() {
        return context;
    }

}
