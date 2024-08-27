package io.github.dmitrytsyvtsyn.fluently.happening_detail.viewmodel

import kotlinx.collections.immutable.PersistentList
import kotlinx.datetime.LocalDateTime

internal sealed interface HappeningSuggestionsState {

    class Suggestions(val ranges: PersistentList<ClosedRange<LocalDateTime>>) :
        HappeningSuggestionsState

    data object NoSuggestions : HappeningSuggestionsState
}