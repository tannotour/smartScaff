package com.tannotour.smartscaff.test

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.tannotour.scafflib.cache.DiskCacheMMKV
import com.tannotour.scafflib.log.logE

/**
 * Created by mitnick on 2018/10/23.
 * Description
 */
class SeedDatabaseWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val num = DiskCacheMMKV.invoke { decodeInt("WorkManagerTest", 0) }
        DiskCacheMMKV.invoke { encode("WorkManagerTest", num + 1) }
        logE("WorkManagerTest value is $num")
        return Result.SUCCESS
    }
}