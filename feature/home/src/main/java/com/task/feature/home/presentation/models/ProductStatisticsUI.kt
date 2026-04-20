package com.task.feature.home.presentation.models

data class ProductStatisticsUI(
    val categoryItemCounts: List<CategoryStatUI>,
    val topCharacters: List<CharStatUI>
)

data class CategoryStatUI(val categoryName: String, val itemCount: Int)

data class CharStatUI(val character: Char, val count: Int)
