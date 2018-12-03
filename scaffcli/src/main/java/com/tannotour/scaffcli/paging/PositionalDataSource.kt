package com.tannotour.scaffcli.paging

import android.arch.lifecycle.MutableLiveData
import android.arch.paging.PositionalDataSource

/**
 * Created by mitnick on 2018/10/30.
 * Description
 * @param loadLocal 从本地加载数据
 * @param loadRemote 从远程加载数据
 * @param state 当前状态
 */
class PositionalDataSource<T>(
        var loadLocal: (requestedLoadSize: Int, requestedStartPosition: Int) -> List<T>?,
        var loadRemote: (requestedLoadSize: Int) -> Unit,
        var state: MutableLiveData<PagingState>? = null
): PositionalDataSource<T>(){

    /* 当前状态对象 */
    private val pagingState: PagingState by lazy { PagingState() }

    /**
     * 发射加载状态
     * @param pageSize 页大小
     * @param positionOffset 当前页起始位置
     * @param curState 当前状态
     */
    private fun postState(pageSize: Int, positionOffset: Int, curState: PagingState.STATE) = state?.let {
        pagingState.pageSize = pageSize
        pagingState.positionOffset = positionOffset
        pagingState.state = curState
        it.postValue(pagingState)
    }

    override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<T>) {
        postState(params.requestedLoadSize, params.requestedStartPosition, PagingState.STATE.LOADING)
        var data: List<T>? = loadLocal(params.requestedLoadSize, 0) ?: ArrayList<T>()
        if(data!!.size < params.requestedLoadSize){
            loadRemote(params.requestedLoadSize - data.size)
            data = loadLocal(params.requestedLoadSize, 0)
            data?.let {
                postState(it.size, params.requestedStartPosition, PagingState.STATE.COMMON)
                callback.onResult(it, params.requestedStartPosition, it.size)
            }
        }else{
            postState(data.size, params.requestedStartPosition, PagingState.STATE.COMMON)
            callback.onResult(data, params.requestedStartPosition, params.requestedLoadSize)
        }
    }

    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<T>) {
        postState(params.loadSize, params.startPosition, PagingState.STATE.LOADING)
        var data: List<T>? = loadLocal(params.loadSize, params.startPosition) ?: ArrayList<T>()
        if(data!!.size < params.loadSize){
            loadRemote(params.loadSize - data.size)
            data = loadLocal(params.loadSize, params.startPosition)
            data?.let {
                postState(data.size, params.startPosition, PagingState.STATE.COMMON)
                callback.onResult(it)
            }
        }else{
            postState(data.size, params.startPosition, PagingState.STATE.COMMON)
            callback.onResult(data)
        }
    }
}