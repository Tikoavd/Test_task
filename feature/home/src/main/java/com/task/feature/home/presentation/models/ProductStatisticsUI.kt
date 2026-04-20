package com.task.feature.home.presentation.models

import androidx.compose.runtime.Immutable

@Immutable
data class ProductStatisticsUI(
    val categoryItemCounts: List<CategoryStatUI>,
    val topCharacters: List<CharStatUI>
)

@Immutable
data class CategoryStatUI(val categoryName: String, val itemCount: Int)

@Immutable
data class CharStatUI(val character: Char, val count: Int)
