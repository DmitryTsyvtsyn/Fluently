package io.github.dmitrytsyvtsyn.fluently.core.navigation

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import kotlinx.coroutines.flow.FlowCollector
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

    object EmptyStateFlow : StateFlow<Unit> {
        override val value: Unit = Unit
        override val replayCache: List<Unit> = emptyList()
        override suspend fun collect(collector: FlowCollector<Unit>): Nothing {
            error("EmptyStateFlow can not collect data")
        }
    }

}