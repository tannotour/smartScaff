package com.tannotour.scaffcli.paging

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.Observer
import android.arch.paging.PagedListAdapter
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tannotour.scaffcli.liveData
import com.tannotour.scaffcli.log.logE
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.android.Main
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import java.lang.Exception

/**
 * Created by mitnick on 2018/10/27.
 * Description
 */
abstract class PagingAdapter<T>(
        diffCallBack: DiffUtil.ItemCallback<T>,
        var recyclerView: RecyclerView? = null,
        var swipeRefreshLayout: SwipeRefreshLayout? = null
): PagedListAdapter<T, PagingAdapter.ViewHolder>(diffCallBack) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(layoutIdMap[viewType]!!, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        val display = displayItemMap[holder.itemViewType]
        display?.invoke(holder.itemView, position, item)
    }

    /**
     * 注意：在该adapter中，payloads中只允许存储类型为T的对象，且只有第一个元素有效，对象中不为null的字段为需要更新的内容
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        if(payloads.isEmpty()){
            onBindViewHolder(holder, position)
        }else{
            var payload: T? = null
            try {
                payload = payloads.first() as T?
            }catch (e: Exception){
                e.printStackTrace()
                logE("PagingAdapter -> payloads is not T,please check the payloads input")
            }finally {
                if(payload == null){
                    payload = getItem(position)
                }
                val display = displayItemMap[holder.itemViewType]
                display?.invoke(holder.itemView, position, payload)
            }
        }
    }

    /**
     * 监听数据源
     * @param lifecycleOwner 生命周期提供者
     */
    fun register(lifecycleOwner: LifecycleOwner){
        dataSourceFactory.observer(lifecycleOwner, loading, common)
        dataSourceFactory.liveData().observe(lifecycleOwner, Observer {
            submitList(it)
        })
    }

    /**
     * 刷新
     * @param lifecycleOwner 生命周期提供者
     */
    fun refresh(lifecycleOwner: LifecycleOwner){
        GlobalScope.launch {
            async { clearLocal() }.await()
            launch(Dispatchers.Main) {
                submitList(null)
                /* 重新绑定数据源 */
                dataSourceFactory.liveData().removeObservers(lifecycleOwner)
                dataSourceFactory.liveData().observe(lifecycleOwner, Observer {
                    submitList(it)
                })
            }
        }
    }

    /**
     * 正在加载
     */
    open val loading = fun(positionOffset: Int, pageSize: Int){  }

    /**
     * 加载完成
     */
    open val common = fun(positionOffset: Int, pageSize: Int){  }

    /* 数据源工厂 */
    private val dataSourceFactory: PositionDataSourceFactory<T> by lazy { PositionDataSourceFactory(loadLocal, loadRemote) }
    /* itemType映射到layoutId */
    abstract val layoutIdMap: HashMap<Int, Int>
    /* itemType映射到更新UI函数 */
    abstract val displayItemMap: HashMap<Int, (itemView: View, position: Int, item: T?) -> Unit>

    /* 加载本地数据 */
    open val loadLocal: (requestedLoadSize: Int, requestedStartPosition: Int) -> List<T>? = { _, _ -> ArrayList<T>() }
    /* 加载网络数据 */
    open val loadRemote: (requestedLoadSize: Int) -> Unit = { _ -> }
    /* 清空数本地据 */
    open suspend fun clearLocal(){  }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
}