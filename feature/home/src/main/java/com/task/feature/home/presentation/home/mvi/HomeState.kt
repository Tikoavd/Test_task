package com.task.feature.home.presentation.home.mvi

import androidx.compose.runtime.Immutable
import com.task.core.presentation.mvi.MviState
import com.task.feature.home.presentation.models.CategoryUI
import com.task.feature.home.presentation.models.ProductStatisticsUI
import com.task.feature.home.presentation.models.ProductUI

@Immutable
data class HomeState(
    val isLoading: Boolean = true,
    val categories: List<CategoryUI> = emptyList(),
    val categoryId: Int = 0,
    val products: List<ProductUI> = emptyList(),
    val query: String = "",
    val isBottomSheetLoading: Boolean = false,
    val statistics: ProductStatisticsUI? = null
) : MviState
