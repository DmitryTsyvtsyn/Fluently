package io.github.dmitrytsyvtsyn.fluently.happening_list.models

import androidx.compose.runtime.Immutable

@Immutable
internal class HappeningTabModel(
    val title: String,
    val onClick: () -> Unit
)