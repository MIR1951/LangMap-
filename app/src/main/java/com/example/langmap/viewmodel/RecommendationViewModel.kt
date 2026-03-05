package com.example.langmap.viewmodel

import android.util.Log
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

    companion object {
        private const val TAG = "LANGMAP"
    }

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
        Log.d(TAG, "========== fetchRecommendation() BOSHLANDI ==========")

        if (isRequestInProgress) {
            Log.w(TAG, "❌ So'rov allaqachon amalga oshirilmoqda")
            errorMessage = "So'rov allaqachon amalga oshirilmoqda. Iltimos, kuting."
            return
        }

        lastRequestTime?.let { lastTime ->
            val elapsed = System.currentTimeMillis() - lastTime
            if (elapsed < minimumRequestInterval) {
                val remaining = (minimumRequestInterval - elapsed) / 1000
                Log.w(TAG, "❌ Minimum interval: ${remaining}s qoldi")
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
        Log.d(TAG, "📌 User ID: $userId")

        if (userId == null) {
            Log.e(TAG, "❌ Foydalanuvchi tizimga kirmagan")
            isLoading = false
            isRequestInProgress = false
            errorMessage = "Foydalanuvchi tizimga kirmagan"
            return
        }

        Log.d(TAG, "📡 Firestore'dan ma'lumot so'ralmoqda: users/$userId")

        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                Log.d(TAG, "📥 Firestore javob: exists=${document.exists()}")

                if (document != null && document.exists()) {
                    val data = document.data ?: run {
                        Log.e(TAG, "❌ document.data = null")
                        isLoading = false
                        isRequestInProgress = false
                        errorMessage = "Foydalanuvchi ma'lumotlari topilmadi"
                        return@addOnSuccessListener
                    }

                    Log.d(TAG, "📋 Firestore'dan olingan maydonlar: ${data.keys}")

                    val userName = data["userName"] as? String ?: "Foydalanuvchi"
                    val level = data["proficiency"] as? String ?: "Boshlang'ich"
                    val goal = data["goal"] as? String ?: "Ingliz tilini o'rganish"
                    val age = data["age"] as? String ?: ""
                    val language = data["language"] as? String ?: ""
                    val experience = data["experience"] as? String ?: ""
                    val learningMethod = data["learningMethod"] as? String ?: ""
                    val duration = data["duration"] as? String ?: ""
                    val skill = data["skill"] as? String ?: ""

                    Log.d(TAG, "👤 userName=$userName, level=$level, goal=$goal")
                    Log.d(TAG, "📊 age=$age, language=$language, experience=$experience")
                    Log.d(TAG, "📊 method=$learningMethod, duration=$duration, skill=$skill")

                    val prompt = """
                        Foydalanuvchi haqida ma'lumotlar:
                        Ismi: $userName
                        Yoshi: $age
                        Darajasi: $level
                        Maqsadi: $goal
                        Til: $language
                        Tajriba: $experience
                        O'rganish usuli: $learningMethod
                        Kundalik vaqt: $duration
                        Ko'nikma: $skill

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

                    Log.d(TAG, "📝 Prompt uzunligi: ${prompt.length} belgi")
                    makeGeminiRequest(prompt)
                } else {
                    Log.e(TAG, "❌ Firestore'da hujjat topilmadi")
                    isLoading = false
                    isRequestInProgress = false
                    errorMessage = "Foydalanuvchi ma'lumotlari topilmadi"
                }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "❌ Firestore xatolik: ${e.message}", e)
                isLoading = false
                isRequestInProgress = false
                errorMessage = "Foydalanuvchi ma'lumotlarini olishda xatolik: ${e.message}"
            }
    }

    private fun makeGeminiRequest(prompt: String) {
        viewModelScope.launch {
            try {
                val apiKey = com.example.langmap.BuildConfig.GEMINI_API_KEY
                val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$apiKey"

                Log.d(TAG, "🔑 API kalit: ${apiKey.take(10)}...${apiKey.takeLast(4)}")
                Log.d(TAG, "🌐 URL: $url")
                Log.d(TAG, "📤 Gemini API'ga so'rov jo'natilmoqda... (retry: $currentRetry)")

                val requestBody = mapOf(
                    "contents" to listOf(
                        mapOf(
                            "parts" to listOf(
                                mapOf(
                                    "text" to "Siz ingliz tili o'qituvchisiz. Foydalanuvchilarga ingliz tilini o'rganish bo'yicha foydali va shaxsiylashtirilgan tavsiyalar berasiz.\n\n$prompt"
                                )
                            )
                        )
                    ),
                    "generationConfig" to mapOf(
                        "temperature" to 0.7,
                        "maxOutputTokens" to 8192,
                        "thinkingConfig" to mapOf(
                            "thinkingBudget" to 1024
                        )
                    )
                )

                val jsonBody = Gson().toJson(requestBody)
                Log.d(TAG, "📦 Request body uzunligi: ${jsonBody.length} belgi")

                val mediaType = "application/json".toMediaType()
                val body = jsonBody.toRequestBody(mediaType)

                val request = Request.Builder()
                    .url(url)
                    .addHeader("Content-Type", "application/json")
                    .post(body)
                    .build()

                val response = withContext(Dispatchers.IO) {
                    Log.d(TAG, "⏳ HTTP so'rov yuborilmoqda...")
                    client.newCall(request).execute()
                }

                isRequestInProgress = false
                Log.d(TAG, "📥 HTTP javob kodi: ${response.code}")

                if (response.code == 400) {
                    Log.e(TAG, "❌ 400 Bad Request")
                    isLoading = false
                    errorMessage = "Noto'g'ri so'rov formati. API kalitni tekshiring."
                    return@launch
                }

                if (response.code == 403) {
                    Log.e(TAG, "❌ 403 Forbidden — API kaliti yaroqsiz")
                    isLoading = false
                    errorMessage = "API kaliti yaroqsiz yoki Gemini API yoqilmagan."
                    return@launch
                }

                if (response.code == 429) {
                    val errorBody = response.body?.string() ?: ""
                    Log.e(TAG, "❌ 429 Rate Limit: $errorBody")
                    isLoading = false
                    errorMessage = "So'rovlar soni chegaradan oshdi. 30 soniyadan keyin qayta urinib ko'ring."
                    lastRequestTime = System.currentTimeMillis() + 30_000
                    return@launch
                }

                if (response.code !in 200..299) {
                    val errorBody = response.body?.string() ?: ""
                    Log.e(TAG, "❌ Server xatosi ${response.code}: $errorBody")
                    isLoading = false
                    errorMessage = "Server xatosi: ${response.code}"
                    return@launch
                }

                val responseBody = response.body?.string()
                Log.d(TAG, "✅ Response body uzunligi: ${responseBody?.length ?: 0} belgi")
                Log.d(TAG, "📄 Raw response (birinchi 500 belgi): ${responseBody?.take(500)}")

                if (responseBody != null) {
                    val json = Gson().fromJson(responseBody, Map::class.java) as? Map<*, *>
                    Log.d(TAG, "📊 JSON top-level keys: ${json?.keys}")

                    val candidates = json?.get("candidates") as? List<*>
                    Log.d(TAG, "📊 candidates count: ${candidates?.size}")

                    val firstCandidate = candidates?.firstOrNull() as? Map<*, *>
                    Log.d(TAG, "📊 firstCandidate keys: ${firstCandidate?.keys}")

                    val content = firstCandidate?.get("content") as? Map<*, *>
                    Log.d(TAG, "📊 content keys: ${content?.keys}")

                    val parts = content?.get("parts") as? List<*>
                    Log.d(TAG, "📊 parts count: ${parts?.size}")

                    val firstPart = parts?.firstOrNull() as? Map<*, *>
                    val text = firstPart?.get("text") as? String

                    Log.d(TAG, "📊 text uzunligi: ${text?.length ?: 0}")
                    Log.d(TAG, "📄 text (birinchi 200 belgi): ${text?.take(200)}")

                    if (text != null) {
                        Log.d(TAG, "✅✅✅ TAVSIYA MUVAFFAQIYATLI OLINDI!")
                        isLoading = false
                        recommendation = text.trim()
                    } else {
                        Log.e(TAG, "❌ text = null — javob formati noto'g'ri")
                        Log.e(TAG, "📄 Full response: $responseBody")
                        isLoading = false
                        errorMessage = "Noto'g'ri javob formati"
                    }
                } else {
                    Log.e(TAG, "❌ Response body = null")
                    isLoading = false
                    errorMessage = "Ma'lumot olinmadi"
                }
            } catch (e: Exception) {
                isRequestInProgress = false
                Log.e(TAG, "❌ Exception: ${e.javaClass.simpleName}: ${e.message}", e)

                if (currentRetry < maxRetries) {
                    currentRetry++
                    Log.d(TAG, "🔄 Retry #$currentRetry / $maxRetries — ${currentRetry * 5}s kutilmoqda")
                    kotlinx.coroutines.delay(currentRetry * 5_000L)
                    makeGeminiRequest(prompt)
                } else {
                    Log.e(TAG, "❌ Barcha retrylar tugadi")
                    isLoading = false
                    errorMessage = "Tarmoq ulanishi uzildi. Internet ulanishingizni tekshiring."
                }
            }
        }
    }
}
