package io.github.dmitrytsyvtsyn.interfunny.core.theme.components

import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember

@Composable
fun DebounceIconButton(
    interval: Long = 500,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    val lastTime = remember { mutableLongStateOf(0L) }
    IconButton(onClick = {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastTime.longValue >= interval) {
            lastTime.longValue = currentTime
            onClick.invoke()
        }
    }, content = content)
}