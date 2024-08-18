package io.github.dmitrytsyvtsyn.fluently.happening_list.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.dmitrytsyvtsyn.fluently.core.theme.FluentlyTheme
import io.github.dmitrytsyvtsyn.fluently.core.theme.composables.FluentlyText
import io.github.dmitrytsyvtsyn.fluently.data.model.HappeningModel
import io.github.dmitrytsyvtsyn.fluently.happening_list.viewmodel.HappeningListItemState
import kotlinx.collections.immutable.PersistentList

@Composable
internal fun HappeningList(
    items: PersistentList<HappeningListItemState>,
    onClick: (HappeningModel) -> Unit,
    onRemove: (HappeningModel) -> Unit,
    onView: (HappeningModel) -> Unit,
    goToYesterday: () -> Unit,
    goToTomorrow: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items.forEach { listItemState ->
            when (listItemState) {
                is HappeningListItemState.Content -> {
                    HappeningContentListItem(
                        item = listItemState,
                        onClick = onClick,
                        onRemove = onRemove,
                        onCalendarView = onView,
                        goToYesterday = goToYesterday,
                        goToTomorrow = goToTomorrow
                    )
                }
                is HappeningListItemState.Title -> {
                    FluentlyText(
                        text = listItemState.value,
                        style = FluentlyTheme.typography.caption3
                    )
                }
                is HappeningListItemState.Timeline -> {
                    TimelineListItem(timeline = listItemState)
                }
            }
        }
    }
}