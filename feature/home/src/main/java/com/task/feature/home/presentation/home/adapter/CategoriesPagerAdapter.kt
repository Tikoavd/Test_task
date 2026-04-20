package com.task.feature.home.presentation.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.task.feature.home.databinding.ItemCategoryBinding
import com.task.feature.home.presentation.models.CategoryUI

class CategoriesPagerAdapter : RecyclerView.Adapter<CategoriesPagerAdapter.CategoryViewHolder>() {

    private val differ = AsyncListDiffer(this, object : DiffUtil.ItemCallback<CategoryUI>() {
        override fun areItemsTheSame(oldItem: CategoryUI, newItem: CategoryUI) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: CategoryUI, newItem: CategoryUI) = oldItem == newItem
    })

    fun submitList(newItems: List<CategoryUI>) = differ.submitList(newItems)

    fun getItemAt(position: Int): CategoryUI? = differ.currentList.getOrNull(position)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    override fun getItemCount(): Int = differ.currentList.size

    class CategoryViewHolder(
        private val binding: ItemCategoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(category: CategoryUI) {
            Glide.with(binding.root.context)
                .load(category.image)
                .centerCrop()
                .into(binding.ivCategory)
        }
    }
}
