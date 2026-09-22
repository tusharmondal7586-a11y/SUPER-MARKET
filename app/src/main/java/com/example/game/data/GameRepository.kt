package com.example.game.data

import android.content.Context
import android.content.SharedPreferences
import com.example.game.model.GameData
import com.example.game.model.MergeCell
import com.example.game.model.ProductItem

class GameRepository(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("supermarket_stories_prefs", Context.MODE_PRIVATE)

    fun loadCoins(defaultVal: Long = 350L): Long = prefs.getLong("coins", defaultVal)
    fun saveCoins(coins: Long) = prefs.edit().putLong("coins", coins).apply()

    fun loadGems(defaultVal: Int = 20): Int = prefs.getInt("gems", defaultVal)
    fun saveGems(gems: Int) = prefs.edit().putInt("gems", gems).apply()

    fun loadEnergy(defaultVal: Int = 100): Int = prefs.getInt("energy", defaultVal)
    fun saveEnergy(energy: Int) = prefs.edit().putInt("energy", energy).apply()

    fun loadXp(defaultVal: Int = 0): Int = prefs.getInt("xp", defaultVal)
    fun saveXp(xp: Int) = prefs.edit().putInt("xp", xp).apply()

    fun loadStoreLevel(defaultVal: Int = 1): Int = prefs.getInt("store_level", defaultVal)
    fun saveStoreLevel(level: Int) = prefs.edit().putInt("store_level", level).apply()

    fun loadReputation(defaultVal: Float = 4.7f): Float = prefs.getFloat("reputation", defaultVal)
    fun saveReputation(reputation: Float) = prefs.edit().putFloat("reputation", reputation).apply()

    fun loadTotalMerges(): Int = prefs.getInt("total_merges", 0)
    fun saveTotalMerges(count: Int) = prefs.edit().putInt("total_merges", count).apply()

    fun loadTotalServed(): Int = prefs.getInt("total_served", 0)
    fun saveTotalServed(count: Int) = prefs.edit().putInt("total_served", count).apply()

    fun loadTotalCoinsEarned(): Long = prefs.getLong("total_coins_earned", 0L)
    fun saveTotalCoinsEarned(amount: Long) = prefs.edit().putLong("total_coins_earned", amount).apply()

    fun loadCurrentChapter(): Int = prefs.getInt("current_chapter", 0)
    fun saveCurrentChapter(idx: Int) = prefs.edit().putInt("current_chapter", idx).apply()

    fun isTutorialCompleted(): Boolean = prefs.getBoolean("tutorial_completed", false)
    fun setTutorialCompleted(completed: Boolean) = prefs.edit().putBoolean("tutorial_completed", completed).apply()

    fun isSoundEnabled(): Boolean = prefs.getBoolean("sound_enabled", true)
    fun setSoundEnabled(enabled: Boolean) = prefs.edit().putBoolean("sound_enabled", enabled).apply()

    // Board serialization
    fun loadBoard(gridSize: Int = 16): List<MergeCell> {
        val serialized = prefs.getString("board_state", null)
        if (serialized.isNullOrEmpty()) {
            // Initial starter board with 2 apples ready to merge!
            val initial = MutableList(gridSize) { MergeCell(it, null) }
            initial[5] = MergeCell(5, GameData.ITEM_APPLE)
            initial[6] = MergeCell(6, GameData.ITEM_APPLE)
            initial[9] = MergeCell(9, GameData.ITEM_WHEAT)
            initial[10] = MergeCell(10, GameData.ITEM_MILK)
            return initial
        }

        val allItemsMap = mutableMapOf<String, ProductItem>()
        GameData.ALL_CHAINS.values.flatten().forEach { allItemsMap[it.id] = it }

        val parts = serialized.split(";")
        return List(gridSize) { idx ->
            val itemId = parts.getOrNull(idx)
            val item = if (!itemId.isNullOrBlank() && itemId != "EMPTY") allItemsMap[itemId] else null
            MergeCell(idx, item)
        }
    }

    fun saveBoard(cells: List<MergeCell>) {
        val str = cells.joinToString(";") { it.item?.id ?: "EMPTY" }
        prefs.edit().putString("board_state", str).apply()
    }

    fun clearAll() {
        prefs.edit().clear().apply()
    }
}
