package com.task.feature.home.presentation.home.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.task.core.presentation.extensions.collectEffects
import com.task.feature.home.presentation.home.HomeViewModel
import com.task.feature.home.presentation.home.components.CategoriesPager
import com.task.feature.home.presentation.home.components.ProductItem
import com.task.feature.home.presentation.home.components.SearchBar
import com.task.feature.home.presentation.home.components.StatisticsBottomSheetContent
import com.task.feature.home.presentation.home.mvi.HomeEffect
import com.task.feature.home.presentation.home.mvi.HomeIntent
import com.task.feature.home.presentation.home.mvi.HomeState
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeRoute() {
    val viewModel: HomeViewModel = koinViewModel()
    val state by viewModel.viewState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    viewModel.collectEffects { effect ->
        when (effect) {
            is HomeEffect.ShowError -> snackbarHostState.showSnackbar(effect.message)
        }
    }

    if (state.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        HomeScreen(
            state = state,
            snackbarHostState = snackbarHostState,
            intentEmitter = viewModel::onIntent
        )
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreen(
    state: HomeState,
    snackbarHostState: SnackbarHostState,
    intentEmitter: (HomeIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showBottomSheet by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    showBottomSheet = true
                    intentEmitter(HomeIntent.LoadStatistics)
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(100)
            ) {
                Icon(Icons.Default.MoreVert, contentDescription = null)
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = paddingValues,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                CategoriesPager(
                    categories = state.categories,
                    categoryId = state.categoryId,
                    onCategoryChange = { intentEmitter(HomeIntent.OnCategoryChange(it)) }
                )
            }

            stickyHeader {
                SearchBar(
                    query = state.query,
                    onQueryChange = { intentEmitter(HomeIntent.Search(it)) }
                )
            }

            items(state.products, key = { it.id }) { product ->
                ProductItem(product = product)
            }
        }
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        ) {
            if (state.isBottomSheetLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                state.statistics?.let { statistics ->
                    StatisticsBottomSheetContent(statistics = statistics)
                }
            }
        }
    }
}