package com.tannotour.scafflib.cache

import android.app.Application
import android.content.Context
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.DecimalFormat
import kotlin.reflect.KClass

/**
 * Created by mitnick on 2018/10/8.
 * Description 硬盘缓存接口规范
 */
interface IDiskCache {

    /**
     * 初始化硬盘缓存
     * @param application APP上下文
     */
    fun init(application: Application)

    /**
     * 从硬盘中读数据并转为对象返回
     */
    fun <T: Any> readFromDisk(key: String, clazz: KClass<T>): T?

    /**
     * 将对象写入硬盘中
     */
    fun writeToDisk(key: String, obj: Any?): Boolean

    /**
     * 从硬盘中读取列表数据
     */
    fun <T: Any> readListFromDisk(key: String, clazz: KClass<T>): List<T>

    /**
     * 在硬盘中删除数据，key为null时表示全部删除
     */
    fun remove(context: Context, key: String? = null)

    /**
     * 获取硬盘缓存占用的空间
     */
    fun totalSize(): String

    /**
     * 将字符串MD5编码
     * @param str 需要编码的字符串
     */
    fun encode(str: String): String {
        try {
            val instance: MessageDigest = MessageDigest.getInstance("MD5")//获取md5加密对象
            val digest:ByteArray = instance.digest(str.toByteArray())//对字符串加密，返回字节数组
            val sb = StringBuffer()
            for (b in digest) {
                val i :Int = b.toInt() and 0xff//获取低八位有效值
                var hexString = Integer.toHexString(i)//将整数转化为16进制
                if (hexString.length < 2) {
                    hexString = "0$hexString"//如果是一位的话，补0
                }
                sb.append(hexString)
            }
            return sb.toString()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
        return ""
    }

    /**
     * 格式化文件大小
     * @param fileSize 字节形式的文件大小
     */
    fun formetFileSize(fileSize: Long?): String {
        if(fileSize == null){
            return "0B"
        }
        val df = DecimalFormat("#.00")
        val fileSizeizeString: String
        if (fileSize < 1024) {
            fileSizeizeString = df.format(fileSize.toDouble()) + "B"
        } else if (fileSize < 1048576) {
            fileSizeizeString = df.format(fileSize.toDouble() / 1024) + "K"
        } else if (fileSize < 1073741824) {
            fileSizeizeString = df.format(fileSize.toDouble() / 1048576) + "M"
        } else {
            fileSizeizeString = df.format(fileSize.toDouble() / 1073741824) + "G"
        }
        return fileSizeizeString
    }
}