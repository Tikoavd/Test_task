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
    private var cacheProducts: List<Product>? = null

    override fun getProducts(): Flow<List<Product>> = emitFlow {
        cacheMutex.withLock {
            if (cacheProducts == null) {
                cacheProducts = productsApi.getProducts().map { it.toDomain() }
            }
            cacheProducts.orEmpty()
        }
    }

    override fun getCategories(): Flow<List<Category>> = emitFlow {
        productsApi.getCategories().map { it.toDomain() }
    }
}
