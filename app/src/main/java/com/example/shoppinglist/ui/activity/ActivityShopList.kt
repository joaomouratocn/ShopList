package com.example.shoppinglist.ui.activity

import android.os.Bundle
import android.view.Menu
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shoppinglist.R
import com.example.shoppinglist.data.room.appDatabase.AppDatabase
import com.example.shoppinglist.data.room.entity.ShopListEntity
import com.example.shoppinglist.data.room.selectmodel.ShopListWithItems
import com.example.shoppinglist.databinding.ActivityShopListBinding
import com.example.shoppinglist.databinding.DialogLayoutRenameShopListBinding
import com.example.shoppinglist.ui.adapter.AdapterShopList
import com.example.shoppinglist.util.LIST_ID
import com.example.shoppinglist.util.createDialog
import com.example.shoppinglist.util.goToActivity
import kotlinx.coroutines.launch
import java.util.Locale


class ActivityShopList : AppCompatActivity(){
    private val binding by lazy { ActivityShopListBinding.inflate(layoutInflater) }
    private val shopListDao by lazy { AppDatabase.getInstance(this).shopListDao() }
    private val shopListAdapter by lazy { initAdapter() }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        configureRecycle()
        configureFab()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_shops_list_act, menu)

        val menuItem = menu?.findItem(R.id.menu_search_shop_list)
        val searchView: SearchView = menuItem?.actionView as SearchView

        searchView.apply{
            imeOptions = EditorInfo.IME_ACTION_DONE
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    lifecycleScope.launch {
                        shopListDao.getShopListByName("%$newText%").collect{
                            loadAdapter(it)
                        }
                    }
                    return true
                }
            })
        }
        return super.onCreateOptionsMenu(menu)
    }

    private fun configureFab() {
        binding.fabAddShopList.setOnClickListener {
            createNewShopListDialog()
        }
    }

    private fun initAdapter():AdapterShopList{
        return AdapterShopList(object : AdapterShopList.OnClickListener {
            override fun onItemClick(shopListWithItems: ShopListWithItems) {
                goToActivity(ActivityBuyShopList::class.java){
                    putExtra(LIST_ID, shopListWithItems.shopList.shopId)
                }
            }

            override fun shopListEdit(shopListWithItems: ShopListWithItems) {
                goToActivity(ActivityNewAndEditShopList::class.java){
                    putExtra(LIST_ID, shopListWithItems.shopList.shopId)
                }
            }

            override fun shopListDelete(shopListWithItems: ShopListWithItems) {
                createDialog(
                    this@ActivityShopList,
                    title = getString(R.string.str_delete_shop_list),
                    message = getString(R.string.str_really_wish_delete, shopListWithItems.shopList.shopName),
                    negativeButtonText = getString(R.string.str_cancel),
                    positiveButtonText = getString(R.string.str_yes)
                ){
                    lifecycleScope.launch {
                        shopListDao.deleteShopList(shopListWithItems.shopList)
                    }
                }.show()
            }
        })
    }

    private fun configureRecycle() {
        binding.rcvShopList.apply {
            adapter = shopListAdapter
            layoutManager = LinearLayoutManager(this@ActivityShopList)
        }
        lifecycleScope.launch {
            shopListDao.getAllShopListWithItems().collect{
                loadAdapter(it)
            }
        }
    }

    private fun loadAdapter(shopListWithItemsList: List<ShopListWithItems>?) {
        if(shopListWithItemsList.isNullOrEmpty()){
            binding.txvEmptyList.visibility = View.VISIBLE
            shopListAdapter.updateList(emptyList())
        }else{
            binding.txvEmptyList.visibility = View.GONE
            shopListAdapter.updateList(shopListWithItemsList)
        }
    }

    private fun createNewShopListDialog() {
        val viewDialog = DialogLayoutRenameShopListBinding.inflate(layoutInflater)

        val dialog = createDialog(this,viewDialog)
        with(viewDialog){
            btnCancel.setOnClickListener { dialog.dismiss() }

            btnSave.setOnClickListener {
                if (edtShopListName.text?.isEmpty() == true) {
                    edtShopListName.error = getString(R.string.str_invalid_field)
                } else {
                    val name = edtShopListName.text.toString()
                        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }

                    lifecycleScope.launch {
                        val newShopListId = shopListDao.insertShopList(ShopListEntity(shopName = name))
                        goToActivity(ActivityNewAndEditShopList::class.java){
                            putExtra(LIST_ID, newShopListId)
                        }
                    }
                    dialog.dismiss()
                }
            }
        }
        dialog.show()
    }
}