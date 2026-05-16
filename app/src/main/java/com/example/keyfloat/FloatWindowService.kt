package com.example.keyfloat

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.core.app.NotificationCompat

class FloatWindowService : Service() {

    private lateinit var windowManager: WindowManager
    private lateinit var floatView: View
    private val tabs = listOf("主页", "辅助", "绘制", "功能")
    private val tabViews = mutableListOf<TextView>()

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        createFloatView()
        startForegroundNotification()
    }

    private fun startForegroundNotification() {
        val channelId = "float_window_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "悬浮窗服务", NotificationManager.IMPORTANCE_LOW)
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("密钥浮窗运行中")
            .setContentText("点击返回应用")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .build()
        startForeground(1, notification)
    }

    private fun createFloatView() {
        val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        floatView = inflater.inflate(R.layout.float_window_main, null)

        val navContainer = floatView.findViewById<LinearLayout>(R.id.nav_container)
        val tabIcons = listOf("🏠", "🎯", "✏️", "⚙️")

        for (i in tabs.indices) {
            val tab = TextView(this).apply {
                text = "${tabIcons[i]}\n${tabs[i]}"
                setPadding(12, 14, 12, 14)
                textSize = 11f
                gravity = Gravity.CENTER
                setTextColor(resources.getColor(R.color.jelly_text_secondary, null))
                setOnClickListener { selectTab(i) }
            }
            navContainer.addView(tab)
            tabViews.add(tab)
        }

        selectTab(0)

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )
        params.gravity = Gravity.TOP or Gravity.END
        params.x = 0
        params.y = 100
        windowManager.addView(floatView, params)
    }

    private fun selectTab(position: Int) {
        tabViews.forEachIndexed { index, tv ->
            tv.isSelected = index == position
            if (index == position) {
                tv.setTextColor(resources.getColor(R.color.jelly_text_accent, null))
                tv.setBackgroundResource(R.drawable.nav_tab_bg)
            } else {
                tv.setTextColor(resources.getColor(R.color.jelly_text_secondary, null))
                tv.setBackgroundColor(resources.getColor(android.R.color.transparent, null))
            }
        }

        val contentFrame = floatView.findViewById<FrameLayout>(R.id.content_frame)
        val inflater = LayoutInflater.from(this)
        contentFrame.removeAllViews()

        when (position) {
            0 -> {
                val homeView = inflater.inflate(R.layout.float_content_home, contentFrame, false)
                contentFrame.addView(homeView)
                val btnReadProcess = homeView.findViewById<Button>(R.id.btn_read_process)
                btnReadProcess.setOnClickListener {
    // 检查使用情况访问权限
    if (!hasUsageStatsPermission()) {
        requestUsageStatsPermission()
        Toast.makeText(this@FloatWindowService, "请先开启使用情况访问权限", Toast.LENGTH_LONG).show()
        return@setOnClickListener
    }

    // 自动获取前台应用包名
    val pkg = ProcessReader.getForegroundPackage(this@FloatWindowService)
    if (pkg != null && pkg != packageName) {
        ProcessReader.targetPackageName = pkg

        // 更新主页UI
        val pm = packageManager
        try {
            val appInfo = pm.getApplicationInfo(pkg, 0)
            val appName = pm.getApplicationLabel(appInfo).toString()
            val appIcon = pm.getApplicationIcon(pkg)

            val iconView = homeView.findViewById<ImageView>(R.id.iv_target_icon)
            val nameView = homeView.findViewById<TextView>(R.id.tv_target_name)
            val infoView = homeView.findViewById<TextView>(R.id.tv_process_info)

            iconView.setImageDrawable(appIcon)
            nameView.text = appName
            infoView.text = "状态：已绑定"

            Toast.makeText(this@FloatWindowService, "已绑定: $appName", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            homeView.findViewById<TextView>(R.id.tv_target_name).text = "绑定失败"
            Toast.makeText(this@FloatWindowService, "绑定失败，请重试", Toast.LENGTH_SHORT).show()
        }
    } else {
        Toast.makeText(this@FloatWindowService, "未检测到前台应用\n请先打开目标游戏再点击读取", Toast.LENGTH_LONG).show()
    }
                }
            }
            1 -> {
                val assistView = inflater.inflate(R.layout.float_content_assist, contentFrame, false)
                contentFrame.addView(assistView)
                Toast.makeText(this, "辅助功能已加载", Toast.LENGTH_SHORT).show()
            }
            2 -> {
                val drawView = inflater.inflate(R.layout.float_content_draw, contentFrame, false)
                contentFrame.addView(drawView)
                Toast.makeText(this, "绘制功能已加载", Toast.LENGTH_SHORT).show()
            }
            3 -> {
                val funcView = inflater.inflate(R.layout.float_content_function, contentFrame, false)
                contentFrame.addView(funcView)
                funcView.findViewById<Button>(R.id.btn_exit_float).setOnClickListener { stopSelf() }
            }
        }
    }

    private fun hasUsageStatsPermission(): Boolean {
    val appOps = getSystemService(Context.APP_OPS_SERVICE) as? android.app.AppOpsManager
    if (appOps != null) {
        val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            appOps.unsafeCheckOpNoThrow(
                android.app.AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(),
                packageName
            )
        } else {
            @Suppress("DEPRECATION")
            appOps.checkOpNoThrow(
                android.app.AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(),
                packageName
            )
        }
        return mode == android.app.AppOpsManager.MODE_ALLOWED
    }
    return false
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        if (::floatView.isInitialized) windowManager.removeView(floatView)
    }
}
