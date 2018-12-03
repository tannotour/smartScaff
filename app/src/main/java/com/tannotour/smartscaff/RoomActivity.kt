package com.tannotour.smartscaff

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.Observer
import android.arch.persistence.room.Room
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.tannotour.scafflib.log.logJson
import com.tannotour.scafflib.toJson
import com.tannotour.smartscaff.cli.*
import com.tannotour.smartscaff.paging.RoomUserAdapter
import com.tannotour.smartscaff.paging.observe
import com.tannotour.smartscaff.room.IRoomUserRepository
import com.tannotour.smartscaff.room.RoomUser
import com.tannotour.smartscaff.room.RoomUserDataBase
import kotlinx.android.synthetic.main.activity_room_user.*

class RoomActivity : AppCompatActivity(), LifecycleOwner {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room_user)

//        val db = Room.databaseBuilder(applicationContext, RoomUserDataBase::class.java, "roomDB").build()
//        db.close()
//        val roomUserDao = db.roomUserDao()
//        roomUserDao.getAll().observe(this, Observer {
//            mainText.text = it.toString()
//        })
//        GlobalScope.launch {
//            repeat(3){
//                delay(2000)
//                val roomUser = RoomUser()
//                roomUser.userName = "tannotour-$it"
//                roomUser.userPhone = "18428323819-$it"
//                roomUserDao.insert(roomUser)
//            }
//        }

        DBHelper.application = application

//        this fetch RoomUserDataBase::class source {
//            roomUserDao().getAll()
//        } register onChanged


//        val adapter = RoomUserAdapter(roomUserRecyclerView, roomUserSwipeRefreshLayout)
//        roomUserRecyclerView.adapter = adapter
//        adapter.register(this)
//
//        roomUserSwipeRefreshLayout.setOnRefreshListener{
//            if(roomUserSwipeRefreshLayout.isRefreshing){
//                adapter.refresh(this)
//            }
//        }

        val adapter = RoomUserAdapter(roomUserRecyclerView, roomUserSwipeRefreshLayout).observe(this)

        RoomUser::class remote{
            login("18428323819", "123456")
        } attachLife this listen {
            runOnUiThread {
                Toast.makeText(this, "${it ?: "登录任务正常完成"}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val onChanged = fun(list: List<RoomUser>?){
        logJson(list.toJson())
        Toast.makeText(this, list.toString(), Toast.LENGTH_SHORT).show()
    }
}
