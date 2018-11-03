package com.tannotour.scafflib

import com.tannotour.scafflib.convert.Converter
import kotlin.reflect.KClass

/**
 * 获取副本
 * @param converter 转化功能提供者
 */
fun <T: Any> T?.duplicate(converter: Converter = Converter.obtain()): T?{
    if(this == null){
        return null
    }
    val json = converter.toJson(this)
    val duplicate = converter.convert(json, this::class)
    converter.release()
    return duplicate
}

/**
 * 对象转json
 * @param converter 转化功能提供者
 */
fun <T: Any> T?.toJson(converter: Converter = Converter.obtain()): String{
    if(this == null){
        return ""
    }
    val json = converter.toJson(this)
    converter.release()
    return json
}

/**
 * json转对象
 * @param converter 转化功能提供者
 */
fun <T: Any> KClass<T>.toObj(json: String, converter: Converter = Converter.obtain()): T?{
    val obj = converter.convert(json, this)
    converter.release()
    return obj
}

/**
 * 更新数据源对应的数据，并通知观察者
 * @param up 更新函数
 */
infix fun <T: RepositoryEntity> KClass<T>.update(up: T?.()->Unit): KClass<T>{
    val repository = RepositoryUtils.obtainRepository(this)
    val oldValue = repository?.repositoryData?.duplicate()
    repository?.repositoryData?.up()
    repository?.notifyAllObserver(oldValue, repository.repositoryData)
    return this
}

/**
 * 获取对应数据源的数据的副本
 */
fun <T: RepositoryEntity> KClass<T>.fetchRepositoryData(): T?{
    val repository = RepositoryUtils.obtainRepository(this)
    val repositoryData = repository?.repositoryData
    return repositoryData?.duplicate()
}