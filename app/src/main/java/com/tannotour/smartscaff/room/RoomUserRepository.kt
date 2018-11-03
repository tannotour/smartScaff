package com.tannotour.smartscaff.room

import com.tannotour.scafflib.log.logE
import com.tannotour.scafflib.network.filter
import com.tannotour.scafflib.network.request
import com.tannotour.smartscaff.cli.Repository
import com.tannotour.smartscaff.test.UserRepository
import kotlinx.coroutines.experimental.delay


/**
 * Created by mitnick on 2018/10/24.
 * Description
 */
class RoomUserRepository: Repository<RoomUserDao, RoomUserDataBase>(RoomUserDataBase::class), IRoomUserRepository {

    override fun obtainSource(local: RoomUserDataBase?): RoomUserDao? = local?.roomUserDao()

    suspend fun fetchUser(params: Map<String, String>){
        delay(2000)
        request(UserRepository.Service::class){
            fetchUser(params["phone"], params["password"])
        } filter {
            isSuccessful
        } operate {
            val roomUser = RoomUser()
            roomUser.userName = "tannotour-$it"
            roomUser.userPhone = "18428323819-$it"
            insert(roomUser)
        }
    }

    override suspend fun login(phone: String, password: String) {
        request(UserRepository.Service::class){
            fetchUser(phone, password)
        } filter {
            isSuccessful
        } operate {
            for(i in 0 until 30){
                val roomUser = RoomUser()
                roomUser.userName = "${it?.data?.userName}"
                roomUser.userPhone = "${it?.data?.phone}"
                insert(roomUser)
            }
        }
    }

    override suspend fun insertRoomUser(requestedLoadSize: Int) {
        operate {
            for(i in 0 until requestedLoadSize){
                val roomUser = RoomUser()
                roomUser.userName = "tannotour"
                roomUser.userPhone = "18428323819"
                insert(roomUser)
            }
            logE("RoomUserRepository:insertRoomUser -> $requestedLoadSize items")
        }
    }

    override fun insert(requestedLoadSize: Int) {
        operate {
            for(i in 0 until requestedLoadSize){
                val roomUser = RoomUser()
                roomUser.userName = "tannotour"
                roomUser.userPhone = "18428323819"
                insert(roomUser)
            }
            logE("RoomUserRepository:insert -> $requestedLoadSize items")
        }
    }
}