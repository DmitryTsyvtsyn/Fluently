package io.github.dmitrytsyvtsyn.fluently.happening_list.composables

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.dmitrytsyvtsyn.fluently.core.data.CalendarRepository
import io.github.dmitrytsyvtsyn.fluently.data.HappeningModel
import io.github.dmitrytsyvtsyn.fluently.happening_list.R
import io.github.dmitrytsyvtsyn.fluently.happening_list.formatFloatingHours
import io.github.dmitrytsyvtsyn.fluently.happening_list.viewmodel.HappeningListItemState
import io.github.dmitrytsyvtsyn.fluently.happening_list.viewmodel.HappeningTimingStatus
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun HappeningContentListItem(
    item: HappeningListItemState.Content,
    onClick: (HappeningModel) -> Unit,
    onRemove: (HappeningModel) -> Unit,
    onCalendarView: (HappeningModel) -> Unit,
    goToYesterday: () -> Unit,
    goToTomorrow: () -> Unit
) {
    val dropdownExpanded = remember { mutableStateOf(false) }

    val model = item.model
    Box(
        modifier = Modifier
            .alpha(
                when (item.timingStatus) {
                    HappeningTimingStatus.ACTUAL -> 1f
                    HappeningTimingStatus.PASSED -> 0.5f
                }
            )
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(8.dp)
            )
            .combinedClickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(bounded = true),
                onClick = { onClick.invoke(model) },
                onLongClick = { dropdownExpanded.value = true }
            )
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = model.title,
                fontSize = 19.sp,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Spacer(modifier = Modifier.size(8.dp))

            Row(verticalAlignment = Alignment.Bottom) {
                if (item.dayStatus == io.github.dmitrytsyvtsyn.fluently.happening_list.viewmodel.HappeningDayStatus.YESTERDAY) {
                    Text(
                        modifier = Modifier.clickable(onClick = goToYesterday),
                        text = stringResource(id = R.string.yesterday),
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.size(4.dp))
                }
                
                Text(
                    text = "${CalendarRepository.formatHoursMinutes(model.startDate)} - ${CalendarRepository.formatHoursMinutes(model.endDate)}",
                    fontSize = 26.sp,
                    textDecoration = if (item.timingStatus != HappeningTimingStatus.ACTUAL) TextDecoration.LineThrough else null,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                
                if (item.dayStatus == io.github.dmitrytsyvtsyn.fluently.happening_list.viewmodel.HappeningDayStatus.TOMORROW) {
                    Spacer(modifier = Modifier.size(4.dp))
                    Text(
                        modifier = Modifier.clickable(onClick = goToTomorrow),
                        text = stringResource(id = R.string.tomorrow),
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            if (dropdownExpanded.value) {
                DropdownMenu(
                    expanded = true,
                    onDismissRequest = { dropdownExpanded.value = false }
                ) {
                    DropdownMenuItem(
                        text = {  Text(stringResource(id = R.string.remove_event)) },
                        onClick = {
                            onRemove.invoke(model)
                            dropdownExpanded.value = false
                        }
                    )
                    if (model.eventId.isNotEmpty) {
                        DropdownMenuItem(
                            text = { Text(stringResource(id = R.string.show_event)) },
                            onClick = {
                                onCalendarView.invoke(model)
                                dropdownExpanded.value = false
                            }
                        )
                    }
                }
            }
        }
        Row(modifier = Modifier.align(Alignment.TopEnd), verticalAlignment = Alignment.CenterVertically) {
            if (model.reminderId.isNotEmpty) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_notification),
                    contentDescription = ""
                )
                Spacer(modifier = Modifier.size(4.dp))
            }

            Text(
                text = formatFloatingHours(model.startDate, model.endDate),
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 14.sp,
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 2.dp),
            )
        }
    }
}