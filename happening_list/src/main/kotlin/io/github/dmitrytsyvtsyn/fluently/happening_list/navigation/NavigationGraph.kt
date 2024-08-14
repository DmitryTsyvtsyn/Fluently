package io.github.dmitrytsyvtsyn.fluently.happening_list.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import io.github.dmitrytsyvtsyn.fluently.happening_list.HappeningListScreen

fun NavGraphBuilder.happeningListDestination() {
    composable(
        route = HappeningListDestination.route
    ) {
        HappeningListScreen()
    }
}