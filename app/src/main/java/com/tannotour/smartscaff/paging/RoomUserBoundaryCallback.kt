package com.tannotour.smartscaff.paging

import android.arch.paging.PagedList
import com.tannotour.scafflib.log.logE
import com.tannotour.smartscaff.cli.remote
import com.tannotour.smartscaff.room.RoomUser

/**
 * Created by mitnick on 2018/10/28.
 * Description
 */
class RoomUserBoundaryCallback: PagedList.BoundaryCallback<RoomUser>(){
    override fun onZeroItemsLoaded() {
        logE("RoomUserBoundaryCallback:onZeroItemsLoaded")
        RoomUser::class remote {
            insertRoomUser(30)
        }
    }

    override fun onItemAtEndLoaded(itemAtEnd: RoomUser) {
        logE("RoomUserBoundaryCallback:onItemAtEndLoaded")
        RoomUser::class remote {
            insertRoomUser(30)
        }
    }

    override fun onItemAtFrontLoaded(itemAtFront: RoomUser) {
        logE("RoomUserBoundaryCallback:onItemAtFrontLoaded")
    }
}