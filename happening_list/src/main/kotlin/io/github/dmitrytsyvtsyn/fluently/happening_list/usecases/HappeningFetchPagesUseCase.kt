package io.github.dmitrytsyvtsyn.fluently.happening_list.usecases

import io.github.dmitrytsyvtsyn.fluently.core.datetime.compareTo
import io.github.dmitrytsyvtsyn.fluently.core.datetime.plus
import io.github.dmitrytsyvtsyn.fluently.core.datetime.toEpochMillis
import io.github.dmitrytsyvtsyn.fluently.data.HappeningModel
import io.github.dmitrytsyvtsyn.fluently.data.HappeningRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.DateTimePeriod
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime

class HappeningFetchPagesUseCase(private val repository: HappeningRepository) {

    suspend fun execute(startDateTime: LocalDateTime, endDateTime: LocalDateTime) =
        withContext<List<FetchPageUseCaseItems>>(Dispatchers.Default) {
            val happenings = repository.fetch(startDateTime, endDateTime)

            val pages = mutableListOf<FetchPageUseCaseItems>()
            var currentDateTime = startDateTime
            while (currentDateTime <= endDateTime) {
                pages.add(FetchPageUseCaseItems(dateTime = currentDateTime))

                currentDateTime = currentDateTime.plus(1, DateTimeUnit.DAY)
            }

            val minimumTimeSpacePeriod = DateTimePeriod(minutes = 15)
            val pagesSize = pages.size

            var currentPageIndex = 0
            var currentHappeningIndex = 0
            var currentTime = LocalTime(0, 0)
            while (currentPageIndex < pagesSize) {
                val currentPage = pages[currentPageIndex]
                val currentPageDate = currentPage.dateTime.date
                val nextPageDate = pages.getOrNull(currentPageIndex + 1)?.dateTime?.date
                    ?: LocalDate(1, 1, 1)

                val currentHappening = happenings.getOrNull(currentHappeningIndex)

                if (currentHappening != null && currentPageDate == currentHappening.startDateTime.date) {
                    val period = currentHappening.startDateTime.time.minus(currentTime)
                    if (period >= minimumTimeSpacePeriod) {
                        currentPage.add(FetchPageUseCaseItem.TimeSpaceItem(period))
                    }
                    currentPage.add(FetchPageUseCaseItem.HappeningItem(currentHappening))
                    currentTime = currentHappening.endDateTime.time

                    if (nextPageDate == currentHappening.endDateTime.date) {
                        currentPageIndex++
                    } else {
                        currentHappeningIndex++
                    }

                    continue
                }

                if (currentHappening != null && currentPageDate == currentHappening.endDateTime.date) {
                    currentPage.add(FetchPageUseCaseItem.HappeningItem(currentHappening))
                    currentTime = currentHappening.endDateTime.time
                    currentHappeningIndex++

                    continue
                }

                if (currentPage.items.isEmpty()) {
                    currentPage.add(
                        FetchPageUseCaseItem.TimeSpaceItem(DateTimePeriod(hours = 24))
                    )
                } else {
                    val period = 24.minusTimeFromHours(currentTime)
                    currentPage.add(
                        FetchPageUseCaseItem.TimeSpaceItem(period)
                    )
                }

                currentTime = LocalTime(0, 0)
                currentPageIndex++
            }

            pages
        }

    private fun Int.minusTimeFromHours(time: LocalTime): DateTimePeriod {
        val hours = this
        return DateTimePeriod(
            hours = hours - time.hour,
            minutes = -time.minute,
            seconds = -time.second,
            nanoseconds = -time.nanosecond.toLong()
        )
    }

    private fun LocalTime.minus(other: LocalTime): DateTimePeriod {
        val current = this
        return DateTimePeriod(
            hours = current.hour - other.hour,
            minutes = current.minute - other.minute,
            seconds = current.second - other.second,
            nanoseconds = (current.nanosecond - other.nanosecond).toLong()
        )
    }

    sealed interface FetchPageUseCaseItem {
        data class HappeningItem(val model: HappeningModel) : FetchPageUseCaseItem
        data class TimeSpaceItem(val period: DateTimePeriod) : FetchPageUseCaseItem
    }

    class FetchPageUseCaseItems(val dateTime: LocalDateTime) {

        private val _items = mutableListOf<FetchPageUseCaseItem>()
        val items: List<FetchPageUseCaseItem> = _items

        fun add(item: FetchPageUseCaseItem) : FetchPageUseCaseItems {
            _items.add(item)
            return this
        }

        override fun equals(other: Any?): Boolean {
            if (other == null) return false
            if (this === other) return true
            if (other !is FetchPageUseCaseItems) return false

            return dateTime == other.dateTime && _items == other._items
        }

        override fun hashCode(): Int {
            var result = dateTime.hashCode()
            result = 31 * result + _items.hashCode()
            result = 31 * result + items.hashCode()
            return result
        }

        override fun toString(): String {
            val itemsString = _items.joinToString(", ") { item -> item.toString() }
            return "dateTime = $dateTime, items = listOf($itemsString)"
        }

    }

}