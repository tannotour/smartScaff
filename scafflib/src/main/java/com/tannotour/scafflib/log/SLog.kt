package com.tannotour.scafflib.log

import android.app.Application
import com.socks.library.KLog

/**
 * Created by mitnick on 2018/10/20.
 * Description
 */

fun Application.initLog(debug: Boolean, tag: String){
    KLog.init(debug, tag)
}

fun Any.logV(log: String){
    KLog.v(javaClass.simpleName, log)
}

fun Any.logD(log: String){
    KLog.d(javaClass.simpleName, log)
}

fun Any.logI(log: String){
    KLog.i(javaClass.simpleName, log)
}

fun Any.logW(log: String){
    KLog.w(javaClass.simpleName, log)
}

fun Any.logE(log: String){
    KLog.e(javaClass.simpleName, log)
}

fun Any.logA(log: String){
    KLog.a(javaClass.simpleName, log)
}

fun Any.logJson(log: String){
    KLog.json(javaClass.simpleName, log)
}

fun Any.logXML(log: String){
    KLog.xml(javaClass.simpleName, log)
}