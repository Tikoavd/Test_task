package com.task.feature.home.data.repository

import com.task.feature.home.data.api.ProductsApi
import com.task.feature.home.data.models.CategoryDto
import com.task.feature.home.data.models.ProductDto
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class HomeRepositoryImplTest {

    private val api: ProductsApi = mockk()
    private lateinit var repository: HomeRepositoryImpl

    private val categoryDto = CategoryDto(id = 1, name = "Electronics", image = "", slug = "", creationAt = "", updatedAt = "")
    private val productDto = ProductDto(id = 1, title = "Laptop", category = categoryDto, description = "desc", price = 999, images = listOf("img.jpg"), slug = "laptop", creationAt = "", updatedAt = "")

    @Before
    fun setup() {
        repository = HomeRepositoryImpl(api)
    }

    @Test
    fun `getProducts returns mapped domain products`() = runTest {
        coEvery { api.getProducts() } returns listOf(productDto)

        val products = repository.getProducts().first()

        assertEquals(1, products.size)
        assertEquals("Laptop", products[0].title)
        assertEquals(999, products[0].price)
        assertEquals(1, products[0].category.id)
    }

    @Test
    fun `getProducts caches results and calls api only once`() = runTest {
        coEvery { api.getProducts() } returns listOf(productDto)

        repository.getProducts().first()
        repository.getProducts().first()

        coVerify(exactly = 1) { api.getProducts() }
    }

    @Test
    fun `getProducts returns empty list when api returns empty`() = runTest {
        coEvery { api.getProducts() } returns emptyList()

        val products = repository.getProducts().first()

        assertTrue(products.isEmpty())
    }

    @Test
    fun `getCategories returns mapped domain categories`() = runTest {
        coEvery { api.getCategories() } returns listOf(categoryDto)

        val categories = repository.getCategories().first()

        assertEquals(1, categories.size)
        assertEquals("Electronics", categories[0].name)
        assertEquals(1, categories[0].id)
    }

    @Test
    fun `getCategories always fetches fresh data without caching`() = runTest {
        coEvery { api.getCategories() } returns listOf(categoryDto)

        repository.getCategories().first()
        repository.getCategories().first()

        coVerify(exactly = 2) { api.getCategories() }
    }

    @Test
    fun `getCategories returns empty list when api returns empty`() = runTest {
        coEvery { api.getCategories() } returns emptyList()

        val categories = repository.getCategories().first()

        assertTrue(categories.isEmpty())
    }

    @Test
    fun `getProducts handles null dto fields with defaults`() = runTest {
        coEvery { api.getProducts() } returns listOf(ProductDto())

        val products = repository.getProducts().first()

        assertEquals(1, products.size)
        assertEquals("", products[0].title)
        assertEquals(0, products[0].price)
    }
}