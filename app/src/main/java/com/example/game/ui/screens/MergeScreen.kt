package com.example.game.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.game.model.CustomerOrder
import com.example.game.ui.components.CustomerQueueBar
import com.example.game.ui.components.MergeBoardGrid
import com.example.game.viewmodel.GameUiState

@Composable
fun MergeScreen(
    state: GameUiState,
    canServeCustomer: (CustomerOrder) -> Boolean,
    onServeCustomer: (String) -> Unit,
    onCellClick: (Int) -> Unit,
    onSpawnItem: () -> Unit,
    onStockToShelf: () -> Unit,
    onSellItem: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        // Customer Quick Queue at top of merge board for rapid serving
        CustomerQueueBar(
            customers = state.customers,
            canServeCustomer = canServeCustomer,
            onServeCustomer = onServeCustomer
        )

        // 4x4 Interactive Merge Board
        MergeBoardGrid(
            cells = state.boardCells,
            selectedCellIndex = state.selectedCellIndex,
            energy = state.energy,
            onCellClick = onCellClick,
            onSpawnItem = onSpawnItem,
            onStockToShelf = onStockToShelf,
            onSellItem = onSellItem
        )
    }
}
