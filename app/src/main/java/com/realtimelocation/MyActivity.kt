package com.realtimelocation

import android.Manifest
import android.app.AppOpsManager
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Process.myUid
import android.os.Process
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.*
import android.provider.Settings

class MyActivity : AppCompatActivity() {

    var backGroundService: Intent? = null
    var txtAppName: TextView? = null
    val TAG: String? by lazy { MyActivity::class.java.canonicalName }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        txtAppName = findViewById(R.id.txtAppName)
        backGroundService = Intent(this, MyBackGroundService::class.java)
        checkIfAppUsageAccess()
    }

    var broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(
            context: Context,
            intent: Intent
        ) {
            if (intent != null && intent.action == CURRENT_RUNNING_APP_NAME) {
                val appNames = intent.getStringExtra("AppNames")
                if (txtAppName != null && appNames != null && appNames.length > 0) {
                    txtAppName!!.text = appNames
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val intentFilter = IntentFilter()
        intentFilter.addAction(CURRENT_RUNNING_APP_NAME)
        registerReceiver(broadcastReceiver, intentFilter)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(broadcastReceiver)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService(backGroundService)
    }

    fun checkIfAppUsageAccess() {
        var granted = false
        val appOps = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            Process.myUid(), packageName
        )
        granted = if (mode == AppOpsManager.MODE_DEFAULT) {
            checkCallingOrSelfPermission(Manifest.permission.PACKAGE_USAGE_STATS) == PackageManager.PERMISSION_GRANTED
        } else {
            mode == AppOpsManager.MODE_ALLOWED
        }
        if (Build.VERSION.SDK_INT >= 21 && !granted) {
            val mUsageStatsManager =
                getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
            val time = System.currentTimeMillis()
            val stats: List<*>? = mUsageStatsManager.queryUsageStats(
                UsageStatsManager.INTERVAL_DAILY, time - 1000 * 120, time
            )
            if (stats == null || stats.isEmpty()) {
                val intent = Intent()
                intent.action = Settings.ACTION_USAGE_ACCESS_SETTINGS
                startActivity(intent)
            }
        } else {
            startService(backGroundService)
        }
    }

    companion object {
        const val CURRENT_RUNNING_APP_NAME = "CURRENT_RUNNING_APP_NAME"
    }
}