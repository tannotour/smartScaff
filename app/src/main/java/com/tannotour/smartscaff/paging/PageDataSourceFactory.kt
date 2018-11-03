package com.tannotour.smartscaff.paging

import android.arch.paging.DataSource

/**
 * Created by mitnick on 2018/10/29.
 * Description
 */
class PageDataSourceFactory<T>(
        var load: (requestedLoadSize: Int, page: Int) -> List<T>?
): DataSource.Factory<Int, T>(){

    override fun create(): DataSource<Int, T> {
        return PageDataSource<T>(load)
    }

}