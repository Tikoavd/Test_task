package com.task.feature.home.presentation.models

import com.task.feature.home.domain.models.Product

data class ProductUI(
    val id: Int,
    val categoryId: Int,
    val image: String,
    val title: String,
    val subtitle: String
)

fun Product.toUI() = ProductUI(
    id = id,
    categoryId = category.id,
    image = images.firstOrNull().orEmpty(),
    title = title,
    subtitle = description
)
