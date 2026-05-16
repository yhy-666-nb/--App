package com.example.keyfloat

import android.app.ActivityManager
import android.app.usage.UsageStatsManager
import android.content.Context
import android.os.Build

object ProcessReader {

    var targetPackageName: String = ""
    var isProcessActive: Boolean = false
        private set

    /**
     * 获取当前前台应用的包名
     */
    fun getForegroundPackage(context: Context): String? {
        // Android 5.0+ 使用 UsageStatsManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as? UsageStatsManager
            val now = System.currentTimeMillis()
            val stats = usageStatsManager?.queryUsageStats(
                UsageStatsManager.INTERVAL_BEST,
                now - 1000 * 60, // 最近1分钟
                now
            )
            // 找出最后使用时间的应用
            val recent = stats?.maxByOrNull { it.lastTimeUsed }
            if (recent != null && recent.packageName != context.packageName) {
                return recent.packageName
            }
        }
        // 备用：ActivityManager（可能受限）
        try {
            val am = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
            val tasks = am?.getRunningTasks(1)
            if (tasks?.isNotEmpty() == true) {
                return tasks[0].topActivity?.packageName
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

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
