package com.example

import android.app.Application
import com.example.data.local.KidooDatabase
import com.example.data.repository.KidooRepository

class KidooApplication : Application() {

  val database: KidooDatabase by lazy { KidooDatabase.getDatabase(this) }

  override fun onCreate() {
    super.onCreate()
    try {
      KidooRepository.init(database.wishlistDao())
    } catch (e: Exception) {
      android.util.Log.e("KidooApplication", "Failed to initialize database", e)
    }
  }
}
