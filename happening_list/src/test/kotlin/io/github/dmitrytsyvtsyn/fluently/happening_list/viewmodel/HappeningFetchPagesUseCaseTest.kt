package io.github.dmitrytsyvtsyn.fluently.happening_list.viewmodel

import io.github.dmitrytsyvtsyn.fluently.HappeningRepositoryTestImpl
import io.github.dmitrytsyvtsyn.fluently.core.data.IdLong
import io.github.dmitrytsyvtsyn.fluently.data.model.HappeningModel
import io.github.dmitrytsyvtsyn.fluently.happening_list.usecases.HappeningFetchPagesUseCase
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.DateTimePeriod
import kotlinx.datetime.LocalDateTime
import org.junit.Assert.assertEquals
import org.junit.Test

class HappeningFetchPagesUseCaseTest {

    @Test
    fun `test when happenings is not empty`() = runBlocking {
        val tenthJulyModel = HappeningModel(
            id = IdLong(1),
            startDateTime = LocalDateTime(2024, 7, 10, 13, 40),
            endDateTime = LocalDateTime(2024, 7, 10, 15, 40)
        )
        val tenthJulyToEleventhJulyModel = HappeningModel(
            id = IdLong(1),
            startDateTime = LocalDateTime(2024, 7, 10, 23, 30),
            endDateTime = LocalDateTime(2024, 7, 11, 1, 30)
        )
        val thirteenthJulyModel1 = HappeningModel(
            id = IdLong(1),
            startDateTime = LocalDateTime(2024, 7, 13, 4, 0),
            endDateTime = LocalDateTime(2024, 7, 13, 12, 0)
        )
        val thirteenthJulyModel2 = HappeningModel(
            id = IdLong(1),
            startDateTime = LocalDateTime(2024, 7, 13, 13, 0),
            endDateTime = LocalDateTime(2024, 7, 13, 20, 0)
        )
        val nineteenthJulyToTwentiethJulyModel = HappeningModel(
            id = IdLong(1),
            startDateTime = LocalDateTime(2024, 7, 19, 20, 0),
            endDateTime = LocalDateTime(2024, 7, 20, 6, 0)
        )
        val twentiethJulyModel = HappeningModel(
            id = IdLong(1),
            startDateTime = LocalDateTime(2024, 7, 20, 11, 40),
            endDateTime = LocalDateTime(2024, 7, 20, 15, 30)
        )

        val mockHappenings = listOf(
            tenthJulyModel,
            tenthJulyToEleventhJulyModel,
            thirteenthJulyModel1,
            thirteenthJulyModel2,
            nineteenthJulyToTwentiethJulyModel,
            twentiethJulyModel
        )

        val useCase = HappeningFetchPagesUseCase(HappeningRepositoryTestImpl(mockHappenings))
        val actual = useCase.execute(
            startDateTime = LocalDateTime(2024, 7, 7, 0, 0),
            endDateTime = LocalDateTime(2024, 7, 23, 0, 0)
        )
        val expected = listOf(
            HappeningFetchPagesUseCase.FetchPageUseCaseItems(
                dateTime = LocalDateTime(2024, 7, 7, 0, 0)
            ).add(
                HappeningFetchPagesUseCase.FetchPageUseCaseItem.TimeSpaceItem(
                    period = DateTimePeriod(hours = 24)
                )
            ),
            HappeningFetchPagesUseCase.FetchPageUseCaseItems(
                dateTime = LocalDateTime(2024, 7, 8, 0, 0)
            ).add(
                HappeningFetchPagesUseCase.FetchPageUseCaseItem.TimeSpaceItem(
                    period = DateTimePeriod(hours = 24)
                )
            ),
            HappeningFetchPagesUseCase.FetchPageUseCaseItems(
                dateTime = LocalDateTime(2024, 7, 9, 0, 0)
            ).add(
                HappeningFetchPagesUseCase.FetchPageUseCaseItem.TimeSpaceItem(
                    period = DateTimePeriod(hours = 24)
                )
            ),
            HappeningFetchPagesUseCase.FetchPageUseCaseItems(
                dateTime = LocalDateTime(2024, 7, 10, 0, 0)
            ).add(
                HappeningFetchPagesUseCase.FetchPageUseCaseItem.TimeSpaceItem(
                    period = DateTimePeriod(hours = 13, minutes = 40)
                )
            ).add(
                HappeningFetchPagesUseCase.FetchPageUseCaseItem.HappeningItem(
                    model = tenthJulyModel
                )
            ).add(
                HappeningFetchPagesUseCase.FetchPageUseCaseItem.TimeSpaceItem(
                    period = DateTimePeriod(hours = 7, minutes = 50)
                )
            ).add(
                HappeningFetchPagesUseCase.FetchPageUseCaseItem.HappeningItem(
                    model = tenthJulyToEleventhJulyModel
                )
            ),
            HappeningFetchPagesUseCase.FetchPageUseCaseItems(
                dateTime = LocalDateTime(2024, 7, 11, 0, 0)
            ).add(
                HappeningFetchPagesUseCase.FetchPageUseCaseItem.HappeningItem(
                    model = tenthJulyToEleventhJulyModel
                )
            ).add(
                HappeningFetchPagesUseCase.FetchPageUseCaseItem.TimeSpaceItem(
                    period = DateTimePeriod(hours = 22, minutes = 30)
                )
            ),
            HappeningFetchPagesUseCase.FetchPageUseCaseItems(
                dateTime = LocalDateTime(2024, 7, 12, 0, 0)
            ).add(
                HappeningFetchPagesUseCase.FetchPageUseCaseItem.TimeSpaceItem(
                    period = DateTimePeriod(hours = 24)
                )
            ),
            HappeningFetchPagesUseCase.FetchPageUseCaseItems(
                dateTime = LocalDateTime(2024, 7, 13, 0, 0)
            ).add(
                HappeningFetchPagesUseCase.FetchPageUseCaseItem.TimeSpaceItem(
                    period = DateTimePeriod(hours = 4, minutes = 0)
                )
            ).add(
                HappeningFetchPagesUseCase.FetchPageUseCaseItem.HappeningItem(
                    model = thirteenthJulyModel1
                )
            ).add(
                HappeningFetchPagesUseCase.FetchPageUseCaseItem.TimeSpaceItem(
                    period = DateTimePeriod(hours = 1, minutes = 0)
                )
            ).add(
                HappeningFetchPagesUseCase.FetchPageUseCaseItem.HappeningItem(
                    model = thirteenthJulyModel2
                )
            ).add(
                HappeningFetchPagesUseCase.FetchPageUseCaseItem.TimeSpaceItem(
                    period = DateTimePeriod(hours = 4)
                )
            ),
            HappeningFetchPagesUseCase.FetchPageUseCaseItems(
                dateTime = LocalDateTime(2024, 7, 14, 0, 0)
            ).add(
                HappeningFetchPagesUseCase.FetchPageUseCaseItem.TimeSpaceItem(
                    period = DateTimePeriod(hours = 24)
                )
            ),
            HappeningFetchPagesUseCase.FetchPageUseCaseItems(
                dateTime = LocalDateTime(2024, 7, 15, 0, 0)
            ).add(
                HappeningFetchPagesUseCase.FetchPageUseCaseItem.TimeSpaceItem(
                    period = DateTimePeriod(hours = 24)
                )
            ),
            HappeningFetchPagesUseCase.FetchPageUseCaseItems(
                dateTime = LocalDateTime(2024, 7, 16, 0, 0)
            ).add(
                HappeningFetchPagesUseCase.FetchPageUseCaseItem.TimeSpaceItem(
                    period = DateTimePeriod(hours = 24)
                )
            ),
            HappeningFetchPagesUseCase.FetchPageUseCaseItems(
                dateTime = LocalDateTime(2024, 7, 17, 0, 0)
            ).add(
                HappeningFetchPagesUseCase.FetchPageUseCaseItem.TimeSpaceItem(
                    period = DateTimePeriod(hours = 24)
                )
            ),
            HappeningFetchPagesUseCase.FetchPageUseCaseItems(
                dateTime = LocalDateTime(2024, 7, 18, 0, 0)
            ).add(
                HappeningFetchPagesUseCase.FetchPageUseCaseItem.TimeSpaceItem(
                    period = DateTimePeriod(hours = 24)
                )
            ),
            HappeningFetchPagesUseCase.FetchPageUseCaseItems(
                dateTime = LocalDateTime(2024, 7, 19, 0, 0)
            ).add(
                HappeningFetchPagesUseCase.FetchPageUseCaseItem.TimeSpaceItem(
                    period = DateTimePeriod(hours = 20)
                )
            ).add(
                HappeningFetchPagesUseCase.FetchPageUseCaseItem.HappeningItem(
                    model = nineteenthJulyToTwentiethJulyModel
                )
            ),
            HappeningFetchPagesUseCase.FetchPageUseCaseItems(
                dateTime = LocalDateTime(2024, 7, 20, 0, 0)
            ).add(
                HappeningFetchPagesUseCase.FetchPageUseCaseItem.HappeningItem(
                    model = nineteenthJulyToTwentiethJulyModel
                )
            ).add(
                HappeningFetchPagesUseCase.FetchPageUseCaseItem.TimeSpaceItem(
                    period = DateTimePeriod(hours = 5, minutes = 40)
                )
            ).add(
                HappeningFetchPagesUseCase.FetchPageUseCaseItem.HappeningItem(
                    model = twentiethJulyModel
                )
            ).add(
                HappeningFetchPagesUseCase.FetchPageUseCaseItem.TimeSpaceItem(
                    period = DateTimePeriod(hours = 8, minutes = 30)
                )
            ),
            HappeningFetchPagesUseCase.FetchPageUseCaseItems(
                dateTime = LocalDateTime(2024, 7, 21, 0, 0)
            ).add(
                HappeningFetchPagesUseCase.FetchPageUseCaseItem.TimeSpaceItem(
                    period = DateTimePeriod(hours = 24)
                )
            ),
            HappeningFetchPagesUseCase.FetchPageUseCaseItems(
                dateTime = LocalDateTime(2024, 7, 22, 0, 0)
            ).add(
                HappeningFetchPagesUseCase.FetchPageUseCaseItem.TimeSpaceItem(
                    period = DateTimePeriod(hours = 24)
                )
            ),
            HappeningFetchPagesUseCase.FetchPageUseCaseItems(
                dateTime = LocalDateTime(2024, 7, 23, 0, 0)
            ).add(
                HappeningFetchPagesUseCase.FetchPageUseCaseItem.TimeSpaceItem(
                    period = DateTimePeriod(hours = 24)
                )
            )
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `test when happenings is empty`() = runBlocking {
        val useCase = HappeningFetchPagesUseCase(HappeningRepositoryTestImpl(listOf()))
        val actual = useCase.execute(
            startDateTime = LocalDateTime(2024, 7, 7, 0, 0),
            endDateTime = LocalDateTime(2024, 7, 14, 0, 0)
        )
        val expected = listOf(
            HappeningFetchPagesUseCase.FetchPageUseCaseItems(
                dateTime = LocalDateTime(2024, 7, 7, 0, 0)
            ).add(
                HappeningFetchPagesUseCase.FetchPageUseCaseItem.TimeSpaceItem(
                    period = DateTimePeriod(hours = 24)
                )
            ),
            HappeningFetchPagesUseCase.FetchPageUseCaseItems(
                dateTime = LocalDateTime(2024, 7, 8, 0, 0)
            ).add(
                HappeningFetchPagesUseCase.FetchPageUseCaseItem.TimeSpaceItem(
                    period = DateTimePeriod(hours = 24)
                )
            ),
            HappeningFetchPagesUseCase.FetchPageUseCaseItems(
                dateTime = LocalDateTime(2024, 7, 9, 0, 0)
            ).add(
                HappeningFetchPagesUseCase.FetchPageUseCaseItem.TimeSpaceItem(
                    period = DateTimePeriod(hours = 24)
                )
            ),
            HappeningFetchPagesUseCase.FetchPageUseCaseItems(
                dateTime = LocalDateTime(2024, 7, 10, 0, 0)
            ).add(
                HappeningFetchPagesUseCase.FetchPageUseCaseItem.TimeSpaceItem(
                    period = DateTimePeriod(hours = 24)
                )
            ),
            HappeningFetchPagesUseCase.FetchPageUseCaseItems(
                dateTime = LocalDateTime(2024, 7, 11, 0, 0)
            ).add(
                HappeningFetchPagesUseCase.FetchPageUseCaseItem.TimeSpaceItem(
                    period = DateTimePeriod(hours = 24)
                )
            ),
            HappeningFetchPagesUseCase.FetchPageUseCaseItems(
                dateTime = LocalDateTime(2024, 7, 12, 0, 0)
            ).add(
                HappeningFetchPagesUseCase.FetchPageUseCaseItem.TimeSpaceItem(
                    period = DateTimePeriod(hours = 24)
                )
            ),
            HappeningFetchPagesUseCase.FetchPageUseCaseItems(
                dateTime = LocalDateTime(2024, 7, 13, 0, 0)
            ).add(
                HappeningFetchPagesUseCase.FetchPageUseCaseItem.TimeSpaceItem(
                    period = DateTimePeriod(hours = 24)
                )
            ),
            HappeningFetchPagesUseCase.FetchPageUseCaseItems(
                dateTime = LocalDateTime(2024, 7, 14, 0, 0)
            ).add(
                HappeningFetchPagesUseCase.FetchPageUseCaseItem.TimeSpaceItem(
                    period = DateTimePeriod(hours = 24)
                )
            )
        )

        assertEquals(expected, actual)
    }

}