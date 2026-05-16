package com.example.keyfloat

import android.graphics.*
import kotlin.math.min

class OverlayRenderer {

    var showPlayerModel: Boolean = false
    var showHealthBar: Boolean = false
    var showBoxEsp: Boolean = false
    var showDistance: Boolean = false
    var showStamina: Boolean = false
    var showWeight: Boolean = false
    var showLoot: Boolean = false
    var showChests: Boolean = false
    var chestFilterCommon: Boolean = true
    var chestFilterRare: Boolean = true
    var chestFilterEpic: Boolean = true
    var chestFilterLegendary: Boolean = true
    var chestDisplayDistance: Int = 1000
    var lootPriceMin: Int = 0
    var lootPriceMax: Int = 1000000

    private val boxPaint = Paint().apply {
        color = Color.RED
        style = Paint.Style.STROKE
        strokeWidth = 2f
        isAntiAlias = true
    }

    private val textPaint = Paint().apply {
        color = Color.WHITE
        textSize = 24f
        isAntiAlias = true
        setShadowLayer(2f, 1f, 1f, Color.BLACK)
    }

    fun render(canvas: Canvas, data: GameDataProvider.GameData) {
        canvas.drawText("密钥浮窗 ESP", 50f, 80f, textPaint)
        data.enemies.forEachIndexed { _, enemy ->
            val x = mapWorld(enemy.x, data.screenWidth.toFloat())
            val y = mapWorld(enemy.y, data.screenHeight.toFloat())
            boxPaint.color = if (enemy.health > 60f) Color.GREEN
            else if (enemy.health > 30f) Color.YELLOW
            else Color.RED
            canvas.drawRect(x - 30f, y - 50f, x + 30f, y + 30f, boxPaint)
            canvas.drawText("${enemy.distance.toInt()}m", x - 20f, y - 55f, textPaint)
        }
    }

    private fun mapWorld(worldCoord: Float, screenSize: Float): Float {
        return (worldCoord / 2000f) * screenSize
    }
}
