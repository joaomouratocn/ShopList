package com.example.shoppinglist.ui.adapter

import android.animation.ObjectAnimator
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppinglist.R
import com.example.shoppinglist.data.room.selectmodel.ShopListWithItems
import com.example.shoppinglist.databinding.RecycleShopListBinding


class AdapterShopList(private val onClickListener: OnClickListener)
    : RecyclerView.Adapter<AdapterShopList.ShopListViewHolder>() {
    private val data: MutableList<ShopListWithItems> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopListViewHolder {
        return ShopListViewHolder.inflateView(parent)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ShopListViewHolder, position: Int) {
        val shopListWithItems = data[position]
        holder.bind(shopListWithItems, onClickListener)
    }

    fun updateList(listShopList : List<ShopListWithItems>){
        data.clear()
        data.addAll(listShopList)
        notifyDataSetChanged()
    }

    class ShopListViewHolder(private val binding: RecycleShopListBinding) : RecyclerView.ViewHolder(binding.root){
        private val txvNameList = binding.txvNameList
        private val txvAmount = binding.txvAmountItems
        private val txvData = binding.txvDate
        private val imgMoreOpt = binding.imgMoreOpt
        private val prbAmountItems = binding.prbAmountItems

        companion object{
            fun inflateView(viewGroup: ViewGroup): ShopListViewHolder {
                val binding =  RecycleShopListBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
                return ShopListViewHolder(binding)
            }
        }

        fun bind(shopListWithItems: ShopListWithItems, onClickListener: OnClickListener) {
            binding.root.setOnClickListener { onClickListener.onItemClick(shopListWithItems) }
            txvNameList.text = shopListWithItems.shopList.shopName
            txvData.text = shopListWithItems.shopList.shopDate
            imgMoreOpt.setOnClickListener {
                setItemClickListener(it, onClickListener, shopListWithItems)
            }

            loadProgressBar(shopListWithItems)
        }

        private fun setItemClickListener(
            it: View,
            onClickListener: OnClickListener,
            shopListWithItems: ShopListWithItems
        ) {
            PopupMenu(it.context, it).apply {
                inflate(R.menu.menu_shop_list_item)

                setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.menu_item_edit_list -> {
                            onClickListener.shopListEdit(shopListWithItems)
                            true
                        }

                        R.id.menu_item_delete_list -> {
                            onClickListener.shopListDelete(shopListWithItems)
                            true
                        }

                        else -> false
                    }
                }
                show()
            }
        }

        private fun loadProgressBar(shopListWithItems: ShopListWithItems) {
            if (shopListWithItems.items.isNotEmpty()) {
                val amountItems = shopListWithItems.items.size
                val amountIsOk = shopListWithItems.items.filter { it.isOk }.size

                txvAmount.text = binding.root.context.getString(
                    R.string.str_amount_check,
                    amountIsOk,
                    amountItems
                )
                prbAmountItems.max = shopListWithItems.items.size
                ObjectAnimator.ofInt(prbAmountItems, "progress", amountIsOk)
                    .setDuration(0)
                    .start()
            }else{
                txvAmount.text = binding.root.context.getString(
                    R.string.str_amount_check, 0,0)
                prbAmountItems.max = shopListWithItems.items.size
                ObjectAnimator.ofInt(prbAmountItems, "progress", 0)
                    .setDuration(0)
                    .start()

            }

            if (prbAmountItems.progress == prbAmountItems.max) {
                prbAmountItems.progressTintList = ColorStateList.valueOf(Color.GREEN)
            } else {
                prbAmountItems.progressTintList =
                    ColorStateList.valueOf(binding.root.context.getColor(R.color.orange_800))
            }
        }
    }

    interface OnClickListener{
        fun onItemClick(shopListWithItems: ShopListWithItems)
        fun shopListEdit(shopListWithItems: ShopListWithItems)
        fun shopListDelete(shopListWithItems: ShopListWithItems)
    }
}
