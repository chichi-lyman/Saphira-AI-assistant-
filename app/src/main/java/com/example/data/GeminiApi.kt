package com.example.data

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

/** The one Gemini endpoint Saphira uses: generateContent. */
interface GeminiApi {

  @POST("v1beta/models/{model}:generateContent")
  suspend fun generateContent(
    @Path("model") model: String,
    @Header("x-goog-api-key") apiKey: String,
    @Body request: GeminiRequest,
  ): GeminiResponse
}
