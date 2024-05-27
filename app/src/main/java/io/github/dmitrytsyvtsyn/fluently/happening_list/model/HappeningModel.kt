package io.github.dmitrytsyvtsyn.fluently.happening_list.model

data class HappeningModel(
    val id: Long = 0,
    val eventId: Long = -1,
    val reminderId: Long = -1,
    val title: String,
    val startDate: Long,
    val endDate: Long
)