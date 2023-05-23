package com.example.shoppinglist.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.shoppinglist.data.room.entity.ProductEntity
import com.example.shoppinglist.databinding.RecycleAddProductToListBinding
import com.example.shoppinglist.ui.adapter.AdapterProduct.*

class AdapterProduct(private val onClickListener : OnClickListener) : RecyclerView.Adapter<ProductViewHolder>(){
    private var productsCheck : List<Long> = emptyList()
    private val data : MutableList<ProductEntity> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : ProductViewHolder {
        return ProductViewHolder.inflate(parent)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val selectedProductEntity = data[position]
        holder.bind(selectedProductEntity, onClickListener, productsCheck)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun updateAdapter(allProducts: List<ProductEntity>, productsAlreadyInsert: List<Long>){
        productsCheck = productsAlreadyInsert
        data.clear()
        data.addAll(allProducts)
        notifyDataSetChanged()
    }

    class ProductViewHolder(private val binding: RecycleAddProductToListBinding) : ViewHolder(binding.root){
        private val productName = binding.txvProductName
        private val imgCheck = binding.imgCheck
        private val imgOpt = binding.imgOptions

        companion object{
            fun inflate(viewGroup: ViewGroup) : ProductViewHolder{
                val binding = RecycleAddProductToListBinding.inflate(
                    LayoutInflater.from(viewGroup.context),
                    viewGroup,
                    false
                )
                return ProductViewHolder(binding)
            }
        }

        init {
            imgCheck.visibility = View.GONE
        }

        fun bind(
            productEntity: ProductEntity,
            onClickListener: OnClickListener,
            productAlreadyInsert: List<Long>,
        ) {
            productName.text = productEntity.prodName
            imgCheck.visibility = if (productEntity.prodId in productAlreadyInsert) {View.VISIBLE} else {View.GONE}
            imgOpt.setOnClickListener {
                onClickListener.onOptMenuClickListener(
                    ProductEntity(
                        prodId = productEntity.prodId,
                        prodName = productEntity.prodName,
                        prodCategoryIndex = productEntity.prodCategoryIndex
                    )
                )
            }
            binding.root.setOnClickListener {
                if (productEntity.prodId in productAlreadyInsert){
                    onClickListener.onDeleteProduct(
                        ProductEntity(
                            prodId = productEntity.prodId,
                            prodName = productEntity.prodName,
                            prodCategoryIndex = productEntity.prodCategoryIndex
                        )
                    )
                }
                else{
                    onClickListener.onInsertProduct(
                        ProductEntity(
                            prodId = productEntity.prodId,
                            prodName = productEntity.prodName,
                            prodCategoryIndex = productEntity.prodCategoryIndex
                        )
                    )
                }
            }
        }
    }

    interface OnClickListener{
        fun onInsertProduct(productEntity: ProductEntity)
        fun onDeleteProduct(productEntity: ProductEntity)
        fun onOptMenuClickListener(productEntity: ProductEntity)
    }
}
