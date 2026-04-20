package com.task.feature.home.data.api

import com.task.feature.home.data.models.CategoryDto
import com.task.feature.home.data.models.ProductDto
import retrofit2.http.GET

interface ProductsApi {

    @GET("products")
    suspend fun getProducts(): List<ProductDto>

    @GET("categories")
    suspend fun getCategories(): List<CategoryDto>
}
