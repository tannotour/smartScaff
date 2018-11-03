package com.tannotour.scafflib.network

import okhttp3.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by mitnick on 2018/10/11.
 * Description
 */
object HTTP{

    /* 基URL */
    var baseURL = ""
    /* 拦截器 */
    private val interceptorSet = HashSet<Interceptor>()
    /* cookies */
    val cookieJar: CookieJar by lazy { ScaffCookieJar() }

    /**
     * 添加拦截器
     * @param intercept 拦截器对象
     */
    fun addIntercept(intercept: Interceptor){
        interceptorSet.add(intercept)
    }

    private val okHttpClient: OkHttpClient by lazy {
        val builder = OkHttpClient.Builder().addInterceptor(HttpLoggingInterceptor())
        interceptorSet.forEach { builder.addInterceptor(it) }
        interceptorSet.clear()
        builder.cookieJar(cookieJar)
        builder.build()
    }

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(baseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
    }
}