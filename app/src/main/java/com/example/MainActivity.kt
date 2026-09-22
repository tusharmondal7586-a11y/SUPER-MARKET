package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Diversity3
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.game.data.GameRepository
import com.example.game.ui.components.NewItemDiscoveredDialog
import com.example.game.ui.components.RandomEventDialog
import com.example.game.ui.components.StoryDialogSheet
import com.example.game.ui.components.TopBarCurrency
import com.example.game.ui.screens.HomeScreen
import com.example.game.ui.screens.MergeScreen
import com.example.game.ui.screens.MissionsScreen
import com.example.game.ui.screens.ShopScreen
import com.example.game.ui.screens.StaffScreen
import com.example.game.viewmodel.SupermarketViewModel
import com.example.ui.theme.MarketOrangePrimary
import com.example.ui.theme.MarketTealSecondary
import com.example.ui.theme.MyApplicationTheme

enum class GameNavigationTab(val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector, val testTag: String) {
    STORE("Store", Icons.Default.Store, "nav_tab_store"),
    MERGE("Merge", Icons.Default.Extension, "nav_tab_merge"),
    STAFF("Staff", Icons.Default.Diversity3, "nav_tab_staff"),
    MISSIONS("Missions", Icons.Default.Flag, "nav_tab_missions"),
    SHOP("Shop", Icons.Default.ShoppingBag, "nav_tab_shop")
}

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: SupermarketViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val repository = GameRepository(applicationContext)
        viewModel = SupermarketViewModel(repository, applicationContext)

        setContent {
            MyApplicationTheme {
                SupermarketStoriesApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun SupermarketStoriesApp(viewModel: SupermarketViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var currentTab by remember { mutableStateOf(GameNavigationTab.MERGE) }
    val snackbarHostState = remember { SnackbarHostState() }

    // Display snackbar toast messages
    LaunchedEffect(state.toastMessage) {
        state.toastMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearToast()
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
        topBar = {
            TopBarCurrency(
                state = state,
                onToggleSound = { viewModel.toggleSound() },
                onEnergyClick = { currentTab = GameNavigationTab.SHOP }
            )
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .testTag("main_navigation_bar"),
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                GameNavigationTab.values().forEach { tab ->
                    NavigationBarItem(
                        selected = currentTab == tab,
                        onClick = { currentTab = tab },
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.title
                            )
                        },
                        label = {
                            Text(
                                text = tab.title,
                                fontWeight = if (currentTab == tab) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 11.sp
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MarketOrangePrimary,
                            selectedTextColor = MarketOrangePrimary,
                            indicatorColor = MarketOrangePrimary.copy(alpha = 0.15f),
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        ),
                        modifier = Modifier.testTag(tab.testTag)
                    )
                }
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                GameNavigationTab.STORE -> {
                    HomeScreen(
                        state = state,
                        canServeCustomer = { viewModel.canServeCustomer(it) },
                        onServeCustomer = { viewModel.serveCustomer(it) },
                        onUpgradeSection = { viewModel.upgradeSection(it) },
                        onCollectPassive = { viewModel.collectPassiveIncome() }
                    )
                }
                GameNavigationTab.MERGE -> {
                    MergeScreen(
                        state = state,
                        canServeCustomer = { viewModel.canServeCustomer(it) },
                        onServeCustomer = { viewModel.serveCustomer(it) },
                        onCellClick = { viewModel.onCellClicked(it) },
                        onSpawnItem = { viewModel.spawnItem() },
                        onStockToShelf = { viewModel.stockSelectedToShelf() },
                        onSellItem = { viewModel.sellSelectedItem() }
                    )
                }
                GameNavigationTab.STAFF -> {
                    StaffScreen(
                        employees = state.employees,
                        coins = state.coins,
                        onHireOrUpgrade = { viewModel.hireOrUpgradeEmployee(it) }
                    )
                }
                GameNavigationTab.MISSIONS -> {
                    MissionsScreen(
                        chapters = state.storyChapters,
                        currentChapterIdx = state.currentChapterIndex,
                        dailyMissions = state.dailyMissions,
                        achievements = state.achievements,
                        onClaimChapter = { viewModel.claimChapterReward(it) },
                        onClaimMission = { viewModel.claimDailyMission(it) }
                    )
                }
                GameNavigationTab.SHOP -> {
                    ShopScreen(
                        userGems = state.gems,
                        onWatchAd = { viewModel.watchRewardedAdSimulator() },
                        onBuyStarterPack = { viewModel.buyStarterPack() },
                        onRefillEnergy = { viewModel.refillEnergyWithGems() },
                        onExchangeGems = { gems, coins -> viewModel.exchangeGemsForCoins(gems, coins) }
                    )
                }
            }

            // Story Mode Dialogue Modal
            state.activeStoryModal?.let { chapter ->
                StoryDialogSheet(
                    chapter = chapter,
                    onDismiss = { viewModel.dismissStoryModal() }
                )
            }

            // New Product Discovered Celebration Modal
            state.discoveredItem?.let { item ->
                NewItemDiscoveredDialog(
                    item = item,
                    onDismiss = { viewModel.dismissDiscoveredItem() }
                )
            }

            // Random Event Alert Modal
            state.activeEvent?.let { event ->
                RandomEventDialog(
                    event = event,
                    onAction = { viewModel.handleRandomEventAction() },
                    onDismiss = { viewModel.dismissRandomEvent() }
                )
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Supermarket Stories: $name", modifier = modifier)
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyApplicationTheme { Greeting("Manager") }
}
