package io.github.dmitrytsyvtsyn.fluently.happening_list.composables

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.dmitrytsyvtsyn.fluently.core.datetime.minus
import io.github.dmitrytsyvtsyn.fluently.core.datetime.toHoursMinutesString
import io.github.dmitrytsyvtsyn.fluently.data.HappeningModel
import io.github.dmitrytsyvtsyn.fluently.happening_list.R
import io.github.dmitrytsyvtsyn.fluently.happening_list.toHoursMinutesString
import io.github.dmitrytsyvtsyn.fluently.happening_list.viewmodel.HappeningDayStatus
import io.github.dmitrytsyvtsyn.fluently.happening_list.viewmodel.HappeningListItemState
import io.github.dmitrytsyvtsyn.fluently.happening_list.viewmodel.HappeningRunningStatus
import kotlinx.datetime.LocalTime
import kotlinx.datetime.format

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun HappeningContentListItem(
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
                    HappeningRunningStatus.ACTUAL -> 1f
                    HappeningRunningStatus.PASSED -> 0.5f
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
                if (item.dayStatus == HappeningDayStatus.TODAY_AND_YESTERDAY) {
                    Text(
                        modifier = Modifier.clickable(onClick = goToYesterday),
                        text = stringResource(id = R.string.yesterday),
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.size(4.dp))
                }

                val startDateTimeString = model.startDateTime.time.toHoursMinutesString()
                val endDateTimeString = model.endDateTime.time.toHoursMinutesString()
                Text(
                    text = "$startDateTimeString - $endDateTimeString",
                    fontSize = 26.sp,
                    textDecoration = if (item.timingStatus != HappeningRunningStatus.ACTUAL) TextDecoration.LineThrough else null,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )

                if (item.dayStatus == HappeningDayStatus.TODAY_AND_TOMORROW) {
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

            val happeningDurationPeriod = model.endDateTime.minus(model.startDateTime)
            Text(
                text = happeningDurationPeriod.toHoursMinutesString(),
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