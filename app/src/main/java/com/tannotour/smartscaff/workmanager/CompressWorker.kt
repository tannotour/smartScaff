package com.tannotour.smartscaff.workmanager

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.tannotour.scafflib.log.logE
import com.tencent.mmkv.MMKV

/**
 * Created by mitnick on 2018/11/2.
 * Description
 */
class CompressWorker(context: Context, params: WorkerParameters): Worker(context, params) {

    override fun doWork(): Result {
        val num = MMKV.defaultMMKV().decodeInt("CompressWorker")
        MMKV.defaultMMKV().encode("CompressWorker", num)
        logE("write the num -> $num")
        return Result.SUCCESS
    }

}