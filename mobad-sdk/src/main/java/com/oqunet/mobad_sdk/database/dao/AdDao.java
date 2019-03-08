package com.oqunet.mobad_sdk.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.oqunet.mobad_sdk.database.entity.Ad;

import java.util.List;

@Dao
public interface AdDao {

    @Insert
    void insertAd(Ad... ad);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllAds(List<Ad> ads);

    @Update
    void updateAd(Ad... ad);

    @Query("SELECT * FROM ad")
    List<Ad> loadAds();

    @Query("select * from ad where ad_id = :adId")
    Ad loadAd(int adId);

    @Delete
    void deleteAd(Ad... ad);

    @Query("DELETE FROM ad")
    void deleteTable();
}
