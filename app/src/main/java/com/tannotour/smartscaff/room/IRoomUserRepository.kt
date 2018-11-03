package com.tannotour.smartscaff.room

/**
 * Created by mitnick on 2018/10/25.
 * Description
 */
interface IRoomUserRepository {
    /**
     * 登录
     */
    suspend fun login(phone: String, password: String)

    suspend fun insertRoomUser(requestedLoadSize: Int)

    fun insert(requestedLoadSize: Int)
}