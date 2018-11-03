package com.tannotour.scaffcli.network

import com.tannotour.scaffcli.cache.DiskCacheMMKV
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl

/**
 * Created by mitnick on 2018/10/22.
 * Description
 */
class ScaffCookieJar: CookieJar {

    /* cookies */
    private val cookieStore = HashMap<String, MutableList<Cookie>>()

    /**
     * 添加Cookie
     * @param host 主机地址
     * @param builder Cookie建造者
     */
    fun addCookie(host: String, builder: Cookie.Builder.() -> Cookie.Builder){
        if(cookieStore["cookies:$host"] == null){
            cookieStore["cookies:$host"] = ArrayList<Cookie>(2)
        }
        val cookie = Cookie.Builder().builder().build()
        cookieStore["cookies:$host"]?.add(cookie)
    }

    /**
     * 删除Cookie
     * @param host 主机地址
     * @param removeBy 过滤条件，true则删除 false不删除
     */
    fun removeCookie(host: String, removeBy: (Cookie.() -> Boolean) = { true }){
        cookieStore["cookies:$host"]?.removeAll {
            it.removeBy()
        }
    }

    override fun saveFromResponse(url: HttpUrl, cookies: MutableList<Cookie>) {
        if(cookies.toString() != cookieStore[url.host()].toString()){
            cookieStore[url.host()] = cookies
            /* 持久化cookies */
            DiskCacheMMKV.writeToDisk("cookies:${url.host()}", cookies)
        }
    }

    override fun loadForRequest(url: HttpUrl): MutableList<Cookie> {
        if(cookieStore[url.host()] == null){
            val cookies = DiskCacheMMKV.readListFromDisk("cookies:${url.host()}", Cookie::class)
            if(cookies.isNotEmpty()){
                cookieStore[url.host()] = cookies as MutableList<Cookie>
            }
        }
        val cookies = cookieStore[url.host()]
        return cookies ?: ArrayList<Cookie>()
    }
}