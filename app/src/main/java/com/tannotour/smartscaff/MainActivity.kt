package com.tannotour.smartscaff

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.tannotour.scafflib.*
import com.tannotour.smartscaff.test.User
import com.tannotour.smartscaff.test.UserRepository
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.experimental.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        /* 临时方法，需要使用apt处理 */
//        RepositoryUtils.addRepositoryMap(User::class, "com.tannotour.smartscaff.test.UserRepository")
//        ViewModelUtils.addViewModel(User::class, "com.tannotour.smartscaff.test.UserViewModel")
        /* 注册监听 */
        register(User::class, onChange)
        button.setOnClickListener {
//            remote(User::class, "phone" to "18428323819", "password" to "123456")
//            startActivity(Intent(this, TwoActivity::class.java))
//            User::class fetchRepositoryMethod "test" feed hashMapOf("phone" to "18428323819", "password" to "123456")
            remote(User::class, "test", "phone" to "18428323819", "password" to "123456")
        }
//        val deferred = GlobalScope.async(Dispatchers.Default, CoroutineStart.LAZY) {
//            delay(6000)
//            User::class update {
//                this?.msg = "我是在本地修改的新消息"
//            }
//            delay(6000)
//            remoteFunc(User::class, "phone" to "18428323819", "password" to "123456")?.invoke()
////            val result = User::class fetchRepositoryMethod "test" feed hashMapOf("phone" to "18428323819", "password" to "123456") type Unit::class
//        }
//        GlobalScope.launch {
//            deferred.await()
//        }
        startActivity(Intent(this, RoomActivity::class.java))
    }

    private val onChange = fun(user: User?){
        mainText.text = "MainActivity:$user"
    }
}
