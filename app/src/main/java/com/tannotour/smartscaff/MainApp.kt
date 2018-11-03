package com.tannotour.smartscaff

import android.app.Application
import com.squareup.leakcanary.LeakCanary
import com.tannotour.scafflib.ScaffInit
import com.tannotour.scafflib.log.initLog
import com.tannotour.scafflib.network.HTTP
import com.tencent.mmkv.MMKV

/**
 * Created by mitnick on 2018/10/10.
 * Description
 */
class MainApp: Application() {
    override fun onCreate() {
        super.onCreate()
        /* 监测内存泄漏 */
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return
        }
        LeakCanary.install(this)
        /* 框架初始化 */
        initLog(BuildConfig.DEBUG, "scaff")
        HTTP.baseURL = "http://api.gaea365.com:18080/"
        MMKV.initialize(this)
        ScaffInit().initScaff()
    }
}