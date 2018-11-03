package com.tannotour.scafflib

import com.tannotour.scaffanno.table.IRepositoryProviderTable
import com.tannotour.scaffanno.table.IViewModelProviderTable
import com.tannotour.scafflib.log.logE

/**
 * Created by mitnick on 2018/10/20.
 * Description
 */
class ScaffInit {

    /**
     * 初始化框架
     * @param modules 模块名若为null则表示是单模块项目
     */
    fun initScaff(modules: List<String>? = null){
        if(modules == null){
            load("df")
        }else{
            modules.forEach {
                load(it)
            }
        }
    }

    /**
     * 加载模块
     * @param module 模块名
     */
    private fun load(module: String){
        /* 加载Repository */
        val repositoryProviders = exists("com.tannotour.smartscaff.$module.repository.RepositoryProviderTable")
        if(repositoryProviders != null){
            val repositoryProvidersInstance = repositoryProviders.newInstance() as IRepositoryProviderTable
            repositoryProvidersInstance.methods().forEach {
                RepositoryUtils.addRepositoryMap(it.key, it.value)
            }
        }else{
            logE("初始化Repository，未找到模块 $module 对应的描述文件")
        }
        /* 加载VirtualViewModel */
        val viewModelProviders = exists("com.tannotour.smartscaff.$module.viewmodel.ViewModelProviderTable")
        if(viewModelProviders != null){
            val viewModelProvidersInstance = viewModelProviders.newInstance() as IViewModelProviderTable
            viewModelProvidersInstance.methods().forEach {
                ViewModelUtils.addViewModel(it.key, it.value)
            }
        }else{
            logE("初始化VirtualViewModel，未找到模块 $module 对应的描述文件")
        }
    }

    /**
     * 获取类，若不存在类则返回null
     * @param clazzName 完整类名
     */
    private fun exists(clazzName: String): Class<*>?{
        return try{
            Class.forName(clazzName)
        }catch (e: Exception){
            null
        }
    }
}