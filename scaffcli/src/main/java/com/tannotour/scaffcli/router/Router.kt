package com.tannotour.scaffcli.router

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import com.tannotour.scaffcli.router.annos.ForegroundService
import com.tannotour.scaffcli.router.annos.PathDynamic
import com.tannotour.smart_lib_annotation.IRouteTable
import kotlin.reflect.jvm.jvmName

/**
 * Created by mitnick on 2018/4/10.
 * Description
 */
object Router {

    private val TAG = "Router"
    /* 路由表 */
    private val routerTable = HashMap<String, String>()
    /* fragment缓存 */
    private val fragmentTable = HashMap<String, HashMap<String, Fragment>>()
    /* 组名 */
    private val groups: ArrayList<String> = arrayListOf()

    /**
     * 初始化编译期生成的路由表
     */
    fun initRouteTable(groupsP: ArrayList<String> = arrayListOf()){
        groups.addAll(groupsP)
        groups.forEach {group ->
            /* 初始化跳转路由表 */
            val clazz = exists("smart.router.$group.RouteTable")
            if(clazz != null){
                val clazzInstance = clazz.newInstance() as IRouteTable
                clazzInstance.routes().forEach {
                    if(routerTable.containsKey(it.key)){
                        Log.e(TAG, "initRouteTable-已存在相同的跳转路由------>path:${it.key}，新className:${it.value}，旧className:${routerTable[it.key]}")
                    }else{
                        routerTable[it.key] = it.value
                    }
                }
            }else{
                Log.e(TAG, "initRouteTable-不存在路由表smart.router.$group.RouteTable")
            }
        }
    }

    /**
     * 运行时动态添加到路由表中
     * @param forced 强制添加，若已存在对应路径的路由则覆盖已存在的路由
     */
    fun Any.add2Router(forced: Boolean = false){
        if(this.javaClass.isAnnotationPresent(PathDynamic::class.java)){
            val anno = this.javaClass.getAnnotation(PathDynamic::class.java) ?: return
            val path = anno.path
            val clazzName = this::class.jvmName
            if(routerTable.containsKey(path)){
                Log.e(TAG, "add2Router-已存在相同的路由------>path:$path，新className:$clazzName，旧className:${routerTable[path]}")
                if(forced){
                    routerTable.remove(path)
                    routerTable.put(path, clazzName)
                }
            }else{
                routerTable.put(path, clazzName)
            }
        }else{
            Log.e(TAG, "$this 必须使用PathDynamic注解修饰")
        }
    }

    /**
     * 跳转到activity或者service
     * @param path 路径
     * @param intent 意图，默认为空，此时为不带任何数据的跳转
     */
    fun Activity.go(
            path: String,
            intent: Intent? = null
    ){
        if(!routerTable.containsKey(path)){
            Log.e(TAG, "未发现路径$path 对应的activity，取消跳转。")
            return
        }
        val clazzName = routerTable[path] ?: ""
        val mIntent = intent ?: Intent()
        mIntent.setClassName(this, clazzName)
        val clazz = Class.forName(clazzName)
        val activityClazz = Class.forName("android.app.Activity")
        val serviceClazz = Class.forName("android.app.Service")
        if(activityClazz.isAssignableFrom(clazz)){
            startActivity(mIntent)
        }else if(serviceClazz.isAssignableFrom(clazz)){
            if(clazz.isAnnotationPresent(ForegroundService::class.java)){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(mIntent)
                }else{
                    Log.e(TAG, "路径$path 对应的service为ForegroundService，必须在API26及以上才可用，取消跳转。")
                }
            }else{
                startService(mIntent)
            }
        }
    }

    /**
     * 获取fragment
     * @param path 路径
     * @param secondKey 第二key
     * @param bundle 构造fragment时传入的参数
     */
    fun Activity.getFragment(path: String, secondKey: String = "", bundle: Bundle? = null): Fragment?{
        var fragment: Fragment? = null
        if(!routerTable.containsKey(path)){
            Log.e(TAG, "未发现路径$path 对应的fragment。")
            return fragment
        }
        val clazzName = routerTable[path] ?: ""
        val clazz = Class.forName(clazzName)
        val fragmentClazz = Class.forName("android.app.Fragment")
        val fragmentv4Clazz = Class.forName("android.support.v4.app.Fragment")
        if(fragmentClazz.isAssignableFrom(clazz) || fragmentv4Clazz.isAssignableFrom(clazz)){
            if(fragmentTable.containsKey(this::class.jvmName)){
                if(fragmentTable[this::class.jvmName]!!.containsKey(path + secondKey)){
                    fragment = fragmentTable[this::class.jvmName]!![path + secondKey]
                }else{
                    try{
                        fragment = clazz.newInstance() as Fragment
                        if(bundle != null){
                            fragment.arguments = bundle
                        }
                        fragmentTable[this::class.jvmName]!!.put(path + secondKey, fragment)
                    }catch (e: Exception){
                        e.printStackTrace()
                    }
                }
            }else{
                try{
                    fragment = clazz.newInstance() as Fragment
                    if(bundle != null){
                        fragment.arguments = bundle
                    }
                    val map = HashMap<String, Fragment>()
                    map.put(path + secondKey, fragment)
                    fragmentTable[this::class.jvmName] = map
                }catch (e: Exception){
                    e.printStackTrace()
                }
            }
        }
        return fragment
    }

    /**
     * 回收缓存的fragment
     */
    fun Activity.recoveryFragment(){
        if(fragmentTable.containsKey(this::class.jvmName)){
            fragmentTable[this::class.jvmName]!!.clear()
            fragmentTable.remove(this::class.jvmName)
        }
    }

    /**
     * 跳转到activity，要求有结果返回
     * @param path 路径
     * @param requestCode 返回Code
     * @param intent 意图，默认为空，此时为不带任何数据的跳转
     */
    fun Activity.goActivityForResult(
            path: String,
            requestCode: Int,
            intent: Intent? = null
    ){
        if(!routerTable.containsKey(path)){
            Log.e(TAG, "未发现路径$path 对应的activity，取消跳转。")
            return
        }
        val clazzName = routerTable[path]
        val clazz = Class.forName(clazzName)
        val activityClazz = Class.forName("android.app.Activity")
        if(!activityClazz.isAssignableFrom(clazz)){
            Log.e(TAG, "路径$path 对应的activity为$clazzName，不是一个activity，取消跳转。")
            return
        }
        val mIntent = intent ?: Intent()
        mIntent.setClassName(this, clazzName)
        startActivityForResult(mIntent, requestCode)
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