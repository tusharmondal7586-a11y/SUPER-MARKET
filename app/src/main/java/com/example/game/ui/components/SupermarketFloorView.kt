package com.example.game.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.model.Employee
import com.example.game.model.EmployeeType
import com.example.game.model.StoreSection
import com.example.ui.theme.MarketGold
import com.example.ui.theme.MarketGreen
import com.example.ui.theme.MarketOrangeLight
import com.example.ui.theme.MarketOrangePrimary
import com.example.ui.theme.MarketTealLight
import com.example.ui.theme.MarketTealSecondary
import com.example.ui.theme.WarmBorder

@Composable
fun SupermarketFloorView(
    sections: List<StoreSection>,
    employees: List<Employee>,
    passiveBank: Int,
    coins: Long,
    storeLevel: Int,
    onUpgradeSection: (String) -> Unit,
    onCollectPassive: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {

        // Floor Atmosphere & Active Staff Simulation Bar
        FloorAtmosphereBar(employees = employees)

        // Passive Income Collection Callout
        if (passiveBank > 0) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .clickable { onCollectPassive() }
                    .testTag("button_collect_passive"),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFE8F5E9)
                ),
                shape = RoundedCornerShape(14.dp),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(MarketGreen))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Payments,
                            contentDescription = "Income",
                            tint = MarketGreen,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Shelf Register Earnings Ready!",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Color(0xFF1B5E20)
                            )
                            Text(
                                text = "+$passiveBank 💵 accumulated by your aisles",
                                fontSize = 11.sp,
                                color = Color(0xFF2E7D32)
                            )
                        }
                    }

                    Button(
                        onClick = onCollectPassive,
                        colors = ButtonDefaults.buttonColors(containerColor = MarketGreen),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Text(text = "COLLECT", fontWeight = FontWeight.ExtraBold, fontSize = 11.sp)
                    }
                }
            }
        }

        // Section Aisle Shelves List
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Supermarket Departments & Shelves",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Upgrade for more stock & revenue",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = false),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(sections, key = { it.id }) { section ->
                SectionShelfCard(
                    section = section,
                    userCoins = coins,
                    userLevel = storeLevel,
                    onUpgrade = { onUpgradeSection(section.id) }
                )
            }
        }
    }
}

@Composable
fun FloorAtmosphereBar(employees: List<Employee>) {
    val transition = rememberInfiniteTransition(label = "staff_walk")
    val walkOffset by transition.animateFloat(
        initialValue = -12f,
        targetValue = 12f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "walk"
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFFFFFDE7),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(WarmBorder))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: Checkout register & cashier
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MarketTealSecondary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🖥️", fontSize = 20.sp)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(text = "Lane 1: Checkout", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    val cashier = employees.find { it.type == EmployeeType.CASHIER }
                    Text(
                        text = if (cashier?.isHired == true) "👩‍💻 Cashier Active (+30% Speed)" else "Needs cashier",
                        fontSize = 10.sp,
                        color = if (cashier?.isHired == true) MarketGreen else Color.Gray
                    )
                }
            }

            // Right: Animated Aisle Browsers & Stocker
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.offset(x = walkOffset.dp)
            ) {
                Text(text = "🛒 🧒", fontSize = 14.sp)
                Spacer(modifier = Modifier.width(6.dp))
                val stocker = employees.find { it.type == EmployeeType.STOCKER }
                if (stocker?.isHired == true) {
                    Text(text = "📦 🧑", fontSize = 14.sp)
                } else {
                    Text(text = "👵 🛍️", fontSize = 14.sp)
                }
            }
        }
    }
}

@Composable
fun SectionShelfCard(
    section: StoreSection,
    userCoins: Long,
    userLevel: Int,
    onUpgrade: () -> Unit
) {
    val isLocked = !section.isUnlocked
    val canAfford = userCoins >= section.upgradeCost
    val canUnlock = userLevel >= section.requiredStoreLevel

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("section_card_${section.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isLocked) Color(0xFFF5F5F5) else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isLocked) 1.dp else 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Section Icon + Name + Level
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isLocked) Color.LightGray else section.category.accentColor.copy(alpha = 0.2f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = section.iconEmoji, fontSize = 22.sp)
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = section.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = if (isLocked) Color.Gray else MaterialTheme.colorScheme.onSurface
                            )
                            if (!isLocked) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(MarketOrangePrimary)
                                        .padding(horizontal = 5.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = "Lv.${section.level}",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 9.sp
                                    )
                                }
                            }
                        }

                        if (!isLocked) {
                            Text(
                                text = "Revenue: $${section.incomePerMinute}/min",
                                fontSize = 11.sp,
                                color = MarketGreen,
                                fontWeight = FontWeight.Medium
                            )
                        } else {
                            Text(
                                text = "Unlocks at Store Level ${section.requiredStoreLevel}",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }

                // Action Button: Upgrade or Unlock
                if (isLocked) {
                    Button(
                        onClick = onUpgrade,
                        enabled = canUnlock && canAfford,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MarketTealSecondary,
                            disabledContainerColor = Color(0xFFE0E0E0)
                        ),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Unlock",
                            tint = Color.White,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "${section.upgradeCost} 💵",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    Button(
                        onClick = onUpgrade,
                        enabled = canAfford,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MarketOrangePrimary,
                            disabledContainerColor = Color(0xFFE0E0E0)
                        ),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowUpward,
                            contentDescription = "Upgrade",
                            tint = Color.White,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "${section.upgradeCost} 💵",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            if (!isLocked) {
                Spacer(modifier = Modifier.height(10.dp))

                // Stock capacity progress
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Shelf Inventory:",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${section.currentStock} / ${section.capacity} items",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (section.currentStock >= section.capacity) MarketOrangePrimary else MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                val stockFraction = (section.currentStock.toFloat() / section.capacity).coerceIn(0f, 1f)
                LinearProgressIndicator(
                    progress = { stockFraction },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = if (stockFraction >= 1f) MarketOrangePrimary else section.category.accentColor,
                    trackColor = Color(0xFFEEEEEE),
                )
            }
        }
    }
}
