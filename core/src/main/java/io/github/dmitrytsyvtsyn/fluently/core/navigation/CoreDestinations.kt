package io.github.dmitrytsyvtsyn.fluently.core.navigation

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry

data object ThemeSettingsDestination : NavigationDestination<ThemeSettingsDestination.Params, Unit>() {

    private const val NAME = "theme_settings_screen"

    override val route: String = NAME
    override val resultKey: String = "${NAME}_result_key"
    override val defaultResult: Unit = Unit
    override val navArguments: List<NamedNavArgument> = emptyList()

    override fun params(backStackEntry: NavBackStackEntry) = Params()

    class Params : NavigationParams {
        override val route: String = NAME
    }

}