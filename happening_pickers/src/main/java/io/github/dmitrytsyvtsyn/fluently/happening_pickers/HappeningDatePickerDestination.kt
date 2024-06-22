package io.github.dmitrytsyvtsyn.fluently.happening_pickers

import androidx.compose.runtime.Immutable
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavType
import androidx.navigation.navArgument
import io.github.dmitrytsyvtsyn.fluently.core.navigation.NavigationDestination

data object HappeningDatePickerDestination : NavigationDestination<HappeningDatePickerDestination.Params, Long>() {

    private const val NAME = "happening_date_picker_dialog"

    private const val INITIAL_DATE = "${NAME}_initial_date"
    private const val MIN_DATE = "${NAME}_min_date"
    private const val MAX_DATE = "${NAME}_max_date"

    override val route: String = "$NAME?$INITIAL_DATE={$INITIAL_DATE}&$MIN_DATE={$MIN_DATE}&$MAX_DATE={$MAX_DATE}"
    override val resultKey: String = "${NAME}_result_key"
    override val defaultResult: Long = -1

    override val navArguments: List<NamedNavArgument>
        get() = listOf(
            navArgument(INITIAL_DATE) { type = NavType.LongType },
            navArgument(MIN_DATE) { type = NavType.LongType },
            navArgument(MAX_DATE) { type = NavType.LongType }
        )

    override fun params(backStackEntry: NavBackStackEntry): Params {
        val arguments = backStackEntry.arguments ?: error("NavBackStackEntry.arguments is empty!")

        return Params(
            initialDate = arguments.getLong(INITIAL_DATE),
            minDate = arguments.getLong(MIN_DATE),
            maxDate = arguments.getLong(MAX_DATE)
        )
    }

    @Immutable
    class Params(
        val initialDate: Long,
        val minDate: Long = Long.MIN_VALUE,
        val maxDate: Long = Long.MAX_VALUE
    ) : NavigationParams {

        override val route: String = "$NAME?$INITIAL_DATE=$initialDate&$MIN_DATE=$minDate&$MAX_DATE=$maxDate"

    }

}