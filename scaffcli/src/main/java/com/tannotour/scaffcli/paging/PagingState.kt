package com.tannotour.scaffcli.paging

/**
 * Created by mitnick on 2018/11/1.
 * Description
 */
class PagingState{

    /* 当前状态 */
    var state: STATE = STATE.COMMON
    /* 加载初始位置 */
    var positionOffset: Int = -1
    /* 加载数量 */
    var pageSize: Int = -1

    enum class STATE{
        LOADING,
        COMMON
    }
}