package com.tannotour.smartscaff.cli

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import android.arch.paging.DataSource
import android.arch.paging.LivePagedListBuilder
import android.arch.paging.PagedList
import android.arch.persistence.room.RoomDatabase
import android.support.v4.app.FragmentActivity
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.launch
import kotlin.reflect.KClass

/**
 * 观察者注册数据源流程
 */

/**
 * 同步方式执行数据源
 * @param source 数据源
 */
infix fun <T: RoomDatabase, R> KClass<T>.source(source: T.() -> R?): R?{
    val db = DBHelper.obtainDB(this)
    val result = db?.source()
    return result
}

/**
 * 获取数据源提供者
 * @param kClass 数据源提供者
 */
infix fun <T: RoomDatabase> FragmentActivity.fetch(kClass: KClass<T>): FetchResult<T>{
    val db = DBHelper.obtainDB(kClass)
    return FetchResult(this, db)
}

/**
 * 获取数据源
 * @param source 数据源
 */
infix fun <T: RoomDatabase, R> FetchResult<T>.source(source: T.() -> LiveData<R>): SourceResult<R>{
    val liveData = db?.source()
    return SourceResult(observer, liveData)
}

/**
 * 监听数据源
 * @param onChanged 数据变化回调
 */
infix fun <T> SourceResult<T>.register(onChanged: (T?) -> Unit){
    liveData?.observe(observer, Observer {
        onChanged(it)
    })
}

data class FetchResult<T: RoomDatabase>(
        val observer: FragmentActivity,
        val db: T?
)

data class SourceResult<T>(
        val observer: FragmentActivity,
        val liveData: LiveData<T>?
)

/**
 * 将DataSource转为LiveData
 * @param pageSize 显示UI时一页的大小
 * @param enablePlaceHolders 是否启用PlaceHolders
 * @param initialPageCount 初始化显示的页数
 */
fun <T, R> DataSource.Factory<T, R>.liveData(pageSize: Int = 10, enablePlaceHolders: Boolean = false, initialPageCount: Int = 3, boundaryCallback: PagedList.BoundaryCallback<R>? = null): LiveData<PagedList<R>>{
    val builder = LivePagedListBuilder(
            this,
            PagedList.Config.Builder()
            .setPageSize(pageSize)
            .setEnablePlaceholders(enablePlaceHolders)
            .setInitialLoadSizeHint(initialPageCount * pageSize)
            .build()
    )
    if(boundaryCallback != null){
        builder.setBoundaryCallback(boundaryCallback)
    }
    return builder.build()
}

/**
 * 调用数据源方法流程
 */

/**
 * 直接执行异步任务
 * @param execute 异步任务
 */
infix fun <T: Any, R> KClass<out RepositoryEntity<T>>.remote(execute: suspend T.() -> R?): Job?{
    val repository = RepositoryHelper.fetchRepositoryProvider(this)
    return if(repository == null){
        null
    }else{
        GlobalScope.launch{
            repository.execute()
        }
    }
}

/**
 * 将异步任务绑定到UI的生命周期中
 * @param lifecycleOwner 生命周期提供者
 */
infix fun Job?.attachLife(lifecycleOwner: LifecycleOwner){
    if(this == null){
        return
    }
    val jobController = JobController(this, lifecycleOwner)
    lifecycleOwner.lifecycle.addObserver(jobController)
}

/**
 * 同步的方式执行任务
 * 次方法必须在协程中运行
 * @param execute 任务
 */
suspend infix fun <T: Any, R> KClass<out RepositoryEntity<T>>.executeSyncSuspend(execute: suspend T.() -> R?): R?{
    val repository = RepositoryHelper.fetchRepositoryProvider(this)
    return repository?.execute()
}

/**
 * 同步的方式执行任务
 * 次方法必须在协程中运行
 * @param execute 任务
 */
infix fun <T: Any, R> KClass<out RepositoryEntity<T>>.executeSync(execute: T.() -> R?): R?{
    val repository = RepositoryHelper.fetchRepositoryProvider(this)
    return repository?.execute()
}
