package com.tannotour.smartscaff.paging

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.arch.paging.DataSource

/**
 * Created by mitnick on 2018/10/29.
 * Description
 * @param loadLocal 从本地加载数据
 * @param loadRemote 从远程加载数据
 */
class PositionDataSourceFactory<T>(
        var loadLocal: (requestedLoadSize: Int, requestedStartPosition: Int) -> List<T>?,
        var loadRemote: (requestedLoadSize: Int) -> Unit
): DataSource.Factory<Int, T>(){

    private val state: MutableLiveData<PagingState> by lazy { MutableLiveData<PagingState>() }

    fun observer(
            lifecycleOwner: LifecycleOwner,
            loading: ((positionOffset: Int, pageSize: Int) -> Unit)? = null,
            common: ((positionOffset: Int, pageSize: Int) -> Unit)? = null
    ){
        state.observe(lifecycleOwner, Observer {
            it?.let { ps ->
                when(ps.state){
                    PagingState.STATE.COMMON -> {
                        common?.invoke(ps.positionOffset, ps.pageSize)
                    }
                    PagingState.STATE.LOADING -> {
                        loading?.invoke(ps.positionOffset, ps.pageSize)
                    }
                }
            }
        })
    }

    override fun create(): DataSource<Int, T> {
        return PositionalDataSource<T>(loadLocal, loadRemote, state)
    }
}