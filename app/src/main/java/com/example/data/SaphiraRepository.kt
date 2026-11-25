package com.example.data

import com.example.SaphiraConfig
import com.squareup.moshi.Moshi
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

/** One turn of the conversation, in the app's own words. */
data class ConversationTurn(val fromUser: Boolean, val text: String)

sealed interface SaphiraResult {
  data class Answered(val text: String) : SaphiraResult

  data class Failed(val message: String) : SaphiraResult
}

/**
 * Saphira's brain: sends the whole conversation to Gemini and brings back her
 * reply. No canned answers live here — if Gemini can't be reached or says no,
 * the result is an honest failure and the screen shows it.
 */
class SaphiraRepository(private val apiKey: String) {

  private val api: GeminiApi by lazy {
    val client =
      OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()
    val moshi = Moshi.Builder().build()
    Retrofit.Builder()
      .baseUrl("https://generativelanguage.googleapis.com/")
      .client(client)
      .addConverterFactory(MoshiConverterFactory.create(moshi))
      .build()
      .create(GeminiApi::class.java)
  }

  suspend fun ask(history: List<ConversationTurn>): SaphiraResult =
    withContext(Dispatchers.IO) {
      val request =
        GeminiRequest(
          systemInstruction =
            GeminiContent(parts = listOf(GeminiPart(SaphiraConfig.SYSTEM_INSTRUCTION))),
          contents =
            history.map { turn ->
              GeminiContent(
                role = if (turn.fromUser) "user" else "model",
                parts = listOf(GeminiPart(turn.text)),
              )
            },
        )
      try {
        val response = api.generateContent(SaphiraConfig.GEMINI_MODEL, apiKey, request)
        val text =
          response.candidates
            ?.firstOrNull()
            ?.content
            ?.parts
            ?.joinToString("") { it.text }
            ?.trim()
        if (text.isNullOrEmpty()) {
          SaphiraResult.Failed(
            "Saphira didn't get an answer back that time. Try sending that again."
          )
        } else {
          SaphiraResult.Answered(text)
        }
      } catch (e: CancellationException) {
        throw e
      } catch (e: HttpException) {
        SaphiraResult.Failed(
          when (e.code()) {
            400,
            401,
            403 ->
              "Gemini wouldn't accept that — the API key may not be working. " +
                "Check the key in the .env file and try again."
            429 -> "Gemini says too many messages too fast. Give it a minute and try again."
            else ->
              "Saphira couldn't get an answer just now (Gemini said ${e.code()}). " +
                "Try again in a bit."
          }
        )
      } catch (e: IOException) {
        SaphiraResult.Failed(
          "Saphira couldn't reach Gemini — the connection didn't go through. " +
            "Check your internet and try again."
        )
      } catch (e: Exception) {
        SaphiraResult.Failed(
          "Something went wrong while Saphira was thinking. Try sending that again."
        )
      }
    }
}
