package com.task.feature.home.domain.usecase

import com.task.core.domain.dispatchers.DispatchersProvider
import com.task.feature.home.domain.models.Category
import com.task.feature.home.domain.models.Product
import com.task.feature.home.domain.repository.HomeRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetProductsUseCaseTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private val dispatchers = object : DispatchersProvider {
        override val main = testDispatcher
        override val io = testDispatcher
        override val default = testDispatcher
    }

    private val repository: HomeRepository = mockk()
    private lateinit var useCase: GetProductsUseCase

    private val electronics = makeCategory(id = 1, name = "Electronics")
    private val clothing = makeCategory(id = 2, name = "Clothing")

    private val products = listOf(
        makeProduct(id = 1, title = "Laptop Pro", category = electronics),
        makeProduct(id = 2, title = "Wireless Mouse", category = electronics),
        makeProduct(id = 3, title = "Winter Jacket", category = clothing),
        makeProduct(id = 4, title = "Running Shoes", category = clothing),
    )

    @Before
    fun setup() {
        useCase = GetProductsUseCase(repository, dispatchers)
        every { repository.getProducts() } returns flowOf(products)
    }

    @Test
    fun `returns all items when query is empty and category matches all`() = runTest {
        val result = useCase("", categoryId = 1).toList().flatten()
        assertEquals(2, result.size)
    }

    @Test
    fun `filters by title substring case-insensitively`() = runTest {
        val result = useCase("laptop", categoryId = 1).toList().flatten()
        assertEquals(1, result.size)
        assertEquals("Laptop Pro", result[0].title)
    }

    @Test
    fun `returns empty list when title does not match`() = runTest {
        val result = useCase("xyz_not_found", categoryId = 1).toList().flatten()
        assertTrue(result.isEmpty())
    }

    @Test
    fun `filters by category id`() = runTest {
        val result = useCase("", categoryId = 2).toList().flatten()
        assertEquals(2, result.size)
        assertTrue(result.all { it.category.id == 2 })
    }

    @Test
    fun `returns empty when category id matches nothing`() = runTest {
        val result = useCase("", categoryId = 99).toList().flatten()
        assertTrue(result.isEmpty())
    }

    @Test
    fun `filters by both title and category simultaneously`() = runTest {
        val result = useCase("jacket", categoryId = 2).toList().flatten()
        assertEquals(1, result.size)
        assertEquals("Winter Jacket", result[0].title)
    }

    @Test
    fun `query matching is case-insensitive`() = runTest {
        val lower = useCase("LAPTOP", categoryId = 1).toList().flatten()
        val upper = useCase("laptop", categoryId = 1).toList().flatten()
        assertEquals(lower, upper)
    }
}

private fun makeCategory(id: Int = 0, name: String = "") = Category(
    id = id, name = name, image = "", slug = "", creationAt = "", updatedAt = ""
)

private fun makeProduct(id: Int = 0, title: String = "", category: Category = makeCategory()) =
    Product(
        id = id, title = title, category = category, description = "", price = 0,
        images = emptyList(), slug = "", creationAt = "", updatedAt = ""
    )