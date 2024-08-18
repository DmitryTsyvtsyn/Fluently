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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import io.github.dmitrytsyvtsyn.fluently.core.datetime.minus
import io.github.dmitrytsyvtsyn.fluently.core.datetime.toHoursMinutesString
import io.github.dmitrytsyvtsyn.fluently.core.theme.FluentlyTheme
import io.github.dmitrytsyvtsyn.fluently.core.theme.composables.FluentlyIcon
import io.github.dmitrytsyvtsyn.fluently.core.theme.composables.FluentlyText
import io.github.dmitrytsyvtsyn.fluently.data.model.HappeningModel
import io.github.dmitrytsyvtsyn.fluently.happening_list.R
import io.github.dmitrytsyvtsyn.fluently.happening_list.viewmodel.HappeningDayStatus
import io.github.dmitrytsyvtsyn.fluently.happening_list.viewmodel.HappeningListItemState
import io.github.dmitrytsyvtsyn.fluently.happening_list.viewmodel.HappeningRunningStatus

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
                color = FluentlyTheme.colors.primaryContainerColor,
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
            FluentlyText(
                text = model.title,
                style = FluentlyTheme.typography.body2
            )

            Spacer(modifier = Modifier.size(8.dp))

            Row(verticalAlignment = Alignment.Bottom) {
                if (item.dayStatus == HappeningDayStatus.TODAY_AND_YESTERDAY) {
                    FluentlyText(
                        modifier = Modifier.clickable(onClick = goToYesterday),
                        text = stringResource(id = R.string.yesterday),
                        style = FluentlyTheme.typography.caption3,
                        color = FluentlyTheme.colors.secondaryColor
                    )
                    Spacer(modifier = Modifier.size(4.dp))
                }

                val startDateTimeString = model.startDateTime.time.toHoursMinutesString()
                val endDateTimeString = model.endDateTime.time.toHoursMinutesString()
                FluentlyText(
                    text = "$startDateTimeString - $endDateTimeString",
                    textDecoration = if (item.timingStatus != HappeningRunningStatus.ACTUAL) TextDecoration.LineThrough else null,
                    style = FluentlyTheme.typography.body3
                )

                if (item.dayStatus == HappeningDayStatus.TODAY_AND_TOMORROW) {
                    Spacer(modifier = Modifier.size(4.dp))
                    FluentlyText(
                        modifier = Modifier.clickable(onClick = goToTomorrow),
                        text = stringResource(id = R.string.tomorrow),
                        style = FluentlyTheme.typography.caption3,
                        color = FluentlyTheme.colors.secondaryColor
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
                FluentlyIcon(
                    painter = painterResource(id = R.drawable.ic_notification),
                    contentDescription = ""
                )
                Spacer(modifier = Modifier.size(4.dp))
            }

            val happeningDurationPeriod = model.endDateTime.minus(model.startDateTime)
            FluentlyText(
                text = happeningDurationPeriod.toHoursMinutesString(),
                modifier = Modifier
                    .background(
                        color = FluentlyTheme.colors.primaryColor,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 2.dp),
                color = FluentlyTheme.colors.onPrimaryColor,
                style = FluentlyTheme.typography.caption4
            )
        }
    }
}