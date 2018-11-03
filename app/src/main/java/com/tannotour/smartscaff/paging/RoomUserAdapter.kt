package com.tannotour.smartscaff.paging

import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.Toast
import com.tannotour.scafflib.log.logE
import com.tannotour.smartscaff.R
import com.tannotour.smartscaff.cli.*
import com.tannotour.smartscaff.room.RoomUser
import com.tannotour.smartscaff.room.RoomUserDataBase
import kotlinx.android.synthetic.main.layout_room_user_item.view.*

/**
 * Created by mitnick on 2018/10/27.
 * Description
 */
class RoomUserAdapter(
        recyclerView: RecyclerView? = null,
        swipeRefreshLayout: SwipeRefreshLayout? = null
): PagingAdapter<RoomUser>(
        diffCallBack = RoomUserDiffCallBack(),
        recyclerView = recyclerView,
        swipeRefreshLayout = swipeRefreshLayout
){

    private val displayRoomUser = fun(itemView: View, position: Int, item: RoomUser?){
        item?.uuid?.let {
            itemView.roomUserItemUUID.text = it.toString()
        }
        item?.userName?.let {
            itemView.roomUserItemUserName.text = it
        }
        item?.userPhone?.let {
            itemView.roomUserItemUserPhone.text = it
        }
        itemView.roomUserItem.setOnClickListener {
            val payload = RoomUser()
            payload.userName = "$position - payload"
            payload.userPhone = null
            notifyItemChanged(position, payload)
        }
    }

    override val layoutIdMap = hashMapOf(0 to R.layout.layout_room_user_item)
    override val displayItemMap = hashMapOf(0 to displayRoomUser)

    override val loadLocal = fun(requestedLoadSize: Int, requestedStartPosition: Int): List<RoomUser>?{
        val list = RoomUserDataBase::class source {
            roomUserDao().positionAll(requestedLoadSize, requestedStartPosition)
        }
        return list
    }

    override val loadRemote = fun(requestedLoadSize: Int){
        RoomUser::class executeSync {
            insert(requestedLoadSize)
        }
    }

    override suspend fun clearLocal() {
        RoomUserDataBase::class source {
            roomUserDao().clear()
        }
    }

    override fun getItemViewType(position: Int): Int {
        return 0
    }

    override val loading = fun(positionOffset: Int, pageSize: Int) {
        recyclerView?.context?.let {
            logE("loading -> positionOffset:$positionOffset pageSize:$pageSize")
            Toast.makeText(it, "loading -> positionOffset:$positionOffset pageSize:$pageSize", Toast.LENGTH_SHORT).show()
        }
    }

    override val common = fun(positionOffset: Int, pageSize: Int) {
        swipeRefreshLayout?.isRefreshing = false
        recyclerView?.context?.let {
            logE("common -> positionOffset:$positionOffset pageSize:$pageSize")
            Toast.makeText(it, "common -> positionOffset:$positionOffset pageSize:$pageSize", Toast.LENGTH_SHORT).show()
        }
    }
}