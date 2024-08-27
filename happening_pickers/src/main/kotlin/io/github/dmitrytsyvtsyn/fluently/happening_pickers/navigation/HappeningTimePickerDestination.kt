package io.github.dmitrytsyvtsyn.fluently.happening_pickers.navigation

import android.os.Parcel
import android.os.Parcelable
import androidx.compose.runtime.Immutable
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavType
import androidx.navigation.navArgument
import io.github.dmitrytsyvtsyn.fluently.core.navigation.NavigationDestination

data object HappeningTimePickerDestination : NavigationDestination<HappeningTimePickerDestination.Params, HappeningTimePickerDestination.Result>() {

    private const val NAME = "happening_time_picker_dialog"

    private const val START_HOURS = "${NAME}_start_hours"
    private const val START_MINUTES = "${NAME}_start_minutes"

    private const val END_HOURS = "${NAME}_end_hours"
    private const val END_MINUTES = "${NAME}_end_minutes"

    override val name: String = NAME
    override val defaultResult: Result = Result()
    override val navArguments: List<NamedNavArgument>
        get() = listOf(
            navArgument(START_HOURS) { type = NavType.IntType },
            navArgument(START_MINUTES) { type = NavType.IntType },
            navArgument(END_HOURS) { type = NavType.IntType },
            navArgument(END_MINUTES) { type = NavType.IntType }
        )

    override fun params(backStackEntry: NavBackStackEntry): Params {
        val arguments = backStackEntry.arguments ?: error("NavBackStackEntry.arguments is empty!")

        return Params(
            startHours = arguments.getInt(START_HOURS),
            startMinutes = arguments.getInt(START_MINUTES),
            endHours = arguments.getInt(END_HOURS),
            endMinutes = arguments.getInt(END_MINUTES)
        )
    }

    @Immutable
    class Params(
        val startHours: Int,
        val startMinutes: Int,
        val endHours: Int,
        val endMinutes: Int
    ) : NavigationParams {

        override val route: String = "$NAME?$START_HOURS=$startHours&$START_MINUTES=$startMinutes&$END_HOURS=$endHours&$END_MINUTES=$endMinutes"

    }

    @Immutable
    class Result(
        val startHours: Int = -1,
        val startMinutes: Int = -1,
        val endHours: Int = -1,
        val endMinutes: Int = -1
    ) : Parcelable {

        val isNotEmpty: Boolean
            get() = startHours != -1 && startMinutes != -1 && endHours != -1 && endMinutes != -1

        constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt()
        )

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeInt(startHours)
            parcel.writeInt(startMinutes)
            parcel.writeInt(endHours)
            parcel.writeInt(endMinutes)
        }

        override fun describeContents(): Int = 0

        companion object CREATOR : Parcelable.Creator<Result> {
            override fun createFromParcel(parcel: Parcel): Result {
                return Result(parcel)
            }

            override fun newArray(size: Int): Array<Result?> {
                return arrayOfNulls(size)
            }
        }

    }

}