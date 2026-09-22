package com.example.game.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.model.MergeCell
import com.example.game.model.ProductItem
import com.example.ui.theme.MarketGold
import com.example.ui.theme.MarketGreen
import com.example.ui.theme.MarketOrangePrimary
import com.example.ui.theme.MarketTealSecondary
import com.example.ui.theme.WarmBorder

@Composable
fun MergeBoardGrid(
    cells: List<MergeCell>,
    selectedCellIndex: Int?,
    energy: Int,
    onCellClick: (Int) -> Unit,
    onSpawnItem: () -> Unit,
    onStockToShelf: () -> Unit,
    onSellItem: () -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedCell = selectedCellIndex?.let { idx -> cells.getOrNull(idx) }
    val selectedItem = selectedCell?.item

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {

        // Order Supply Crate Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Interactive Merge Board",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Merge 2 identical items to upgrade!",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Spawn Generator Button
            Button(
                onClick = onSpawnItem,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MarketOrangePrimary
                ),
                shape = RoundedCornerShape(14.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 3.dp),
                modifier = Modifier
                    .height(44.dp)
                    .testTag("button_spawn_crate")
            ) {
                Icon(
                    imageVector = Icons.Default.Inventory2,
                    contentDescription = "Order Crate",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "ORDER CRATE",
                    fontWeight = FontWeight.Black,
                    fontSize = 12.sp,
                    color = Color.White
                )
                Spacer(modifier = Modifier.width(4.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFE65100))
                        .padding(horizontal = 5.dp, vertical = 2.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.FlashOn,
                            contentDescription = "Cost",
                            tint = MarketGold,
                            modifier = Modifier.size(11.dp)
                        )
                        Text(
                            text = "2",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 4x4 Grid Board Container
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("merge_board_grid"),
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFFF7F3E9),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(WarmBorder)),
            shadowElevation = 2.dp
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                userScrollEnabled = false
            ) {
                items(cells, key = { it.index }) { cell ->
                    val isSelected = cell.index == selectedCellIndex
                    val isMatchCandidate = selectedItem != null &&
                            cell.item != null &&
                            cell.index != selectedCellIndex &&
                            cell.item.category == selectedItem.category &&
                            cell.item.level == selectedItem.level &&
                            cell.item.level < 4

                    MergeCellTile(
                        cell = cell,
                        isSelected = isSelected,
                        isMatchCandidate = isMatchCandidate,
                        onClick = { onCellClick(cell.index) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Selected Item Action Drawer
        AnimatedVisibility(visible = selectedItem != null) {
            if (selectedItem != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("selected_item_card"),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(selectedItem.category.accentColor)
                    )
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = selectedItem.iconEmoji, fontSize = 28.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = selectedItem.name,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(selectedItem.category.accentColor.copy(alpha = 0.2f))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = "Tier ${selectedItem.level}",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = selectedItem.category.accentColor
                                            )
                                        }
                                    }
                                    Text(
                                        text = selectedItem.description,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            IconButton(
                                onClick = { onCellClick(selectedCellIndex ?: 0) },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close",
                                    tint = Color.Gray,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Actions Row: Stock to Shelf & Quick Sell
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = onStockToShelf,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(38.dp)
                                    .testTag("button_stock_selected"),
                                colors = ButtonDefaults.buttonColors(containerColor = MarketTealSecondary),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Archive,
                                    contentDescription = "Stock",
                                    tint = Color.White,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Stock Shelf (+${(selectedItem.sellPrice * 0.75).toInt()}💵)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            OutlinedButton(
                                onClick = onSellItem,
                                modifier = Modifier
                                    .height(38.dp)
                                    .testTag("button_sell_selected"),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AttachMoney,
                                    contentDescription = "Sell",
                                    tint = Color(0xFFE65100),
                                    modifier = Modifier.size(15.dp)
                                )
                                Text(
                                    text = "Sell ${selectedItem.sellPrice}💵",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFE65100)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MergeCellTile(
    cell: MergeCell,
    isSelected: Boolean,
    isMatchCandidate: Boolean,
    onClick: () -> Unit
) {
    val item = cell.item

    val transition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by transition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "match_pulse"
    )

    val scaleModifier = if (isMatchCandidate || isSelected) {
        Modifier.scale(pulseScale)
    } else {
        Modifier
    }

    val borderColor = when {
        isSelected -> MarketOrangePrimary
        isMatchCandidate -> MarketGreen
        else -> Color.Transparent
    }

    val borderWidth = if (isSelected || isMatchCandidate) 2.5.dp else 0.dp

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .then(scaleModifier)
            .clip(RoundedCornerShape(14.dp))
            .background(
                if (item != null) Color.White else Color(0xFFEFECE5)
            )
            .border(borderWidth, borderColor, RoundedCornerShape(14.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true)
            ) { onClick() }
            .testTag("merge_tile_${cell.index}"),
        contentAlignment = Alignment.Center
    ) {
        if (item != null) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(4.dp)
            ) {
                // Tier indicator dots / stars
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 2.dp)
                ) {
                    repeat(item.level) {
                        Text(text = "★", fontSize = 8.sp, color = MarketGold)
                    }
                }

                Text(
                    text = item.iconEmoji,
                    fontSize = 28.sp,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = item.name,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Match highlight badge
            if (isMatchCandidate) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .clip(CircleShape)
                        .background(MarketGreen)
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                ) {
                    Text(text = "MERGE!", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Black)
                }
            }
        } else {
            // Empty slot subtle marker
            Text(text = "·", color = Color.LightGray, fontSize = 18.sp, fontWeight = FontWeight.Black)
        }
    }
}
