package com.tannotour.smartscaff

import android.arch.lifecycle.LifecycleOwner
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.tannotour.scafflib.register
import com.tannotour.scafflib.remote
import com.tannotour.smartscaff.test.User
import kotlinx.android.synthetic.main.activity_main.*

class TwoActivity : AppCompatActivity(), LifecycleOwner {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        register(
                clazz = User::class,
                map = {
                    ""
                },
                onChanged = {
                    mainText.text = "TwoActivity:$it"
                }
        )
        button.setOnClickListener {
            remote(User::class, "test", "phone" to "18428323819", "password" to "123456")
        }
    }
}
