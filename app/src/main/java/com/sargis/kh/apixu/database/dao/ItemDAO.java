package com.sargis.kh.apixu.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.sargis.kh.apixu.database.models.Item;

import java.util.List;

@Dao
public interface ItemDAO {

    @Insert
    Long insert(Item item);

    @Update
    int update(Item... items);

    @Query("DELETE FROM items WHERE id = :id")
    int delete(Long id);

    @Query("SELECT * FROM items ORDER BY order_index DESC")
    List<Item> getItems();

    @Query("SELECT * FROM items WHERE id = :id")
    Item getItemById(Long id);

    @Query("SELECT * FROM items WHERE name = :name AND region = :region AND country = :country")
    Item getItemByFullName(String name, String region, String country);

}
