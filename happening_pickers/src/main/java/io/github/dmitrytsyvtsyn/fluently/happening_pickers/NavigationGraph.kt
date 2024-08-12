package io.github.dmitrytsyvtsyn.fluently.happening_pickers

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.dialog
import io.github.dmitrytsyvtsyn.fluently.core.navigation.LocalNavController
import io.github.dmitrytsyvtsyn.fluently.happening_pickers.date.HappeningDatePicker
import io.github.dmitrytsyvtsyn.fluently.happening_pickers.time.HappeningTimePicker

fun NavGraphBuilder.dateTimePickerDestinations() {
    dialog(
        route = HappeningDatePickerDestination.route,
        arguments = HappeningDatePickerDestination.navArguments
    ) { backStackEntry ->
        val navController = LocalNavController.current
        HappeningDatePicker(
            params = HappeningDatePickerDestination.params(backStackEntry),
            dismiss = { navController.popBackStack() },
            apply = { date ->
                HappeningDatePickerDestination.putResult(navController, date)

                navController.popBackStack()
            }
        )
    }
    dialog(
        route = HappeningTimePickerDestination.route,
        arguments = HappeningTimePickerDestination.navArguments
    ) { backStackEntry ->
        val navController = LocalNavController.current
        HappeningTimePicker(
            params = HappeningTimePickerDestination.params(backStackEntry),
            dismiss = { navController.popBackStack() },
            apply = { startHours, startMinutes, endHours, endMinutes ->
                HappeningTimePickerDestination.putResult(
                    navController = navController,
                    result = HappeningTimePickerDestination.Result(
                        startHours,
                        startMinutes,
                        endHours,
                        endMinutes
                    )
                )

                navController.popBackStack()
            }
        )
    }
}