package com.example.keyfloat

import android.app.ActivityManager
import android.app.usage.UsageStatsManager
import android.content.Context
import android.os.Build

object ProcessReader {

    var targetPackageName: String = "com.pi.czrxdfirst"
    var isProcessActive: Boolean = false
        private set

    fun checkProcess(context: Context): Boolean {
        if (targetPackageName.isEmpty()) return false

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as? UsageStatsManager
                val currentTime = System.currentTimeMillis()
                val stats = usageStatsManager?.queryUsageStats(
                    UsageStatsManager.INTERVAL_DAILY,
                    currentTime - 1000 * 60 * 60 * 24,
                    currentTime
                )
                val targetStats = stats?.find { it.packageName == targetPackageName }
                if (targetStats != null && targetStats.lastTimeUsed > System.currentTimeMillis() - 5000) {
                    isProcessActive = true
                    return true
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            val am = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
            val runningTasks = am?.getRunningTasks(1)
            if (runningTasks?.isNotEmpty() == true) {
                val topPackage = runningTasks[0].topActivity?.packageName
                if (topPackage == targetPackageName) {
                    isProcessActive = true
                    return true
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        isProcessActive = false
        return false
    }
}
