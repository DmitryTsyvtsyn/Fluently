package io.github.dmitrytsyvtsyn.fluently.happening_list.navigation

import androidx.navigation.NavBackStackEntry
import io.github.dmitrytsyvtsyn.fluently.core.navigation.NavigationDestination

data object HappeningListDestination : NavigationDestination<HappeningListDestination.Params, Unit>() {

    private const val NAME = "happening_list_screen"

    override val name: String = NAME
    override val defaultResult: Unit = Unit

    override fun params(backStackEntry: NavBackStackEntry): Params = Params()

    class Params : NavigationParams {
        override val route: String = NAME
    }
}