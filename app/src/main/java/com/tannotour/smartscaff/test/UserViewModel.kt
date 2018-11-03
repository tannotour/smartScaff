package com.tannotour.smartscaff.test

import com.tannotour.scaffanno.anno.ViewModelProvider
import com.tannotour.scafflib.VirtualViewModel

/**
 * Created by mitnick on 2018/10/8.
 * Description 这个类需要使用apt动态生成
 */
@ViewModelProvider(kClass = User::class)
class UserViewModel: VirtualViewModel<User>(User::class) {

}