package com.task.feature.home.presentation.home.mvi

import com.task.core.presentation.mvi.MviIntent

sealed interface HomeIntent : MviIntent {

    data class Search(val query: String) : HomeIntent
    data class OnCategoryChange(val categoryId: Int) : HomeIntent
    data object LoadStatistics : HomeIntent
}
