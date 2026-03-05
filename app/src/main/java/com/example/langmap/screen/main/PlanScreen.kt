package com.example.langmap.screen.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.langmap.model.Achievement
import com.example.langmap.model.LearningTask
import com.example.langmap.ui.theme.Blue
import com.example.langmap.ui.theme.Green
import com.example.langmap.viewmodel.PlanViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanScreen(
    viewModel: PlanViewModel,
    modifier: Modifier = Modifier
) {
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
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Progress summary card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(listOf(Color(0xFF667eea), Color(0xFF764ba2)))
                        )
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                "Bugungi vazifalar",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 14.sp
                            )
                            Text(
                                "${viewModel.completedTasksToday}/${viewModel.totalTasksToday}",
                                color = Color.White,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                if (viewModel.completedTasksToday == viewModel.totalTasksToday && viewModel.totalTasksToday > 0)
                                    "🎉 Barcha vazifalar bajarildi!"
                                else "Davom eting!",
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 14.sp
                            )
                        }
                        // Circular progress
                        Box(
                            modifier = Modifier.size(64.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            val progress = if (viewModel.totalTasksToday > 0)
                                viewModel.completedTasksToday.toFloat() / viewModel.totalTasksToday
                            else 0f
                            CircularProgressIndicator(
                                progress = { progress },
                                modifier = Modifier.size(64.dp),
                                color = Color.White,
                                trackColor = Color.White.copy(alpha = 0.2f),
                                strokeWidth = 6.dp
                            )
                            Text(
                                "${(progress * 100).toInt()}%",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            // Weekly Progress
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Haftalik progress",
                        fontSize = 18.sp,
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
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Bugungi vazifalar",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    viewModel.todayTasks.forEach { task ->
                        TaskCard(
                            task = task,
                            onToggle = { viewModel.toggleTask(task.id) }
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
            }

            // Achievements
            Column {
                Text(
                    "Yutuqlar",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(viewModel.achievements) { achievement ->
                        AchievementCard(achievement = achievement)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
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
                        .background(Color.Gray.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(value.toFloat().coerceIn(0f, 1f))
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (value > 0) Brush.verticalGradient(
                                    listOf(Color(0xFF667eea), Color(0xFF764ba2))
                                ) else Brush.verticalGradient(
                                    listOf(Color.Transparent, Color.Transparent)
                                )
                            )
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
fun TaskCard(task: LearningTask, onToggle: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (task.isCompleted) Green.copy(alpha = 0.08f)
                else Color(0xFFF8F9FA)
            )
            .clickable { onToggle() }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(if (task.isCompleted) Green else Color.Gray.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            if (task.isCompleted) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                task.title,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                color = if (task.isCompleted) Color.Gray else Color.Black
            )
            Text(
                task.description,
                fontSize = 13.sp,
                color = Color.Gray
            )
        }

        Surface(
            shape = RoundedCornerShape(8.dp),
            color = Blue.copy(alpha = 0.1f)
        ) {
            Text(
                task.duration,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                fontSize = 12.sp,
                color = Blue,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun AchievementCard(achievement: Achievement) {
    Card(
        modifier = Modifier.size(width = 120.dp, height = 150.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (achievement.isUnlocked) Color(0xFFFFF8E1) else Color(0xFFF5F5F5)
        )
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
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                achievement.title,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                color = if (achievement.isUnlocked) Color.Black else Color.Gray
            )
            Text(
                achievement.description,
                fontSize = 11.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                lineHeight = 14.sp
            )
        }
    }
}
