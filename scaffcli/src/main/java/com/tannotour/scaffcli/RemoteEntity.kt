package com.tannotour.scaffcli

import com.tannotour.scaffcli.convert.Converter
import kotlin.reflect.KClass

/**
 * 获取副本
 * @param converter 转化功能提供者
 */
fun <T: Any> T?.duplicate(converter: Converter = Converter.obtain()): T?{
    if(this == null){
        return null
    }
    val json = converter.toJson(this)
    val duplicate = converter.convert(json, this::class)
    converter.release()
    return duplicate
}

/**
 * 对象转json
 * @param converter 转化功能提供者
 */
fun <T: Any> T?.toJson(converter: Converter = Converter.obtain()): String{
    if(this == null){
        return ""
    }
    val json = converter.toJson(this)
    converter.release()
    return json
}

/**
 * json转对象
 * @param converter 转化功能提供者
 */
fun <T: Any> KClass<T>.toObj(json: String, converter: Converter = Converter.obtain()): T?{
    val obj = converter.convert(json, this)
    converter.release()
    return obj
}