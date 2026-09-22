package com.example.game.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.model.GameData
import com.example.game.viewmodel.GameUiState
import com.example.ui.theme.MarketGold
import com.example.ui.theme.MarketGreen
import com.example.ui.theme.MarketOrangePrimary
import com.example.ui.theme.MarketRed
import com.example.ui.theme.MarketTealSecondary

@Composable
fun TopBarCurrency(
    state: GameUiState,
    onToggleSound: () -> Unit,
    onEnergyClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            // Row 1: Currencies + Sound Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Coins Pill
                CurrencyBadge(
                    emoji = "💵",
                    value = formatNumber(state.coins),
                    bgColor = Color(0xFFFFF8E1),
                    textColor = Color(0xFFE65100),
                    testTag = "currency_coins"
                )

                // Gems Pill
                CurrencyBadge(
                    emoji = "💎",
                    value = state.gems.toString(),
                    bgColor = Color(0xFFE1F5FE),
                    textColor = Color(0xFF0277BD),
                    testTag = "currency_gems"
                )

                // Energy Pill (Interactive to refill)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFEDE7F6))
                        .clickable { onEnergyClick() }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .testTag("currency_energy")
                ) {
                    Icon(
                        imageVector = Icons.Default.FlashOn,
                        contentDescription = "Energy",
                        tint = Color(0xFF7E57C2),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${state.energy}/${state.maxEnergy}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = Color(0xFF4527A0)
                    )
                    if (state.energy < state.maxEnergy) {
                        Text(
                            text = " (${state.energySecondsLeft}s)",
                            fontSize = 10.sp,
                            color = Color(0xFF7E57C2)
                        )
                    }
                }

                // Sound Toggle
                IconButton(
                    onClick = onToggleSound,
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("button_toggle_sound")
                ) {
                    Icon(
                        imageVector = if (state.isSoundEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                        contentDescription = "Sound Settings",
                        tint = if (state.isSoundEnabled) MarketOrangePrimary else Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Row 2: Store Level, Rank Title & Reputation
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Level & Rank badge
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(MarketOrangePrimary)
                            .padding(horizontal = 7.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "Lv.${state.storeLevel}",
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text(
                            text = GameData.getStoreRankTitle(state.storeLevel),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        // Mini XP bar
                        LinearProgressIndicator(
                            progress = { (state.xp.toFloat() / state.xpForNextLevel).coerceIn(0f, 1f) },
                            modifier = Modifier
                                .width(90.dp)
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp)),
                            color = MarketTealSecondary,
                            trackColor = Color(0xFFE0E0E0),
                        )
                    }
                }

                // Reputation Stars
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFFFF3E0))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Reputation",
                        tint = MarketGold,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "${String.format("%.1f", state.reputation)} ★",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 11.sp,
                        color = Color(0xFFBF360C)
                    )
                }
            }

            // Optional Shopping Rush Active banner
            AnimatedVisibility(visible = state.shoppingRushSecondsLeft > 0) {
                val transition = rememberInfiniteTransition(label = "rush")
                val pulseScale by transition.animateFloat(
                    initialValue = 0.98f,
                    targetValue = 1.02f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(400),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "rush_scale"
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp)
                        .scale(pulseScale)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(MarketRed, MarketOrangePrimary)
                            )
                        )
                        .padding(vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🛒 SHOPPING RUSH ACTIVE! 2X TIPS (${state.shoppingRushSecondsLeft}s left)",
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun CurrencyBadge(
    emoji: String,
    value: String,
    bgColor: Color,
    textColor: Color,
    testTag: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .testTag(testTag)
    ) {
        Text(text = emoji, fontSize = 13.sp)
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = value,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 12.sp,
            color = textColor
        )
    }
}

private fun formatNumber(num: Long): String {
    return if (num >= 1_000_000) {
        String.format("%.1fM", num / 1_000_000.0)
    } else if (num >= 10_000) {
        String.format("%.1fK", num / 1_000.0)
    } else {
        num.toString()
    }
}
