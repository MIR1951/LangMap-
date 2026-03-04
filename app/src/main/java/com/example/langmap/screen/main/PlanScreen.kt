package com.example.langmap.screen.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.langmap.model.Achievement
import com.example.langmap.model.LearningTask
import com.example.langmap.ui.theme.Blue
import com.example.langmap.ui.theme.Green
import com.example.langmap.ui.theme.Yellow
import com.example.langmap.viewmodel.PlanViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanScreen(
    viewModel: PlanViewModel,
    modifier: Modifier = Modifier
) {
    var selectedDate by remember { mutableStateOf(System.currentTimeMillis()) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Reja") })
        },
        modifier = modifier
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Calendar placeholder
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(5.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "📅 Tanlangan sana",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Bugun",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Blue
                    )
                }
            }

            // Weekly Progress
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(5.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Haftalik progress",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    WeeklyProgressView(progress = viewModel.weeklyProgress)
                }
            }

            // Today's Tasks
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(5.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Bugungi vazifalar",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    viewModel.todayTasks.forEach { task ->
                        TaskCard(task = task)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }

            // Achievements
            Column {
                Text(
                    "Yutuqlar",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(viewModel.achievements) { achievement ->
                        AchievementCard(achievement = achievement)
                    }
                }
            }
        }
    }
}

@Composable
fun WeeklyProgressView(progress: List<Double>) {
    val days = listOf("Du", "Se", "Ch", "Pa", "Ju", "Sh", "Ya")

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        progress.forEachIndexed { index, value ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .width(30.dp)
                        .height(100.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Gray.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(value.toFloat())
                            .clip(RoundedCornerShape(8.dp))
                            .background(Blue)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    days.getOrElse(index) { "" },
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun TaskCard(task: LearningTask) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(if (task.isCompleted) Green else Color.Gray.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            if (task.isCompleted) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(task.title, fontWeight = FontWeight.Bold)
            Text(task.description, fontSize = 14.sp, color = Color.Gray)
        }

        Text(task.duration, fontSize = 12.sp, color = Color.Gray)
    }
}

@Composable
fun AchievementCard(achievement: Achievement) {
    Card(
        modifier = Modifier.size(width = 120.dp, height = 160.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(5.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val icon = when (achievement.iconName) {
                "star" -> "⭐"
                "book" -> "📚"
                "check_circle" -> "✅"
                else -> "🏆"
            }
            Text(
                icon,
                fontSize = 32.sp,
                color = if (achievement.isUnlocked) Color.Unspecified else Color.Gray
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                achievement.title,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
            Text(
                achievement.description,
                fontSize = 11.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    }
}
