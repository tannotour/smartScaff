package com.tannotour.smartscaff.wind

import android.app.Activity
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.tannotour.smartscaff.R
import android.content.ComponentName
import android.app.admin.DevicePolicyManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*


/**
 * Created by mitnick on 2018/10/12.
 * Description
 */
class WindTestActivity: AppCompatActivity() {

    private lateinit var policyManager: DevicePolicyManager
    private lateinit var myComponentName: ComponentName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 获取设备管理服务
        policyManager = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        myComponentName = ComponentName(this, MyDeviceAdminReceiver::class.java)

        // 启动设备管理(隐式Intent) - 在AndroidManifest.xml中设定相应过滤器
        val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
        // 权限列表
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, myComponentName)
        // 描述(additional explanation)
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "激活后才能使用锁屏功能")
        startActivityForResult(intent, 1000)

        button.setOnClickListener {
            policyManager.lockNow()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == 1000 && resultCode == Activity.RESULT_OK) {
            Toast.makeText(this, "成功", Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(this, "失败s", Toast.LENGTH_SHORT).show()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}