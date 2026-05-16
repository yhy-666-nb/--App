package com.example.keyfloat

import android.content.Context
import android.util.Log

object GameModifier {
    private const val TAG = "GameModifier"

    var isNightVisionEnabled: Boolean = false
    var isFogRemoved: Boolean = false
    var isFovModified: Boolean = false
    var currentFovValue: Float = 70f

    enum class ViewMode(val fov: Float, val label: String) {
        PHONE(70f, "手机视角"), TABLET(90f, "平板视角")
    }

    var currentViewMode = ViewMode.PHONE

    fun initialize(context: Context): Boolean {
        Log.d(TAG, "初始化")
        return true
    }

    fun enableNightVisionAndClearFog(): Boolean {
        isNightVisionEnabled = true
        isFogRemoved = true
        return true
    }

    fun disableNightVisionAndClearFog(): Boolean {
        isNightVisionEnabled = false
        isFogRemoved = false
        return true
    }

    fun setFov(viewMode: ViewMode): Boolean {
        currentFovValue = viewMode.fov
        currentViewMode = viewMode
        isFovModified = true
        return true
    }

    fun resetFov(): Boolean {
        currentFovValue = ViewMode.PHONE.fov
        currentViewMode = ViewMode.PHONE
        isFovModified = false
        return true
    }

    fun clearMapFog(): Boolean {
        Log.d(TAG, "地图迷雾已清除")
        return true
    }

    fun showAllChests(): Boolean {
        Log.d(TAG, "宝箱已显示")
        return true
    }

    fun cleanup() {
        Log.d(TAG, "清理")
    }
}
