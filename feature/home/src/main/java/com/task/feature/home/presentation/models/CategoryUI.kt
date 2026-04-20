package com.task.feature.home.presentation.models

import androidx.compose.runtime.Immutable
import com.task.feature.home.domain.models.Category

@Immutable
data class CategoryUI(
    val id: Int,
    val image: String
)

fun Category.toUI() = CategoryUI(
    id = id,
    image = image
)
