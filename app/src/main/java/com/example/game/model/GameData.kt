package com.example.game.model

object GameData {

    // 🍎 Fruit & Veg Chain
    val ITEM_APPLE = ProductItem("fv_1", "Crisp Apple", "🍎", ProductCategory.FRUIT_VEG, 1, 15, 1, 5, "Crisp red apple freshly picked.")
    val ITEM_APPLE_BASKET = ProductItem("fv_2", "Apple Basket", "🧺", ProductCategory.FRUIT_VEG, 2, 35, 2, 12, "Woven basket filled with sweet apples.")
    val ITEM_FRUIT_BOX = ProductItem("fv_3", "Premium Fruit Box", "📦", ProductCategory.FRUIT_VEG, 3, 85, 4, 30, "Assorted farm-fresh organic fruits.")
    val ITEM_FRUIT_DISPLAY = ProductItem("fv_4", "Deluxe Fruit Display", "✨", ProductCategory.FRUIT_VEG, 4, 210, 8, 75, "Spectacular orchard centerpiece.")

    // 🍞 Bakery Chain
    val ITEM_WHEAT = ProductItem("bk_1", "Farm Wheat", "🌾", ProductCategory.BAKERY, 1, 15, 1, 5, "Golden wheat straight from the windmill.")
    val ITEM_BREAD = ProductItem("bk_2", "Fresh Baguette", "🥖", ProductCategory.BAKERY, 2, 35, 2, 12, "Warm, crusty artisanal loaf.")
    val ITEM_PASTRY = ProductItem("bk_3", "Pastry Assortment", "🥐", ProductCategory.BAKERY, 3, 85, 4, 30, "Buttery croissants and flaky danishes.")
    val ITEM_CAKE = ProductItem("bk_4", "Deluxe Cake", "🎂", ProductCategory.BAKERY, 4, 210, 8, 75, "Decadent multi-layered celebration cake.")

    // 🥛 Dairy Chain
    val ITEM_MILK = ProductItem("dy_1", "Fresh Milk", "🥛", ProductCategory.DAIRY, 1, 18, 1, 6, "Chilled whole farm milk.")
    val ITEM_CHEESE = ProductItem("dy_2", "Artisanal Cheese", "🧀", ProductCategory.DAIRY, 2, 40, 2, 14, "Aged sharp cheddar wheel.")
    val ITEM_BUTTER = ProductItem("dy_3", "Golden Butter", "🧈", ProductCategory.DAIRY, 3, 95, 4, 32, "Rich cultured European-style butter.")
    val ITEM_DAIRY_TOWER = ProductItem("dy_4", "Gourmet Dairy Platter", "🍨", ProductCategory.DAIRY, 4, 230, 8, 80, "Exquisite selection of cheeses and creams.")

    // 🧃 Drinks Chain
    val ITEM_LEMON = ProductItem("dk_1", "Fresh Lemon", "🍋", ProductCategory.DRINKS, 1, 15, 1, 5, "Zesty sun-ripened citrus.")
    val ITEM_JUICE = ProductItem("dk_2", "Citrus Juice", "🧃", ProductCategory.DRINKS, 2, 35, 2, 12, "Refreshing cold-pressed juice box.")
    val ITEM_SODA = ProductItem("dk_3", "Sparkling Soda", "🍾", ProductCategory.DRINKS, 3, 85, 4, 30, "Bubbly artisanal craft soda.")
    val ITEM_NECTAR = ProductItem("dk_4", "Deluxe Nectar Bottle", "🍷", ProductCategory.DRINKS, 4, 210, 8, 75, "Vintage botanical fruit nectar.")

    // 🥫 Groceries / Canned Chain
    val ITEM_CAN = ProductItem("cn_1", "Tomato Can", "🥫", ProductCategory.CANNED, 1, 20, 1, 6, "Sun-dried Italian tomatoes.")
    val ITEM_SOUP = ProductItem("cn_2", "Harvest Soup", "🥣", ProductCategory.CANNED, 2, 45, 2, 15, "Steaming pot of vegetable minestrone.")
    val ITEM_PASTA = ProductItem("cn_3", "Gourmet Pasta Pack", "🍝", ProductCategory.CANNED, 3, 105, 4, 35, "Handcrafted semolina pasta dinner.")
    val ITEM_HAMPER = ProductItem("cn_4", "Grand Pantry Hamper", "👑", ProductCategory.CANNED, 4, 250, 8, 90, "Opulent banquet basket filled with delicacies.")

    // 🍪 Snacks Chain
    val ITEM_CORN = ProductItem("sn_1", "Sweet Corn", "🌽", ProductCategory.SNACKS, 1, 15, 1, 5, "Sweet yellow kernels.")
    val ITEM_POPCORN = ProductItem("sn_2", "Butter Popcorn", "🍿", ProductCategory.SNACKS, 2, 35, 2, 12, "Fluffy movie-theater style popcorn.")
    val ITEM_COOKIES = ProductItem("sn_3", "Choco Cookie Box", "🍪", ProductCategory.SNACKS, 3, 85, 4, 30, "Double-chocolate chip crunch cookies.")
    val ITEM_SWEET_TOWER = ProductItem("sn_4", "Luxury Sweet Tower", "🍫", ProductCategory.SNACKS, 4, 210, 8, 75, "Towering assortment of Belgian chocolates.")

    val ALL_CHAINS: Map<ProductCategory, List<ProductItem>> = mapOf(
        ProductCategory.FRUIT_VEG to listOf(ITEM_APPLE, ITEM_APPLE_BASKET, ITEM_FRUIT_BOX, ITEM_FRUIT_DISPLAY),
        ProductCategory.BAKERY to listOf(ITEM_WHEAT, ITEM_BREAD, ITEM_PASTRY, ITEM_CAKE),
        ProductCategory.DAIRY to listOf(ITEM_MILK, ITEM_CHEESE, ITEM_BUTTER, ITEM_DAIRY_TOWER),
        ProductCategory.DRINKS to listOf(ITEM_LEMON, ITEM_JUICE, ITEM_SODA, ITEM_NECTAR),
        ProductCategory.CANNED to listOf(ITEM_CAN, ITEM_SOUP, ITEM_PASTA, ITEM_HAMPER),
        ProductCategory.SNACKS to listOf(ITEM_CORN, ITEM_POPCORN, ITEM_COOKIES, ITEM_SWEET_TOWER),
    )

    fun getNextLevelItem(current: ProductItem): ProductItem? {
        val list = ALL_CHAINS[current.category] ?: return null
        val nextIdx = current.level // current.level is 1-based, so next item is at index level
        return if (nextIdx < list.size) list[nextIdx] else null
    }

    val INITIAL_SECTIONS = listOf(
        StoreSection("sec_fv", "Fruit & Vegetables", ProductCategory.FRUIT_VEG, "🍎", level = 1, capacity = 10, currentStock = 4, incomePerMinute = 50, upgradeCost = 150, isUnlocked = true, requiredStoreLevel = 1),
        StoreSection("sec_bk", "Bakery Corner", ProductCategory.BAKERY, "🍞", level = 1, capacity = 10, currentStock = 2, incomePerMinute = 45, upgradeCost = 250, isUnlocked = true, requiredStoreLevel = 1),
        StoreSection("sec_dy", "Dairy Fresh", ProductCategory.DAIRY, "🥛", level = 1, capacity = 10, currentStock = 2, incomePerMinute = 40, upgradeCost = 350, isUnlocked = true, requiredStoreLevel = 2),
        StoreSection("sec_dk", "Beverage Bar", ProductCategory.DRINKS, "🧃", level = 1, capacity = 10, currentStock = 0, incomePerMinute = 60, upgradeCost = 450, isUnlocked = false, requiredStoreLevel = 3),
        StoreSection("sec_sn", "Snack Alley", ProductCategory.SNACKS, "🍪", level = 1, capacity = 10, currentStock = 0, incomePerMinute = 55, upgradeCost = 600, isUnlocked = false, requiredStoreLevel = 4),
        StoreSection("sec_cn", "Grocery & Canned", ProductCategory.CANNED, "🥫", level = 1, capacity = 10, currentStock = 0, incomePerMinute = 70, upgradeCost = 800, isUnlocked = false, requiredStoreLevel = 5),
        StoreSection("sec_fz", "Frozen Foods", ProductCategory.SNACKS, "🧊", level = 1, capacity = 12, currentStock = 0, incomePerMinute = 85, upgradeCost = 1100, isUnlocked = false, requiredStoreLevel = 7),
        StoreSection("sec_mt", "Meat & Seafood", ProductCategory.CANNED, "🥩", level = 1, capacity = 12, currentStock = 0, incomePerMinute = 100, upgradeCost = 1500, isUnlocked = false, requiredStoreLevel = 10),
        StoreSection("sec_hh", "Household & Living", ProductCategory.FRUIT_VEG, "🧼", level = 1, capacity = 15, currentStock = 0, incomePerMinute = 120, upgradeCost = 2000, isUnlocked = false, requiredStoreLevel = 15),
        StoreSection("sec_pm", "VIP Premium Vault", ProductCategory.BAKERY, "💎", level = 1, capacity = 20, currentStock = 0, incomePerMinute = 200, upgradeCost = 3500, isUnlocked = false, requiredStoreLevel = 20)
    )

    val INITIAL_EMPLOYEES = listOf(
        Employee(EmployeeType.MANAGER, isHired = true, level = 1, hireCost = 0, upgradeCost = 300),
        Employee(EmployeeType.CASHIER, isHired = false, level = 1, hireCost = 250, upgradeCost = 400),
        Employee(EmployeeType.STOCKER, isHired = false, level = 1, hireCost = 450, upgradeCost = 600),
        Employee(EmployeeType.CLEANER, isHired = false, level = 1, hireCost = 350, upgradeCost = 500),
        Employee(EmployeeType.DELIVERY, isHired = false, level = 1, hireCost = 600, upgradeCost = 800)
    )

    val STORY_CHAPTERS = listOf(
        StoryChapter(
            chapterNumber = 1,
            title = "The Little Shop",
            speaker = "Grandfather's Memory",
            speakerAvatar = "👴",
            storyDialogue = "“This store belonged to my grandfather. I won't let his dream disappear. Let's tidy the shelves and get our first customers!”",
            goalDescription = "Earn 500 total coins to renovate the front counter.",
            goalType = ChapterGoalType.EARN_COINS,
            targetValue = 500,
            rewardCoins = 300,
            rewardGems = 10
        ),
        StoryChapter(
            chapterNumber = 2,
            title = "The First Rush",
            speaker = "Manager Sam",
            speakerAvatar = "🧑‍💼",
            storyDialogue = "“Uh-oh! Word has spread around town and customers are flocking in! Keep merging items to satisfy their requests!”",
            goalDescription = "Serve 15 supermarket customers.",
            goalType = ChapterGoalType.SERVE_CUSTOMERS,
            targetValue = 15,
            rewardCoins = 500,
            rewardGems = 15
        ),
        StoryChapter(
            chapterNumber = 3,
            title = "Bakery Dreams",
            speaker = "Baker Pierre",
            speakerAvatar = "👨‍🍳",
            storyDialogue = "“If we bake warm crusty baguettes and deluxe cakes, customers will fall in love with our aroma! Let's craft our first bakery displays!”",
            goalDescription = "Perform 25 item merges on the merge board.",
            goalType = ChapterGoalType.MERGE_ITEMS,
            targetValue = 25,
            rewardCoins = 800,
            rewardGems = 20
        ),
        StoryChapter(
            chapterNumber = 4,
            title = "Rival Supermarket",
            speaker = "Rival Tycoon",
            speakerAvatar = "💼",
            storyDialogue = "“You think your cozy corner market can compete with my mega chain? Show me what you've got!”",
            goalDescription = "Reach 4.8⭐ store reputation and upgrade 3 sections.",
            goalType = ChapterGoalType.UPGRADE_SECTIONS,
            targetValue = 3,
            rewardCoins = 1200,
            rewardGems = 30
        ),
        StoryChapter(
            chapterNumber = 5,
            title = "Supermarket Empire",
            speaker = "City Mayor",
            speakerAvatar = "🏛️",
            storyDialogue = "“Congratulations! Your supermarket has become the pride of our entire city. Let's expand into an all-inclusive retail empire!”",
            goalDescription = "Earn 5,000 coins and reach Store Level 10.",
            goalType = ChapterGoalType.EARN_COINS,
            targetValue = 5000,
            rewardCoins = 2500,
            rewardGems = 50
        )
    )

    val INITIAL_MISSIONS = listOf(
        DailyMission("dm_1", "Merge 10 grocery items", "🧩", 0, 10, rewardCoins = 250, rewardEnergy = 20),
        DailyMission("dm_2", "Serve 8 hungry customers", "🛒", 0, 8, rewardCoins = 350, rewardEnergy = 25),
        DailyMission("dm_3", "Stock 6 items to store shelves", "📦", 0, 6, rewardCoins = 400, rewardEnergy = 30)
    )

    val INITIAL_ACHIEVEMENTS = listOf(
        Achievement("ach_first_sale", "First Sale", "Complete your very first customer order.", "🏆", 0, 1, 5),
        Achievement("ach_master_merger", "Master Merger", "Perform 50 item merges on the board.", "✨", 0, 50, 15),
        Achievement("ach_store_owner", "Store Owner", "Upgrade supermarket departments 5 times.", "🏪", 0, 5, 20),
        Achievement("ach_customer_fav", "Customer Favorite", "Serve 25 satisfied customers.", "❤️", 0, 25, 25),
        Achievement("ach_tycoon", "Supermarket Tycoon", "Accumulate over 3,000 total earned coins.", "👑", 0, 3000, 40)
    )

    fun getStoreRankTitle(level: Int): String = when {
        level < 5 -> "Mini Market"
        level < 10 -> "Neighborhood Store"
        level < 20 -> "City Supermarket"
        level < 30 -> "Mega Market"
        else -> "Supermarket Empire"
    }
}
