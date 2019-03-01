package com.oqunet.admob_sdk.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.oqunet.admob_sdk.database.entity.CarouselAdItem;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface CarouselAdItemDao {

    @Insert
    void insertCarouselItem(CarouselAdItem... carouselItem);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllCarouselItems(List<CarouselAdItem> carouselItems);

    @Update
    void updateCarouselItem(CarouselAdItem... carouselItem);

    @Query("SELECT * FROM carouseladitem")
    List<CarouselAdItem> loadCarouselItems();

    @Query("select * from carouseladitem where title = :title")
    CarouselAdItem loadCarouselItem(String title);

    @Delete
    void deleteCarouselItem(CarouselAdItem... carouselItem);

    @Query("DELETE FROM carouseladitem")
    void deleteTable();
}
