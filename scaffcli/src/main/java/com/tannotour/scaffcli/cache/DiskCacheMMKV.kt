package com.tannotour.scaffcli.cache

import android.app.Application
import android.content.Context
import com.tannotour.scaffcli.log.logE
import com.tannotour.scaffcli.toJson
import com.tannotour.scaffcli.toObj
import com.tencent.mmkv.MMKV
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import kotlin.collections.ArrayList
import kotlin.reflect.KClass

/**
 * Created by mitnick on 2018/10/8.
 * Description
 */
object DiskCacheMMKV: IDiskCache {

    override fun init(application: Application) {
        val path = MMKV.initialize(application)
        logE("初始化缓存，缓存路径为：$path")
    }

    override fun <T : Any> readFromDisk(key: String, clazz: KClass<T>): T? {
        val json = MMKV.defaultMMKV().decodeString(encode(key), "")
        if(json.isEmpty()){
            logE("读取到的[$key , $clazz]缓存为空，自动创建对象")
            return null
        }else{
            logE("读取到的[$key , $clazz]缓存为：$json")
            return clazz.toObj(json)
        }
    }

    override fun <T : Any> readListFromDisk(key: String, clazz: KClass<T>): List<T> {
        val json = MMKV.defaultMMKV().decodeString(encode(key), "")
        val result = ArrayList<T>()
        if(json.isEmpty()){
            logE("读取到的[$key , $clazz]缓存为空，自动创建对象")
        }else{
            logE("读取到的[$key , $clazz]缓存为：$json")
            val obj = ArrayList::class.toObj(json)
            obj?.forEach {
                val json0 = it.toJson()
                val data = clazz.toObj(json0)
                if(data != null){
                    result.add(data)
                }
            }
        }
        return result
    }

    override fun writeToDisk(key: String, obj: Any?): Boolean {
        val json = obj?.toJson()
        val result = MMKV.defaultMMKV().encode(encode(key), json)
        if(result){
            logE("写入[$key , $json]缓存成功")
        }else{
            logE("写入[$key , $json]缓存失败")
        }
        return result
    }

    override fun remove(context: Context, key: String?) {
        if(key == null){
            logE("清空缓存")
            MMKV.defaultMMKV().clearAll()
        }else{
            logE("删除[$key]缓存")
            MMKV.defaultMMKV().removeValueForKey(encode(key))
        }
    }

    override fun totalSize(): String {
        val size = MMKV.defaultMMKV().totalSize()
        val formatSize = formetFileSize(size)
        logE("缓存总大小为：$formatSize")
        return formatSize
    }

    fun <T> invoke(method: MMKV.() -> T): T{
        val result = MMKV.defaultMMKV().method()
        return result
    }

    private fun <T> obj2Bytes(obj: T?): ByteArray{
        val out = ByteArrayOutputStream()
        val sOut = ObjectOutputStream(out)
        sOut.writeObject(obj)
        sOut.flush()
        val bytes = out.toByteArray()
        return bytes
    }

    private fun <T> bytes2Obj(bytes: ByteArray): T{
        val inStream = ByteArrayInputStream(bytes)
        val sInStream = ObjectInputStream(inStream)
        val obj = sInStream.readObject() as T
        return obj
    }
}