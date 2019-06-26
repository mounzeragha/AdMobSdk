package com.oqunet.mobad_sdk.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.oqunet.mobad_sdk.database.entity.Ad;
import com.oqunet.mobad_sdk.database.entity.User;

import java.util.List;

@Dao
public interface UserDao {

    @Insert
    void insertUser(User... user);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllUsers(List<User> users);

    @Update
    void updateUser(User... user);

    @Query("SELECT * FROM user")
    List<User> loadUsers();

    @Delete
    void deleteUser(User... user);

    @Query("DELETE FROM user")
    void deleteTable();
}
