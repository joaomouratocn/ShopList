package com.example.shoppinglist.ui.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shoppinglist.R
import com.example.shoppinglist.data.room.appDatabase.AppDatabase
import com.example.shoppinglist.data.room.entity.ItemShopListEntity
import com.example.shoppinglist.databinding.ActivityBuyShopListBinding
import com.example.shoppinglist.databinding.DialogLayoutGetPriceBinding
import com.example.shoppinglist.ui.adapter.AdapterBuyShopList
import com.example.shoppinglist.util.LIST_ID
import com.example.shoppinglist.util.SUM_VALUES
import com.example.shoppinglist.util.createDialog
import com.example.shoppinglist.util.dataStore
import com.example.shoppinglist.util.goToActivity
import com.example.shoppinglist.util.toCoinString
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ActivityBuyShopList : AppCompatActivity(){
    private val binding by lazy { ActivityBuyShopListBinding.inflate(layoutInflater) }
    private val appDatabase by lazy { AppDatabase.getInstance(this) }
    private val buyShopListAdapter by lazy { initAdapter() }
    private var receivedId:Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        informSumValues()
        initReceivedShopList()
        configureRecycle()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_buy_list_act, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        lifecycleScope.launch {
            menu?.let {menuItem ->
                val item = menuItem.findItem(R.id.menu_disable_sum_values)
                dataStore.data.collect{preferences ->
                    if ( preferences[SUM_VALUES] == true){
                        item.title = getString(R.string.str_disable_sum_values)
                    }else{
                        item.title = getString(R.string.str_enable_sum_values)
                        binding.constLayoutBottom.visibility = View.GONE
                    }
                }
            }
        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_done ->{
                finish()
            }
            R.id.menu_add_new_product ->{
                goToActivity(ActivityAddProduct::class.java){
                    putExtra(LIST_ID, receivedId)
                }
            }
            R.id.menu_disable_sum_values ->{
                alterSumValues()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun informSumValues() {
        lifecycleScope.launch {
            if (dataStore.data.first()[SUM_VALUES] == null) {
                createDialog(
                    context = this@ActivityBuyShopList,
                    title = getString(R.string.str_information),
                    message = getString(R.string.str_by_default_the_app_calculates_values_automatically_to_deactivate_use_the_menu_on_this_screen),
                    positiveButtonText = getString(R.string.str_ok),
                    actionPositive = {
                        alterSumValues()
                    }
                ).show()
            }
        }
    }

    private fun alterSumValues(){
        lifecycleScope.launch {
            dataStore.edit { preferences ->
                when (preferences[SUM_VALUES]) {
                    true -> {
                        preferences[SUM_VALUES] = false
                        binding.constLayoutBottom.visibility = View.GONE
                    }
                    false -> {
                        preferences[SUM_VALUES] = true
                        binding.constLayoutBottom.visibility = View.VISIBLE
                    }
                    else -> {
                        preferences[SUM_VALUES] = true
                        binding.constLayoutBottom.visibility = View.VISIBLE
                    }
                }
            }

        }
    }

    private fun initReceivedShopList() {
        lifecycleScope.launch {
            receivedId = intent.getLongExtra(LIST_ID, 0)
            appDatabase.shopListDao().getShopListById(receivedId).collect {
                it?.let {
                    title = it.shopList.shopName
                    if (it.items.isEmpty()){
                        goToActivity(ActivityAddProduct::class.java){
                            putExtra(LIST_ID, it.shopList.shopId)
                        }
                    }else{
                        val sumList = it.items.sumOf { item -> item.amount * item.price }
                        binding.txvPartialValue.text = sumList.toCoinString()
                        updateAdapter(it.items)
                    }
                }
            }
        }
    }

    private fun updateAdapter(items: List<ItemShopListEntity>) {
        if(items.isEmpty()){
            binding.txvEmptyList.visibility = View.VISIBLE
            buyShopListAdapter.updateList(items)
        }else{
            binding.txvEmptyList.visibility = View.GONE
            buyShopListAdapter.updateList(items)
        }
    }

    private fun initAdapter(): AdapterBuyShopList {
        return AdapterBuyShopList(object : AdapterBuyShopList.OnItemClickListener {
            override fun checkItem(item: ItemShopListEntity) {
                lifecycleScope.launch {
                    if(dataStore.data.first()[SUM_VALUES] == true){
                        showDialogGetPrice(item)
                    }else{
                        updateItem(item.copy(isOk = true))
                    }
                }
            }

            override fun unCheckItem(item: ItemShopListEntity) {
                val refactored = item.copy(isOk = false, price = 0.0)
                updateItem(refactored)
            }
        })
    }

    private fun updateItem(itemShopListEntityRefactored: ItemShopListEntity){
        lifecycleScope.launch {
            appDatabase.itemShopListDao().insertItem(itemShopListEntity = itemShopListEntityRefactored)
        }
    }

    private fun configureRecycle() {
        binding.recycleBuyProducts.apply {
            layoutManager = LinearLayoutManager(this@ActivityBuyShopList)
            adapter = this@ActivityBuyShopList.buyShopListAdapter
        }
    }

    private fun showDialogGetPrice(item: ItemShopListEntity){
        val viewDialog = DialogLayoutGetPriceBinding.inflate(layoutInflater)
        val dialog = createDialog(context = this, viewBinding = viewDialog)
        dialog.setCancelable(false)
        dialog.setTitle(getString(R.string.str_add_price))

        with(viewDialog){
            edtAmount.setText(item.amount.toString())
            edtPrice.setText(item.price.toString())

            btnAdd.setOnClickListener {
                if (edtAmount.text.isNullOrEmpty()){
                    edtAmount.error = getString(R.string.str_insert_amount_item)
                }else{
                    val amount = edtAmount.text.toString().toInt()
                    val price = edtPrice.text.toString().toDouble()
                    updateItem(item.copy(amount = amount, price = price, isOk = true))
                }
                dialog.dismiss()
            }

            btnCancel.setOnClickListener {
                dialog.dismiss()
            }
        }
        dialog.show()
    }
}