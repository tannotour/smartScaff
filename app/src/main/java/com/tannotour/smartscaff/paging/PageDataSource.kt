package com.tannotour.smartscaff.paging

import android.arch.paging.PageKeyedDataSource
import com.tannotour.scafflib.log.logE

/**
 * Created by mitnick on 2018/10/29.
 * Description
 */
class PageDataSource<T>(
        var load: (requestedLoadSize: Int, page: Int) -> List<T>?
): PageKeyedDataSource<Int, T>(){

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, T>) {
        logE("PageDataSource:loadInitial -> ${params.requestedLoadSize}")
        val data = load(params.requestedLoadSize, 0)
        data?.let {
            callback.onResult(data, null, 1)
        }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, T>) {
        logE("PageDataSource:loadAfter -> ${params.requestedLoadSize} , ${params.key}")
        val data = load(params.requestedLoadSize, params.key)
        data?.let {
            callback.onResult(data, params.key + 1)
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, T>) {
        logE("PageDataSource:loadBefore -> ${params.requestedLoadSize} , ${params.key}")
        val data = load(params.requestedLoadSize, params.key)
        data?.let {
            callback.onResult(data, params.key - 1)
        }
    }

}