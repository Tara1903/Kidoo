package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.WishlistDao
import com.example.data.local.entity.WishlistItemEntity

@Database(
  entities = [WishlistItemEntity::class],
  version = 1,
  exportSchema = false
)
abstract class KidooDatabase : RoomDatabase() {

  abstract fun wishlistDao(): WishlistDao

  companion object {
    @Volatile
    private var INSTANCE: KidooDatabase? = null

    fun getDatabase(context: Context): KidooDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          KidooDatabase::class.java,
          "kidoo_database"
        )
          .fallbackToDestructiveMigration()
          .build()
        INSTANCE = instance
        instance
      }
    }
  }
}
