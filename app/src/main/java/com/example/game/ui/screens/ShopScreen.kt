package com.example.game.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.OndemandVideo
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.MarketGold
import com.example.ui.theme.MarketGreen
import com.example.ui.theme.MarketOrangePrimary
import com.example.ui.theme.MarketTealSecondary

@Composable
fun ShopScreen(
    userGems: Int,
    onWatchAd: () -> Unit,
    onBuyStarterPack: () -> Unit,
    onRefillEnergy: () -> Unit,
    onExchangeGems: (Int, Long) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp, vertical = 6.dp)
            .testTag("shop_screen_list"),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // Rewarded Video Sponsor Perk
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFE8F5E9)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(MarketGreen))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(MarketGreen),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.OndemandVideo,
                                contentDescription = "Watch Video",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Sponsor Boost (Free)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color(0xFF1B5E20)
                            )
                            Text(
                                text = "Instant +50 ⚡ Energy & +1,000 💵 Coins",
                                fontSize = 11.sp,
                                color = Color(0xFF2E7D32)
                            )
                        }
                    }

                    Button(
                        onClick = onWatchAd,
                        colors = ButtonDefaults.buttonColors(containerColor = MarketGreen),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("button_watch_ad")
                    ) {
                        Text(text = "CLAIM", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        // Starter Pack Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFFF3E0)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(MarketOrangePrimary))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🎁", fontSize = 28.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Grand Starter Pack",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 15.sp,
                                    color = Color(0xFFBF360C)
                                )
                                Text(
                                    text = "5,000 💵 Coins + 100 💎 Gems + 100 ⚡",
                                    fontSize = 11.sp,
                                    color = Color(0xFFE65100),
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Button(
                            onClick = onBuyStarterPack,
                            colors = ButtonDefaults.buttonColors(containerColor = MarketOrangePrimary),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("button_buy_starter_pack")
                        ) {
                            Text(text = "CLAIM PACK", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // Energy Refill
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFEDE7F6)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.FlashOn,
                                contentDescription = null,
                                tint = Color(0xFF7E57C2),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(text = "Full Energy Refill", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(text = "Recharge to 100/100 ⚡ instantly", fontSize = 11.sp, color = Color.Gray)
                        }
                    }

                    Button(
                        onClick = onRefillEnergy,
                        enabled = userGems >= 10,
                        colors = ButtonDefaults.buttonColors(containerColor = MarketTealSecondary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("button_refill_energy")
                    ) {
                        Text(text = "10 💎", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        // Currency Exchanges (Gems -> Coins)
        item {
            Text(
                text = "Currency Exchange",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        val coinPacks = listOf(
            Triple(10, 1000L, "Small Coin Stash"),
            Triple(50, 6000L, "Medium Coin Crate"),
            Triple(100, 15000L, "Vault of Coins")
        )

        items(coinPacks.size) { i ->
            val (gemCost, coinsGain, name) = coinPacks[i]
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "💰", fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(text = name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(text = "+$coinsGain 💵 Coins", fontSize = 11.sp, color = Color(0xFFE65100), fontWeight = FontWeight.Bold)
                        }
                    }

                    Button(
                        onClick = { onExchangeGems(gemCost, coinsGain) },
                        enabled = userGems >= gemCost,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0277BD)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("button_exchange_${gemCost}_gems")
                    ) {
                        Text(text = "$gemCost 💎", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        // VIP Pass Info Box
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF3E5F5))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "👑", fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Supermarket VIP Pass Perks", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF4A148C))
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = "• +50 Max Energy capacity\n• VIP Shoppers visit your aisles 2x more often\n• Daily 20 free Gems bonus\n• Exclusive festival themed products", fontSize = 11.sp, lineHeight = 17.sp, color = Color(0xFF311B92))
                }
            }
        }
    }
}
