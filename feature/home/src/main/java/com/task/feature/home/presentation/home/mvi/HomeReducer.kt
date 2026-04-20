package com.task.feature.home.presentation.home.mvi

import com.task.core.domain.utils.orDefault
import com.task.core.presentation.mvi.Reducer
import org.koin.core.annotation.Single

@Single
class HomeReducer : Reducer<HomeAction, HomeState> {

    override fun reduce(action: HomeAction, state: HomeState): HomeState =
        when (action) {
            is HomeAction.UpdateCategories -> state.copy(
                categories = action.categories,
                categoryId = action.categories.firstOrNull()?.id.orDefault()
            )

            is HomeAction.UpdateProducts -> state.copy(
                products = action.products,
                isLoading = false
            )
            is HomeAction.UpdateQuery -> state.copy(
                query = action.query
            )

            is HomeAction.ShowBottomSheetLoading -> state.copy(
                isBottomSheetLoading = true
            )

            is HomeAction.ChangeCategoryId -> state.copy(
                categoryId = action.categoryId
            )

            is HomeAction.UpdateStatistics -> state.copy(
                statistics = action.statistics,
                isBottomSheetLoading = false
            )
        }
}