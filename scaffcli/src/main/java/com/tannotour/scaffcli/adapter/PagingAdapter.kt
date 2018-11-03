package com.tannotour.scaffcli.adapter

import android.arch.paging.PagedListAdapter
import android.support.v4.app.FragmentActivity
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.coroutines.experimental.CompletionHandler
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.launch

/**
 * Created by mitnick on 2018/10/27.
 * Description
 */
abstract class PagingAdapter<T>(
        /* 当前状态回调 */
        open var state: ((state: STATE, msg: String) -> Unit)? = null,
        /* 判断更新后两个值的ID是否相等 */
        open var areItemsTheSame: ((oldItem: T, newItem: T) -> Boolean) = { _, _ -> false },
        /* 判断更新后两个值的数据内容是否相等，仅当areItemsTheSame返回true时才调用此函数做进一步判断 */
        open var areContentsTheSame: ((oldItem: T, newItem: T) -> Boolean) = { oldItem, newItem -> oldItem == newItem }
): PagedListAdapter<T, PagingAdapter.ViewHolder>(
        object : DiffUtil.ItemCallback<T>(){
            override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
                return areItemsTheSame(oldItem, newItem)
            }

            override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
                return areContentsTheSame(oldItem, newItem)
            }
        }
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(layoutIdMap[viewType]!!, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        val display = displayItemMap[holder.itemViewType]
        display?.invoke(holder.itemView, position, item)
        if(!loading && shouldLoadMore(itemCount, position, holder.itemViewType)){
            loading = true
            state?.invoke(STATE.STATE_LOADING, "loading")
            val checkResult = checkBeforeLoad(itemCount, position, holder.itemViewType)
            if(checkResult.isOk){
                val job = GlobalScope.launch {
                    load(itemCount, position, holder.itemViewType)
                }
                job.invokeOnCompletion(
                        object: CompletionHandler{
                            override fun invoke(cause: Throwable?) {
                                loading = false
                                state?.invoke(STATE.STATE_COMMON, "common")
                            }
                        }
                )
            }else{
                loading = false
                state?.invoke(STATE.STATE_ERROR, checkResult.msg)
            }
        }
    }

    /**
     * 监听数据源
     * @param fragmentActivity 生命周期提供者
     */
    abstract fun register(fragmentActivity: FragmentActivity)

    /**
     * 返回是否应该加载更多数据了
     * @param totalSize 当前已经加载的数据数量
     * @param position 当前加载的下标
     * @param itemType 当前加载的类型
     */
    protected open fun shouldLoadMore(totalSize: Int, position: Int, itemType: Int): Boolean{
        return false
    }

    /**
     * 加载数据前的检查，比如检查网络状态，权限等
     * @param totalSize 当前已经加载的数据数量
     * @param position 当前加载的下标
     * @param itemType 当前加载的类型
     */
    protected open fun checkBeforeLoad(totalSize: Int, position: Int, itemType: Int): CheckResult{
        return CheckResult()
    }

    /**
     * 加载数据
     * @param totalSize 当前已经加载的数据数量
     * @param position 当前加载的下标
     * @param itemType 当前加载的类型
     */
    protected open suspend fun load(totalSize: Int, position: Int, itemType: Int){

    }

    /* 标识是否正在加载数据 */
    private var loading = false
    /* itemType映射到layoutId */
    abstract val layoutIdMap: HashMap<Int, Int>
    /* itemType映射到更新UI函数 */
    abstract val displayItemMap: HashMap<Int, (itemView: View, position: Int, item: T?) -> Unit>

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    /**
     * 当前状态
     */
    enum class STATE{
        /* 正常状态 */
        STATE_COMMON,
        /* 加载数据状态 */
        STATE_LOADING,
        /* 异常状态 */
        STATE_ERROR
    }

    /**
     * 加载数据前状态检查结果
     */
    data class CheckResult(
            /* 检查是否通过 */
            var isOk: Boolean = true,
            /* 检查结果 */
            var msg: String = ""
    )
}