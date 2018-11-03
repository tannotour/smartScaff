package com.tannotour.smartscaff.cli

import android.arch.persistence.room.RoomDatabase
import retrofit2.Response
import kotlin.reflect.KClass

/**
 * Created by mitnick on 2018/9/30.
 * Description 数据提供者
 */
abstract class Repository<T: Any, R: RoomDatabase>(
        private val dbClass: KClass<R>
) {
    /* 数据库DAO接口 */
    val dao: T? by lazy {
        obtainSource(DBHelper.obtainDB(dbClass))
    }

    /**
     * 获取数据源
     * @param local 本地数据库
     */
    protected abstract fun obtainSource(local: R?): T?

    /**
     * 操作数据持久层
     * @param operate 具体的操作
     */
    protected fun operate(operate: T.() -> Unit){
        dao?.operate()
    }

    /**
     * 更新数据
     * @param operate 更新数据的DSL
     */
    protected infix fun <M> Response<M>?.operate(operate: T.(M?)->Unit): Response<M>?{
        if(this != null){
            dao?.operate(this.body())
        }
        return this
    }

}