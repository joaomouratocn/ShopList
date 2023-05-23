package com.example.shoppinglist.data.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.shoppinglist.data.room.entity.ShopListEntity
import com.example.shoppinglist.data.room.selectmodel.ShopListWithItems
import kotlinx.coroutines.flow.Flow

@Dao
interface ShopListDao {
    @Insert
    suspend fun insertShopList(shopListEntity: ShopListEntity):Long

    @Update
    suspend fun updateShopList(shopListEntity: ShopListEntity)

    @Delete
    suspend fun deleteShopList(shopListEntity: ShopListEntity)

    @Transaction
    @Query("SELECT * FROM ShopListEntity WHERE shopId = :shopId")
    fun getShopListById(shopId:Long):Flow<ShopListWithItems?>

    @Transaction
    @Query("SELECT * FROM ShopListEntity")
    fun getAllShopListWithItems():Flow<List<ShopListWithItems>?>

    @Transaction
    @Query("SELECT * FROM ShopListEntity WHERE shopName LIKE :nameShop")
    fun getShopListByName(nameShop:String):Flow<List<ShopListWithItems>?>
}