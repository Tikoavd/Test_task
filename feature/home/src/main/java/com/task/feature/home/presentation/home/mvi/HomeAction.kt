package com.task.feature.home.presentation.home.mvi

import androidx.compose.runtime.Stable
import com.task.core.presentation.mvi.MviAction
import com.task.feature.home.presentation.models.CategoryUI
import com.task.feature.home.presentation.models.ProductStatisticsUI
import com.task.feature.home.presentation.models.ProductUI

@Stable
sealed interface HomeAction : MviAction {

    data class UpdateProducts(val products: List<ProductUI>) : HomeAction
    data class UpdateCategories(val categories: List<CategoryUI>) : HomeAction
    data class UpdateQuery(val query: String) : HomeAction
    data object ShowBottomSheetLoading : HomeAction
    data class ChangeCategoryId(val categoryId: Int) : HomeAction
    data class UpdateStatistics(val statistics: ProductStatisticsUI) : HomeAction
}
