package com.example.shoppinglist.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.shoppinglist.data.room.entity.ProductEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(productEntity: ProductEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllProducts(productEntityList: List<ProductEntity>)

    @Query("SELECT * FROM ProductEntity ORDER BY prodCategoryIndex ASC")
    fun getAllProducts():Flow<List<ProductEntity>?>

    @Query("SELECT * FROM ProductEntity WHERE prodName LIKE :prodName")
    fun getProductByName(prodName:String):Flow<List<ProductEntity>?>
}