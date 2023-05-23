package com.example.shoppinglist.data.room.appDatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.shoppinglist.data.room.dao.ItemShopListDao
import com.example.shoppinglist.data.room.dao.ProductDao
import com.example.shoppinglist.data.room.dao.ShopListDao
import com.example.shoppinglist.data.room.entity.ItemShopListEntity
import com.example.shoppinglist.data.room.entity.ProductEntity
import com.example.shoppinglist.data.room.entity.ShopListEntity

@Database(
    entities = [ItemShopListEntity::class, ProductEntity::class, ShopListEntity::class],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase(){
    abstract fun itemShopListDao():ItemShopListDao
    abstract fun productDao():ProductDao
    abstract fun shopListDao():ShopListDao

    companion object{
        @Volatile
        private var db : AppDatabase? = null

        fun getInstance(context: Context):AppDatabase{
            return db ?: Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                "shopList.db"
            )
                .createFromAsset("database/productsDb")
                .build().also { db = it }
        }
    }
}