package com.example.shoppinglist.data.room.entity

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(primaryKeys = ["shopId","prodId"],
    foreignKeys =
    [ForeignKey(
        entity = ProductEntity::class,
        parentColumns = ["prodId"],
        childColumns = ["prodId"]
    ),
    ForeignKey(
        entity = ShopListEntity::class,
        parentColumns = ["shopId"],
        childColumns = ["shopId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class ItemShopListEntity(
    val shopId:Long,
    val prodId:Long,
    val prodName: String,
    val prodCategoryIndex: Int,
    val amount: Int,
    val price: Double,
    val description: String,
    val type: Int,
    val isOk: Boolean = false,
)
