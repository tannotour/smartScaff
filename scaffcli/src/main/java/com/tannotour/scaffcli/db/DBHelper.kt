package com.tannotour.scaffcli.db

import android.app.Application
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import kotlin.reflect.KClass

/**
 * Created by mitnick on 2018/10/24.
 * Description
 */
object DBHelper {

    var application: Application? = null

    private val dbMap = HashMap<String, RoomDatabase>()

    fun <T: RoomDatabase> obtainDB(kClass: KClass<T>): T?{
        if(application == null){
            return null
        }else if(dbMap.containsKey(kClass.qualifiedName ?: "")){
            return dbMap[kClass.qualifiedName ?: ""] as T?
        }else{
            val db = Room.databaseBuilder(application!!.applicationContext, kClass.java, kClass.simpleName ?: "roomDB").build()
            dbMap[kClass.qualifiedName ?: ""] = db
            return db
        }
    }
}