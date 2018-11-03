package com.tannotour.smartscaff.paging

import android.support.v7.util.DiffUtil
import com.tannotour.smartscaff.room.RoomUser

/**
 * Created by mitnick on 2018/10/27.
 * Description
 */
class RoomUserDiffCallBack: DiffUtil.ItemCallback<RoomUser>() {

    override fun areItemsTheSame(oldItem: RoomUser, newItem: RoomUser): Boolean {
        return oldItem.uuid == newItem.uuid
    }

    override fun areContentsTheSame(oldItem: RoomUser, newItem: RoomUser): Boolean {
        return oldItem == newItem
    }
}