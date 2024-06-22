package io.github.dmitrytsyvtsyn.fluently.core.navigation

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import kotlinx.coroutines.flow.StateFlow

abstract class NavigationDestination<T : NavigationDestination.NavigationParams, R> {

    abstract val route: String

    abstract val resultKey: String

    abstract val defaultResult: R

    abstract val navArguments: List<NamedNavArgument>

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