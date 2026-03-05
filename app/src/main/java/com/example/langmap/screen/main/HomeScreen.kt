package com.example.langmap.screen.main

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.langmap.ui.theme.Blue
import com.example.langmap.viewmodel.RecommendationViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: RecommendationViewModel,
    modifier: Modifier = Modifier
) {
    var userName by remember { mutableStateOf("") }

    // Foydalanuvchi ismini olish
    LaunchedEffect(Unit) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid != null) {
            FirebaseFirestore.getInstance().collection("users").document(uid).get()
                .addOnSuccessListener { doc ->
                    userName = doc.getString("userName") ?: ""
                }
        }
        // Cache'dan foydalanadi, API'ga qayta so'rov yubormaslik
        viewModel.fetchRecommendation()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        if (userName.isNotEmpty()) {
                            Text(
                                "Salom, $userName! 👋",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            "AI Tavsiyalari",
                            fontSize = if (userName.isNotEmpty()) 14.sp else 20.sp,
                            color = if (userName.isNotEmpty()) Color.Gray else Color.Unspecified
                        )
                    }
                }
            )
        },
        modifier = modifier
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (viewModel.isLoading) {
                // Shimmer loading
                repeat(3) {
                    ShimmerCard()
                }
            } else if (viewModel.recommendation != null) {
                RecommendationContent(viewModel.recommendation!!)
            } else if (viewModel.errorMessage != null) {
                // Xatolik kartasi
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFF3F3)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("⚠️", fontSize = 40.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            viewModel.errorMessage!!,
                            color = Color(0xFFCC0000),
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                // Bo'sh holat
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFF0F4FF)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🤖", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "AI tavsiyasini olish uchun\ntugmani bosing",
                            color = Color.Gray,
                            fontSize = 16.sp,
                            lineHeight = 24.sp
                        )
                    }
                }
            }

            // Yangilash tugmasi
            Button(
                onClick = { viewModel.fetchRecommendation(forceRefresh = true) },
                enabled = !viewModel.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Blue)
            ) {
                Icon(
                    Icons.Default.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Tavsiyani yangilash",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
fun ShimmerCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF2F2F7))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(16.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.Gray.copy(alpha = 0.15f))
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(12.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.Gray.copy(alpha = 0.1f))
                )
            }
        }
    }
}

@Composable
fun RecommendationContent(recommendation: String) {
    val sections = remember(recommendation) { parseRecommendation(recommendation) }
    var expandedSections by remember { mutableStateOf(setOf(0)) } // Birinchisi ochiq

    val sectionColors = listOf(
        Brush.linearGradient(listOf(Color(0xFF667eea), Color(0xFF764ba2))),
        Brush.linearGradient(listOf(Color(0xFF11998e), Color(0xFF38ef7d))),
        Brush.linearGradient(listOf(Color(0xFFfc5c7d), Color(0xFF6a82fb))),
        Brush.linearGradient(listOf(Color(0xFFf093fb), Color(0xFFf5576c)))
    )

    val sectionEmojis = listOf("📚", "🎯", "📖", "📅", "💡", "🏆")

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        sections.forEachIndexed { index, (title, content) ->
            val gradient = sectionColors[index % sectionColors.size]
            val emoji = sectionEmojis.getOrElse(index) { "📌" }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column {
                    // Header with gradient
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(gradient)
                            .clickable {
                                expandedSections = if (expandedSections.contains(index)) {
                                    expandedSections - index
                                } else {
                                    expandedSections + index
                                }
                            }
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(emoji, fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                title,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 16.sp
                            )
                        }
                        Icon(
                            if (expandedSections.contains(index))
                                Icons.Default.KeyboardArrowUp
                            else
                                Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.8f)
                        )
                    }

                    // Content
                    AnimatedVisibility(visible = expandedSections.contains(index)) {
                        Column(
                            modifier = Modifier.padding(
                                start = 16.dp,
                                end = 16.dp,
                                top = 12.dp,
                                bottom = 16.dp
                            ),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            content.lines().filter { it.isNotBlank() }.forEach { line ->
                                Row(verticalAlignment = Alignment.Top) {
                                    Text(
                                        "•",
                                        color = Blue,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(end = 8.dp, top = 2.dp)
                                    )
                                    Text(
                                        line.trimStart('-', ' ', '•', '*'),
                                        fontSize = 15.sp,
                                        lineHeight = 22.sp,
                                        color = Color(0xFF333333)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun parseRecommendation(text: String): List<Pair<String, String>> {
    val lines = text.split("\n")
    val sections = mutableListOf<Pair<String, String>>()
    var currentTitle = ""
    var currentContent = StringBuilder()

    for (line in lines) {
        if (line.matches(Regex("^\\d+\\..*")) || line.contains("**")) {
            if (currentTitle.isNotEmpty()) {
                sections.add(currentTitle to currentContent.toString())
                currentContent = StringBuilder()
            }
            currentTitle = line
                .replace(Regex("^\\d+\\.\\s*"), "")
                .replace("**", "")
                .trim()
        } else if (line.isNotBlank() && currentTitle.isNotEmpty()) {
            currentContent.appendLine(line.trim())
        }
    }

    if (currentTitle.isNotEmpty()) {
        sections.add(currentTitle to currentContent.toString())
    }

    return sections
}
