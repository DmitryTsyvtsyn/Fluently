package io.github.dmitrytsyvtsyn.fluently.core.navigation

import androidx.navigation.NavBackStackEntry

data object ThemeSettingsDestination : NavigationDestination<ThemeSettingsDestination.Params, Unit>() {

    private const val NAME = "theme_settings_screen"

    override val name: String = NAME
    override val defaultResult: Unit = Unit

    override fun params(backStackEntry: NavBackStackEntry) = Params()

    class Params : NavigationParams {
        override val route: String = NAME
    }

}