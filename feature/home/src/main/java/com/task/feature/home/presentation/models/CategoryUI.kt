package com.task.feature.home.presentation.models

import com.task.feature.home.domain.models.Category

data class CategoryUI(
    val id: Int,
    val image: String
)

fun Category.toUI() = CategoryUI(
    id = id,
    image = image
)
