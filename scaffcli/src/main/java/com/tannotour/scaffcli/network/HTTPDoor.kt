package com.tannotour.scaffcli.network

import com.tannotour.scaffcli.repository.RepositoryEntity
import retrofit2.Call
import retrofit2.Response
import kotlin.reflect.KClass

/**
 * Created by mitnick on 2018/10/13.
 * Description
 */

/**
 * 发起HTTP请求
 * @param service 服务端接口
 * @param method 具体调用的方法
 */
fun <T: Any, K: Any> request(service: KClass<T>, method: T.()-> Call<K>): Response<K> {
    return HTTP.retrofit.create(service.java).method().execute()
}

/**
 * 连接上一个HTTP请求
 * @param chain 转化函数，转化为下一个服务端接口
 */
infix fun <T: Any, K: Any> Response<T>?.chain(chain: Response<T>?.() -> K): K{
    return chain()
}

/**
 * 在上一个HTTP请求之后执行HTTP请求
 * @param method 具体调用的方法
 */
infix fun <T: Any, K: Any> KClass<T>.then(method: T.()-> Call<K>): Response<K> {
    return HTTP.retrofit.create(this.java).method().execute()
}

/**
 * 判断结果是否需要拦截
 * @param judge 判断逻辑
 */
infix fun <K: Any> Response<K>.filter(judge: Response<K>.() -> Boolean): Response<K>?{
    return if(this.judge()){
        this
    }else{
        null
    }
}

///**
// * 发起HTTP请求
// * @param service 服务端接口
// * @param method 具体调用的方法
// */
//fun <T: Any, K: Any> request(service: KClass<T>, method: T.()-> Call<RepositoryEntity<K>>): Response<RepositoryEntity<K>> {
//    return HTTP.retrofit.create(service.java).method().execute()
//}
//
///**
// * 连接上一个HTTP请求
// * @param chain 转化函数，转化为下一个服务端接口
// */
//infix fun <T: Any, K: Any> Response<RepositoryEntity<T>>?.chain(chain: Response<RepositoryEntity<T>>?.() -> K): K{
//    return chain()
//}
//
///**
// * 在上一个HTTP请求之后执行HTTP请求
// * @param method 具体调用的方法
// */
//infix fun <T: Any, K: Any> KClass<RepositoryEntity<T>>.then(method: RepositoryEntity<T>.()-> Call<RepositoryEntity<K>>): Response<RepositoryEntity<K>> {
//    return HTTP.retrofit.create(this.java).method().execute()
//}
//
///**
// * 判断结果是否需要拦截
// * @param judge 判断逻辑
// */
//infix fun <K: Any> Response<RepositoryEntity<K>>.filter(judge: Response<RepositoryEntity<K>>.() -> Boolean): Response<RepositoryEntity<K>>?{
//    return if(this.judge()){
//        this
//    }else{
//        null
//    }
//}