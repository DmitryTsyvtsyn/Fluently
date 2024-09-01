package io.github.dmitrytsyvtsyn.fluently.core.compose

import android.content.res.Configuration
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier

@Stable
fun Modifier.configurations(
    configuration: Configuration,
    landscape: Modifier = Modifier,
    portrait: Modifier = Modifier
): Modifier {
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    return if (isLandscape) {
        then(landscape)
    } else {
        then(portrait)
    }
}