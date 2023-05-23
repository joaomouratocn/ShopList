package com.example.shoppinglist.data.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.shoppinglist.util.getDate

@Entity
data class ShopListEntity(
    @PrimaryKey(autoGenerate = true)
    val shopId:Long = 0L,
    val shopName:String,
    val shopDate:String = getDate()
)
