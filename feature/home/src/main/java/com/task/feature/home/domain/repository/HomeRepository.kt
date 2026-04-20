package com.task.feature.home.domain.repository

import com.task.feature.home.domain.models.Category
import com.task.feature.home.domain.models.Product
import kotlinx.coroutines.flow.Flow

interface HomeRepository {

    fun getProducts(): Flow<List<Product>>

    fun getCategories(): Flow<List<Category>>
}
