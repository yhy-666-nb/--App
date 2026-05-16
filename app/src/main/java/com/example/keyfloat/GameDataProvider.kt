package com.example.keyfloat

import android.content.Context
import kotlin.random.Random

object GameDataProvider {

    data class Entity(
        val type: EntityType,
        val name: String = "",
        val x: Float = 0f,
        val y: Float = 0f,
        val z: Float = 0f,
        val health: Float = 100f,
        val maxHealth: Float = 100f,
        val distance: Float = 0f,
        val isVisible: Boolean = true,
        val itemValue: Int = 0,
        val itemRarity: ItemRarity = ItemRarity.COMMON,
        val bones: List<BonePoint> = emptyList()
    )

    data class BonePoint(val name: String, val screenX: Float, val screenY: Float)

    enum class EntityType { PLAYER, ENEMY, LOOT, VEHICLE, AIRDROP }

    enum class ItemRarity(val color: Int, val label: String) {
        COMMON(0xFFAAAAAA.toInt(), "普通"),
        UNCOMMON(0xFF00AA00.toInt(), "稀有"),
        RARE(0xFF0088FF.toInt(), "精良"),
        EPIC(0xFFAA00FF.toInt(), "史诗"),
        LEGENDARY(0xFFFFAA00.toInt(), "传说")
    }

    data class GameData(
        val screenWidth: Int = 1080,
        val screenHeight: Int = 2400,
        val player: Entity = Entity(type = EntityType.PLAYER, name = "Player", x = 500f, y = 1200f, health = 85f),
        val enemies: List<Entity> = emptyList(),
        val lootItems: List<Entity> = emptyList(),
        val stamina: Float = 80f,
        val maxStamina: Float = 100f,
        val weight: Float = 23.5f,
        val maxWeight: Float = 50f,
        val fps: Int = 60,
        val ping: Int = 30
    )

    var targetPackage: String = ""

    fun start(context: Context) {}
    fun stop() {}

    fun generateMockData(): GameData {
        val enemies = (0..5).map { i ->
            val ex = 500f + (Random.nextFloat() - 0.5f) * 800f
            val ey = 1200f + (Random.nextFloat() - 0.5f) * 600f
            val dist = kotlin.math.sqrt(((ex - 500f) * (ex - 500f) + (ey - 1200f) * (ey - 1200f)).toDouble()).toFloat()
            Entity(
                type = EntityType.ENEMY, name = "敌人${i + 1}", x = ex, y = ey,
                health = Random.nextFloat() * 100f, maxHealth = 100f, distance = dist,
                bones = listOf(
                    BonePoint("head", ex, ey - 80f), BonePoint("chest", ex, ey - 40f),
                    BonePoint("pelvis", ex, ey), BonePoint("left_knee", ex - 15f, ey + 40f),
                    BonePoint("right_knee", ex + 15f, ey + 40f)
                )
            )
        }
        return GameData(enemies = enemies)
    }
}
