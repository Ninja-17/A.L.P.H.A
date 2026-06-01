package com.alpha.assistant.alpha

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

data class AlphaConfig(
    val apiKey: String,
    val model: String = "gemini-3.5-flash",
    val systemPrompt: String = PromptBuilder.defaultSystemPrompt
)

data class GeminiRequest(
    val contents: List<Content>,
    val systemInstruction: SystemInstruction? = null
)

data class SystemInstruction(
    val parts: List<Part>
)

data class Content(
    val role: String,
    val parts: List<Part>
)

data class Part(
    val text: String? = null,
    val inlineData: InlineData? = null
)

data class InlineData(
    val mimeType: String,
    val data: String
)

data class GeminiResponse(
    val candidates: List<Candidate>? = null
)

data class Candidate(
    val content: Content? = null,
    @SerializedName("finishReason")
    val finishReason: String? = null
)

class AlphaClient(private val config: AlphaConfig) {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val gson = Gson()
    private val jsonMediaType = "application/json".toMediaType()

    suspend fun sendMessage(
        message: String,
        context: String = "",
        screenshotBase64: String? = null
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val parts = mutableListOf<Part>()

            if (context.isNotBlank()) {
                parts.add(Part(text = "[Contexto de pantalla]\n$context"))
            }

            parts.add(Part(text = message))

            screenshotBase64?.let {
                parts.add(Part(inlineData = InlineData(mimeType = "image/jpeg", data = it)))
            }

            val requestBody = GeminiRequest(
                contents = listOf(Content(role = "user", parts = parts)),
                systemInstruction = SystemInstruction(
                    parts = listOf(Part(text = config.systemPrompt))
                )
            )

            val json = gson.toJson(requestBody)
            val body = json.toRequestBody(jsonMediaType)

            val request = Request.Builder()
                .url("https://generativelanguage.googleapis.com/v1beta/models/${config.model}:generateContent?key=${config.apiKey}")
                .post(body)
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()

            if (!response.isSuccessful) {
                return@withContext Result.failure(
                    Exception("Gemini API error ${response.code}: $responseBody")
                )
            }

            val geminiResponse = gson.fromJson(responseBody, GeminiResponse::class.java)
            val text = geminiResponse.candidates
                ?.firstOrNull()
                ?.content
                ?.parts
                ?.firstOrNull()
                ?.text

            if (text != null) {
                Result.success(text)
            } else {
                Result.failure(Exception("Respuesta vacía de Gemini"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
