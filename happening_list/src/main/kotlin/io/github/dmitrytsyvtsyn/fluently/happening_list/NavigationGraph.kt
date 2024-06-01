package io.github.dmitrytsyvtsyn.fluently.happening_list

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

fun NavGraphBuilder.happeningListDestination() {
    composable(
        route = HappeningListDestination.route
    ) {
        HappeningListScreen()
    }
}