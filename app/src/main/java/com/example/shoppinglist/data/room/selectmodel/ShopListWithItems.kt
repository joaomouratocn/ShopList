package com.example.shoppinglist.data.room.selectmodel

import androidx.room.Embedded
import androidx.room.Relation
import com.example.shoppinglist.data.room.entity.ItemShopListEntity
import com.example.shoppinglist.data.room.entity.ShopListEntity

data class ShopListWithItems(
    @Embedded val shopList:ShopListEntity,
    @Relation(
        parentColumn = "shopId",
        entityColumn = "shopId"
    )val items:List<ItemShopListEntity>
)
