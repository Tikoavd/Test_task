package com.task.feature.home.domain.usecase

import com.task.core.domain.dispatchers.DispatchersProvider
import com.task.feature.home.domain.models.Category
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

class GetCategoriesUseCaseTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private val dispatchers = object : DispatchersProvider {
        override val main = testDispatcher
        override val io = testDispatcher
        override val default = testDispatcher
    }

    private val repository: HomeRepository = mockk()
    private lateinit var useCase: GetCategoriesUseCase

    private val sampleCategories = listOf(
        makeCategory(id = 1, name = "Electronics"),
        makeCategory(id = 2, name = "Clothing"),
    )

    @Before
    fun setup() {
        useCase = GetCategoriesUseCase(repository, dispatchers)
        every { repository.getCategories() } returns flowOf(sampleCategories)
    }

    @Test
    fun `emits all categories from repository`() = runTest {
        val result = useCase().toList().flatten()
        assertEquals(sampleCategories, result)
    }

    @Test
    fun `delegates to repository getCategories`() = runTest {
        useCase().toList()
        verify(exactly = 1) { repository.getCategories() }
    }

    @Test
    fun `emits empty list when repository returns empty`() = runTest {
        every { repository.getCategories() } returns flowOf(emptyList())
        val result = useCase().toList().flatten()
        assertEquals(emptyList<Category>(), result)
    }
}

private fun makeCategory(id: Int = 0, name: String = "") = Category(
    id = id, name = name, image = "", slug = "", creationAt = "", updatedAt = ""
)