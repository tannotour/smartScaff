package com.tannotour.scaffcli

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.OnLifecycleEvent
import com.tannotour.scaffcli.log.logE
import kotlinx.coroutines.experimental.Job

/**
 * Created by mitnick on 2018/10/25.
 * Description 监听生命周期，生命周期结束后自动结束还没有结束的任务
 */
class JobController(private val job: Job, var lifecycleOwner: LifecycleOwner?): LifecycleObserver {

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        logE("$lifecycleOwner -> onCreate")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        logE("$lifecycleOwner -> onStart")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        logE("$lifecycleOwner -> onResume")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
        logE("$lifecycleOwner -> onPause")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop() {
        logE("$lifecycleOwner -> onStop")
        if(job.isActive){
            job.cancel()
        }
        lifecycleOwner?.lifecycle?.removeObserver(this)
        lifecycleOwner = null
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        logE("$lifecycleOwner -> onDestroy")

    }
}