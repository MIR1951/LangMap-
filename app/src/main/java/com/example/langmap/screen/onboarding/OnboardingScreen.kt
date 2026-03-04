package com.example.langmap.screen.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
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
import com.example.langmap.model.AgeOption
import com.example.langmap.ui.theme.Blue
import com.example.langmap.viewmodel.OnboardingViewModel

@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel,
    onFinish: () -> Unit
) {
    if (viewModel.showAuthView) {
        LaunchedEffect(Unit) { onFinish() }
        return
    }

    Column(modifier = Modifier.fillMaxSize()) {
        when (viewModel.pageIndex) {
            0 -> OnboardingPage1(viewModel)
            1 -> OnboardingPage2(viewModel)
            2 -> OnboardingPage3(viewModel)
            3 -> OnboardingPage4(viewModel)
            4 -> OnboardingPage5(viewModel)
            5 -> OnboardingPage6(viewModel)
            6 -> OnboardingPage7(viewModel)
            7 -> OnboardingPage8(viewModel)
            8 -> OnboardingPage9(viewModel)
            9 -> OnboardingPage10(viewModel)
            10 -> OnboardingPage11(viewModel)
            11 -> OnboardingPage12(viewModel)
            12 -> OnboardingPage13(viewModel)
            13 -> OnboardingPage14(viewModel)
            14 -> OnboardingPage15(viewModel)
            15 -> OnboardingPage16(viewModel)
            16 -> OnboardingPage17(viewModel)
            17 -> OnboardingPage18(viewModel)
            else -> GenericContinuePage(viewModel, onFinish)
        }
    }
}

// ==================== COMPONENTS ====================

@Composable
fun OnboardingHeader(
    currentStep: Int,
    totalSteps: Int,
    onBack: () -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)) {
        if (currentStep > 0) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.height(35.dp)
            ) {
                IconButton(onClick = onBack, modifier = Modifier.size(25.dp)) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(8.dp))
                ProgressIndicatorBar(totalSteps = totalSteps, currentStep = currentStep + 1)
            }
        }
    }
}

@Composable
fun ProgressIndicatorBar(totalSteps: Int, currentStep: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(6.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(Color.Gray.copy(alpha = 0.3f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(currentStep.toFloat() / totalSteps.toFloat())
                .fillMaxHeight()
                .clip(RoundedCornerShape(4.dp))
                .background(Blue)
        )
    }
}

@Composable
fun ContinueButton(
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Blue,
            disabledContainerColor = Color.Gray.copy(alpha = 0.5f)
        )
    ) {
        Text("Davom etish", fontWeight = FontWeight.SemiBold, fontSize = 17.sp)
    }
}

@Composable
fun SelectableOption(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) Blue.copy(alpha = 0.1f) else Color(0xFFF7F7F7))
            .border(
                width = if (isSelected) 2.dp else 0.dp,
                color = if (isSelected) Blue else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Text(
            text = text,
            fontSize = 17.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun VocabularyQuestionView(
    question: String,
    selectedAnswer: String?,
    onAnswerSelected: (String) -> Unit
) {
    val answers = listOf("To'g'ri", "Qisman to'g'ri", "Bu men uchun to'g'ri emas")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = question,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Blue,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 20.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            "Quyidagi gap sizga to'g'ri keladimi?",
            fontSize = 17.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))
        answers.forEach { answer ->
            SelectableOption(
                text = answer,
                isSelected = selectedAnswer == answer,
                onClick = { onAnswerSelected(answer) }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

// ==================== PAGES ====================

@Composable
fun OnboardingPage1(viewModel: OnboardingViewModel) {
    val ageOptions = listOf(
        AgeOption("Yosh: 18-24", "age_18_24"),
        AgeOption("Yosh: 25-34", "age_25_34"),
        AgeOption("Yosh: 35-44", "age_35_44"),
        AgeOption("Yosh: 45+", "age_44_plus")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Ingliz tilidagi\nimkoniyatlaringizni\nkashf eting",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Blue,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Til o'rganish maqsadlaringizga moslashtirilgan\nshaxsiy o'quv rejangizni oling",
            fontSize = 16.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            "3 DAQIQALIK TEST",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(20.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(ageOptions) { option ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .background(Color.Gray.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("👤", fontSize = 40.sp)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = {
                                viewModel.updateSelectedAge(option)
                                viewModel.onNext()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(22.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Blue)
                        ) {
                            Text(option.label, fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(14.dp))
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingPage2(viewModel: OnboardingViewModel) {
    val languages = listOf("O'zbek", "Rus", "Ingliz", "Turk", "Qozoq", "Boshqa")
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        OnboardingHeader(viewModel.pageIndex, viewModel.totalPages, viewModel::onBack)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                "Qaysi tilni o'rganmoqchisiz?",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Spacer(modifier = Modifier.height(32.dp))

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = it }
            ) {
                OutlinedTextField(
                    value = viewModel.selectedLanguage.ifEmpty { "Variantni tanlang" },
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    shape = RoundedCornerShape(12.dp)
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    languages.forEach { lang ->
                        DropdownMenuItem(
                            text = { Text(lang) },
                            onClick = {
                                viewModel.updateSelectedLanguage(lang)
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))
            ContinueButton(enabled = viewModel.selectedLanguage.isNotEmpty()) { viewModel.onNext() }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun OnboardingPage3(viewModel: OnboardingViewModel) {
    val proficiencyLevels = listOf(
        "Boshlang'ich" to "🌱",
        "Boshlang'ichdan yuqori" to "🌿",
        "O'rta" to "🌱🌿",
        "O'rtadan yuqori" to "🌿🌿",
        "Ilg'or" to "🌸",
        "Mukammal" to "🌳"
    )

    Column(modifier = Modifier.fillMaxSize()) {
        OnboardingHeader(viewModel.pageIndex, viewModel.totalPages, viewModel::onBack)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                "Ingliz tili darajangiz\nqanday?",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(32.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(proficiencyLevels) { (level, icon) ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.updateSelectedProficiency(level) },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (viewModel.selectedProficiency == level)
                                Blue.copy(alpha = 0.1f) else Color(0xFFF2F2F2)
                        ),
                        border = if (viewModel.selectedProficiency == level)
                            androidx.compose.foundation.BorderStroke(2.dp, Blue) else null
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(CircleShape)
                                    .background(Blue.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(icon, fontSize = 40.sp)
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(level, fontSize = 17.sp, textAlign = TextAlign.Center)
                        }
                    }
                }
            }

            ContinueButton(enabled = viewModel.selectedProficiency.isNotEmpty()) { viewModel.onNext() }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun OnboardingPage4(viewModel: OnboardingViewModel) {
    Column(modifier = Modifier.fillMaxSize()) {
        OnboardingHeader(viewModel.pageIndex, viewModel.totalPages, viewModel::onBack)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(10.dp))
            Text("10", fontSize = 36.sp, fontWeight = FontWeight.Bold, color = Blue)
            Text(
                "milliondan ortiq\nfoydalanuvchi",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "LangMap bilan ingliz tilini o'rganmoqda",
                fontSize = 15.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Reviews
            val reviews = listOf(
                Triple("Owen Sinclair, 26", "Studying with LangMap is super comfortable. Strongly suggest!", "🤩"),
                Triple("Sarah, UK", "It is customized just for me. Feels like a personal tutor.", "😋"),
                Triple("Emma, US", "I grasp difficult concepts effortlessly. A must-have!", "💫")
            )
            reviews.forEach { (name, text, emoji) ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5).copy(alpha = 0.5f))
                ) {
                    Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Blue.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) { Text("👤", fontSize = 24.sp) }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(name, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                            Text(text, fontSize = 15.sp, color = Color.Gray)
                        }
                        Text(emoji, fontSize = 24.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))
            ContinueButton { viewModel.onNext() }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun OnboardingPage5(viewModel: OnboardingViewModel) {
    val goals = listOf(
        "💼" to "Karyera, ish",
        "👨‍👩‍👧‍👦" to "Oila va do'stlar",
        "✈️" to "Sayohatlar",
        "💕" to "Hamkorlar bilan muloqot",
        "🧠" to "Aqliy mashq",
        "🎓" to "Ta'lim"
    )

    Column(modifier = Modifier.fillMaxSize()) {
        OnboardingHeader(viewModel.pageIndex, viewModel.totalPages, viewModel::onBack)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                "Nima uchun ingliz tilini o'rganmoqchisiz?",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Spacer(modifier = Modifier.height(32.dp))

            LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(goals) { (emoji, goal) ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.updateSelectedGoal(goal) },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF2F2F2)),
                        border = if (viewModel.selectedGoal == goal)
                            androidx.compose.foundation.BorderStroke(2.dp, Blue) else null
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(emoji, fontSize = 28.sp)
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(goal, fontSize = 17.sp, modifier = Modifier.weight(1f))
                            if (viewModel.selectedGoal == goal) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Blue)
                            }
                        }
                    }
                }
            }

            ContinueButton(enabled = viewModel.selectedGoal.isNotEmpty()) { viewModel.onNext() }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun OnboardingPage6(viewModel: OnboardingViewModel) {
    val goalLevels = listOf(
        "Ingliz tilida so'zlashuvchilar bilan bemalol gaplashing",
        "Filmlarni subtitrlarsiz tomosha qiling",
        "Suhbatlarni oson tushuning",
        "Matnlarni ravon o'qing"
    )
    val icons = listOf("🗣️", "🎬", "👂", "📖")

    Column(modifier = Modifier.fillMaxSize()) {
        OnboardingHeader(viewModel.pageIndex, viewModel.totalPages, viewModel::onBack)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                "Ingliz tilida qanday\ndarajaga erishmoqchisiz?",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(32.dp))

            LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(goalLevels.zip(icons)) { (title, icon) ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.updateSelectedGoalLevel(title) },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F7F7)),
                        border = if (viewModel.selectedGoalLevel == title)
                            androidx.compose.foundation.BorderStroke(2.dp, Blue) else null
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(icon, fontSize = 36.sp)
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(title, fontSize = 17.sp, modifier = Modifier.weight(1f))
                            RadioButton(
                                selected = viewModel.selectedGoalLevel == title,
                                onClick = { viewModel.updateSelectedGoalLevel(title) },
                                colors = RadioButtonDefaults.colors(selectedColor = Blue)
                            )
                        }
                    }
                }
            }

            ContinueButton(enabled = viewModel.selectedGoalLevel.isNotEmpty()) { viewModel.onNext() }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun OnboardingPage7(viewModel: OnboardingViewModel) {
    Column(modifier = Modifier.fillMaxSize()) {
        OnboardingHeader(viewModel.pageIndex, viewModel.totalPages, viewModel::onBack)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                "O'z maqsadingizni\nbelgilang!",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Blue,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F7F7))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("II", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Blue)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Intizom – maqsad va yutuq\no'rtasidagi ko'prikdir.",
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("- Jim Rohn", fontSize = 17.sp, color = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            Text("🎯", fontSize = 80.sp)

            Spacer(modifier = Modifier.weight(1f))
            ContinueButton { viewModel.onNext() }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun OnboardingPage8(viewModel: OnboardingViewModel) {
    val options = listOf("Yaqinda", "Bir yil oldin", "Bir yildan ko'proq vaqt oldin", "Hech qachon")

    Column(modifier = Modifier.fillMaxSize()) {
        OnboardingHeader(viewModel.pageIndex, viewModel.totalPages, viewModel::onBack)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                "Oxirgi marta qachon yangi\ntil o'rgandingiz?",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(32.dp))

            options.forEach { option ->
                SelectableOption(
                    text = option,
                    isSelected = viewModel.selectedLastLanguageTime == option,
                    onClick = { viewModel.updateSelectedLastLanguageTime(option) }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            Spacer(modifier = Modifier.weight(1f))
            ContinueButton(enabled = viewModel.selectedLastLanguageTime.isNotEmpty()) { viewModel.onNext() }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun OnboardingPage9(viewModel: OnboardingViewModel) {
    val methods = listOf(
        "📱" to "Mobil ilova",
        "💻" to "Onlayn o'qituvchi",
        "📚" to "Maktab",
        "🏫" to "Til kurslari",
        "▶️" to "YouTube"
    )

    Column(modifier = Modifier.fillMaxSize()) {
        OnboardingHeader(viewModel.pageIndex, viewModel.totalPages, viewModel::onBack)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                "Qanday usulda\no'rganishga harakat\nqilgansiz?",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(32.dp))

            methods.forEach { (icon, title) ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.updateSelectedLearningMethod(title) },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F7F7))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(icon, fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(title, fontSize = 17.sp, modifier = Modifier.weight(1f))
                        if (viewModel.selectedLearningMethod == title) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = Blue)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            Spacer(modifier = Modifier.weight(1f))
            ContinueButton(enabled = viewModel.selectedLearningMethod.isNotEmpty()) { viewModel.onNext() }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun OnboardingPage10(viewModel: OnboardingViewModel) {
    val experiences = listOf(
        "Juda qimmat edi va men buncha pul sarflashni xohlamadim.",
        "Vaqt ajratishga qiynaldim va davom ettira olmadim.",
        "Boshqalar oldida gapirishni mashq qilishni xohlamasdim.",
        "Menga moslashtirilgan dastur yo'q edi.",
        "Jadvalim o'qituvchimning jadvaliga to'g'ri kelmadi.",
        "Men o'sishga erishdim, lekin bundan ham yuqoriroq natijalarga umid qilgan edim."
    )

    Column(modifier = Modifier.fillMaxSize()) {
        OnboardingHeader(viewModel.pageIndex, viewModel.totalPages, viewModel::onBack)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                "Ingliz tilini o'rganish\nbo'yicha oldingi\ntajribangizni qanday\nta'riflaysiz?",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(experiences) { experience ->
                    SelectableOption(
                        text = experience,
                        isSelected = viewModel.selectedExperience == experience,
                        onClick = { viewModel.updateSelectedExperience(experience) }
                    )
                }
            }

            ContinueButton(enabled = viewModel.selectedExperience.isNotEmpty()) { viewModel.onNext() }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun OnboardingPage11(viewModel: OnboardingViewModel) {
    Column(modifier = Modifier.fillMaxSize()) {
        OnboardingHeader(viewModel.pageIndex, viewModel.totalPages, viewModel::onBack)
        VocabularyQuestionView(
            question = "Ingliz tilida ravon\ngapirganda\ntushunmayman.",
            selectedAnswer = viewModel.selectedUnderstandingLevel,
            onAnswerSelected = { viewModel.updateSelectedUnderstandingLevel(it) }
        )
        Spacer(modifier = Modifier.weight(1f))
        ContinueButton(enabled = viewModel.selectedUnderstandingLevel.isNotEmpty()) {
            viewModel.navigateToPage(12)
        }
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun OnboardingPage12(viewModel: OnboardingViewModel) {
    Column(modifier = Modifier.fillMaxSize()) {
        OnboardingHeader(viewModel.pageIndex, viewModel.totalPages, viewModel::onBack)
        VocabularyQuestionView(
            question = "So'z boyligim kamligi\nsababli gapirishga\nqiynalaman.",
            selectedAnswer = viewModel.selectedAnswer12,
            onAnswerSelected = { viewModel.updateSelectedAnswer12(it) }
        )
        Spacer(modifier = Modifier.weight(1f))
        ContinueButton(enabled = viewModel.selectedAnswer12 != null) {
            viewModel.navigateToPage(13)
        }
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun OnboardingPage13(viewModel: OnboardingViewModel) {
    Column(modifier = Modifier.fillMaxSize()) {
        OnboardingHeader(viewModel.pageIndex, viewModel.totalPages, viewModel::onBack)
        VocabularyQuestionView(
            question = "Gapirganda doim noto'g'ri\ngaplar tuzaman.",
            selectedAnswer = viewModel.selectedAnswer13,
            onAnswerSelected = { viewModel.updateSelectedAnswer13(it) }
        )
        Spacer(modifier = Modifier.weight(1f))
        ContinueButton(enabled = viewModel.selectedAnswer13 != null) {
            viewModel.navigateToPage(14)
        }
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun OnboardingPage14(viewModel: OnboardingViewModel) {
    Column(modifier = Modifier.fillMaxSize()) {
        OnboardingHeader(viewModel.pageIndex, viewModel.totalPages, viewModel::onBack)
        VocabularyQuestionView(
            question = "Tushunaman, lekin\ngapira olmayman.",
            selectedAnswer = viewModel.selectedAnswer14,
            onAnswerSelected = { viewModel.updateSelectedAnswer14(it) }
        )
        Spacer(modifier = Modifier.weight(1f))
        ContinueButton(enabled = viewModel.selectedAnswer14 != null) {
            viewModel.navigateToPage(15)
        }
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun OnboardingPage15(viewModel: OnboardingViewModel) {
    Column(modifier = Modifier.fillMaxSize()) {
        OnboardingHeader(viewModel.pageIndex, viewModel.totalPages, viewModel::onBack)
        VocabularyQuestionView(
            question = "Ingliz tilida gapira\nolaman, lekin ravonroq\nbo'lishim kerak.",
            selectedAnswer = viewModel.selectedAnswer15,
            onAnswerSelected = { viewModel.updateSelectedAnswer15(it) }
        )
        Spacer(modifier = Modifier.weight(1f))
        ContinueButton(enabled = viewModel.selectedAnswer15 != null) {
            viewModel.navigateToPage(16)
        }
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun OnboardingPage16(viewModel: OnboardingViewModel) {
    Column(modifier = Modifier.fillMaxSize()) {
        OnboardingHeader(viewModel.pageIndex, viewModel.totalPages, viewModel::onBack)
        VocabularyQuestionView(
            question = "Xohlagan paytimda mashq\nqilish uchun sherigim\nyo'q.",
            selectedAnswer = viewModel.selectedAnswer16,
            onAnswerSelected = { viewModel.updateSelectedAnswer16(it) }
        )
        Spacer(modifier = Modifier.weight(1f))
        ContinueButton(enabled = viewModel.selectedAnswer16 != null) {
            viewModel.navigateToPage(17)
        }
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun OnboardingPage17(viewModel: OnboardingViewModel) {
    Column(modifier = Modifier.fillMaxSize()) {
        OnboardingHeader(viewModel.pageIndex, viewModel.totalPages, viewModel::onBack)
        VocabularyQuestionView(
            question = "Ingliz tilidagi filmlarni\nko'rishda qiynalaman.",
            selectedAnswer = viewModel.selectedAnswer17,
            onAnswerSelected = { viewModel.updateSelectedAnswer17(it) }
        )
        Spacer(modifier = Modifier.weight(1f))
        ContinueButton(enabled = viewModel.selectedAnswer17 != null) {
            viewModel.navigateToPage(18)
        }
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun OnboardingPage18(viewModel: OnboardingViewModel) {
    var nameInput by remember { mutableStateOf(viewModel.userName) }

    Column(modifier = Modifier.fillMaxSize()) {
        OnboardingHeader(viewModel.pageIndex, viewModel.totalPages, viewModel::onBack)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                "Ismingiz nima?",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = nameInput,
                onValueChange = {
                    nameInput = it
                    viewModel.updateUserName(it)
                },
                label = { Text("Ismingizni kiriting") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.weight(1f))
            ContinueButton(enabled = nameInput.isNotEmpty()) {
                viewModel.finishOnboarding()
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun GenericContinuePage(viewModel: OnboardingViewModel, onFinish: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        OnboardingHeader(viewModel.pageIndex, viewModel.totalPages, viewModel::onBack)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "Davom etish uchun tugmani bosing",
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(32.dp))
            ContinueButton {
                if (viewModel.pageIndex >= viewModel.totalPages - 1) {
                    viewModel.finishOnboarding()
                } else {
                    viewModel.onNext()
                }
            }
        }
    }
}
