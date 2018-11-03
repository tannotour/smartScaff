package com.tannotour.smartscaff.room

import android.arch.lifecycle.LiveData
import android.arch.paging.DataSource
import android.arch.persistence.room.*

/**
 * Created by mitnick on 2018/10/23.
 * Description
 */
@Dao
interface RoomUserDao {

    @Query("select * from room_user")
    fun all(): DataSource.Factory<Int, RoomUser>

    @Query("select * from room_user limit :pageSize offset :page*:pageSize")
    fun allPaged(page: Int, pageSize: Int): List<RoomUser>

    @Query("select * from room_user limit :requestedLoadSize offset :requestedStartPosition")
    fun positionAll(requestedLoadSize: Int, requestedStartPosition: Int): List<RoomUser>

    @Query("select * from room_user")
    fun getAll(): LiveData<List<RoomUser>>

    @Query("select * from room_user where user_phone = :userPhone")
    fun fetch(userPhone: String): LiveData<List<RoomUser>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg roomUsers: RoomUser)

    @Delete
    fun delete(roomUser: RoomUser)

    @Update
    fun update(roomUser: RoomUser)

    @Query("delete from room_user")
    fun clear()
}