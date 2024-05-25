package io.github.dmitrytsyvtsyn.fluently.happening_list.viewmodel

import io.github.dmitrytsyvtsyn.fluently.core.viewmodel.ViewSideEffect

sealed interface HappeningListSideEffect : ViewSideEffect {
    data class ShowDetail(val id: Long = -1, val date: Long = System.currentTimeMillis()) :
        HappeningListSideEffect
    data class ShowDatePicker(val date: Long) : HappeningListSideEffect
}