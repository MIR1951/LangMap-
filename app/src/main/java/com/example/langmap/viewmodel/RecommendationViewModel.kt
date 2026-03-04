package com.example.langmap.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

class RecommendationViewModel : ViewModel() {

    var isLoading by mutableStateOf(false)
        private set

    var recommendation by mutableStateOf<String?>(null)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    private val maxRetries = 2
    private var currentRetry = 0
    private var lastRequestTime: Long? = null
    private val minimumRequestInterval = 10_000L // 10 seconds
    private var isRequestInProgress = false

    private val db = FirebaseFirestore.getInstance()

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    fun fetchRecommendation() {
        if (isRequestInProgress) {
            errorMessage = "So'rov allaqachon amalga oshirilmoqda. Iltimos, kuting."
            return
        }

        lastRequestTime?.let { lastTime ->
            val elapsed = System.currentTimeMillis() - lastTime
            if (elapsed < minimumRequestInterval) {
                val remaining = (minimumRequestInterval - elapsed) / 1000
                errorMessage = "Iltimos, ${remaining} soniyadan keyin qayta urinib ko'ring."
                return
            }
        }

        isLoading = true
        errorMessage = null
        recommendation = null
        currentRetry = 0
        lastRequestTime = System.currentTimeMillis()
        isRequestInProgress = true

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            isLoading = false
            isRequestInProgress = false
            errorMessage = "Foydalanuvchi tizimga kirmagan"
            return
        }

        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val data = document.data ?: run {
                        isLoading = false
                        isRequestInProgress = false
                        errorMessage = "Foydalanuvchi ma'lumotlari topilmadi"
                        return@addOnSuccessListener
                    }

                    val userName = data["userName"] as? String ?: "Foydalanuvchi"
                    val level = data["proficiency"] as? String ?: "Boshlang'ich"
                    val goal = data["goal"] as? String ?: "Ingliz tilini o'rganish"
                    val age = data["age"] as? String ?: ""
                    val language = data["language"] as? String ?: ""
                    val experience = data["experience"] as? String ?: ""
                    val learningMethod = data["learningMethod"] as? String ?: ""

                    val prompt = """
                        Foydalanuvchi haqida ma'lumotlar:
                        Ismi: $userName
                        Yoshi: $age
                        Darajasi: $level
                        Maqsadi: $goal
                        Til: $language
                        Tajriba: $experience
                        O'rganish usuli: $learningMethod

                        Ushbu foydalanuvchiga ingliz tili o'rganish bo'yicha shaxsiylashtirilgan tavsiya bering.
                        Har bir tavsiya quyidagi formatda bo'lsin:

                        1. **Darajaga mos mashqlar**
                        - Mashq 1
                        - Mashq 2
                        - Mashq 3

                        2. **Maqsad strategiyalari**
                        - Strategiya 1
                        - Strategiya 2
                        - Strategiya 3

                        3. **O'rganish materiallari**
                        - Material 1
                        - Material 2
                        - Material 3

                        4. **Rejalar**
                        - Qisqa muddatli reja
                        - Uzoq muddatli reja

                        Iltimos, har bir tavsiyani sarlavha va ro'yxat shaklida berib bering.
                        Sarlavhalarni ** belgilari orasiga oling.
                    """.trimIndent()

                    makeAIRequest(prompt)
                } else {
                    isLoading = false
                    isRequestInProgress = false
                    errorMessage = "Foydalanuvchi ma'lumotlari topilmadi"
                }
            }
            .addOnFailureListener { e ->
                isLoading = false
                isRequestInProgress = false
                errorMessage = "Foydalanuvchi ma'lumotlarini olishda xatolik: ${e.message}"
            }
    }

    private fun makeAIRequest(prompt: String) {
        viewModelScope.launch {
            try {
                val apiKey = "YOUR_OPENAI_API_KEY" // Set your API key here
                val url = "https://api.openai.com/v1/chat/completions"

                val requestBody = mapOf(
                    "model" to "gpt-4o-mini",
                    "messages" to listOf(
                        mapOf(
                            "role" to "system",
                            "content" to "Siz ingliz tili o'qituvchisiz. Foydalanuvchilarga ingliz tilini o'rganish bo'yicha foydali va shaxsiylashtirilgan tavsiyalar berasiz."
                        ),
                        mapOf("role" to "user", "content" to prompt)
                    ),
                    "temperature" to 0.7,
                    "max_tokens" to 1000
                )

                val jsonBody = Gson().toJson(requestBody)
                val mediaType = "application/json".toMediaType()
                val body = jsonBody.toRequestBody(mediaType)

                val request = Request.Builder()
                    .url(url)
                    .addHeader("Authorization", "Bearer $apiKey")
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Cache-Control", "no-cache")
                    .post(body)
                    .build()

                val response = withContext(Dispatchers.IO) {
                    client.newCall(request).execute()
                }

                isRequestInProgress = false

                if (response.code == 404) {
                    isLoading = false
                    errorMessage = "API endpoint yoki model nomi noto'g'ri."
                    return@launch
                }

                if (response.code == 429) {
                    isLoading = false
                    errorMessage = "So'rovlar soni chegaradan oshib ketdi. 30 soniyadan keyin qayta urinib ko'ring."
                    lastRequestTime = System.currentTimeMillis() + 30_000
                    return@launch
                }

                if (response.code !in 200..299) {
                    isLoading = false
                    errorMessage = "Server xatosi: ${response.code}"
                    return@launch
                }

                val responseBody = response.body?.string()
                if (responseBody != null) {
                    val json = Gson().fromJson(responseBody, Map::class.java) as? Map<*, *>
                    val choices = json?.get("choices") as? List<*>
                    val firstChoice = choices?.firstOrNull() as? Map<*, *>
                    val message = firstChoice?.get("message") as? Map<*, *>
                    val content = message?.get("content") as? String

                    if (content != null) {
                        isLoading = false
                        recommendation = content.trim()
                    } else {
                        isLoading = false
                        errorMessage = "Noto'g'ri javob formati"
                    }
                } else {
                    isLoading = false
                    errorMessage = "Ma'lumot olinmadi"
                }
            } catch (e: Exception) {
                isRequestInProgress = false

                if (currentRetry < maxRetries) {
                    currentRetry++
                    kotlinx.coroutines.delay(currentRetry * 10_000L)
                    makeAIRequest(prompt)
                } else {
                    isLoading = false
                    errorMessage = "Tarmoq ulanishi uzildi. Internet ulanishingizni tekshiring."
                }
            }
        }
    }
}
