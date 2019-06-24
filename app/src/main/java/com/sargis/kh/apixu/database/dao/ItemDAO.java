package com.sargis.kh.apixu.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.sargis.kh.apixu.database.models.Item;

import java.util.List;

@Dao
public interface ItemDAO {

//    @Insert
//    void insert(Item... items);

    @Insert
    Long insert(Item item);

//    @Update
//    void update(Item... items);

    @Update
    int update(Item... items);

//    @Query("UPDATE items FROM items WHERE name = :name")
//    void update(Item item, String name);

    @Delete
    void delete(Item item);

    @Query("DELETE FROM items WHERE name = :name")
    void delete(String name);

    @Query("SELECT * FROM items ORDER BY id DESC")
    List<Item> getItems();

    @Query("SELECT * FROM items WHERE id = :id")
    Item getItemById(Long id);

    @Query("SELECT * FROM items WHERE name = :name")
    Item getItemByName(String name);

}
