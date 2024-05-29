package io.github.dmitrytsyvtsyn.fluently.happening_list.viewmodel

import androidx.compose.runtime.Immutable
import io.github.dmitrytsyvtsyn.fluently.happening_list.components.TimeFactorForToday
import io.github.dmitrytsyvtsyn.fluently.happening_list.model.HappeningModel
import kotlinx.collections.immutable.PersistentList

data class HappeningListState(
    val nowDate: Long,
    val currentDate: Long,
    val currentPage: Int,
    val pages: PersistentList<HappeningListPagingState>
)

@Immutable
class HappeningListPagingState(
    val timeFactorForToday: TimeFactorForToday,
    val date: Long,
    val items: PersistentList<HappeningListItemState>
) {
    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (this === other) return true
        if (other !is HappeningListPagingState) return false

        return date == other.date && items == other.items
    }

    override fun hashCode(): Int {
        var result = date.hashCode()
        result = 31 * result + items.hashCode()
        return result
    }
}

sealed interface HappeningListItemState {

    data class Title(val value: String) : HappeningListItemState

    data class Content(
        val model: HappeningModel,
        val timingStatus: HappeningTimingStatus,
        val dayStatus: HappeningDayStatus
    ) : HappeningListItemState

    data class Timeline(val startDate: Long, val endDate: Long) : HappeningListItemState
}

enum class HappeningTimingStatus {
    PASSED, ACTUAL
}

enum class HappeningDayStatus {
    ONLY_TODAY, TOMORROW, YESTERDAY
}