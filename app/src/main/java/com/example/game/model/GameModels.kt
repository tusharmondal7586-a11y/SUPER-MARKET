package com.example.game.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.MarketBlue
import com.example.ui.theme.MarketGold
import com.example.ui.theme.MarketGreen
import com.example.ui.theme.MarketOrangePrimary
import com.example.ui.theme.MarketPurple
import com.example.ui.theme.MarketRed
import com.example.ui.theme.MarketTealSecondary

enum class ProductCategory(
    val displayName: String,
    val emoji: String,
    val accentColor: Color
) {
    FRUIT_VEG("Fruit & Veg", "🍎", MarketGreen),
    BAKERY("Bakery", "🍞", MarketOrangePrimary),
    DAIRY("Dairy", "🥛", MarketTealSecondary),
    DRINKS("Drinks", "🧃", MarketBlue),
    CANNED("Groceries", "🥫", MarketRed),
    SNACKS("Snacks", "🍪", MarketGold),
}

data class ProductItem(
    val id: String,
    val name: String,
    val iconEmoji: String,
    val category: ProductCategory,
    val level: Int, // 1 to 4
    val sellPrice: Int,
    val stockValue: Int,
    val xpValue: Int,
    val description: String
)

data class MergeCell(
    val index: Int,
    val item: ProductItem? = null
)

data class StoreSection(
    val id: String,
    val name: String,
    val category: ProductCategory,
    val iconEmoji: String,
    val level: Int = 1,
    val capacity: Int = 10,
    val currentStock: Int = 0,
    val incomePerMinute: Int = 30,
    val upgradeCost: Int = 200,
    val isUnlocked: Boolean = true,
    val requiredStoreLevel: Int = 1
)

enum class CustomerType(
    val title: String,
    val avatarEmoji: String,
    val personality: String
) {
    BUSY_MOM("Busy Mom", "👩", "Shopping quick for dinner tonight!"),
    OFFICE_WORKER("Office Worker", "👨", "Grabbing a fresh lunch on a short break."),
    GRANDMA("Grandma", "👵", "Looking for healthy pantry essentials."),
    KID("Kid", "🧒", "Sweet treats and juices are the best!"),
    STUDENT("Student", "🧑", "Stocking up for study week snacks."),
    CHEF("Restaurant Owner", "👨‍🍳", "Sourcing top-tier gourmet displays."),
    VIP("VIP Shopper", "💼", "Wants only premier deluxe items, pays top coin!")
}

data class CustomerOrder(
    val id: String,
    val customerType: CustomerType,
    val dialogue: String,
    val requiredItems: List<ProductItem>,
    val coinReward: Int,
    val gemReward: Int = 0,
    val xpReward: Int = 15,
    val patienceSeconds: Int = 60,
    val maxPatienceSeconds: Int = 60,
    val isVip: Boolean = false
)

enum class EmployeeType(
    val roleTitle: String,
    val avatarEmoji: String,
    val perkSummary: String
) {
    MANAGER("Manager", "🧑‍💼", "Auto-organizes merge board & +25% customer tips"),
    CASHIER("Cashier", "👩‍💻", "Speeds up customer checkout by 30%"),
    STOCKER("Stocker", "📦", "Auto-restocks shelves periodically from warehouse"),
    CLEANER("Cleaner", "🧹", "Maintains store cleanliness & guarantees +0.5⭐ reputation"),
    DELIVERY("Delivery Worker", "🚚", "Drops off mystery supply crates with free items")
}

data class Employee(
    val type: EmployeeType,
    val isHired: Boolean = false,
    val level: Int = 1,
    val hireCost: Int = 300,
    val upgradeCost: Int = 450
)

enum class ChapterGoalType {
    EARN_COINS,
    SERVE_CUSTOMERS,
    MERGE_ITEMS,
    REACH_REPUTATION,
    UPGRADE_SECTIONS
}

data class StoryChapter(
    val chapterNumber: Int,
    val title: String,
    val speaker: String,
    val speakerAvatar: String,
    val storyDialogue: String,
    val goalDescription: String,
    val goalType: ChapterGoalType,
    val targetValue: Int,
    val rewardCoins: Int,
    val rewardGems: Int,
    val isCompleted: Boolean = false
)

data class DailyMission(
    val id: String,
    val title: String,
    val iconEmoji: String,
    val currentProgress: Int,
    val targetProgress: Int,
    val rewardCoins: Int,
    val rewardEnergy: Int,
    val isClaimed: Boolean = false
)

data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val iconEmoji: String,
    val currentProgress: Int,
    val targetProgress: Int,
    val rewardGems: Int,
    val isUnlocked: Boolean = false
)

enum class RandomEventType(
    val title: String,
    val description: String,
    val actionLabel: String,
    val iconEmoji: String
) {
    DELIVERY_ARRIVED("Delivery Arrived!", "Your wholesaler just unloaded a mystery supply crate!", "Open Crate", "🚚"),
    SHOPPING_RUSH("Shopping Rush!", "A tour bus stopped by! Customers are tipping double!", "Serve Rush", "🛒"),
    POWER_OUTAGE("Power Outage!", "Refrigerators are beeping! Tap to restart the backup generator!", "Restart Power", "💡"),
    VIP_CUSTOMER("VIP Customer!", "A VIP connoisseur is browsing for top-tier goods!", "Welcome VIP", "💎"),
    LOST_PACKAGE("Lost Package!", "A shopper dropped their parcel by the cart corral!", "Return Bag", "📦")
}

data class ActiveRandomEvent(
    val type: RandomEventType,
    val id: Long = System.currentTimeMillis()
)
