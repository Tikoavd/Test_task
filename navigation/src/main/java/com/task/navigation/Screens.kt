package com.task.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.task.feature.home.presentation.home.screen.HomeRoute
import com.task.screens.Screens

fun NavGraphBuilder.homeScreen(
) {
    composable<Screens.Home> {
        HomeRoute()
    }
}
