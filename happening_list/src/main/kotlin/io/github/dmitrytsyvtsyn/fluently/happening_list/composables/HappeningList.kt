package io.github.dmitrytsyvtsyn.fluently.happening_list.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.dmitrytsyvtsyn.fluently.data.model.HappeningModel
import io.github.dmitrytsyvtsyn.fluently.happening_list.viewmodel.HappeningListItemState
import kotlinx.collections.immutable.PersistentList

@Composable
internal fun HappeningList(
    listItemStates: PersistentList<HappeningListItemState>,
    onClick: (HappeningModel) -> Unit,
    onRemove: (HappeningModel) -> Unit,
    onView: (HappeningModel) -> Unit,
    goToYesterday: () -> Unit,
    goToTomorrow: () -> Unit,
    titleStyle: TextStyle = TextStyle(
        fontSize = 18.sp,
        fontWeight = FontWeight.Medium,
        color = MaterialTheme.colorScheme.primary
    )
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        listItemStates.forEach { listItemState ->
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
                    Text(
                        text = listItemState.value,
                        style = titleStyle
                    )
                }
                is HappeningListItemState.Timeline -> {
                    TimelineListItem(timeline = listItemState)
                }
            }
        }
    }
}