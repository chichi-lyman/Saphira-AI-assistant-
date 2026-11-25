package com.example.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/** Request and response shapes for the Gemini generateContent REST API. */

@JsonClass(generateAdapter = true)
data class GeminiRequest(
  @Json(name = "system_instruction") val systemInstruction: GeminiContent? = null,
  @Json(name = "contents") val contents: List<GeminiContent>,
)

@JsonClass(generateAdapter = true)
data class GeminiContent(
  @Json(name = "role") val role: String? = null,
  @Json(name = "parts") val parts: List<GeminiPart>,
)

@JsonClass(generateAdapter = true)
data class GeminiPart(@Json(name = "text") val text: String)

@JsonClass(generateAdapter = true)
data class GeminiResponse(
  @Json(name = "candidates") val candidates: List<GeminiCandidate>? = null
)

@JsonClass(generateAdapter = true)
data class GeminiCandidate(@Json(name = "content") val content: GeminiContent? = null)
