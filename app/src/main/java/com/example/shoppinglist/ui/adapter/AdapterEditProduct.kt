package com.example.shoppinglist.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.shoppinglist.data.room.entity.ProductEntity
import com.example.shoppinglist.databinding.RecycleEditProductBinding
import com.example.shoppinglist.ui.adapter.AdapterEditProduct.ProductEditViewHolder

class AdapterEditProduct(private val editProductListener:(productEntity:ProductEntity)->Unit) : RecyclerView.Adapter<ProductEditViewHolder>(){
    private val data :MutableList<ProductEntity> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductEditViewHolder {
        return ProductEditViewHolder.inflate(parent)
    }

    override fun onBindViewHolder(holder: ProductEditViewHolder, position: Int) {
        val selectedProductEntity = data[position]
        holder.bind(selectedProductEntity, editProductListener)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun updateAdapter(allProducts:List<ProductEntity>){
        data.clear()
        data.addAll(allProducts)
        notifyDataSetChanged()
    }

    class ProductEditViewHolder(binding: RecycleEditProductBinding) : ViewHolder(binding.root){
        private val txvProductJvmName = binding.txvProductName
        private val imgEdit = binding.imgEditProduct

        companion object{
            fun inflate(viewGroup: ViewGroup) : ProductEditViewHolder{
                val binding = RecycleEditProductBinding.inflate(
                    LayoutInflater.from(viewGroup.context),
                    viewGroup,
                    false
                )
                return ProductEditViewHolder(binding)
            }
        }

        fun bind(
            productEntity: ProductEntity, editProductListener: (productEntity: ProductEntity) -> Unit, ){
            txvProductJvmName.text = productEntity.prodName
            imgEdit.setOnClickListener {
                editProductListener(productEntity)
            }
        }
    }
}