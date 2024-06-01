package io.github.dmitrytsyvtsyn.fluently.happening_detail

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

fun NavGraphBuilder.happeningDetailDestination() {
    composable(
        route = HappeningDetailDestination.route,
        arguments = HappeningDetailDestination.navArguments
    ) { backStackEntry ->
        HappeningDetailScreen(HappeningDetailDestination.params(backStackEntry))
    }
}