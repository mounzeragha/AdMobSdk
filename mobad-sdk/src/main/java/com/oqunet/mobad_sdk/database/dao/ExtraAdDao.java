package com.oqunet.mobad_sdk.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.oqunet.mobad_sdk.database.entity.Ad;
import com.oqunet.mobad_sdk.database.entity.ExtraAd;

import java.util.List;

@Dao
public interface ExtraAdDao {

    @Insert
    void insertExtraAd(ExtraAd... ad);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllExtraAds(List<ExtraAd> ads);

    @Update
    void updateExtraAd(ExtraAd... ad);

    @Query("SELECT * FROM extraad")
    List<ExtraAd> loadExtraAds();

    @Query("select * from extraad where ad_id = :adId")
    ExtraAd loadExtraAd(int adId);

    @Delete
    void deleteExtraAd(ExtraAd... ad);

    @Query("DELETE FROM extraad")
    void deleteTable();
}
