package com.tannotour.smartscaff.test

import com.tannotour.scafflib.RepositoryEntity

/**
 * Created by mitnick on 2018/10/8.
 * Description
 */
class User: RepositoryEntity {
    var code = "000000"
    var msg = ""
    var data: UserBean = UserBean()

    inner class UserBean{
        var phone: String = ""
        var userName: String = ""
        override fun toString(): String {
            return "UserBean(phone='$phone', userName='$userName')"
        }

    }

    override fun toString(): String {
        return "User(code='$code', msg='$msg', data=$data)"
    }

}