package com.task.feature.home.presentation.home

import androidx.lifecycle.viewModelScope
import com.task.core.presentation.mvi.MviBaseViewModel
import com.task.feature.home.domain.usecase.GetAllProductsUseCase
import com.task.feature.home.domain.usecase.GetCategoriesUseCase
import com.task.feature.home.domain.usecase.GetProductsUseCase
import com.task.feature.home.presentation.home.mvi.HomeAction
import com.task.feature.home.presentation.home.mvi.HomeEffect
import com.task.feature.home.presentation.home.mvi.HomeIntent
import com.task.feature.home.presentation.home.mvi.HomeReducer
import com.task.feature.home.presentation.home.mvi.HomeState
import com.task.feature.home.presentation.models.CategoryStatUI
import com.task.feature.home.presentation.models.CharStatUI
import com.task.feature.home.presentation.models.ProductStatisticsUI
import com.task.feature.home.presentation.models.toUI
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import org.koin.android.annotation.KoinViewModel

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@KoinViewModel
class HomeViewModel(
    private val getProductsUseCase: GetProductsUseCase,
    private val getAllProductsUseCase: GetAllProductsUseCase,
    getCategoriesUseCase: GetCategoriesUseCase,
    reducer: HomeReducer
) : MviBaseViewModel<HomeState, HomeAction, HomeIntent, HomeEffect>(HomeState(), reducer) {

    init {
        getCategoriesUseCase().onEach { categories ->
            onAction(HomeAction.UpdateCategories(categories.map { it.toUI() }))
        }.catch { e ->
            sendEffect(HomeEffect.ShowError(e.message.orEmpty()))
        }.launchIn(viewModelScope)

        combine(
            viewState.map { it.query }.distinctUntilChanged(),
            viewState.map { it.categoryId }.distinctUntilChanged()
        ) { query, categoryId -> query to categoryId }
            .filter { (_, categoryId) -> categoryId > 0 }
            .debounce(300)
            .flatMapLatest { (query, categoryId) ->
                getProductsUseCase(query, categoryId)
                    .map { products -> onAction(HomeAction.UpdateProducts(products.map { it.toUI() })) }
                    .catch { e -> sendEffect(HomeEffect.ShowError(e.message.orEmpty())) }
            }
            .launchIn(viewModelScope)
    }

    override fun handleIntent(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.Search -> onAction(HomeAction.UpdateQuery(intent.query))

            is HomeIntent.OnCategoryChange -> onAction(HomeAction.ChangeCategoryId(intent.categoryId))

            is HomeIntent.LoadStatistics -> {
                onAction(HomeAction.ShowBottomSheetLoading)
                getAllProductsUseCase()
                    .map { products ->
                        val categoryItemCounts = products
                            .groupBy { it.category.name }
                            .map { (name, items) -> CategoryStatUI(name, items.size) }

                        val topCharacters = products
                            .flatMap { it.title.toList() }
                            .filter { it.isLetter() }
                            .map { it.lowercaseChar() }
                            .groupingBy { it }
                            .eachCount()
                            .entries
                            .sortedByDescending { it.value }
                            .take(3)
                            .map { CharStatUI(it.key, it.value) }

                        ProductStatisticsUI(categoryItemCounts, topCharacters)
                    }
                    .onEach { statsUI -> onAction(HomeAction.UpdateStatistics(statsUI)) }
                    .catch { e -> sendEffect(HomeEffect.ShowError(e.message.orEmpty())) }
                    .launchIn(viewModelScope)
            }
        }
    }
}