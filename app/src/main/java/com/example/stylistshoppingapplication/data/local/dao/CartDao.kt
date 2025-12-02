package com.example.stylistshoppingapplication.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.stylistshoppingapplication.data.local.model.CartEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {
    @Query("SELECT * FROM cart")
    fun getAllCartItems(): Flow<List<CartEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartItem(cartItem: CartEntity)

    @Update
    suspend fun updateCartItem(cartItem: CartEntity)

    @Query("DELETE FROM cart WHERE productId = :productId")
    suspend fun deleteCartItem(productId: Int)

    @Query("DELETE FROM cart")
    suspend fun clearCart()

    @Query("SELECT COUNT(*) FROM cart")
    suspend fun getCartItemCount(): Int
}