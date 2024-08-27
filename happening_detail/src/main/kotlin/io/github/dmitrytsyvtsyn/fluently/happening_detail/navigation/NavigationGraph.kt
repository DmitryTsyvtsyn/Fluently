package io.github.dmitrytsyvtsyn.fluently.happening_detail.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import io.github.dmitrytsyvtsyn.fluently.happening_detail.HappeningDetailScreen

fun NavGraphBuilder.happeningDetailDestination() {
    composable(
        route = HappeningDetailDestination.route,
        arguments = HappeningDetailDestination.navArguments
    ) { backStackEntry ->
        HappeningDetailScreen(HappeningDetailDestination.params(backStackEntry))
    }
}