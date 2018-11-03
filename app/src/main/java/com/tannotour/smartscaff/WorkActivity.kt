package com.tannotour.smartscaff

import android.arch.lifecycle.LifecycleOwner
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import androidx.work.WorkManager
import com.tannotour.smartscaff.workmanager.CompressWorker
import com.tencent.mmkv.MMKV
import kotlinx.android.synthetic.main.activity_main.*
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequest.MIN_PERIODIC_INTERVAL_MILLIS
import java.util.concurrent.TimeUnit

class WorkActivity : AppCompatActivity(), LifecycleOwner {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mainText.text = "the value is ${MMKV.defaultMMKV().decodeInt("CompressWorker", -1)}"
        button.setOnClickListener {
            val workRequest =  PeriodicWorkRequest.Builder(CompressWorker::class.java, MIN_PERIODIC_INTERVAL_MILLIS, TimeUnit.MILLISECONDS)
                    .build()
            WorkManager.getInstance().enqueue(workRequest)
        }
    }
}
