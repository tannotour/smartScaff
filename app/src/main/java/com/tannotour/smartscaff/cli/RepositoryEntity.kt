package com.tannotour.smartscaff.cli

import kotlin.reflect.KClass

/**
 * Created by mitnick on 2018/10/26.
 * Description
 */
interface RepositoryEntity<T: Any> {

    /**
     * 获取数据源提供者
     */
    fun fetchRepositoryProvider(): KClass<out T>?{
        val provider = this::class.java.getAnnotation(RepositoryWorker::class.java).repositoryProvider
        return provider as KClass<out T>
    }

    /**
     * 获取数据源提供者类名
     */
    fun fetchRepositoryProviderName(): String{
        val provider = this::class.java.getAnnotation(RepositoryWorker::class.java).repositoryProvider
        return provider.qualifiedName ?: ""
    }
}