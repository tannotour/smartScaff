package com.tannotour.scaffcli.network

import com.tannotour.scaffcli.log.logE
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * Created by mitnick on 2018/11/23.
 * Description 错误重试拦截器
 * @param maxRetry 最大重试次数
 */
class RetryIntercepter(private var maxRetry: Int = 3): Interceptor {

    private var retryNum = 1

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        var response = chain.proceed(request)
        while(!response.isSuccessful && retryNum <= maxRetry){
            retryNum++
            logE("net retryNum : $retryNum")
            Thread.sleep((500*retryNum).toLong())
            response = chain.proceed(request)
        }
        return response
    }
}