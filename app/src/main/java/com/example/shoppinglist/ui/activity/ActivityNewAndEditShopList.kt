package com.example.shoppinglist.ui.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shoppinglist.R
import com.example.shoppinglist.data.room.appDatabase.AppDatabase
import com.example.shoppinglist.data.room.entity.ItemShopListEntity
import com.example.shoppinglist.data.room.selectmodel.ShopListWithItems
import com.example.shoppinglist.databinding.ActivityNewAndEditShopListBinding
import com.example.shoppinglist.databinding.DialogLayoutRenameShopListBinding
import com.example.shoppinglist.databinding.DialogSuccessBinding
import com.example.shoppinglist.ui.adapter.AdapterNewAndEditShopList
import com.example.shoppinglist.util.LIST_ID
import com.example.shoppinglist.util.createDialog
import com.example.shoppinglist.util.goToActivity
import kotlinx.coroutines.launch
import java.util.Locale

class ActivityNewAndEditShopList : AppCompatActivity(){
    private val binding by lazy { ActivityNewAndEditShopListBinding.inflate(layoutInflater) }
    private val appDatabase by lazy { AppDatabase.getInstance(this) }
    private val newAndEditAdapter by lazy { initNewAndEditAdapter() }
    private lateinit var shopListWithItems: ShopListWithItems

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initShopListReceived()
        configureRecycle()
        configureFab()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_edit_shop_list_act, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_rename_shop_list ->{
                changeShopListName()
            }
            R.id.menu_done_edit ->{
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initShopListReceived() {
        lifecycleScope.launch {
            val receivedId = intent.getLongExtra(LIST_ID, 0L)
            appDatabase.shopListDao().getShopListById(receivedId).collect {
                it?.let {
                    shopListWithItems = it
                    title = shopListWithItems.shopList.shopName
                    if (shopListWithItems.items.isEmpty()){
                        goToActivity(ActivityAddProduct::class.java){
                            putExtra(LIST_ID, shopListWithItems.shopList.shopId)
                        }
                    }else{
                        updateAdapter()
                    }
                }
            }
        }
    }

    private fun updateAdapter() {
        if (shopListWithItems.items.isEmpty()){
            binding.txvEmptyList.visibility = View.VISIBLE
            newAndEditAdapter.updateList(emptyList())
        }else{
            binding.txvEmptyList.visibility = View.GONE
            newAndEditAdapter.updateList(shopListWithItems.items)
        }
    }


    private fun initNewAndEditAdapter():AdapterNewAndEditShopList{
        return AdapterNewAndEditShopList(object : AdapterNewAndEditShopList.OnItemClickListener {
            override fun onUpdateListener(itemShopListEntity: ItemShopListEntity) {
                lifecycleScope.launch {
                    appDatabase.itemShopListDao().insertItem(itemShopListEntity)
                    showSuccessDialog()
                }
            }

            override fun onDeleteListener(itemShopListEntity: ItemShopListEntity) {
                createDialog(
                    this@ActivityNewAndEditShopList,
                    title = getString(R.string.str_delete_item_list),
                    message = getString(
                        R.string.str_really_wish_delete,
                        itemShopListEntity.prodName
                    ),
                    negativeButtonText = getString(R.string.str_cancel),
                    positiveButtonText = getString(R.string.str_delete), actionPositive = {
                        lifecycleScope.launch {
                            appDatabase.itemShopListDao().deleteItem(itemShopListEntity)
                        }
                    }
                ).show()
            }
        })
    }

    private fun configureRecycle() {
        binding.rcvItemList.apply {
            adapter = newAndEditAdapter
            layoutManager = LinearLayoutManager(this@ActivityNewAndEditShopList)
        }
    }

    private fun configureFab() {
        binding.fabAddItemShopList.setOnClickListener {
            goToActivity(ActivityAddProduct::class.java){
                putExtra(LIST_ID, shopListWithItems.shopList.shopId)
            }
        }
    }

    private fun showSuccessDialog() {
        val viewDialog = DialogSuccessBinding.inflate(layoutInflater)
        val dialog = createDialog(this, viewDialog)
        with(viewDialog){
            btnClose.setOnClickListener { dialog.dismiss() }
        }
        dialog.show()
    }

    private fun changeShopListName() {
        val viewDialog = DialogLayoutRenameShopListBinding.inflate(layoutInflater)
        val dialog = createDialog(this, viewDialog)
        with(viewDialog){
            txvTitle.text = getString(R.string.str_rename)
            edtShopListName.setText(shopListWithItems.shopList.shopName)
            btnCancel.setOnClickListener { dialog.dismiss() }
            btnSave.setOnClickListener {
                val newName = edtShopListName.text.toString().replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString()}

                if (newName.isNotEmpty()){
                    lifecycleScope.launch {
                        val shopListCopy = shopListWithItems.shopList.copy(shopName = newName)
                        appDatabase.shopListDao().updateShopList(shopListCopy)
                        showSuccessDialog()
                        dialog.dismiss()
                    }
                }else {
                    edtShopListName.error = getString(R.string.str_invalid_field)
                }
            }
        }
        dialog.show()
    }
}