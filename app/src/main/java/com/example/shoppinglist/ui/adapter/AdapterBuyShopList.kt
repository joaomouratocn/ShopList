package com.example.shoppinglist.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.shoppinglist.R
import com.example.shoppinglist.data.room.entity.ItemShopListEntity
import com.example.shoppinglist.databinding.RecycleBuyShopListBinding
import com.example.shoppinglist.ui.adapter.AdapterBuyShopList.*
import com.example.shoppinglist.util.toCoinString

class AdapterBuyShopList(private val onItemClickListener:OnItemClickListener) : RecyclerView.Adapter<BuyShopListViewHolder>() {
    private val data:MutableList<ItemShopListEntity> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BuyShopListViewHolder {
        return BuyShopListViewHolder.inflate(parent)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: BuyShopListViewHolder, position: Int) {
        val itemShopListEntity = data[position]
        holder.bind(itemShopListEntity, onItemClickListener)
    }

    fun updateList(allItems: List<ItemShopListEntity>) {
        data.clear()
        data.addAll(allItems)
        notifyDataSetChanged()
    }

    class BuyShopListViewHolder(private val binding: RecycleBuyShopListBinding) : ViewHolder(binding.root){
        private val imgCheck = binding.imgCheck
        private val nameItem = binding.txvNameItem
        private val amountItem = binding.txvAmountItems
        private val typeItem = binding.txvType
        private val priceItem = binding.txvPrice
        private val descItem = binding.txvDesc

        private val typeArray: Array<String> = binding.root.context.resources.getStringArray(R.array.types)
                
        companion object{
            fun inflate(viewGroup: ViewGroup):BuyShopListViewHolder{
                val binding = RecycleBuyShopListBinding.inflate(
                    LayoutInflater.from(viewGroup.context),
                    viewGroup,
                    false)
                return BuyShopListViewHolder(binding)
            }
        }

        fun bind(itemShopListEntity: ItemShopListEntity, onItemClickListener: OnItemClickListener){
            imgCheck.visibility = if (itemShopListEntity.isOk){View.VISIBLE}else{View.GONE}
            nameItem.text = itemShopListEntity.prodName
            amountItem.text = itemShopListEntity.amount.toString()
            typeItem.text = typeArray[itemShopListEntity.type]
            priceItem.text = itemShopListEntity.price.toCoinString()
            descItem.text = itemShopListEntity.description

            binding.root.setOnClickListener {
                if (itemShopListEntity.isOk){
                    onItemClickListener.unCheckItem(item = itemShopListEntity)
                }
                else{
                    onItemClickListener.checkItem(item = itemShopListEntity)
                }
            }
        }
    }
    interface OnItemClickListener{
        fun checkItem(item: ItemShopListEntity)
        fun unCheckItem(item: ItemShopListEntity)
    }
}