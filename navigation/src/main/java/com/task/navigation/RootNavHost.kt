package com.task.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.task.screens.Screens

@Composable
fun RootNavHost(
    modifier: Modifier = Modifier,
    startDestination: Screens = Screens.Home,
) {
    val rootNavController: NavHostController = rememberNavController()

    NavHost(
        navController = rootNavController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        homeScreen()
    }
}
