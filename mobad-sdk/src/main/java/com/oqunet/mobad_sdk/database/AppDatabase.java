package com.oqunet.mobad_sdk.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.oqunet.mobad_sdk.database.dao.AdDao;
import com.oqunet.mobad_sdk.database.dao.CarouselAdItemDao;
import com.oqunet.mobad_sdk.database.dao.ExtraAdDao;
import com.oqunet.mobad_sdk.database.dao.UserDao;
import com.oqunet.mobad_sdk.database.entity.Ad;
import com.oqunet.mobad_sdk.database.entity.CarouselAdItem;
import com.oqunet.mobad_sdk.database.entity.ExtraAd;
import com.oqunet.mobad_sdk.database.entity.User;


@Database(entities = {User.class, Ad.class, CarouselAdItem.class, ExtraAd.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static final String DB_NAME = "mobad_sdk_db";

    private static AppDatabase appDatabase;

    public abstract UserDao getUserDao();
    public abstract AdDao getAdDao();
    public abstract ExtraAdDao getExtraAdDao();

    public abstract CarouselAdItemDao getCarouselAdItemDao();

    public static AppDatabase getInstance(Context context){

        if(appDatabase == null){
            appDatabase = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, DB_NAME)
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return appDatabase;
    }

    public static void destroyInstance() {
        appDatabase = null;
    }


}
