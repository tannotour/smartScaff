package com.tannotour.scafflib

import com.tannotour.scafflib.cache.DiskCacheMMKV
import com.tannotour.scafflib.cache.IDiskCache
import kotlinx.coroutines.experimental.*
import retrofit2.Response
import kotlin.reflect.KClass

/**
 * Created by mitnick on 2018/9/30.
 * Description 数据提供者。注意：对于同一个数据，最多只能存在一个Repository
 */
abstract class Repository<T: RepositoryEntity>(
        private val kClass: KClass<T>,
        private val defaultParams: Map<String, String>? = null
) {

    /* 数据对象 */
    var repositoryData: T? = null
        get() {
            if(field == null){
                field = kClass.java.newInstance()
            }
            return field
        }
        set(value) {
            val oldValue = repositoryData?.duplicate()
            observers.forEach {
                it.invoke(oldValue, value)
            }
            field = value
        }
    /* 监听数据变化的ViewModel们 */
    private val observers = HashSet<(oldValue: T?, newValue: T?) -> Unit>()
    /* 硬盘缓存 */
    private val diskCache: IDiskCache = DiskCacheMMKV

    /* 从硬盘中读取数据 */
    val reStore = GlobalScope.async(Dispatchers.Default, CoroutineStart.LAZY){
        val key = this@Repository::class.qualifiedName?: ""
        repositoryData = diskCache.readFromDisk(key, kClass)
    }

    /* 保存数据到硬盘 */
    val store = GlobalScope.async(Dispatchers.Default, CoroutineStart.LAZY){
        val key = this@Repository::class.qualifiedName?: ""
        diskCache.writeToDisk(key, repositoryData)
    }

    /**
     * 增加数据变化回调
     * @param observer 数据变化回调
     */
    fun addObserver(observer: (oldValue: T?, newValue: T?) -> Unit){
        observers.add(observer)
    }

    /**
     * 移除数据变化回调
     * @param observer 数据变化回调
     */
    fun removeObserver(observer: (oldValue: T?, newValue: T?) -> Unit){
        observers.remove(observer)
    }

    /**
     * 通知所有观察者（不会更新数据）
     * @param oldValue 旧数据
     * @param newValue 新数据
     */
    fun notifyAllObserver(oldValue: T?, newValue: T?){
        observers.forEach {
            it.invoke(oldValue, newValue)
        }
    }

    /**
     * 更新数据
     * @param newData 更新数据的DSL
     */
    protected infix fun Response<T>?.set(newData: T.()->Unit): Response<T>?{
        if(this == null){
            return null
        }
        val oldValue = repositoryData?.duplicate()
        this.body()?.newData()
        observers.forEach {
            it.invoke(oldValue, repositoryData)
        }
        return this
    }

    /**
     * 获取最新数据
     */
    protected open suspend fun remote(params: HashMap<String, String>): T?{
        return null
    }

    /* 获取最新数据函数指针 */
    val remoteFunc = fun(params: HashMap<String, String>): suspend () -> T?{
        /* 添加默认参数 */
        val pBuffer = HashMap<String, String>(params.size + (defaultParams?.size ?: 0))
        pBuffer.putAll(params)
        if(defaultParams != null){
            pBuffer.putAll(defaultParams)
        }
        return {
            remote(pBuffer)
        }
    }
}