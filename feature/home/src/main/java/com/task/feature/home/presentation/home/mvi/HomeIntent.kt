package com.task.feature.home.presentation.home.mvi

import androidx.compose.runtime.Stable
import com.task.core.presentation.mvi.MviIntent

@Stable
sealed interface HomeIntent : MviIntent {

    data class Search(val query: String) : HomeIntent
    data class OnCategoryChange(val categoryId: Int) : HomeIntent
    data object LoadStatistics : HomeIntent
}
