package com.example.keyfloat

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.PorterDuff
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.*

class GameOverlayService : Service() {

    private lateinit var windowManager: WindowManager
    private var overlayView: OverlayCanvasView? = null
    private val renderer = OverlayRenderer()
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    companion object {
        var isRunning = false
        var showPlayerModel = false
        var showHealthBar = false
        var showBoxEsp = false
        var showDistance = false
        var showStamina = false
        var showWeight = false
        var showLoot = false
        var showChests = false
        var chestFilterCommon = true
        var chestFilterRare = true
        var chestFilterEpic = true
        var chestFilterLegendary = true
        var chestDisplayDistance = 1000
        var lootPriceMin = 0
        var lootPriceMax = 1000000

        fun start(context: Context) {
            if (!isRunning) {
                val intent = Intent(context, GameOverlayService::class.java)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    context.startForegroundService(intent)
                else
                    context.startService(intent)
            }
        }

        fun stop(context: Context) {
            context.stopService(Intent(context, GameOverlayService::class.java))
        }
    }

    override fun onCreate() {
        super.onCreate()
        isRunning = true
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        createOverlayView()
        startForegroundNotification()
        startRenderLoop()
    }

    private fun createOverlayView() {
        overlayView = OverlayCanvasView(this)
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        )
        params.gravity = Gravity.TOP or Gravity.START
        windowManager.addView(overlayView, params)
    }

    private fun startForegroundNotification() {
        val channelId = "game_overlay_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "绘制服务", NotificationManager.IMPORTANCE_LOW)
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
        startForeground(2, NotificationCompat.Builder(this, channelId)
            .setContentTitle("绘制服务运行中")
            .setSmallIcon(android.R.drawable.ic_menu_view)
            .build())
    }

    private fun startRenderLoop() {
        scope.launch {
            while (isActive) {
                renderer.showPlayerModel = showPlayerModel
                renderer.showHealthBar = showHealthBar
                renderer.showBoxEsp = showBoxEsp
                renderer.showDistance = showDistance
                renderer.showStamina = showStamina
                renderer.showWeight = showWeight
                renderer.showLoot = showLoot
                renderer.showChests = showChests
                overlayView?.invalidate()
                delay(16)
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        isRunning = false
        scope.cancel()
        if (overlayView != null) windowManager.removeView(overlayView)
    }

    inner class OverlayCanvasView(context: Context) : View(context) {
        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
            renderer.render(canvas, GameDataProvider.generateMockData())
        }
    }
}
