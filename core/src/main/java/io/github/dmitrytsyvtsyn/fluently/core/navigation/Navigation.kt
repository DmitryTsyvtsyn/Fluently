package io.github.dmitrytsyvtsyn.fluently.core.navigation

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavController
import androidx.navigation.NavHostController

val LocalNavController = staticCompositionLocalOf<NavHostController> { error("NavController is not provided!") }

fun NavController.navigate(params: NavigationDestination.NavigationParams) {
    navigate(params.route)
}