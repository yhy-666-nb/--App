package com.example.keyfloat

import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build

object ProcessReader {

    var targetPackageName: String = ""
    var isProcessActive: Boolean = false
        private set

    fun checkProcess(context: Context): Boolean {
        if (targetPackageName.isEmpty()) return false

        try {
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
        } catch (e: Exception) {
            e.printStackTrace()
        }

        isProcessActive = false
        return false
    }

    /**
     * 获取用户安装的应用列表（按最近使用排序）
     */
    fun getInstalledApps(context: Context): List<AppInfo> {
        val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as? UsageStatsManager
        val packageManager = context.packageManager
        val currentTime = System.currentTimeMillis()
        val appList = mutableListOf<AppInfo>()

        val stats = usageStatsManager?.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            currentTime - 1000 * 60 * 60 * 24 * 7, // 最近7天
            currentTime
        ) ?: return appList

        // 过滤系统应用，只保留用户应用
        stats.sortByDescending { it.lastTimeUsed }
        for (stat in stats) {
            try {
                val appInfo = packageManager.getApplicationInfo(stat.packageName, 0)
                // 排除系统应用和自己
                if ((appInfo.flags and ApplicationInfo.FLAG_SYSTEM) == 0 &&
                    stat.packageName != context.packageName) {
                    val appName = packageManager.getApplicationLabel(appInfo).toString()
                    appList.add(AppInfo(appName, stat.packageName))
                }
            } catch (e: PackageManager.NameNotFoundException) {
                // 忽略已卸载的应用
            }
        }

        return appList
    }

    data class AppInfo(val name: String, val packageName: String)
}
