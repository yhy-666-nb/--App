package com.example.keyfloat

import android.content.Context
import android.util.Log

object AimAssistController {
    private const val TAG = "AimAssistController"

    var isAimAssistEnabled: Boolean = false
    var isPredictionEnabled: Boolean = false
    var isAutoFireEnabled: Boolean = false
    var isNoRecoilEnabled: Boolean = false
    var isNoSwitchDelayEnabled: Boolean = false
    var isQuickReloadEnabled: Boolean = false
    var isAutoSwitchWeaponEnabled: Boolean = false
    var isJetpackEnabled: Boolean = false
    var isFreeViewEnabled: Boolean = false

    var crosshairX: Float = 1080f
    var crosshairY: Float = 2160f
    var crosshairZ: Float = 0f

    enum class BodyPart(val label: String, val boneName: String) {
        HEAD("头部", "head"), CHEST("胸部", "chest"),
        PELVIS("骨盆", "pelvis"), LEFT_LEG("左腿", "left_knee"), RIGHT_LEG("右腿", "right_knee")
    }

    data class PredictionConfig(
        val distanceMin: Float = 0f, val distanceMax: Float = 2500f,
        val aimX: Float = 1080f, val aimY: Float = 2160f,
        val aimBodyPart: BodyPart = BodyPart.CHEST,
        val aimOffsetX: Float = 50f, val aimOffsetY: Float = 30f
    )

    var predictionConfig = PredictionConfig()
    val customPredictions = mutableListOf(PredictionConfig())

    fun startAimAssist(context: Context) {
        Log.d(TAG, "自瞄已启动")
    }

    fun stopAimAssist() {
        Log.d(TAG, "自瞄已停止")
    }

    fun startAutoFire(context: Context) {
        Log.d(TAG, "自动开火已启动")
    }

    fun stopAutoFire() {
        Log.d(TAG, "自动开火已停止")
    }

    fun enableNoRecoil(enable: Boolean) {
        isNoRecoilEnabled = enable
        Log.d(TAG, "后坐力: ${if(enable) "取消" else "恢复"}")
    }

    fun enableNoSwitchDelay(enable: Boolean) {
        isNoSwitchDelayEnabled = enable
        Log.d(TAG, "后摇: ${if(enable) "取消" else "恢复"}")
    }

    fun performQuickReload() {
        Log.d(TAG, "一键换弹")
    }

    fun enableFreeView(enable: Boolean) {
        isFreeViewEnabled = enable
        Log.d(TAG, "视角限制: ${if(enable) "取消" else "恢复"}")
    }
}
