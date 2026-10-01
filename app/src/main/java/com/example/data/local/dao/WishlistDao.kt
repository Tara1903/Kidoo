package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.WishlistItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WishlistDao {

  @Query("SELECT * FROM wishlist_items ORDER BY addedAt DESC")
  fun getAllWishlistItems(): Flow<List<WishlistItemEntity>>

  @Query("SELECT productId FROM wishlist_items")
  fun getAllWishlistProductIds(): Flow<List<String>>

  @Query("SELECT COUNT(*) FROM wishlist_items")
  fun getWishlistCount(): Flow<Int>

  @Query("SELECT EXISTS(SELECT 1 FROM wishlist_items WHERE productId = :productId)")
  fun isProductWishlisted(productId: String): Flow<Boolean>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertWishlistItem(item: WishlistItemEntity)

  @Query("DELETE FROM wishlist_items WHERE productId = :productId")
  suspend fun deleteWishlistItemById(productId: String)

  @Delete
  suspend fun deleteWishlistItem(item: WishlistItemEntity)

  @Query("DELETE FROM wishlist_items")
  suspend fun clearWishlist()
}
