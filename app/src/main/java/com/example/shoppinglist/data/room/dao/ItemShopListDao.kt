package com.example.shoppinglist.data.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.example.shoppinglist.data.room.entity.ItemShopListEntity

@Dao
interface ItemShopListDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(itemShopListEntity: ItemShopListEntity)

    @Delete
    suspend fun deleteItem(itemShopListEntity: ItemShopListEntity)

}