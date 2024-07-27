package io.github.dmitrytsyvtsyn.fluently.happening_detail.viewmodel

import io.github.dmitrytsyvtsyn.fluently.HappeningRepositoryTestImpl
import io.github.dmitrytsyvtsyn.fluently.data.HappeningModel
import io.github.dmitrytsyvtsyn.fluently.happening_detail.usecases.HappeningFetchSuggestionsUseCase
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDateTime
import org.junit.Assert.assertEquals
import org.junit.Test

class HappeningFetchSuggestionsUseCaseTest {

    @Test
    fun `test when no suggestions`() = runBlocking {
        val startDateTime = LocalDateTime(2024, 7, 26, 16, 30)
        val endDateTime = LocalDateTime(2024, 7, 26, 18, 0)

        val mockHappenings = listOf(
            HappeningModel(
                startDateTime = LocalDateTime(2024, 7, 26, 18, 5),
                endDateTime = LocalDateTime(2024, 7, 26, 20, 0)
            )
        )

        val useCase = HappeningFetchSuggestionsUseCase(HappeningRepositoryTestImpl(mockHappenings))
        val actual = useCase.execute(startDateTime, endDateTime)
        val expected = HappeningFetchSuggestionsUseCase.FetchSuggestionsUseCaseResult.NoSuggestions

        assertEquals(expected, actual)
    }

    @Test
    fun `test when suggestions is not empty`() = runBlocking {
        val startDateTime = LocalDateTime(2024, 7, 27, 10, 0)
        val endDateTime = LocalDateTime(2024, 7, 27, 12, 30)

        val plannedHappening = HappeningModel(
            startDateTime = LocalDateTime(2024, 7, 27, 11, 30),
            endDateTime = LocalDateTime(2024, 7, 27, 12, 30)
        )
        val mockHappenings = listOf(
            plannedHappening,
            HappeningModel(
                startDateTime = LocalDateTime(2024, 7, 27, 20, 0),
                endDateTime = LocalDateTime(2024, 7, 27, 22, 30)
            ),
            HappeningModel(
                startDateTime = LocalDateTime(2024, 7, 28, 13, 0),
                endDateTime = LocalDateTime(2024, 7, 28, 17, 0)
            ),
            HappeningModel(
                startDateTime = LocalDateTime(2024, 7, 30, 11, 30),
                endDateTime = LocalDateTime(2024, 7, 30, 12, 30)
            ),
            HappeningModel(
                startDateTime = LocalDateTime(2024, 8, 2, 11, 30),
                endDateTime = LocalDateTime(2024, 8, 2, 12, 30)
            ),
        )

        val useCase = HappeningFetchSuggestionsUseCase(HappeningRepositoryTestImpl(mockHappenings))
        val actual = useCase.execute(startDateTime, endDateTime)
        val expected = HappeningFetchSuggestionsUseCase.FetchSuggestionsUseCaseResult.Suggestions(
            plannedHappenings = listOf(plannedHappening),
            ranges = listOf(
                LocalDateTime(2024, 7, 28, 10, 0)..LocalDateTime(2024, 7, 28, 12, 30),
                LocalDateTime(2024, 7, 29, 10, 0)..LocalDateTime(2024, 7, 29, 12, 30),
                LocalDateTime(2024, 7, 31, 10, 0)..LocalDateTime(2024, 7, 31, 12, 30),
                LocalDateTime(2024, 8, 1, 10, 0)..LocalDateTime(2024, 8, 1, 12, 30),
                LocalDateTime(2024, 8, 3, 10, 0)..LocalDateTime(2024, 8, 3, 12, 30),
                LocalDateTime(2024, 8, 4, 10, 0)..LocalDateTime(2024, 8, 4, 12, 30),
            )
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `test when suggestions is not empty, first week is busy`() = runBlocking {
        val startDateTime = LocalDateTime(2024, 7, 27, 10, 0)
        val endDateTime = LocalDateTime(2024, 7, 27, 12, 30)

        val plannedHappening = HappeningModel(
            startDateTime = LocalDateTime(2024, 7, 27, 11, 30),
            endDateTime = LocalDateTime(2024, 7, 27, 12, 30)
        )
        val mockHappenings = listOf(
            plannedHappening,
            HappeningModel(
                startDateTime = LocalDateTime(2024, 7, 28, 12, 0),
                endDateTime = LocalDateTime(2024, 7, 28, 14, 40)
            ),
            HappeningModel(
                startDateTime = LocalDateTime(2024, 7, 29, 9, 0),
                endDateTime = LocalDateTime(2024, 7, 29, 14, 40)
            ),
            HappeningModel(
                startDateTime = LocalDateTime(2024, 7, 30, 11, 30),
                endDateTime = LocalDateTime(2024, 7, 30, 12, 30)
            ),
            HappeningModel(
                startDateTime = LocalDateTime(2024, 7, 31, 11, 30),
                endDateTime = LocalDateTime(2024, 7, 31, 12, 30)
            ),
            HappeningModel(
                startDateTime = LocalDateTime(2024, 8, 1, 2, 45),
                endDateTime = LocalDateTime(2024, 8, 1, 10, 50)
            ),
            HappeningModel(
                startDateTime = LocalDateTime(2024, 8, 2, 11, 30),
                endDateTime = LocalDateTime(2024, 8, 2, 12, 30)
            ),
            HappeningModel(
                startDateTime = LocalDateTime(2024, 8, 3, 7, 0),
                endDateTime = LocalDateTime(2024, 8, 3, 12, 0)
            ),
            HappeningModel(
                startDateTime = LocalDateTime(2024, 8, 4, 8, 0),
                endDateTime = LocalDateTime(2024, 8, 4, 10, 30)
            ),
            HappeningModel(
                startDateTime = LocalDateTime(2024, 8, 9, 8, 0),
                endDateTime = LocalDateTime(2024, 8, 9, 18, 0)
            ),
        )

        val useCase = HappeningFetchSuggestionsUseCase(HappeningRepositoryTestImpl(mockHappenings))
        val actual = useCase.execute(startDateTime, endDateTime)
        val expected = HappeningFetchSuggestionsUseCase.FetchSuggestionsUseCaseResult.Suggestions(
            plannedHappenings = listOf(plannedHappening),
            ranges = listOf(
                LocalDateTime(2024, 8, 5, 10, 0)..LocalDateTime(2024, 8, 5, 12, 30),
                LocalDateTime(2024, 8, 6, 10, 0)..LocalDateTime(2024, 8, 6, 12, 30),
                LocalDateTime(2024, 8, 7, 10, 0)..LocalDateTime(2024, 8, 7, 12, 30),
                LocalDateTime(2024, 8, 8, 10, 0)..LocalDateTime(2024, 8, 8, 12, 30),
                LocalDateTime(2024, 8, 10, 10, 0)..LocalDateTime(2024, 8, 10, 12, 30),
                LocalDateTime(2024, 8, 11, 10, 0)..LocalDateTime(2024, 8, 11, 12, 30),
                LocalDateTime(2024, 8, 12, 10, 0)..LocalDateTime(2024, 8, 12, 12, 30),
            )
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `test when suggestions is not empty, startDate and endDate are different`() = runBlocking {
        val startDateTime = LocalDateTime(2024, 7, 27, 20, 0)
        val endDateTime = LocalDateTime(2024, 7, 28, 8, 0)

        val plannedHappening = HappeningModel(
            startDateTime = LocalDateTime(2024, 7, 27, 20, 30),
            endDateTime = LocalDateTime(2024, 7, 28, 10, 30)
        )
        val mockHappenings = listOf(
            plannedHappening,
            HappeningModel(
                startDateTime = LocalDateTime(2024, 7, 30, 23, 30),
                endDateTime = LocalDateTime(2024, 7, 31, 7, 50)
            ),
            HappeningModel(
                startDateTime = LocalDateTime(2024, 8, 2, 4, 0),
                endDateTime = LocalDateTime(2024, 8, 3, 4, 0)
            ),
        )

        val useCase = HappeningFetchSuggestionsUseCase(HappeningRepositoryTestImpl(mockHappenings))
        val actual = useCase.execute(startDateTime, endDateTime)
        val expected = HappeningFetchSuggestionsUseCase.FetchSuggestionsUseCaseResult.Suggestions(
            plannedHappenings = listOf(plannedHappening),
            ranges = listOf(
                LocalDateTime(2024, 7, 28, 20, 0)..LocalDateTime(2024, 7, 29, 8, 0),
                LocalDateTime(2024, 7, 29, 20, 0)..LocalDateTime(2024, 7, 30, 8, 0),
                LocalDateTime(2024, 7, 31, 20, 0)..LocalDateTime(2024, 8, 1, 8, 0),
                LocalDateTime(2024, 8, 3, 20, 0)..LocalDateTime(2024, 8, 4, 8, 0),
                LocalDateTime(2024, 8, 4, 20, 0)..LocalDateTime(2024, 8, 5, 8, 0),
                LocalDateTime(2024, 8, 5, 20, 0)..LocalDateTime(2024, 8, 6, 8, 0),
            )
        )

        assertEquals(expected, actual)
    }

}