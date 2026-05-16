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

    /**
     * 获取最近使用的第三方应用列表（排除系统应用和自身）
     */
    fun getRecentApps(context: Context): List<AppInfo> {
        val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as? UsageStatsManager
        val currentTime = System.currentTimeMillis()
        val stats = usageStatsManager?.queryUsageStats(
            UsageStatsManager.INTERVAL_BEST,
            currentTime - 1000 * 60 * 60 * 24, // 最近24小时
            currentTime
        ) ?: return emptyList()

        val pm = context.packageManager
        val myPackage = context.packageName

        return stats
            .filter { it.packageName != myPackage }
            .sortedByDescending { it.lastTimeUsed }
            .mapNotNull { stat ->
                try {
                    val appInfo = pm.getApplicationInfo(stat.packageName, 0)
                    // 只显示用户安装的应用（非系统应用）
                    if ((appInfo.flags and ApplicationInfo.FLAG_SYSTEM) == 0) {
                        AppInfo(
                            packageName = stat.packageName,
                            appName = pm.getApplicationLabel(appInfo).toString(),
                            icon = pm.getApplicationIcon(stat.packageName)
                        )
                    } else null
                } catch (e: PackageManager.NameNotFoundException) {
                    null
                }
            }
            .distinctBy { it.packageName }
            .take(20) // 最多显示20个
    }

    fun checkProcess(context: Context): Boolean {
        if (targetPackageName.isEmpty()) return false
        // 简化检测：只要目标应用在最近使用列表中即可
        val recent = getRecentApps(context)
        return recent.any { it.packageName == targetPackageName }
    }

    data class AppInfo(
        val packageName: String,
        val appName: String,
        val icon: android.graphics.drawable.Drawable
    )
}
