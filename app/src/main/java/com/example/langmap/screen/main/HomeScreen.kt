package com.example.langmap.screen.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.langmap.ui.theme.Blue
import com.example.langmap.viewmodel.RecommendationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: RecommendationViewModel,
    modifier: Modifier = Modifier
) {
    var isFirstAppear by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        if (isFirstAppear) {
            viewModel.fetchRecommendation()
            isFirstAppear = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("AI Tavsiyalari") })
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
            if (viewModel.isLoading) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = Blue)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Tavsiya yuklanmoqda...", color = Color.Gray)
                    }
                }
            } else if (viewModel.recommendation != null) {
                RecommendationContent(viewModel.recommendation!!)
            } else if (viewModel.errorMessage != null) {
                Text(
                    viewModel.errorMessage!!,
                    color = Color.Red,
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                Text(
                    "AI'dan tavsiya olish uchun tugmani bosing.",
                    color = Color.Gray,
                    modifier = Modifier.padding(16.dp)
                )
            }

            Button(
                onClick = { viewModel.fetchRecommendation() },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Blue)
            ) {
                Text("♻️ Tavsiyani yangilash", modifier = Modifier.padding(8.dp))
            }
        }
    }
}

@Composable
fun RecommendationContent(recommendation: String) {
    val sections = remember(recommendation) { parseRecommendation(recommendation) }
    var expandedSections by remember { mutableStateOf(setOf<Int>()) }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        sections.forEachIndexed { index, (title, content) ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF2F2F7))
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
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
                        Text(
                            title,
                            fontWeight = FontWeight.Bold,
                            color = Blue,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            if (expandedSections.contains(index))
                                Icons.Default.KeyboardArrowUp
                            else
                                Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = Blue
                        )
                    }

                    AnimatedVisibility(visible = expandedSections.contains(index)) {
                        Column(
                            modifier = Modifier.padding(start = 24.dp, end = 16.dp, bottom = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            content.lines().filter { it.isNotBlank() }.forEach { line ->
                                Row {
                                    Text("•", color = Blue, modifier = Modifier.padding(end = 8.dp))
                                    Text(line.trimStart('-', ' ', '•'))
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
