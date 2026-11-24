package com.example

import android.app.Application
import com.google.firebase.Firebase
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.initialize

class SaphiraApp : Application() {
  override fun onCreate() {
    super.onCreate()
    // Required for Firebase AI Logic + App Check enforcement (debug builds).
    Firebase.initialize(this)
    FirebaseAppCheck.getInstance().installAppCheckProviderFactory(
      DebugAppCheckProviderFactory.getInstance()
    )
  }
}
