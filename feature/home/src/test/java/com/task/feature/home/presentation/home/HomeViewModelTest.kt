package com.task.feature.home.presentation.home

import com.task.feature.home.domain.models.Category
import com.task.feature.home.domain.models.Product
import com.task.feature.home.domain.usecase.GetAllProductsUseCase
import com.task.feature.home.domain.usecase.GetCategoriesUseCase
import com.task.feature.home.domain.usecase.GetProductsUseCase
import com.task.feature.home.presentation.home.mvi.HomeIntent
import com.task.feature.home.presentation.home.mvi.HomeReducer
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private val getProductsUseCase: GetProductsUseCase = mockk()
    private val getAllProductsUseCase: GetAllProductsUseCase = mockk()
    private val getCategoriesUseCase: GetCategoriesUseCase = mockk()

    private lateinit var viewModel: HomeViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        every { getCategoriesUseCase() } returns flowOf(emptyList())
        every { getProductsUseCase(any(), any()) } returns flowOf(emptyList())
        viewModel = HomeViewModel(getProductsUseCase, getAllProductsUseCase, getCategoriesUseCase, HomeReducer())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `counts items per category correctly`() = runTest {
        val electronics = makeCategory(id = 1, name = "Electronics")
        val clothing = makeCategory(id = 2, name = "Clothing")
        every { getAllProductsUseCase() } returns flowOf(
            listOf(
                makeProduct(title = "Laptop", category = electronics),
                makeProduct(title = "Phone", category = electronics),
                makeProduct(title = "Jacket", category = clothing),
            )
        )

        viewModel.onIntent(HomeIntent.LoadStatistics)
        advanceUntilIdle()

        val stats = viewModel.viewState.value.statistics
        assertNotNull(stats)
        val counts = stats!!.categoryItemCounts.associateBy { it.categoryName }
        assertEquals(2, counts["Electronics"]?.itemCount)
        assertEquals(1, counts["Clothing"]?.itemCount)
    }

    @Test
    fun `returns top 3 characters by frequency`() = runTest {
        every { getAllProductsUseCase() } returns flowOf(listOf(makeProduct(title = "aaa bbb cc")))

        viewModel.onIntent(HomeIntent.LoadStatistics)
        advanceUntilIdle()

        val topChars = viewModel.viewState.value.statistics!!.topCharacters
        assertEquals(3, topChars.size)
        assertEquals('a', topChars[0].character)
        assertEquals('b', topChars[1].character)
        assertEquals('c', topChars[2].character)
    }

    @Test
    fun `characters are lowercased before counting`() = runTest {
        every { getAllProductsUseCase() } returns flowOf(listOf(makeProduct(title = "AaA")))

        viewModel.onIntent(HomeIntent.LoadStatistics)
        advanceUntilIdle()

        val topChars = viewModel.viewState.value.statistics!!.topCharacters
        assertEquals(1, topChars.size)
        assertEquals('a', topChars[0].character)
        assertEquals(3, topChars[0].count)
    }

    @Test
    fun `non-letter characters are excluded from top chars`() = runTest {
        every { getAllProductsUseCase() } returns flowOf(listOf(makeProduct(title = "a1 2 3!")))

        viewModel.onIntent(HomeIntent.LoadStatistics)
        advanceUntilIdle()

        val topChars = viewModel.viewState.value.statistics!!.topCharacters
        assertTrue(topChars.all { it.character.isLetter() })
    }

    @Test
    fun `top characters are sorted by descending frequency`() = runTest {
        every { getAllProductsUseCase() } returns flowOf(listOf(makeProduct(title = "aaabbc")))

        viewModel.onIntent(HomeIntent.LoadStatistics)
        advanceUntilIdle()

        val counts = viewModel.viewState.value.statistics!!.topCharacters.map { it.count }
        assertEquals(counts.sortedDescending(), counts)
    }

    @Test
    fun `empty product list produces empty statistics`() = runTest {
        every { getAllProductsUseCase() } returns flowOf(emptyList())

        viewModel.onIntent(HomeIntent.LoadStatistics)
        advanceUntilIdle()

        val stats = viewModel.viewState.value.statistics
        assertNotNull(stats)
        assertTrue(stats!!.categoryItemCounts.isEmpty())
        assertTrue(stats.topCharacters.isEmpty())
    }

    @Test
    fun `returns at most 3 top characters`() = runTest {
        every { getAllProductsUseCase() } returns flowOf(listOf(makeProduct(title = "abcde")))

        viewModel.onIntent(HomeIntent.LoadStatistics)
        advanceUntilIdle()

        assertTrue(viewModel.viewState.value.statistics!!.topCharacters.size <= 3)
    }
}

private fun makeCategory(id: Int = 0, name: String = "") = Category(
    id = id, name = name, image = "", slug = "", creationAt = "", updatedAt = ""
)

private fun makeProduct(title: String = "", category: Category = makeCategory()) = Product(
    id = 0, title = title, category = category, description = "", price = 0,
    images = emptyList(), slug = "", creationAt = "", updatedAt = ""
)