package com.task.feature.home.domain.usecase

import com.task.core.domain.dispatchers.DispatchersProvider
import com.task.feature.home.domain.models.Category
import com.task.feature.home.domain.models.Product
import com.task.feature.home.domain.repository.HomeRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetAllProductsUseCaseTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private val dispatchers = object : DispatchersProvider {
        override val main = testDispatcher
        override val io = testDispatcher
        override val default = testDispatcher
    }

    private val repository: HomeRepository = mockk()
    private lateinit var useCase: GetAllProductsUseCase

    private val sampleProducts = listOf(
        makeProduct(id = 1, title = "Product A"),
        makeProduct(id = 2, title = "Product B"),
    )

    @Before
    fun setup() {
        useCase = GetAllProductsUseCase(repository, dispatchers)
        every { repository.getProducts() } returns flowOf(sampleProducts)
    }

    @Test
    fun `emits all products from repository`() = runTest {
        val result = useCase().toList().flatten()
        assertEquals(sampleProducts, result)
    }

    @Test
    fun `delegates to repository getProducts`() = runTest {
        useCase().toList()
        verify(exactly = 1) { repository.getProducts() }
    }

    @Test
    fun `emits empty list when repository returns empty`() = runTest {
        every { repository.getProducts() } returns flowOf(emptyList())
        val result = useCase().toList().flatten()
        assertEquals(emptyList<Product>(), result)
    }
}

private fun makeProduct(id: Int = 0, title: String = "") = Product(
    id = id, title = title, category = Category("", 0, "", "", "", ""),
    description = "", price = 0, images = emptyList(), slug = "", creationAt = "", updatedAt = ""
)