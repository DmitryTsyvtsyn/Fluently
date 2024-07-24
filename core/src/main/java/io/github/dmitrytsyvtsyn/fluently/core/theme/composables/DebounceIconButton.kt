package io.github.dmitrytsyvtsyn.fluently.core.theme.composables

import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import io.github.dmitrytsyvtsyn.fluently.core.data.CalendarRepository
import io.github.dmitrytsyvtsyn.fluently.core.datetime.toEpochMillis

@Composable
fun DebounceIconButton(
    intervalInMillis: Long = 500,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    val state = remember { mutableLongStateOf(0L) }
    IconButton(
        onClick = {
            val currentDateTimeMillis = CalendarRepository.nowDateTime().toEpochMillis()
            val lastDateTimeMillis = state.longValue
            if (currentDateTimeMillis - lastDateTimeMillis >= intervalInMillis) {
                state.longValue = currentDateTimeMillis
                onClick.invoke()
            }
        },
        content = content
    )
}