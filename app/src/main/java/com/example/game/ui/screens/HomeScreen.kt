package com.example.game.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.game.model.CustomerOrder
import com.example.game.ui.components.CustomerQueueBar
import com.example.game.ui.components.SupermarketFloorView
import com.example.game.viewmodel.GameUiState

@Composable
fun HomeScreen(
    state: GameUiState,
    canServeCustomer: (CustomerOrder) -> Boolean,
    onServeCustomer: (String) -> Unit,
    onUpgradeSection: (String) -> Unit,
    onCollectPassive: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        // Customer Checkout Line at top of store
        CustomerQueueBar(
            customers = state.customers,
            canServeCustomer = canServeCustomer,
            onServeCustomer = onServeCustomer
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Supermarket floor with aisles, shelves and animated characters
        SupermarketFloorView(
            sections = state.sections,
            employees = state.employees,
            passiveBank = state.passiveIncomeBank,
            coins = state.coins,
            storeLevel = state.storeLevel,
            onUpgradeSection = onUpgradeSection,
            onCollectPassive = onCollectPassive
        )
    }
}
