package com.tannotour.smartscaff.paging

import android.arch.lifecycle.LifecycleOwner

/**
 * Created by mitnick on 2018/11/2.
 * Description
 */

/**
 * 初始化RecyclerView
 * @param lifecycleOwner 生命周期拥有者
 */
fun <T> PagingAdapter<T>.observe(lifecycleOwner: LifecycleOwner): PagingAdapter<T>{
    this.recyclerView?.adapter = this
    this.swipeRefreshLayout?.setOnRefreshListener {
        if(this.swipeRefreshLayout?.isRefreshing == true){
            this.refresh(lifecycleOwner)
        }
    }
    this.register(lifecycleOwner)
    return this
}