package com.tannotour.scafflib

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.launch
import kotlin.reflect.KClass

/**
 * Created by mitnick on 2018/9/29.
 * Description
 * @param kClass 数据类型
 */
abstract class VirtualViewModel<T: RepositoryEntity>(kClass: KClass<T>): ViewModel() {

    /* LiveData对象实体，用于通知UI更新 */
    val liveData: MutableLiveData<T> = MutableLiveData()
    /* 数据源 */
    private val repository: Repository<T>? by lazy {
        val repository = RepositoryUtils.obtainRepository(kClass)
        repository?.addObserver(dataChangeFunc)
        repository
    }

    /**
     * 异步获取最新数据
     * @param pairs 参数
     */
    fun remote(vararg pairs: Pair<String, String>){
        GlobalScope.launch {
            remoteFunc(*pairs)?.invoke()
        }
    }

    fun remoteFunc(vararg pairs: Pair<String, String>): (suspend () -> T?)?{
        return repository?.remoteFunc?.invoke(hashMapOf(*pairs))
    }

    /**
     * 数据变化监听回调函数体
     */
    private val dataChangeFunc: (oldValue: T?, newValue: T?) -> Unit = { p1, p2 ->
        onDataChange(p1, p2)
    }

    /**
     * 数据变化监听回调
     */
    fun onDataChange(oldValue: T?, newValue: T?){
        /* 向UI层提供数据的副本 */
        liveData.postValue(newValue.duplicate())
    }

    /**
     * 与该ViewModel对象绑定的观察者们被销毁
     */
    override fun onCleared() {
        /* 将数据保存到硬盘 */
        GlobalScope.launch { repository?.store?.await() }
        /* 取消数据变化监听回调 */
        repository?.removeObserver(dataChangeFunc)
        super.onCleared()
    }

    /**
     * 初始化ViewModel对象
     */
    init {
        /* 恢复缓存到硬盘上的数据 */
        GlobalScope.launch { repository?.reStore?.await() }
    }
}