package com.tannotour.scafflib

import android.util.LruCache
import com.tannotour.scafflib.cache.DiskCacheMMKV
import com.tannotour.scafflib.log.logE
import kotlin.collections.HashMap
import kotlin.reflect.KClass

/**
 * Created by mitnick on 2018/9/30.
 * Description
 */
object RepositoryUtils {

    /* 数据源对象缓存 */
    private val repositorySet = RepositoryCache()
    /* 数据源对象映射 */
    private val repositoryMap = HashMap<String, String>()

    /**
     * 获取数据源对象
     * @param kClass 数据类
     */
    fun <T: RepositoryEntity> obtainRepository(kClass: KClass<T>): Repository<T>?{
        /* 动态添加数据源 */
        val kClassName = kClass.qualifiedName ?: ""
        var result = repositorySet[kClassName] as Repository<T>?
        if(result == null){
            result = fetchRepository(kClassName) as Repository<T>?
//            val className = repositoryMap[kClass.qualifiedName]
//            if(className != null){
//                val instance = Class.forName(className).newInstance() as Repository<T>
//                repositorySet.put(kClass.qualifiedName, instance)
//                result = instance
//            }
        }else{

        }
        return result
    }

    /**
     * 获取数据源对象
     * @param kClassName 数据类名
     */
    private fun fetchRepository(kClassName: String?): Any?{
        /* 动态添加数据源 */
        if(kClassName == null){
            return null
        }
        val className = repositoryMap[kClassName]
        if(className != null){
            val instance = Class.forName(className).newInstance()
            repositorySet.put(kClassName, instance as Repository<out RepositoryEntity>?)
            return instance
        }
        return null
    }

    /**
     * 添加数据源对象
     * @param kClass 数据类
     */
    fun <T: RepositoryEntity> addRepositoryMap(kClass: KClass<T>, path: String){
        val name = kClass.qualifiedName ?: ""
        repositoryMap[name] = path
    }

    /**
     * 添加数据源对象
     * @param kClassPath 数据类路径
     * @param repositoryProviderPath 数据提供者路径
     */
    fun addRepositoryMap(kClassPath: String, repositoryProviderPath: String){
        if(repositoryMap.containsKey(kClassPath)){
            logE("已存在Repository映射 -> $kClassPath : ${repositoryMap[kClassPath]} 覆盖为 -> $kClassPath : $repositoryProviderPath")
        }
        logE("添加Repository映射 -> $kClassPath : $repositoryProviderPath")
        repositoryMap[kClassPath] = repositoryProviderPath
    }

    /**
     * 添加数据源对象
     * @param kClass 数据类
     * @param repository 数据源对象
     */
    @Deprecated("暂时无用")
    fun <T: RepositoryEntity> addRepository(kClass: KClass<T>, repository: Repository<T>){
        if(repositorySet[kClass.qualifiedName] == null){
            repositorySet.put(kClass.qualifiedName, repository)
        }
    }

    /**
     * LRU缓存数据源对象
     * @param maxSize 在内存中缓存的数据源最大个数
     */
    class RepositoryCache(maxSize: Int = DiskCacheMMKV.invoke { decodeInt("RepositoryCacheCapacity", 8) }): LruCache<String, Repository<out RepositoryEntity>>(maxSize){

        override fun create(key: String?): Repository<out RepositoryEntity>? {
            val repository = fetchRepository(key) as Repository<out RepositoryEntity>?
            return repository
        }

        override fun sizeOf(key: String, value: Repository<out RepositoryEntity>): Int {
            return 1
        }
    }
}