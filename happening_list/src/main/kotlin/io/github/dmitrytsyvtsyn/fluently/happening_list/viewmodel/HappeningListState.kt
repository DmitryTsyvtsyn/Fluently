package io.github.dmitrytsyvtsyn.fluently.happening_list.viewmodel

import androidx.compose.runtime.Immutable
import io.github.dmitrytsyvtsyn.fluently.data.HappeningModel
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.datetime.DateTimePeriod
import kotlinx.datetime.LocalDateTime

internal data class HappeningListState(
    val nowDateTime: LocalDateTime,
    val currentDateTime: LocalDateTime,
    val currentPage: Int = 0,
    val pages: PersistentList<HappeningListPagingState> = persistentListOf()
)

@Immutable
internal class HappeningListPagingState(
    val dateTime: LocalDateTime,
    val items: PersistentList<HappeningListItemState>
) {
    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (this === other) return true
        if (other !is HappeningListPagingState) return false

        return dateTime == other.dateTime && items == other.items
    }

    override fun hashCode(): Int {
        var result = dateTime.hashCode()
        result = 31 * result + items.hashCode()
        return result
    }
}

internal sealed interface HappeningListItemState {

    data class Title(val value: String) : HappeningListItemState

    data class Content(
        val model: HappeningModel,
        val timingStatus: HappeningRunningStatus,
        val dayStatus: HappeningDayStatus
    ) : HappeningListItemState

    data class Timeline(val period: DateTimePeriod) : HappeningListItemState
}

internal enum class HappeningRunningStatus {
    PASSED, ACTUAL
}

internal enum class HappeningDayStatus {
    ONLY_TODAY, TODAY_AND_TOMORROW, TODAY_AND_YESTERDAY
}