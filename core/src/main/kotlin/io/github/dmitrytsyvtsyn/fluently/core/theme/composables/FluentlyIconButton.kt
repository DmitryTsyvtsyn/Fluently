package io.github.dmitrytsyvtsyn.fluently.core.theme.composables

import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import io.github.dmitrytsyvtsyn.fluently.core.datetime.DateTimeExtensions
import io.github.dmitrytsyvtsyn.fluently.core.theme.FluentlyTheme

@Composable
fun FluentlyIconButton(
    modifier: Modifier = Modifier,
    debounceIntervalInMillis: Long = 500,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    val state = remember { mutableLongStateOf(0L) }
    IconButton(
        onClick = {
            val currentDateTimeMillis = DateTimeExtensions.nowDateTimeMillis()
            val lastDateTimeMillis = state.longValue
            if (currentDateTimeMillis - lastDateTimeMillis >= debounceIntervalInMillis) {
                state.longValue = currentDateTimeMillis
                onClick.invoke()
            }
        },
        modifier = modifier,
        colors = IconButtonDefaults.iconButtonColors(
            contentColor = FluentlyTheme.colors.primaryTextColor
        ),
        content = content
    )
}