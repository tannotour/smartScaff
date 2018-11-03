package com.tannotour.smartscaff.cli

import android.util.LruCache
import com.tannotour.scafflib.cache.DiskCacheMMKV
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation

/**
 * Created by mitnick on 2018/10/25.
 * Description
 */
object RepositoryHelper {

    /* 数据源集合 */
    private val repositoryProviderSet = RepositoryProviderCache()

    /**
     * 获取数据源提供者
     * @param repositoryEntity 数据
     */
    fun <T: Any> fetchRepositoryProvider(repositoryEntity: KClass<out RepositoryEntity<out T>>): T?{
        val anno = repositoryEntity.java.getAnnotation(RepositoryWorker::class.java) ?: return null
        val repositoryProviderClass = anno.repositoryProvider
        val repositoryProviderClassName = repositoryProviderClass.qualifiedName ?: ""
        var repositoryProvider = repositoryProviderSet[repositoryProviderClassName]
        if(repositoryProvider == null){
            repositoryProvider = repositoryProviderClass.java.newInstance()
            repositoryProviderSet.put(repositoryProviderClassName, repositoryProvider)
        }
        return repositoryProvider as T?
    }

    /**
     * LRU缓存数据源对象
     * @param maxSize 在内存中缓存的数据源最大个数
     */
    class RepositoryProviderCache(maxSize: Int = DiskCacheMMKV.invoke { decodeInt("RepositoryCacheCapacity", 16) }): LruCache<String, Any>(maxSize){

        override fun create(key: String?): Any? {
            if(key == null){
                return null
            }
            return Class.forName(key).newInstance()
        }

        override fun sizeOf(key: String?, value: Any?): Int {
            return 1
        }
    }
}