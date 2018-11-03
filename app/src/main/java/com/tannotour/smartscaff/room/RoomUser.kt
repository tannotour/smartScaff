package com.tannotour.smartscaff.room

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.tannotour.smartscaff.cli.RepositoryWorker
import com.tannotour.smartscaff.cli.RepositoryEntity

/**
 * Created by mitnick on 2018/10/23.
 * Description
 */
@Entity(tableName = "room_user")
@RepositoryWorker(repositoryProvider = RoomUserRepository::class)
class RoomUser: RepositoryEntity<IRoomUserRepository> {

    @PrimaryKey(autoGenerate = true)
    var uuid: Int = 0
    @ColumnInfo(name = "user_name")
    var userName: String? = ""
    @ColumnInfo(name = "user_phone")
    var userPhone: String? = ""

    override fun toString(): String {
        return "RoomUser(uuid=$uuid, userName='$userName', userPhone='$userPhone')"
    }
}