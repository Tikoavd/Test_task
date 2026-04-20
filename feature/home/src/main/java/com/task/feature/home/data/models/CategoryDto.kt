package com.task.feature.home.data.models

import com.task.core.domain.utils.orDefault
import com.task.feature.home.domain.models.Category
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CategoryDto(
    @SerialName("creationAt")
    val creationAt: String? = null,
    @SerialName("id")
    val id: Int? = null,
    @SerialName("image")
    val image: String? = null,
    @SerialName("name")
    val name: String? = null,
    @SerialName("slug")
    val slug: String? = null,
    @SerialName("updatedAt")
    val updatedAt: String? = null
) {
    fun toDomain() = Category(
        creationAt = creationAt.orEmpty(),
        id = id.orDefault(),
        image = image.orEmpty(),
        name = name.orEmpty(),
        slug = slug.orEmpty(),
        updatedAt = updatedAt.orEmpty()
    )
}
