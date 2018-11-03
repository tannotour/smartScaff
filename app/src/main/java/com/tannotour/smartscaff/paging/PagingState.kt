package com.tannotour.smartscaff.paging

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
        /* 加载更多状态 */
        LOADING,
        /* 加载完成状态 */
        COMMON
    }
}