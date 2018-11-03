package com.tannotour.smartscaff.room

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase

/**
 * Created by mitnick on 2018/10/23.
 * Description
 */
@Database(entities = [RoomUser::class], version = 1, exportSchema = false)
abstract class RoomUserDataBase: RoomDatabase() {

    abstract fun roomUserDao(): RoomUserDao

}