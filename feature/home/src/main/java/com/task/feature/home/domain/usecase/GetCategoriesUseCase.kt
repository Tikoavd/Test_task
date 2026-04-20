package com.task.feature.home.domain.usecase

import com.task.core.domain.dispatchers.DispatchersProvider
import com.task.feature.home.domain.models.Category
import com.task.feature.home.domain.repository.HomeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import org.koin.core.annotation.Factory

@Factory
class GetCategoriesUseCase(
    private val homeRepository: HomeRepository,
    private val dispatcherProvider: DispatchersProvider
) {
    operator fun invoke(): Flow<List<Category>> = homeRepository.getCategories()
        .flowOn(dispatcherProvider.io)
}
