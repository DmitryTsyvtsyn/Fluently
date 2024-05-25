package io.github.dmitrytsyvtsyn.fluently.core.navigation

import android.os.Parcel
import android.os.Parcelable

sealed interface Screens {

    data object HappeningListScreen : Screens {
        const val NAME = "happening_list_screen"
    }

    data object HappeningDetailScreen : Screens {
        const val NAME = "happening_detail_screen"
        const val ID = "${NAME}_id"
        const val INITIAL_DATE = "${NAME}_initial_date"
    }

    data object HappeningThemeSettingsScreen : Screens {
        const val NAME = "happening_theme_settings_screen"
    }

    data object HappeningDatePickerDialog : Screens {
        const val NAME = "happening_date_picker_dialog"
        const val INITIAL_DATE = "${NAME}_initial_date"
        const val MIN_DATE = "${NAME}_min_date"
        const val MAX_DATE = "${NAME}_max_date"
        const val RESULT_KEY = "${NAME}_result_key"
    }

    data object HappeningTimePickerDialog : Screens {
        const val NAME = "happening_time_picker_dialog"

        const val START_HOURS = "${NAME}_start_hours"
        const val START_MINUTES = "${NAME}_start_minutes"

        const val END_HOURS = "${NAME}_end_hours"
        const val END_MINUTES = "${NAME}_end_minutes"

        const val RESULT_KEY = "${NAME}_result_key"

        data class Result(
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

}