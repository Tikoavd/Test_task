package com.task.feature.home.presentation.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.task.core.domain.utils.orDefault
import com.task.feature.home.presentation.models.CategoryUI
import kotlinx.coroutines.launch

@Composable
fun CategoriesPager(
    modifier: Modifier = Modifier,
    categories: List<CategoryUI>,
    categoryId: Int,
    onCategoryChange: (Int) -> Unit
) {
    val pagerState = rememberPagerState { categories.size }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(pagerState.currentPage) {
        if (categories.isNotEmpty()
            && categories.getOrNull(pagerState.currentPage)?.id != categoryId
        ) {
            onCategoryChange(categories.getOrNull(pagerState.currentPage)?.id.orDefault())
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HorizontalPager(
            modifier = modifier
                .padding(bottom = 16.dp)
                .fillMaxWidth(),
            state = pagerState,
            contentPadding = PaddingValues(horizontal = 24.dp),
            pageSpacing = 24.dp
        ) { page ->
            categories.getOrNull(page)?.let { category ->
                SubcomposeAsyncImage(
                    model = category.image,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(5f/3)
                        .clip(RoundedCornerShape(16.dp)),
                    error = {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(5f/3)
                                .clip(RoundedCornerShape(16.dp))
                                .background(MaterialTheme.colorScheme.error),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Warning,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onError,
                                modifier = Modifier.fillMaxSize(0.5f)
                            )
                        }
                    },
                    contentScale = ContentScale.Crop
                )
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            repeat(categories.size) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(RoundedCornerShape(100))
                        .background(
                            if (it == pagerState.currentPage) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.secondary
                        )
                        .clickable {
                            if (it != pagerState.currentPage && !pagerState.isScrollInProgress) {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(it)
                                }
                            }
                        }
                )
            }
        }
    }
}
