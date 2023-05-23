package com.example.shoppinglist.ui.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import com.example.shoppinglist.R
import com.example.shoppinglist.data.room.appDatabase.AppDatabase
import com.example.shoppinglist.data.room.entity.ProductEntity
import com.example.shoppinglist.databinding.ActivityAddProductBinding
import com.example.shoppinglist.databinding.DialogLayoutOptProductBinding
import com.example.shoppinglist.databinding.DialogNewAndEditProductBinding
import com.example.shoppinglist.ui.adapter.AdapterEditProduct
import com.example.shoppinglist.ui.adapter.AdapterProduct
import com.example.shoppinglist.util.LIST_ID
import com.example.shoppinglist.util.createDialog
import com.example.shoppinglist.util.toItemShopListEntity
import kotlinx.coroutines.launch
import java.util.Locale

class ActivityAddProduct : AppCompatActivity() {
    private val binding by lazy { ActivityAddProductBinding.inflate(layoutInflater) }
    private val appDatabase by lazy { AppDatabase.getInstance(this) }
    private val productAdapter by lazy { initProductAdapter() }
    private val editProductAdapter by lazy { initEditProductAdapter() }
    private var editProductMode = false
    private var receivedId = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        configInit()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (editProductMode) {
            menuInflater.inflate(R.menu.menu_edit_product, menu)
        } else {
            menuInflater.inflate(R.menu.menu_add_product_act, menu)
            val menuItem = menu?.findItem(R.id.menu_search_add_product)
            val searchView: SearchView = menuItem?.actionView as SearchView

            searchView.apply {
                imeOptions = EditorInfo.IME_ACTION_DONE
                setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        return false
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        lifecycleScope.launch {
                            appDatabase.productDao().getProductByName("%$newText%").collect{
                                it?.let {
                                    loadAdapter(it)
                                }
                            }
                        }
                        return true
                    }
                })
            }
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_done_add -> {
                finish()
            }

            R.id.menu_add_new_product -> {
                editOrInsertProduct(null)
            }

            R.id.menu_edit_product -> {
                editProductMode = true
                changeAdapter()
                invalidateOptionsMenu()
            }

            R.id.menu_close -> {
                editProductMode = false
                invalidateOptionsMenu()
                changeAdapter()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun configInit(){
        title = getString(R.string.str_add_product)
        receivedId = intent.getLongExtra(LIST_ID, 0L)
        changeAdapter()
    }

    private fun initProductAdapter(): AdapterProduct {
        return AdapterProduct(object : AdapterProduct.OnClickListener {
            override fun onInsertProduct(productEntity: ProductEntity) {
                lifecycleScope.launch {
                    val itemShopListEntity = productEntity.toItemShopListEntity(
                        shopId = receivedId
                    )
                    appDatabase.itemShopListDao().insertItem(itemShopListEntity)
                }
            }

            override fun onDeleteProduct(productEntity: ProductEntity) {
                lifecycleScope.launch {
                    val itemShopListEntity = productEntity.toItemShopListEntity(
                        shopId = receivedId
                    )
                    appDatabase.itemShopListDao().deleteItem(itemShopListEntity)
                }
            }

            override fun onOptMenuClickListener(productEntity: ProductEntity) {
                showDialogOptionsMenu(productEntity)
            }
        })
    }

    private fun initEditProductAdapter(): AdapterEditProduct {
        return AdapterEditProduct { editOrInsertProduct(it) }
    }

    private fun changeAdapter() {
        if (editProductMode) {
            binding.recycleProducts.adapter = editProductAdapter
            loadAdapter(emptyList())
        } else {
            binding.recycleProducts.adapter = productAdapter
            lifecycleScope.launch {
                appDatabase.productDao().getAllProducts().collect{
                    it?.let { loadAdapter(it) }
                }
            }
        }
    }

    fun loadAdapter(allProducts:List<ProductEntity>){
        if (editProductMode){
            lifecycleScope.launch {
                appDatabase.productDao().getAllProducts().collect{
                    it?.let {
                        editProductAdapter.updateAdapter(it)
                    }
                }
            }
        }else{
            lifecycleScope.launch {
                appDatabase.shopListDao().getShopListById(receivedId).collect {shopListWithItems ->
                    shopListWithItems?.let {
                        productAdapter.updateAdapter(
                            allProducts = allProducts,
                            productsAlreadyInsert = shopListWithItems.items.map { it.prodId }
                        )
                    }
                }
            }
        }
    }

    private fun showDialogOptionsMenu(productEntity: ProductEntity) {
        val viewDialog =
            DialogLayoutOptProductBinding.inflate(layoutInflater)
        val dialog = createDialog(this, viewDialog)

        with(viewDialog) {
            txvNameProduct.text = productEntity.prodName
            edtAmount.setText("1")
            btnCancel.setOnClickListener {
                dialog.dismiss()
            }

            btnSave.setOnClickListener {
                if (edtAmount.text.toString().isEmpty()) {
                    edtAmount.error = binding.root.context.getString(R.string.str_invalid_field)
                } else {
                    val amount = edtAmount.text.toString().toInt()
                    val type = spnType.selectedItemPosition
                    val desc = edtDesc.text.toString()
                        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }

                    val itemShopListEntity = productEntity.toItemShopListEntity(
                        shopId = receivedId,
                        amount = amount,
                        type = type,
                        desc = desc
                    )

                    lifecycleScope.launch { appDatabase.itemShopListDao().insertItem(itemShopListEntity) }

                    dialog.dismiss()
                }
            }
        }
        dialog.show()
    }

    private fun editOrInsertProduct(productEntity: ProductEntity?) {
        val viewDialog = DialogNewAndEditProductBinding.inflate(layoutInflater)
        val dialog = createDialog(this, viewDialog)

        with(viewDialog) {
            productEntity?.let {
                edtNameProduct.setText(it.prodName)
                spnCategories.setSelection(it.prodCategoryIndex)
                txvTitle.text = getString(R.string.str_edit_product)
            }

            btnCancel.setOnClickListener {
                dialog.dismiss()
            }

            btnSave.setOnClickListener {
                if (edtNameProduct.text.toString().isEmpty()) {
                    edtNameProduct.error = getString(R.string.str_invalid_field)
                } else {
                    lifecycleScope.launch {
                        val productEntityForInsert = ProductEntity(
                            prodId = productEntity?.prodId ?: 0L,
                            prodName = edtNameProduct.text.toString().replaceFirstChar {
                                if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString()
                            },
                            prodCategoryIndex = spnCategories.selectedItemPosition
                        )
                        appDatabase.productDao().insertProduct(productEntityForInsert)
                    }
                }
                dialog.dismiss()
            }

            spnCategories.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    txvSpnError.visibility = View.GONE
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    txvSpnError.visibility = View.VISIBLE
                }

            }

        }
        dialog.show()
    }
}