package com.task.feature.home.data.repository

import com.task.core.domain.utils.emitFlow
import com.task.feature.home.data.api.ProductsApi
import com.task.feature.home.domain.models.Category
import com.task.feature.home.domain.models.Product
import com.task.feature.home.domain.repository.HomeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.koin.core.annotation.Single

@Single
class HomeRepositoryImpl(
    private val productsApi: ProductsApi
) : HomeRepository {

    private val cacheMutex = Mutex()
    private var cacheProducts: List<Product> = emptyList()

    override fun getProducts(): Flow<List<Product>> = emitFlow {
        if (cacheProducts.isEmpty()) {
            cacheMutex.withLock {
                cacheProducts = productsApi.getProducts().map { it.toDomain() }
            }
        }
        cacheMutex.withLock { cacheProducts }
    }

    override fun getCategories(): Flow<List<Category>> = emitFlow {
        productsApi.getCategories().map { it.toDomain() }
    }
}
