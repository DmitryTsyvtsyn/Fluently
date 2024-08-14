package io.github.dmitrytsyvtsyn.fluently.core.navigation

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import kotlinx.coroutines.flow.StateFlow

abstract class NavigationDestination<T : NavigationDestination.NavigationParams, R> {

    abstract val name: String
    abstract val defaultResult: R
    open val navArguments: List<NamedNavArgument> = emptyList()

    val route: String
        get() {
            if (navArguments.isEmpty()) return name
            val navArgumentsString = navArguments.joinToString("&") { navArgument ->
                val name = navArgument.name
                "$name={$name}"
            }
            return "$name?$navArgumentsString"
        }

    private val resultKey: String
        get() = "${name}_result_key"

    abstract fun params(backStackEntry: NavBackStackEntry): T

    fun fetchResult(navController: NavController): StateFlow<R> {
        val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle ?: error("currentBackStackEntry.savedStateHandle is null!")
        return savedStateHandle.getStateFlow(resultKey, defaultResult)
    }

    fun putResult(navController: NavController, result: R) {
        navController.previousBackStackEntry
            ?.savedStateHandle
            ?.set(resultKey, result)
    }

    interface NavigationParams {
        val route: String
    }

}