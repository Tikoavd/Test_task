package com.task.feature.home.domain.usecase

import com.task.core.domain.dispatchers.DispatchersProvider
import com.task.feature.home.domain.models.Product
import com.task.feature.home.domain.repository.HomeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Factory

@Factory
class GetProductsUseCase(
    private val homeRepository: HomeRepository,
    private val dispatcherProvider: DispatchersProvider
) {
    operator fun invoke(query: String, categoryId: Int): Flow<List<Product>> =
        homeRepository.getProducts()
            .flowOn(dispatcherProvider.io)
            .map { list ->
                list.filter {
                    it.title.lowercase().contains(query.lowercase()) &&
                            it.category.id == categoryId
                }
            }
            .flowOn(dispatcherProvider.default)
}
