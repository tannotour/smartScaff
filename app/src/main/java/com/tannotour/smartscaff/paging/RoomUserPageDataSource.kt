package com.tannotour.smartscaff.paging

import android.arch.paging.PageKeyedDataSource
import com.tannotour.smartscaff.room.RoomUser

/**
 * Created by mitnick on 2018/10/28.
 * Description
 */
class RoomUserPageDataSource: PageKeyedDataSource<Int, RoomUser>(){
    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, RoomUser>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, RoomUser>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, RoomUser>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}