package io.github.dmitrytsyvtsyn.fluently.happening_detail

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavType
import androidx.navigation.navArgument
import io.github.dmitrytsyvtsyn.fluently.core.data.CalendarRepository
import io.github.dmitrytsyvtsyn.fluently.core.navigation.NavigationDestination

object HappeningDetailDestination : NavigationDestination<HappeningDetailDestination.Params, Unit>() {

    private const val NAME = "happening_detail_screen"

    private const val ID = "${NAME}_id"
    private const val INITIAL_DATE = "${NAME}_initial_date"

    override val route: String = "${NAME}?$ID={$ID}&$INITIAL_DATE={$INITIAL_DATE}"
    override val resultKey: String = "${NAME}_result_key"
    override val defaultResult: Unit = Unit
    override val navArguments: List<NamedNavArgument> = listOf(
        navArgument(ID) { type = NavType.LongType },
        navArgument(INITIAL_DATE) { type = NavType.LongType }
    )

    override fun params(backStackEntry: NavBackStackEntry): Params {
        val arguments = backStackEntry.arguments ?: error("NavBackStackEntry.arguments is empty!")

        return Params(
            id = arguments.getLong(ID, -1),
            initialDate = arguments.getLong(INITIAL_DATE, CalendarRepository.nowDate())
        )
    }

    data class Params(
        val id: Long,
        val initialDate: Long
    ) : NavigationParams {
        override val route: String = "${NAME}?$ID=$id&$INITIAL_DATE=$initialDate"
    }
}