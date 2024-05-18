package io.github.dmitrytsyvtsyn.interfunny.core.navigation

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavHostController

val LocalNavController = staticCompositionLocalOf<NavHostController> { error("No shapes provided") }