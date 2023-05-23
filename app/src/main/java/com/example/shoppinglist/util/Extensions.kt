package com.example.shoppinglist.util

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import androidx.viewbinding.ViewBinding
import com.example.shoppinglist.data.room.entity.ItemShopListEntity
import com.example.shoppinglist.data.room.entity.ProductEntity
import java.text.NumberFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

fun Context.goToActivity(
    clazz: Class<*>,
    intent: Intent.() -> Unit = {}
) {
    Intent(this, clazz)
        .apply {
            intent()
            startActivity(this)
        }
}

fun Double.toCoinString():String{
    val currencyCoinFormat = NumberFormat.getCurrencyInstance(Locale.getDefault())
    return currencyCoinFormat.format(this)
}

fun ProductEntity.toItemShopListEntity(shopId:Long, amount: Int = 1, type: Int = 0, desc: String = "", price:Double = 0.0 ):ItemShopListEntity{
    return ItemShopListEntity(
        shopId = shopId,
        prodId = prodId,
        prodName = prodName,
        prodCategoryIndex = prodCategoryIndex,
        amount = amount,
        type = type,
        price = price,
        description = desc
    )
}

//---------- FUNÇÕES UTEIS ----------//

fun createDialog(
    context: Context,
    viewBinding: ViewBinding? = null,
    title: String? = null,
    message: String? = null,
    negativeButtonText: String? = null,
    positiveButtonText: String? = null,
    actionPositive: (() -> Unit)? = null
): AlertDialog {
    return if(viewBinding != null){
        AlertDialog.Builder(context).let {
            it.setView(viewBinding.root)
            it.create()
        }
    }else{
        AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setCancelable(false)
            .setNegativeButton(negativeButtonText, null)
            .setPositiveButton(positiveButtonText){_,_ ->
                actionPositive?.invoke()
            }
            .create()
    }
}

fun getDate():String{
    return LocalDate.now().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT))
}