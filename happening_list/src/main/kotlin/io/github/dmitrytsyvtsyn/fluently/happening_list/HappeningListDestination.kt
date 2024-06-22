package io.github.dmitrytsyvtsyn.fluently.happening_list

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import io.github.dmitrytsyvtsyn.fluently.core.navigation.NavigationDestination

data object HappeningListDestination : NavigationDestination<HappeningListDestination.Params, Unit>() {

    private const val NAME = "happening_list_screen"

    override val route: String = NAME
    override val resultKey: String = "${NAME}_result_key"
    override val defaultResult: Unit = Unit
    override val navArguments: List<NamedNavArgument> = emptyList()

    override fun params(backStackEntry: NavBackStackEntry): Params = Params()

    class Params : NavigationParams {
        override val route: String = NAME
    }
}