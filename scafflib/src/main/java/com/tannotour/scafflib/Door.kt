package com.tannotour.scafflib

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.support.v4.app.FragmentActivity
import com.tannotour.scaffanno.anno.RepositoryProvider
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.launch
import java.lang.reflect.Method
import kotlin.coroutines.experimental.Continuation
import kotlin.reflect.KClass
import kotlin.coroutines.experimental.intrinsics.*

/**
 * 注册观察者
 * @param clazz 需要观察的数据类
 * @param onChanged 数据更新回调方法
 */
fun <T: RepositoryEntity> FragmentActivity.register(clazz: KClass<T>, onChanged: (data: T?) -> Unit): VirtualViewModel<T>?{
    var viewModel: VirtualViewModel<T>? = null
    val viewModelKClass: KClass<VirtualViewModel<T>>? = ViewModelUtils.obtainViewModelClass(clazz)
    if(viewModelKClass != null){
        viewModel = ViewModelProviders.of(this).get(viewModelKClass.java)
        viewModel.liveData.observe(this, Observer<T> {
            onChanged(it)
        })
    }
    return viewModel
}

/**
 * 注册观察者
 * @param clazz 需要观察的数据类
 * @param map 转化器，将观察的数据转为另一种格式的数据再回调
 * @param onChanged 数据更新回调方法
 */
fun <T: RepositoryEntity, R> FragmentActivity.register(clazz: KClass<T>, map:(data: T?) -> R?, onChanged: (data: R?) -> Unit): VirtualViewModel<T>?{
    var viewModel: VirtualViewModel<T>? = null
    val viewModelKClass: KClass<VirtualViewModel<T>>? = ViewModelUtils.obtainViewModelClass(clazz)
    if(viewModelKClass != null){
        viewModel = ViewModelProviders.of(this).get(viewModelKClass.java)
        viewModel.liveData.observe(this, Observer<T> {
            val data = map(it)
            onChanged(data)
        })
    }
    return viewModel
}

/**
 * 异步执行数据源的数据更新方法
 * @param clazz 需要观察的数据类
 * @param pairs 请求数据时的参数
 */
fun <T: RepositoryEntity> FragmentActivity.remote(clazz: KClass<T>, vararg pairs: Pair<String, String>){
    val viewModelKClass: KClass<VirtualViewModel<T>>? = ViewModelUtils.obtainViewModelClass(clazz)
    if(viewModelKClass != null){
        val viewModel = ViewModelProviders.of(this).get(viewModelKClass.java)
        viewModel.remote(*pairs)
    }
}

/**
 * 返回异步执行数据源的数据更新方法，不自动执行方法
 * @param clazz 需要观察的数据类
 */
fun <T: RepositoryEntity> FragmentActivity.remoteFunc(clazz: KClass<T>, vararg pairs: Pair<String, String>): (suspend () -> T?)?{
    val viewModelKClass: KClass<VirtualViewModel<T>>? = ViewModelUtils.obtainViewModelClass(clazz)
    return if(viewModelKClass != null){
        val viewModel = ViewModelProviders.of(this).get(viewModelKClass.java)
        viewModel.remoteFunc(*pairs)
//        viewModel.remoteFunc?.invoke(hashMapOf(*pairs))
    }else{
        null
    }
}

/**
 * 异步执行数据源的指定方法
 * @param clazz 需要观察的数据类
 * @param methodName 需要执行的方法的名字
 * @param pairs 请求数据时的参数
 */
fun <T: RepositoryEntity> FragmentActivity.remote(clazz: KClass<T>, methodName: String, vararg pairs: Pair<String, String>){
    clazz fetchRepositoryMethod methodName feed hashMapOf(*pairs)
}

/**
 * 获取对应的repository的方法
 * @param methodName 方法名
 */
infix fun <T: RepositoryEntity> KClass<T>.fetchRepositoryMethod(methodName: String): Method? {
    val repository = RepositoryUtils.obtainRepository(this)
    return if(repository?.javaClass?.methods?.any { it.name == methodName } == true){
        repository.javaClass.getMethod(methodName, Map::class.java, Continuation::class.java)
    }else{
        null
    }
}

/**
 * 执行方法
 * @param params 执行方法时传入的参数
 */
infix fun Method?.feed(params: Map<String, String>?){
    if(this == null){
        return
    }
    val annotation = declaringClass.getAnnotation(RepositoryProvider::class.java) ?: return
    val kClass = annotation.kClass
    if(!RepositoryEntity::class.java.isAssignableFrom(kClass.java)){
        return
    }
    val repository = RepositoryUtils.obtainRepository(kClass as KClass<RepositoryEntity>)
    GlobalScope.launch{
        suspendCoroutineOrReturn<Any> {
            if(params == null){
                invoke(repository, it)
            }else{
                invoke(repository, params, it)
            }
        }
    }
}

/**
 * 在协程中执行方法
 * @param params 执行方法时传入的参数
 */
suspend infix fun Method?.feedSync(params: Map<String, String>?): Any?{
    if(this == null){
        return null
    }
    val annotation = declaringClass.getAnnotation(RepositoryProvider::class.java) ?: return null
    val kClass = annotation.kClass
    if(!RepositoryEntity::class.java.isAssignableFrom(kClass.java)){
        return null
    }
    val repository = RepositoryUtils.obtainRepository(kClass as KClass<RepositoryEntity>)
    return suspendCoroutineOrReturn {
        if(params == null){
            invoke(repository, it)
        }else{
            invoke(repository, params, it)
        }
    }
}

/**
 * 类型转化，与上一个方法共同使用
 * @param type 目标类型
 */
infix fun <T: Any> Any?.type(type: KClass<T>): T?{
    return if(this == null){
        null
    } else{
        this as T?
    }
}

/**
 * 反注册观察者
 * @param clazz 需要观察的类
 */
fun <T: RepositoryEntity> FragmentActivity.unRegister(clazz: KClass<T>){
    val viewModelKClass: KClass<VirtualViewModel<T>>? = ViewModelUtils.obtainViewModelClass(clazz)
    if(viewModelKClass != null){
        ViewModelProviders.of(this).get(viewModelKClass.java).liveData.removeObservers(this)
    }
}