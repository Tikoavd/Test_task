package com.task.feature.home.domain.usecase

import com.task.core.domain.dispatchers.DispatchersProvider
import com.task.feature.home.domain.models.Product
import com.task.feature.home.domain.repository.HomeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import org.koin.core.annotation.Factory

@Factory
class GetAllProductsUseCase(
    private val homeRepository: HomeRepository,
    private val dispatchersProvider: DispatchersProvider
) {
    operator fun invoke(): Flow<List<Product>> =
        homeRepository.getProducts().flowOn(dispatchersProvider.io)
}