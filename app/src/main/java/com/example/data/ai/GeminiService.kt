package com.example.data.ai

import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GeminiService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    fun hasValidApiKey(userKey: String? = null): Boolean {
        if (!userKey.isNullOrBlank() && userKey != "MY_GEMINI_API_KEY") return true
        val configKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (_: Exception) {
            ""
        }
        return configKey.isNotBlank() && configKey != "MY_GEMINI_API_KEY"
    }

    private fun getResolvedApiKey(userKey: String? = null): String {
        if (!userKey.isNullOrBlank() && userKey != "MY_GEMINI_API_KEY") return userKey
        return try {
            BuildConfig.GEMINI_API_KEY
        } catch (_: Exception) {
            ""
        }
    }

    suspend fun generateWithGemini(
        systemPrompt: String,
        userPrompt: String,
        userKey: String? = null
    ): Result<String> = withContext(Dispatchers.IO) {
        val apiKey = getResolvedApiKey(userKey)
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext Result.failure(IllegalStateException("No valid Gemini API key configured"))
        }

        try {
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"

            val jsonBody = JSONObject().apply {
                val contentsArray = JSONArray().apply {
                    val contentObj = JSONObject().apply {
                        val partsArray = JSONArray().apply {
                            val part = JSONObject().apply {
                                put("text", "$systemPrompt\n\nUser Request: $userPrompt")
                            }
                            put(part)
                        }
                        put("parts", partsArray)
                    }
                    put(contentObj)
                }
                put("contents", contentsArray)

                val genConfig = JSONObject().apply {
                    put("temperature", 0.8)
                    put("topP", 0.95)
                    put("topK", 40)
                }
                put("generationConfig", genConfig)
            }

            val requestBody = jsonBody.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            val bodyString = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                return@withContext Result.failure(Exception("Gemini API error ${response.code}: $bodyString"))
            }

            val responseJson = JSONObject(bodyString)
            val candidates = responseJson.optJSONArray("candidates")
            if (candidates != null && candidates.length() > 0) {
                val firstCandidate = candidates.getJSONObject(0)
                val content = firstCandidate.optJSONObject("content")
                val parts = content?.optJSONArray("parts")
                if (parts != null && parts.length() > 0) {
                    val text = parts.getJSONObject(0).optString("text")
                    return@withContext Result.success(text)
                }
            }

            Result.failure(Exception("No content returned from Gemini model"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
