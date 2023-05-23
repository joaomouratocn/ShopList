package com.example.shoppinglist.data.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ProductEntity(
    @PrimaryKey(autoGenerate = true)
    val prodId:Long = 0L,
    val prodName:String,
    val prodCategoryIndex:Int
)