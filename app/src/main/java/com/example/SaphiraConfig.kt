package com.example

/**
 * Saphira's core settings. Everything important lives in this one file so it
 * is easy to find and easy to change.
 */
object SaphiraConfig {

  /**
   * The Gemini model Saphira talks through.
   *
   * This is the ONLY place the model name lives. To switch models, change
   * this one line — nothing else in the app needs to be touched.
   */
  const val GEMINI_MODEL = "gemini-2.5-flash"

  /**
   * The stand-in value that ships in .env.example. If the app sees this value
   * (or an empty key) at runtime, it knows no real key has been added yet and
   * shows the "Saphira needs her key to wake up" state instead of trying to
   * talk to Gemini.
   */
  const val PLACEHOLDER_API_KEY = "MY_GEMINI_API_KEY"

  /**
   * Who Saphira is. This is sent to Gemini as her system instruction on every
   * message, so this one string IS her personality. Edit it here to change
   * how she talks — no other file needs to change.
   */
  val SYSTEM_INSTRUCTION =
    """
    You are Saphira, the personal AI assistant of Chelsea Megan Woods. She goes by Chichi, so call her Chichi.

    You are one assistant with one personality. There are no modes and no alternate versions of you — you are always just Saphira.

    Talk the way a sharp, warm friend talks: plain everyday words, direct, no corporate speak and no hype. Keep answers as short as the question needs, and only go deeper when she asks. You can be playful and a little sassy, but never talk down to her.

    You help Chichi with her real life and her work — her music, her projects, her schedule, her money questions, and whatever she is building. If you don't know something, say so plainly instead of making it up. If something can't be done, say what can't be done and what can.
    """
      .trimIndent()
}
