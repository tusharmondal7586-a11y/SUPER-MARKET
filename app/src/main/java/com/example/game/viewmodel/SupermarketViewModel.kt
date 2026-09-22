package com.example.game.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.game.audio.GameAudioEngine
import com.example.game.data.GameRepository
import com.example.game.model.Achievement
import com.example.game.model.ActiveRandomEvent
import com.example.game.model.ChapterGoalType
import com.example.game.model.CustomerOrder
import com.example.game.model.CustomerType
import com.example.game.model.DailyMission
import com.example.game.model.Employee
import com.example.game.model.EmployeeType
import com.example.game.model.GameData
import com.example.game.model.MergeCell
import com.example.game.model.ProductCategory
import com.example.game.model.ProductItem
import com.example.game.model.RandomEventType
import com.example.game.model.StoryChapter
import com.example.game.model.StoreSection
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

data class GameUiState(
    val coins: Long = 350L,
    val gems: Int = 20,
    val energy: Int = 100,
    val maxEnergy: Int = 100,
    val energySecondsLeft: Int = 30,
    val xp: Int = 0,
    val xpForNextLevel: Int = 100,
    val storeLevel: Int = 1,
    val reputation: Float = 4.8f,
    val totalMerges: Int = 0,
    val totalServed: Int = 0,
    val totalCoinsEarned: Long = 0L,
    val boardCells: List<MergeCell> = emptyList(),
    val selectedCellIndex: Int? = null,
    val sections: List<StoreSection> = emptyList(),
    val customers: List<CustomerOrder> = emptyList(),
    val employees: List<Employee> = emptyList(),
    val storyChapters: List<StoryChapter> = emptyList(),
    val currentChapterIndex: Int = 0,
    val dailyMissions: List<DailyMission> = emptyList(),
    val achievements: List<Achievement> = emptyList(),
    val activeEvent: ActiveRandomEvent? = null,
    val activeStoryModal: StoryChapter? = null,
    val discoveredItem: ProductItem? = null,
    val isTutorialActive: Boolean = false,
    val tutorialStep: Int = 0,
    val shoppingRushSecondsLeft: Int = 0,
    val isSoundEnabled: Boolean = true,
    val toastMessage: String? = null,
    val passiveIncomeBank: Int = 0
)

class SupermarketViewModel(
    private val repository: GameRepository,
    private val appContext: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    init {
        loadInitialState()
        startPeriodicLoop()
    }

    private fun loadInitialState() {
        val coins = repository.loadCoins(350L)
        val gems = repository.loadGems(20)
        val energy = repository.loadEnergy(100)
        val xp = repository.loadXp(0)
        val level = repository.loadStoreLevel(1)
        val rep = repository.loadReputation(4.8f)
        val merges = repository.loadTotalMerges()
        val served = repository.loadTotalServed()
        val coinsEarned = repository.loadTotalCoinsEarned()
        val chapterIdx = repository.loadCurrentChapter()
        val board = repository.loadBoard(16)
        val soundOn = repository.isSoundEnabled()
        val tutorialCompleted = repository.isTutorialCompleted()

        GameAudioEngine.setMuted(!soundOn)

        val initialChapters = GameData.STORY_CHAPTERS.mapIndexed { idx, ch ->
            ch.copy(isCompleted = idx < chapterIdx)
        }

        _uiState.update { current ->
            current.copy(
                coins = coins,
                gems = gems,
                energy = energy,
                xp = xp,
                xpForNextLevel = calculateXpForLevel(level),
                storeLevel = level,
                reputation = rep,
                totalMerges = merges,
                totalServed = served,
                totalCoinsEarned = coinsEarned,
                boardCells = board,
                sections = GameData.INITIAL_SECTIONS,
                employees = GameData.INITIAL_EMPLOYEES,
                storyChapters = initialChapters,
                currentChapterIndex = chapterIdx.coerceIn(0, initialChapters.size - 1),
                dailyMissions = GameData.INITIAL_MISSIONS,
                achievements = GameData.INITIAL_ACHIEVEMENTS,
                isTutorialActive = !tutorialCompleted,
                tutorialStep = if (!tutorialCompleted) 0 else 3,
                isSoundEnabled = soundOn,
                activeStoryModal = if (!tutorialCompleted) initialChapters.firstOrNull() else null
            )
        }

        // Spawn first customers
        spawnInitialCustomers()
    }

    private fun calculateXpForLevel(level: Int): Int = level * 120

    private fun startPeriodicLoop() {
        viewModelScope.launch {
            while (true) {
                delay(1000) // 1 second game tick
                onSecondTick()
            }
        }
    }

    private var tickCount = 0
    private fun onSecondTick() {
        tickCount++
        _uiState.update { current ->
            var newEnergy = current.energy
            var newEnergyTimer = current.energySecondsLeft - 1

            if (newEnergyTimer <= 0) {
                newEnergyTimer = 25
                if (newEnergy < current.maxEnergy) {
                    newEnergy += 1
                }
            }

            // Customer patience update
            val updatedCustomers = current.customers.mapNotNull { cust ->
                val remaining = cust.patienceSeconds - 1
                if (remaining <= 0) {
                    // Customer leaves disappointed
                    null
                } else {
                    cust.copy(patienceSeconds = remaining)
                }
            }

            // Rush timer
            val rushSeconds = (current.shoppingRushSecondsLeft - 1).coerceAtLeast(0)

            // Stocker employee auto-restock check (every 30s if Stocker hired)
            var updatedSections = current.sections
            val stocker = current.employees.find { it.type == EmployeeType.STOCKER }
            if (stocker?.isHired == true && tickCount % 30 == 0) {
                updatedSections = updatedSections.map { sec ->
                    if (sec.isUnlocked && sec.currentStock < sec.capacity) {
                        sec.copy(currentStock = sec.currentStock + 1)
                    } else sec
                }
            }

            // Accumulate passive store income (every 10 seconds)
            var newBank = current.passiveIncomeBank
            if (tickCount % 10 == 0) {
                val activeIncome = current.sections.filter { it.isUnlocked }.sumOf { it.incomePerMinute / 6 }
                newBank += activeIncome.coerceAtLeast(1)
            }

            current.copy(
                energy = newEnergy,
                energySecondsLeft = newEnergyTimer,
                customers = updatedCustomers,
                shoppingRushSecondsLeft = rushSeconds,
                sections = updatedSections,
                passiveIncomeBank = newBank
            )
        }

        // Customer replenishment if less than 3
        if (uiState.value.customers.size < 3 && tickCount % 8 == 0) {
            spawnNewCustomer()
        }

        // Random event spawn chance (every 75s)
        if (tickCount % 75 == 0 && uiState.value.activeEvent == null) {
            triggerRandomEvent()
        }
    }

    fun toggleSound() {
        val newSound = !_uiState.value.isSoundEnabled
        _uiState.update { it.copy(isSoundEnabled = newSound) }
        GameAudioEngine.setMuted(!newSound)
        repository.setSoundEnabled(newSound)
    }

    fun dismissStoryModal() {
        _uiState.update { it.copy(activeStoryModal = null) }
        if (_uiState.value.isTutorialActive && _uiState.value.tutorialStep == 0) {
            _uiState.update { it.copy(tutorialStep = 1) }
        }
    }

    fun dismissDiscoveredItem() {
        _uiState.update { it.copy(discoveredItem = null) }
    }

    fun clearToast() {
        _uiState.update { it.copy(toastMessage = null) }
    }

    fun showToast(msg: String) {
        _uiState.update { it.copy(toastMessage = msg) }
    }

    // ==========================================
    // MERGE ACTIONS
    // ==========================================

    fun spawnItem() {
        val state = _uiState.value
        val energyCost = 2
        if (state.energy < energyCost) {
            showToast("⚡ Need 2 energy! Wait to recharge or refill in Shop.")
            return
        }

        val emptyCell = state.boardCells.firstOrNull { it.item == null }
        if (emptyCell == null) {
            showToast("📦 Board is full! Merge existing items or stock them to shelves.")
            return
        }

        // Pick an unlocked category level 1 item
        val unlockedCategories = state.sections.filter { it.isUnlocked }.map { it.category }.distinct()
        val category = if (unlockedCategories.isNotEmpty()) {
            unlockedCategories.random()
        } else {
            ProductCategory.FRUIT_VEG
        }

        val level1Item = GameData.ALL_CHAINS[category]?.firstOrNull() ?: GameData.ITEM_APPLE

        val newBoard = state.boardCells.map { cell ->
            if (cell.index == emptyCell.index) cell.copy(item = level1Item) else cell
        }

        val newEnergy = state.energy - energyCost
        _uiState.update {
            it.copy(
                energy = newEnergy,
                boardCells = newBoard
            )
        }

        repository.saveEnergy(newEnergy)
        repository.saveBoard(newBoard)
        GameAudioEngine.playPop(appContext)

        if (state.isTutorialActive && state.tutorialStep == 1) {
            // Check if user has 2 apples
            val appleCount = newBoard.count { it.item?.id == GameData.ITEM_APPLE.id }
            if (appleCount >= 2) {
                showToast("🍎 Tap one Apple, then tap the other Apple to MERGE!")
            }
        }
    }

    fun onCellClicked(index: Int) {
        val state = _uiState.value
        val clickedCell = state.boardCells.getOrNull(index) ?: return
        val currentSelectedIdx = state.selectedCellIndex

        if (currentSelectedIdx == null) {
            // No selection yet -> select if has item
            if (clickedCell.item != null) {
                _uiState.update { it.copy(selectedCellIndex = index) }
                GameAudioEngine.playPop(appContext)
            }
        } else if (currentSelectedIdx == index) {
            // Deselect
            _uiState.update { it.copy(selectedCellIndex = null) }
        } else {
            // Attempt move or merge from currentSelectedIdx to index
            val fromCell = state.boardCells.getOrNull(currentSelectedIdx)
            if (fromCell?.item == null) {
                _uiState.update { it.copy(selectedCellIndex = null) }
                return
            }

            val fromItem = fromCell.item
            val toItem = clickedCell.item

            if (toItem == null) {
                // Move item to empty slot
                val newBoard = state.boardCells.map { cell ->
                    when (cell.index) {
                        currentSelectedIdx -> cell.copy(item = null)
                        index -> cell.copy(item = fromItem)
                        else -> cell
                    }
                }
                _uiState.update { it.copy(boardCells = newBoard, selectedCellIndex = null) }
                repository.saveBoard(newBoard)
                GameAudioEngine.playPop(appContext)
            } else if (toItem.category == fromItem.category && toItem.level == fromItem.level && toItem.level < 4) {
                // MERGE SUCCESS!
                executeMerge(currentSelectedIdx, index, fromItem)
            } else {
                // Different items: select the clicked cell instead
                _uiState.update { it.copy(selectedCellIndex = index) }
                GameAudioEngine.playPop(appContext)
            }
        }
    }

    private fun executeMerge(fromIndex: Int, toIndex: Int, currentItem: ProductItem) {
        val nextItem = GameData.getNextLevelItem(currentItem) ?: return
        val state = _uiState.value

        val newBoard = state.boardCells.map { cell ->
            when (cell.index) {
                fromIndex -> cell.copy(item = null)
                toIndex -> cell.copy(item = nextItem)
                else -> cell
            }
        }

        val earnedXp = nextItem.xpValue
        val earnedCoins = nextItem.sellPrice / 2
        val newMerges = state.totalMerges + 1

        GameAudioEngine.playMergeSuccess(appContext)

        _uiState.update { current ->
            current.copy(
                boardCells = newBoard,
                selectedCellIndex = null,
                totalMerges = newMerges,
                discoveredItem = nextItem,
                toastMessage = "✨ MERGE! Created ${nextItem.name}!"
            )
        }

        repository.saveBoard(newBoard)
        repository.saveTotalMerges(newMerges)
        addXp(earnedXp)
        addCoins(earnedCoins.toLong())

        // Update missions
        updateMissionProgress("dm_1", 1)
        updateChapterProgress(ChapterGoalType.MERGE_ITEMS, 1)
        updateAchievementProgress("ach_master_merger", 1)

        // Tutorial progress
        if (state.isTutorialActive && state.tutorialStep == 1) {
            _uiState.update { it.copy(tutorialStep = 2) }
            showToast("🎉 Awesome! Now let's serve a customer or stock it to shelves!")
        }
    }

    fun stockSelectedItemToShelf() {
        stockSelectedToShelf()
    }

    fun stockSelectedToShelf() {
        val state = _uiState.value
        val selectedIdx = state.selectedCellIndex ?: return
        val cell = state.boardCells.getOrNull(selectedIdx) ?: return
        val item = cell.item ?: return

        // Find matching section
        val section = state.sections.find { it.category == item.category }
        if (section == null) {
            showToast("Section not unlocked yet!")
            return
        }

        if (section.currentStock >= section.capacity) {
            showToast("⚠️ ${section.name} shelf is at max capacity (${section.capacity})! Upgrade section or serve customers.")
            return
        }

        // Add to stock
        val updatedSections = state.sections.map { sec ->
            if (sec.id == section.id) sec.copy(currentStock = (sec.currentStock + item.stockValue).coerceAtMost(sec.capacity))
            else sec
        }

        // Clear cell
        val newBoard = state.boardCells.map { c ->
            if (c.index == selectedIdx) c.copy(item = null) else c
        }

        val bonusCoins = (item.sellPrice * 0.75).toLong()
        _uiState.update {
            it.copy(
                boardCells = newBoard,
                selectedCellIndex = null,
                sections = updatedSections,
                toastMessage = "📦 Stocked ${item.name} to ${section.name}! (+${bonusCoins} 💵)"
            )
        }

        repository.saveBoard(newBoard)
        addCoins(bonusCoins)
        updateMissionProgress("dm_3", 1)
        GameAudioEngine.playPop(appContext)
    }

    fun sellSelectedItem() {
        val state = _uiState.value
        val selectedIdx = state.selectedCellIndex ?: return
        val cell = state.boardCells.getOrNull(selectedIdx) ?: return
        val item = cell.item ?: return

        val newBoard = state.boardCells.map { c ->
            if (c.index == selectedIdx) c.copy(item = null) else c
        }

        _uiState.update {
            it.copy(
                boardCells = newBoard,
                selectedCellIndex = null,
                toastMessage = "Sold ${item.name} for ${item.sellPrice} 💵"
            )
        }

        repository.saveBoard(newBoard)
        addCoins(item.sellPrice.toLong())
        GameAudioEngine.playChaChing(appContext)
    }

    // ==========================================
    // CUSTOMER SYSTEM
    // ==========================================

    private fun spawnInitialCustomers() {
        val cust1 = createCustomerOrder(CustomerType.BUSY_MOM, listOf(GameData.ITEM_APPLE))
        val cust2 = createCustomerOrder(CustomerType.OFFICE_WORKER, listOf(GameData.ITEM_BREAD))
        _uiState.update { it.copy(customers = listOf(cust1, cust2)) }
    }

    private fun spawnNewCustomer() {
        val types = CustomerType.values().toList()
        val randomType = types.random()
        val possibleItems = GameData.ALL_CHAINS.values.flatten().filter { it.level <= (_uiState.value.storeLevel / 2).coerceIn(1, 4) }
        val reqItems = if (randomType == CustomerType.VIP) {
            listOf(GameData.ITEM_FRUIT_DISPLAY, GameData.ITEM_CAKE).shuffled().take(1)
        } else {
            listOf(possibleItems.random())
        }

        val newCustomer = createCustomerOrder(randomType, reqItems)
        _uiState.update { current ->
            if (current.customers.size < 4) {
                current.copy(customers = current.customers + newCustomer)
            } else current
        }
    }

    private fun createCustomerOrder(type: CustomerType, items: List<ProductItem>): CustomerOrder {
        val baseCoin = items.sumOf { it.sellPrice } * 2
        val isVip = type == CustomerType.VIP
        val gemReward = if (isVip) 5 else 0
        return CustomerOrder(
            id = "cust_${System.currentTimeMillis()}_${Random.nextInt(1000)}",
            customerType = type,
            dialogue = when (type) {
                CustomerType.BUSY_MOM -> "“I need fresh ${items.first().name} for dinner tonight!”"
                CustomerType.OFFICE_WORKER -> "“Quick lunch break! Grab me ${items.first().name}!”"
                CustomerType.GRANDMA -> "“Hello dear, do you have any good ${items.first().name}?”"
                CustomerType.KID -> "“Yum! Can I have ${items.first().name} please?”"
                CustomerType.STUDENT -> "“Late night study snack! ${items.first().name} is perfect!”"
                CustomerType.CHEF -> "“Need fresh ${items.first().name} for my restaurant dishes!”"
                CustomerType.VIP -> "“Only the very best ${items.first().name} will do for my event!”"
            },
            requiredItems = items,
            coinReward = baseCoin + Random.nextInt(10, 30),
            gemReward = gemReward,
            xpReward = 20 * items.size,
            patienceSeconds = if (isVip) 90 else 60,
            maxPatienceSeconds = if (isVip) 90 else 60,
            isVip = isVip
        )
    }

    fun canServeCustomer(order: CustomerOrder): Boolean {
        val state = _uiState.value
        // Check if required items exist either on board or in shelf stock
        return order.requiredItems.all { req ->
            val hasOnBoard = state.boardCells.any { it.item?.id == req.id }
            val section = state.sections.find { it.category == req.category }
            val hasInShelf = (section?.currentStock ?: 0) >= req.stockValue
            hasOnBoard || hasInShelf
        }
    }

    fun serveCustomer(orderId: String) {
        val state = _uiState.value
        val order = state.customers.find { it.id == orderId } ?: return

        var newBoard = state.boardCells.toMutableList()
        var newSections = state.sections.toMutableList()

        for (req in order.requiredItems) {
            val boardIdx = newBoard.indexOfFirst { it.item?.id == req.id }
            if (boardIdx != -1) {
                newBoard[boardIdx] = newBoard[boardIdx].copy(item = null)
            } else {
                val secIdx = newSections.indexOfFirst { it.category == req.category }
                if (secIdx != -1 && newSections[secIdx].currentStock >= req.stockValue) {
                    newSections[secIdx] = newSections[secIdx].copy(
                        currentStock = newSections[secIdx].currentStock - req.stockValue
                    )
                } else {
                    showToast("Missing ${req.name} to complete order!")
                    return
                }
            }
        }

        // Manager perk: +25% tips
        val hasManager = state.employees.any { it.type == EmployeeType.MANAGER && it.isHired }
        val managerBonus = if (hasManager) 1.25f else 1.0f

        // Shopping rush multiplier
        val rushBonus = if (state.shoppingRushSecondsLeft > 0) 2.0f else 1.0f

        val finalCoins = (order.coinReward * managerBonus * rushBonus).toLong()
        val finalGems = order.gemReward
        val newServed = state.totalServed + 1

        GameAudioEngine.playChaChing(appContext)
        GameAudioEngine.playHappyCustomer(appContext)

        _uiState.update { current ->
            current.copy(
                boardCells = newBoard,
                sections = newSections,
                customers = current.customers.filter { it.id != orderId },
                totalServed = newServed,
                toastMessage = "💰 SALE COMPLETE! +$finalCoins 💵 Customer happy!"
            )
        }

        repository.saveBoard(newBoard)
        repository.saveTotalServed(newServed)
        addCoins(finalCoins)
        if (finalGems > 0) addGems(finalGems)
        addXp(order.xpReward)

        updateMissionProgress("dm_2", 1)
        updateChapterProgress(ChapterGoalType.SERVE_CUSTOMERS, 1)
        updateAchievementProgress("ach_first_sale", 1)
        updateAchievementProgress("ach_customer_fav", 1)

        if (state.isTutorialActive && state.tutorialStep == 2) {
            _uiState.update { it.copy(tutorialStep = 3, isTutorialActive = false) }
            repository.setTutorialCompleted(true)
            showToast("🎉 Tutorial completed! Build your supermarket empire!")
        }
    }

    // ==========================================
    // STORE MANAGEMENT & UPGRADES
    // ==========================================

    fun upgradeSection(sectionId: String) {
        val state = _uiState.value
        val section = state.sections.find { it.id == sectionId } ?: return

        if (!section.isUnlocked) {
            if (state.storeLevel < section.requiredStoreLevel) {
                showToast("Requires Store Level ${section.requiredStoreLevel} to unlock!")
                return
            }
            if (state.coins < section.upgradeCost) {
                showToast("Need ${section.upgradeCost} 💵 to unlock ${section.name}!")
                return
            }

            addCoins(-section.upgradeCost.toLong())
            val updated = state.sections.map { sec ->
                if (sec.id == sectionId) sec.copy(isUnlocked = true) else sec
            }
            _uiState.update { it.copy(sections = updated, toastMessage = "🎉 Unlocked ${section.name} department!") }
            GameAudioEngine.playLevelUp(appContext)
            updateChapterProgress(ChapterGoalType.UPGRADE_SECTIONS, 1)
            updateAchievementProgress("ach_store_owner", 1)
            return
        }

        if (state.coins < section.upgradeCost) {
            showToast("Need ${section.upgradeCost} 💵 to upgrade ${section.name}!")
            return
        }

        addCoins(-section.upgradeCost.toLong())
        val newLevel = section.level + 1
        val newCapacity = section.capacity + 10
        val newIncome = section.incomePerMinute + 25
        val nextCost = (section.upgradeCost * 1.5).toInt()

        val updated = state.sections.map { sec ->
            if (sec.id == sectionId) {
                sec.copy(
                    level = newLevel,
                    capacity = newCapacity,
                    incomePerMinute = newIncome,
                    upgradeCost = nextCost
                )
            } else sec
        }

        _uiState.update {
            it.copy(
                sections = updated,
                toastMessage = "⭐ Upgraded ${section.name} to Level $newLevel! Capacity: $newCapacity"
            )
        }
        GameAudioEngine.playLevelUp(appContext)
        updateChapterProgress(ChapterGoalType.UPGRADE_SECTIONS, 1)
        updateAchievementProgress("ach_store_owner", 1)
    }

    fun collectPassiveIncome() {
        val bank = _uiState.value.passiveIncomeBank
        if (bank <= 0) {
            showToast("No passive revenue ready yet! Sections generate income over time.")
            return
        }
        addCoins(bank.toLong())
        _uiState.update { it.copy(passiveIncomeBank = 0, toastMessage = "Collected $bank 💵 from supermarket shelves!") }
        GameAudioEngine.playChaChing(appContext)
    }

    // ==========================================
    // EMPLOYEES / STAFF
    // ==========================================

    fun hireOrUpgradeEmployee(type: EmployeeType) {
        val state = _uiState.value
        val emp = state.employees.find { it.type == type } ?: return

        val cost = if (!emp.isHired) emp.hireCost else emp.upgradeCost
        if (state.coins < cost) {
            showToast("Need $cost 💵 to ${if (!emp.isHired) "hire" else "upgrade"} ${emp.type.roleTitle}!")
            return
        }

        addCoins(-cost.toLong())
        val updated = state.employees.map { e ->
            if (e.type == type) {
                if (!e.isHired) {
                    e.copy(isHired = true, upgradeCost = (e.hireCost * 1.5).toInt())
                } else {
                    e.copy(level = e.level + 1, upgradeCost = (e.upgradeCost * 1.4).toInt())
                }
            } else e
        }

        _uiState.update {
            it.copy(
                employees = updated,
                toastMessage = "${if (!emp.isHired) "Hired" else "Promoted"} ${type.roleTitle}!"
            )
        }
        GameAudioEngine.playLevelUp(appContext)
    }

    // ==========================================
    // RANDOM EVENTS
    // ==========================================

    private fun triggerRandomEvent() {
        val types = RandomEventType.values()
        val picked = types.random()
        _uiState.update { it.copy(activeEvent = ActiveRandomEvent(picked)) }
    }

    fun handleRandomEventAction() {
        val active = _uiState.value.activeEvent ?: return
        when (active.type) {
            RandomEventType.DELIVERY_ARRIVED -> {
                addGems(5)
                addEnergy(30)
                showToast("🚚 Opened delivery crate! +5 💎 Gems, +30 ⚡ Energy!")
            }
            RandomEventType.SHOPPING_RUSH -> {
                _uiState.update { it.copy(shoppingRushSecondsLeft = 60) }
                showToast("🛒 Shopping Rush started! 2x customer tips for 60 seconds!")
            }
            RandomEventType.POWER_OUTAGE -> {
                addCoins(350L)
                showToast("💡 Generator restored! Refrigerators safe! (+350 💵)")
            }
            RandomEventType.VIP_CUSTOMER -> {
                spawnNewCustomer()
                showToast("💎 Welcomed VIP Customer to the supermarket aisle!")
            }
            RandomEventType.LOST_PACKAGE -> {
                addCoins(250L)
                addGems(2)
                showToast("📦 Returned lost bag to customer! Received +250 💵 & +2 💎!")
            }
        }
        GameAudioEngine.playChaChing(appContext)
        _uiState.update { it.copy(activeEvent = null) }
    }

    fun dismissRandomEvent() {
        _uiState.update { it.copy(activeEvent = null) }
    }

    // ==========================================
    // STORY & MISSIONS
    // ==========================================

    fun claimChapterReward(chapterIdx: Int) {
        val chapter = _uiState.value.storyChapters.getOrNull(chapterIdx) ?: return
        if (chapter.isCompleted) return

        addCoins(chapter.rewardCoins.toLong())
        addGems(chapter.rewardGems)

        val updatedChapters = _uiState.value.storyChapters.mapIndexed { idx, ch ->
            if (idx == chapterIdx) ch.copy(isCompleted = true) else ch
        }

        val nextIdx = (chapterIdx + 1).coerceAtMost(updatedChapters.size - 1)
        _uiState.update {
            it.copy(
                storyChapters = updatedChapters,
                currentChapterIndex = nextIdx,
                activeStoryModal = updatedChapters.getOrNull(nextIdx),
                toastMessage = "🏆 Completed Chapter: ${chapter.title}!"
            )
        }
        repository.saveCurrentChapter(nextIdx)
        GameAudioEngine.playLevelUp(appContext)
    }

    fun claimDailyMission(missionId: String) {
        val mission = _uiState.value.dailyMissions.find { it.id == missionId } ?: return
        if (mission.isClaimed || mission.currentProgress < mission.targetProgress) return

        addCoins(mission.rewardCoins.toLong())
        addEnergy(mission.rewardEnergy)

        val updated = _uiState.value.dailyMissions.map { m ->
            if (m.id == missionId) m.copy(isClaimed = true) else m
        }
        _uiState.update { it.copy(dailyMissions = updated, toastMessage = "🎁 Claimed Daily Mission: +${mission.rewardCoins} 💵, +${mission.rewardEnergy} ⚡") }
        GameAudioEngine.playChaChing(appContext)
    }

    private fun updateMissionProgress(missionId: String, delta: Int) {
        val updated = _uiState.value.dailyMissions.map { m ->
            if (m.id == missionId && !m.isClaimed) {
                m.copy(currentProgress = (m.currentProgress + delta).coerceAtMost(m.targetProgress))
            } else m
        }
        _uiState.update { it.copy(dailyMissions = updated) }
    }

    private fun updateChapterProgress(goalType: ChapterGoalType, delta: Int) {
        val currentIdx = _uiState.value.currentChapterIndex
        val chapter = _uiState.value.storyChapters.getOrNull(currentIdx) ?: return
        if (chapter.isCompleted || chapter.goalType != goalType) return

        // Check if goal reached
        val isMet = when (goalType) {
            ChapterGoalType.EARN_COINS -> _uiState.value.totalCoinsEarned >= chapter.targetValue
            ChapterGoalType.SERVE_CUSTOMERS -> _uiState.value.totalServed >= chapter.targetValue
            ChapterGoalType.MERGE_ITEMS -> _uiState.value.totalMerges >= chapter.targetValue
            ChapterGoalType.UPGRADE_SECTIONS -> _uiState.value.sections.count { it.level > 1 } >= chapter.targetValue
            ChapterGoalType.REACH_REPUTATION -> _uiState.value.reputation >= 4.8f
        }

        if (isMet) {
            showToast("🎯 Chapter Goal Completed: ${chapter.title}! Claim your reward in Missions.")
        }
    }

    private fun updateAchievementProgress(achId: String, delta: Int) {
        val updated = _uiState.value.achievements.map { ach ->
            if (ach.id == achId && !ach.isUnlocked) {
                val newProg = ach.currentProgress + delta
                if (newProg >= ach.targetProgress) {
                    addGems(ach.rewardGems)
                    showToast("🏆 Achievement Unlocked: ${ach.title}! (+${ach.rewardGems} 💎)")
                    ach.copy(currentProgress = newProg, isUnlocked = true)
                } else {
                    ach.copy(currentProgress = newProg)
                }
            } else ach
        }
        _uiState.update { it.copy(achievements = updated) }
    }

    // ==========================================
    // REWARDS & MONETIZATION / SHOP
    // ==========================================

    fun watchRewardedAdSimulator() {
        // Rewarded Ad perk: +50 energy and 1,000 coins
        addEnergy(50)
        addCoins(1000L)
        showToast("📺 Sponsored Bonus Claimed! +50 ⚡ Energy & +1,000 💵 Coins!")
        GameAudioEngine.playChaChing(appContext)
    }

    fun buyStarterPack() {
        val state = _uiState.value
        addCoins(5000L)
        addGems(100)
        addEnergy(100)
        showToast("🌟 Starter Pack Claimed! +5,000 💵 Coins, +100 💎 Gems, +100 ⚡ Energy!")
        GameAudioEngine.playLevelUp(appContext)
    }

    fun exchangeGemsForCoins(gemCost: Int, coinsGain: Long) {
        val state = _uiState.value
        if (state.gems < gemCost) {
            showToast("Need $gemCost 💎 gems!")
            return
        }
        addGems(-gemCost)
        addCoins(coinsGain)
        showToast("Exchanged $gemCost 💎 for $coinsGain 💵!")
        GameAudioEngine.playChaChing(appContext)
    }

    fun refillEnergyWithGems(gemCost: Int = 10) {
        val state = _uiState.value
        if (state.gems < gemCost) {
            showToast("Need $gemCost 💎 gems!")
            return
        }
        addGems(-gemCost)
        _uiState.update { it.copy(energy = it.maxEnergy) }
        repository.saveEnergy(_uiState.value.maxEnergy)
        showToast("⚡ Energy fully recharged!")
        GameAudioEngine.playPop(appContext)
    }

    // ==========================================
    // CURRENCY HELPERS
    // ==========================================

    private fun addCoins(amount: Long) {
        val newCoins = (_uiState.value.coins + amount).coerceAtLeast(0L)
        val newTotal = if (amount > 0) _uiState.value.totalCoinsEarned + amount else _uiState.value.totalCoinsEarned
        _uiState.update { it.copy(coins = newCoins, totalCoinsEarned = newTotal) }
        repository.saveCoins(newCoins)
        repository.saveTotalCoinsEarned(newTotal)
        updateChapterProgress(ChapterGoalType.EARN_COINS, 0)
        updateAchievementProgress("ach_tycoon", amount.toInt().coerceAtLeast(0))
    }

    private fun addGems(amount: Int) {
        val newGems = (_uiState.value.gems + amount).coerceAtLeast(0)
        _uiState.update { it.copy(gems = newGems) }
        repository.saveGems(newGems)
    }

    private fun addEnergy(amount: Int) {
        val newEnergy = (_uiState.value.energy + amount).coerceAtMost(_uiState.value.maxEnergy)
        _uiState.update { it.copy(energy = newEnergy) }
        repository.saveEnergy(newEnergy)
    }

    private fun addXp(amount: Int) {
        val state = _uiState.value
        var newXp = state.xp + amount
        var newLevel = state.storeLevel
        var required = state.xpForNextLevel

        while (newXp >= required) {
            newXp -= required
            newLevel++
            required = calculateXpForLevel(newLevel)
            showToast("🎉 STORE EXPANDED! Now Level $newLevel: ${GameData.getStoreRankTitle(newLevel)}!")
            GameAudioEngine.playLevelUp(appContext)
        }

        _uiState.update {
            it.copy(
                xp = newXp,
                storeLevel = newLevel,
                xpForNextLevel = required
            )
        }
        repository.saveXp(newXp)
        repository.saveStoreLevel(newLevel)
    }
}
