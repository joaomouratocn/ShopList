package com.example.shoppinglist.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.shoppinglist.data.room.entity.ItemShopListEntity
import com.example.shoppinglist.databinding.RecycleEditItemsShopListBinding
import com.example.shoppinglist.ui.adapter.AdapterNewAndEditShopList.*
import java.util.Locale

class AdapterNewAndEditShopList(
    private val onItemClickListener: OnItemClickListener
) : RecyclerView.Adapter<NewAndEditShopListViewHolder>() {

    private val data:MutableList<ItemShopListEntity> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewAndEditShopListViewHolder {
        return NewAndEditShopListViewHolder.inflate(parent)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: NewAndEditShopListViewHolder, position: Int) {
        val itemShopListEntity = data[position]
        holder.bind(itemShopListEntity, onItemClickListener)
    }

    fun updateList(allItems : List<ItemShopListEntity>){
        data.clear()
        data.addAll(allItems)
        notifyDataSetChanged()
    }

    class NewAndEditShopListViewHolder(binding: RecycleEditItemsShopListBinding) : ViewHolder(binding.root){
        private val txvNameProduct = binding.txvNameItem
        private val edtAmount = binding.edtAmountItem
        private val edtDesc = binding.edtDescItem
        private val spnType = binding.spnType
        private val imgSave = binding.imgSave
        private val imgDelete = binding.imgDeleteItem

        companion object{
            fun inflate(viewGroup: ViewGroup) : NewAndEditShopListViewHolder{
                val binding = RecycleEditItemsShopListBinding.inflate(
                    LayoutInflater.from(viewGroup.context),
                    viewGroup,
                    false)
                return NewAndEditShopListViewHolder(binding)
            }
        }

        fun bind(itemShopListEntity: ItemShopListEntity, onItemClickListener: OnItemClickListener){
            txvNameProduct.text = itemShopListEntity.prodName
            edtAmount.setText(itemShopListEntity.amount.toString())
            edtDesc.setText(itemShopListEntity.description)
            spnType.setSelection(itemShopListEntity.type)
            imgDelete.setOnClickListener {
                onItemClickListener.onDeleteListener(itemShopListEntity)
            }

            imgSave.setOnClickListener {
                val editedItem = ItemShopListEntity(
                    shopId = itemShopListEntity.shopId,
                    prodId = itemShopListEntity.prodId,
                    prodName = txvNameProduct.text.toString(),
                    prodCategoryIndex = itemShopListEntity.prodCategoryIndex,
                    description = edtDesc.text.toString()
                        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() },
                    type = spnType.selectedItemPosition,
                    amount = edtAmount.text.toString().toInt(),
                    price = 0.0
                )

                onItemClickListener.onUpdateListener(editedItem)
                clearFocus()
            }
        }


        private fun clearFocus() {
            edtAmount.clearFocus()
            edtDesc.clearFocus()
        }
    }

    interface OnItemClickListener {
        fun onUpdateListener(itemShopListEntity: ItemShopListEntity)
        fun onDeleteListener(itemShopListEntity: ItemShopListEntity)
    }
}