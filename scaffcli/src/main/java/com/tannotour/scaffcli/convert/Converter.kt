package com.tannotour.scaffcli.convert

import android.support.v4.util.Pools.SynchronizedPool
import android.util.Log
import com.google.gson.Gson
import kotlin.reflect.KClass


/**
 * Created by mitnick on 2018/2/15.
 * Description
 */
class Converter {

    private val TAG = "Converter"
    private val gson: Gson by lazy { Gson() }

    companion object {
        /* 对象池 */
        private val pool = SynchronizedPool<Converter>(3)

        /**
         * 获取对象
         */
        fun obtain(): Converter {
            var instance = pool.acquire()
            if(instance == null){
                instance = Converter()
            }
            return instance
        }
    }

    /**
     * 释放对象
     */
    fun release(){
        pool.release(this)
    }

    fun <T: Any> convert(json: String, clazz: KClass<T>): T?{
        var result: T? = null
        try {
            if(json.isNotEmpty()){
                result = gson.fromJson(json, clazz.java)
            }else{
                Log.e(TAG, "json为空，转化失败")
            }
        }catch (e: Exception){
            e.printStackTrace()
        }finally {
            return result
        }
    }

    fun toJson(obj: Any?): String{
        if(obj == null){
            return ""
        }else{
            return gson.toJson(obj)
        }
    }

    fun <T: Any> copy(obj: T?): T?{
        if(obj == null){
            return null
        }
        val json = toJson(obj)
        val duplicate = convert(json, obj::class)
        return duplicate
    }
}