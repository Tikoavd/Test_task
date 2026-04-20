package com.task.feature.home.data.models

import com.task.core.domain.utils.orDefault
import com.task.feature.home.domain.models.Product
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProductDto(
    @SerialName("category")
    val category: CategoryDto? = null,
    @SerialName("creationAt")
    val creationAt: String? = null,
    @SerialName("description")
    val description: String? = null,
    @SerialName("id")
    val id: Int? = null,
    @SerialName("images")
    val images: List<String>? = null,
    @SerialName("price")
    val price: Int? = null,
    @SerialName("slug")
    val slug: String? = null,
    @SerialName("title")
    val title: String? = null,
    @SerialName("updatedAt")
    val updatedAt: String? = null
) {
    fun toDomain() = Product(
        category = (category ?: CategoryDto()).toDomain(),
        creationAt = creationAt.orEmpty(),
        description = description.orEmpty(),
        id = id.orDefault(),
        images = images.orEmpty(),
        price = price.orDefault(),
        slug = slug.orEmpty(),
        title = title.orEmpty(),
        updatedAt = updatedAt.orEmpty()
    )
}
