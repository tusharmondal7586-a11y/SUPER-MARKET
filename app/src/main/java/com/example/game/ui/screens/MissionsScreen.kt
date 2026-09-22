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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.model.Achievement
import com.example.game.model.DailyMission
import com.example.game.model.StoryChapter
import com.example.ui.theme.MarketGold
import com.example.ui.theme.MarketGreen
import com.example.ui.theme.MarketOrangePrimary
import com.example.ui.theme.MarketTealSecondary

@Composable
fun MissionsScreen(
    chapters: List<StoryChapter>,
    currentChapterIdx: Int,
    dailyMissions: List<DailyMission>,
    achievements: List<Achievement>,
    onClaimChapter: (Int) -> Unit,
    onClaimMission: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabTitles = listOf("📖 Story", "🎯 Daily", "🍕 Events", "🏆 Trophies")

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.Transparent,
            contentColor = MarketOrangePrimary,
            modifier = Modifier.fillMaxWidth()
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 12.sp
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        when (selectedTab) {
            0 -> StoryTab(
                chapters = chapters,
                currentChapterIdx = currentChapterIdx,
                onClaimChapter = onClaimChapter
            )
            1 -> DailyMissionsTab(
                missions = dailyMissions,
                onClaimMission = onClaimMission
            )
            2 -> SpecialEventsTab()
            3 -> AchievementsTab(achievements = achievements)
        }
    }
}

@Composable
fun StoryTab(
    chapters: List<StoryChapter>,
    currentChapterIdx: Int,
    onClaimChapter: (Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("story_chapters_list"),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        items(chapters) { chapter ->
            val idx = chapter.chapterNumber - 1
            val isCurrent = idx == currentChapterIdx
            val isUnlocked = idx <= currentChapterIdx

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (chapter.isCompleted) Color(0xFFE8F5E9)
                    else if (isCurrent) Color(0xFFFFF8E1)
                    else Color(0xFFF5F5F5)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = if (isCurrent) 3.dp else 1.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(if (chapter.isCompleted) MarketGreen else Color(0xFFFFCC80)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = chapter.speakerAvatar, fontSize = 20.sp)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Chapter ${chapter.chapterNumber}: ${chapter.title}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = chapter.speaker,
                                    fontSize = 11.sp,
                                    color = MarketOrangePrimary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        if (chapter.isCompleted) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Completed",
                                tint = MarketGreen,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = chapter.storyDialogue,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Goal: ${chapter.goalDescription}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFFE65100)
                            )
                            Text(
                                text = "Reward: +${chapter.rewardCoins} 💵  +${chapter.rewardGems} 💎",
                                fontSize = 10.sp,
                                color = Color.Gray
                            )
                        }

                        if (isCurrent && !chapter.isCompleted) {
                            Button(
                                onClick = { onClaimChapter(idx) },
                                colors = ButtonDefaults.buttonColors(containerColor = MarketOrangePrimary),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                modifier = Modifier.height(34.dp)
                            ) {
                                Text(text = "CLAIM", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DailyMissionsTab(
    missions: List<DailyMission>,
    onClaimMission: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("daily_missions_list"),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        items(missions, key = { it.id }) { mission ->
            val isReady = mission.currentProgress >= mission.targetProgress && !mission.isClaimed

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = mission.iconEmoji, fontSize = 26.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(text = mission.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Spacer(modifier = Modifier.height(3.dp))
                            LinearProgressIndicator(
                                progress = { (mission.currentProgress.toFloat() / mission.targetProgress).coerceIn(0f, 1f) },
                                modifier = Modifier
                                    .width(120.dp)
                                    .height(5.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = MarketOrangePrimary,
                                trackColor = Color(0xFFEEEEEE),
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = "${mission.currentProgress} / ${mission.targetProgress} · Reward: +${mission.rewardCoins} 💵 +${mission.rewardEnergy} ⚡",
                                fontSize = 10.sp,
                                color = Color.Gray
                            )
                        }
                    }

                    if (mission.isClaimed) {
                        Text(text = "Claimed", color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    } else {
                        Button(
                            onClick = { onClaimMission(mission.id) },
                            enabled = isReady,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MarketGreen,
                                disabledContainerColor = Color(0xFFE0E0E0)
                            ),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Text(text = "CLAIM", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SpecialEventsTab() {
    val events = listOf(
        Triple("🍕 Weekend Food Festival", "Merge gourmet treats to win double festival tokens!", "ACTIVE NOW"),
        Triple("🎃 Halloween Market", "Special pumpkin pies and trick-or-treat shelves!", "Coming Soon"),
        Triple("🎄 Christmas Supermarket", "Holiday decorations and gingerbread merges!", "Coming Soon"),
        Triple("💝 Valentine's Shopping Event", "Deluxe chocolate boxes and romantic bouquets!", "Coming Soon")
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        items(events) { (title, desc, status) ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (status == "ACTIVE NOW") Color(0xFFFFF3E0) else Color(0xFFFAFAFA)
                )
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (status == "ACTIVE NOW") MarketGreen else Color.LightGray)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(text = status, color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = desc, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
fun AchievementsTab(achievements: List<Achievement>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        items(achievements, key = { it.id }) { ach ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (ach.isUnlocked) Color(0xFFE8F5E9) else MaterialTheme.colorScheme.surface
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = ach.iconEmoji, fontSize = 28.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = ach.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            if (ach.isUnlocked) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = "UNLOCKED", color = MarketGreen, fontSize = 10.sp, fontWeight = FontWeight.Black)
                            }
                        }
                        Text(text = ach.description, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${ach.currentProgress} / ${ach.targetProgress} · Reward: +${ach.rewardGems} 💎",
                            fontSize = 10.sp,
                            color = MarketTealSecondary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
