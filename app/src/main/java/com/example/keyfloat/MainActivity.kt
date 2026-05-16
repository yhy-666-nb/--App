package com.example.keyfloat

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.keyfloat.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val prefs: SharedPreferences by lazy {
        getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    }

    data class PermissionItem(val name: String, val key: String)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (isActivated()) {
            goToDesktopAndStartFloat()
            return
        }

        binding.btnActivate.setOnClickListener {
            val code = binding.etKey.text.toString().trim()
            if (code.isEmpty()) {
                Toast.makeText(this, "请输入卡密", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            checkPermissionsBeforeActivate(code)
        }

        binding.btnExit.setOnClickListener { finish() }
    }

    private fun isActivated() = prefs.getBoolean("is_activated", false)

    private fun checkPermissionsBeforeActivate(code: String) {
        val missing = mutableListOf<PermissionItem>()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                missing.add(PermissionItem("悬浮窗", "SYSTEM_ALERT_WINDOW"))
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                missing.add(PermissionItem("管理所有文件", "MANAGE_EXTERNAL_STORAGE"))
            }
        }

        if (missing.isEmpty()) {
            activateKey(code)
        } else {
            val names = missing.joinToString("、") { it.name }
            AlertDialog.Builder(this)
                .setTitle("权限未开启")
                .setMessage("使用前需授权：$names\n\n即将跳转到设置页面")
                .setPositiveButton("去设置") { _, _ ->
                    openPermissionSettings(missing.first())
                }
                .setNegativeButton("取消", null)
                .show()
        }
    }

    private fun openPermissionSettings(item: PermissionItem) {
        val intent = when (item.key) {
            "SYSTEM_ALERT_WINDOW" -> Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION).apply {
                data = Uri.parse("package:$packageName")
            }
            "MANAGE_EXTERNAL_STORAGE" -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
                    data = Uri.parse("package:$packageName")
                }
            } else {
                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.parse("package:$packageName")
                }
            }
            else -> Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.parse("package:$packageName")
            }
        }
        startActivity(intent)
    }

    private fun activateKey(key: String) {
        if (key == "123456") {
            prefs.edit().putBoolean("is_activated", true).putString("token", "mock_token").apply()
            Toast.makeText(this, "激活成功", Toast.LENGTH_SHORT).show()
            goToDesktopAndStartFloat()
        } else {
            Toast.makeText(this, "卡密错误，请重试", Toast.LENGTH_LONG).show()
            binding.etKey.error = "卡密无效"
        }
    }

    private fun goToDesktopAndStartFloat() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            Toast.makeText(this, "请先开启悬浮窗权限", Toast.LENGTH_LONG).show()
            return
        }
        val serviceIntent = Intent(this, FloatWindowService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }
        val homeIntent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_HOME)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(homeIntent)
        finish()
    }
}
