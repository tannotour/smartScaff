package com.tannotour.scafflib

import com.tannotour.scafflib.log.logE
import kotlin.reflect.KClass

/**
 * Created by mitnick on 2018/10/8.
 * Description
 */
object ViewModelUtils {

    /* 保存数据类与viewModel的映射 */
    private val viewModelMap = HashMap<String, String>()

    /**
     * 添加ViewModel包名
     * @param clazz 数据类
     * @param path 包名
     */
    fun <T: RepositoryEntity> addViewModel(clazz: KClass<T>, path: String){
        val dataName = clazz.qualifiedName ?: ""
        viewModelMap[dataName] = path
    }

    /**
     * 添加数据源对象
     * @param kClassPath 数据类路径
     * @param viewModelPath viewmodel路径
     */
    fun addViewModel(kClassPath: String, viewModelPath: String){
        if(viewModelMap.containsKey(kClassPath)){
            logE("已存在ViewModel映射 -> $kClassPath : ${viewModelMap[kClassPath]} 覆盖为 -> $kClassPath : $viewModelPath")
        }
        logE("添加ViewModel映射 -> $kClassPath : $viewModelPath")
        viewModelMap[kClassPath] = viewModelPath
    }

    /**
     * 获取对应的ViewModel
     * @param clazz 数据类
     */
    fun <T: RepositoryEntity> obtainViewModelClass(clazz: KClass<T>): KClass<VirtualViewModel<T>>?{
        val dataName = clazz.qualifiedName
        val viewModelClassName = viewModelMap[dataName] ?: ""
        if(viewModelClassName.isEmpty()){
            return null
        }else{
            val kClazz = Class.forName(viewModelClassName).kotlin as KClass<VirtualViewModel<T>>
            return kClazz
        }
    }
}