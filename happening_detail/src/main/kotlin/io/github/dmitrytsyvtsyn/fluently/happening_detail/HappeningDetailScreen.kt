package io.github.dmitrytsyvtsyn.fluently.happening_detail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.dmitrytsyvtsyn.fluently.core.data.CalendarRepository
import io.github.dmitrytsyvtsyn.fluently.core.datetime.toEpochMillis
import io.github.dmitrytsyvtsyn.fluently.core.datetime.toHoursMinutesString
import io.github.dmitrytsyvtsyn.fluently.core.datetime.toLocalDateTime
import io.github.dmitrytsyvtsyn.fluently.core.navigation.LocalNavController
import io.github.dmitrytsyvtsyn.fluently.core.navigation.navigate
import io.github.dmitrytsyvtsyn.fluently.core.theme.composables.DebounceIconButton
import io.github.dmitrytsyvtsyn.fluently.happening_detail.composables.Suggestions
import io.github.dmitrytsyvtsyn.fluently.happening_detail.viewmodel.HappeningDetailEvent
import io.github.dmitrytsyvtsyn.fluently.happening_detail.viewmodel.HappeningDetailViewModel
import io.github.dmitrytsyvtsyn.fluently.happening_detail.viewmodel.HappeningDetailSideEffect
import io.github.dmitrytsyvtsyn.fluently.happening_detail.viewmodel.HappeningSuggestionsState
import io.github.dmitrytsyvtsyn.fluently.happening_pickers.HappeningDatePickerDestination
import io.github.dmitrytsyvtsyn.fluently.happening_pickers.HappeningTimePickerDestination
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.datetime.LocalTime
import io.github.dmitrytsyvtsyn.fluently.core.R as CoreRes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HappeningDetailScreen(params: HappeningDetailDestination.Params) {
    val viewModel: HappeningDetailViewModel = viewModel()

    val navController = LocalNavController.current

    LaunchedEffect(key1 = Unit) {
        viewModel.handleEvent(HappeningDetailEvent.Init(params.id, params.initialDate.toLocalDateTime()))
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = if (params.id.isNotEmpty) stringResource(id = R.string.editing_interview) else stringResource(id = R.string.new_interview),
                        textAlign = TextAlign.Center
                    )
                },
                navigationIcon = {
                    DebounceIconButton(
                        onClick = {
                            viewModel.handleEvent(HappeningDetailEvent.Back)
                        }
                    ) {
                        Icon(painter = painterResource(id = CoreRes.drawable.ic_back), contentDescription = "")
                    }
                },
                actions = {
                    DebounceIconButton(
                        onClick = {
                            viewModel.handleEvent(HappeningDetailEvent.SaveHappening)
                        }
                    ) {
                        Icon(painter = painterResource(id = R.drawable.ic_check), contentDescription = "")
                    }
                }
            )
        },
    ) { innerPadding ->
        val state by viewModel.viewState.collectAsState()

        val newSelectedDate = HappeningDatePickerDestination.fetchResult(navController).collectAsState()
        LaunchedEffect(key1 = newSelectedDate.value) {
            val date = newSelectedDate.value
            if (date > 0) {
                viewModel.handleEvent(HappeningDetailEvent.DateChanged(date.toLocalDateTime()))
            }
        }

        val newSelectedTime = HappeningTimePickerDestination.fetchResult(navController).collectAsState()
        LaunchedEffect(key1 = newSelectedTime.value) {
            val time = newSelectedTime.value
            if (time.isNotEmpty) {
                viewModel.handleEvent(HappeningDetailEvent.TimeChanged(
                    startTime = LocalTime(hour = time.startHours, minute = time.startMinutes),
                    endTime = LocalTime(hour = time.endHours, minute = time.endMinutes)
                ))
            }
        }

        val calendarPermissionsRequester = rememberCalendarPermissionsRequester { isAllowed ->
            viewModel.handleEvent(HappeningDetailEvent.ChangeCalendarPermissionsStatus(isAllowed))
        }

        LaunchedEffect(key1 = "side_effects") {
            viewModel.effect.onEach { sideEffect ->
                when (sideEffect) {
                    is HappeningDetailSideEffect.DatePicker -> {
                        navController.navigate(HappeningDatePickerDestination.Params(
                            initialDate = sideEffect.dateTime.toEpochMillis(),
                            minDate = CalendarRepository.dateMonthYearMillis()
                        ))
                    }
                    is HappeningDetailSideEffect.TimePicker -> {
                        navController.navigate(HappeningTimePickerDestination.Params(
                            startHours = sideEffect.startTime.hour,
                            startMinutes = sideEffect.startTime.minute,
                            endHours = sideEffect.endTime.hour,
                            endMinutes = sideEffect.endTime.minute
                        ))
                    }
                    is HappeningDetailSideEffect.Back -> {
                        navController.popBackStack()
                    }
                    is HappeningDetailSideEffect.CheckCalendarPermission -> {
                        calendarPermissionsRequester.requestPermissions()
                    }
                }
            }.collect()
        }

        Box(modifier = Modifier.padding(innerPadding)) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            ) {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    TextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusable(true),
                        shape = RoundedCornerShape(16.dp),
                        textStyle = LocalTextStyle.current.copy(
                            fontSize = 21.sp,
                        ),
                        placeholder = {
                            Text(stringResource(id = R.string.interview_company_name))
                        },
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent
                        ),
                        singleLine = true,
                        isError = state.titleError,
                        value = state.title,
                        onValueChange = { title ->
                            viewModel.handleEvent(HappeningDetailEvent.TitleChanged(title))
                        }
                    )

                    if (state.titleError) {
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(
                            text = stringResource(id = R.string.title_must_not_be_less_three_symbols),
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    Spacer(modifier = Modifier.size(24.dp))

                    Text(
                        text = stringResource(id = R.string.date),
                        fontSize = 18.sp
                    )

                    Spacer(modifier = Modifier.size(8.dp))

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = rememberRipple(bounded = true),
                            ) {
                                viewModel.handleEvent(HappeningDetailEvent.ShowDatePicker)
                            }
                            .padding(8.dp)

                    ) {
                        Text(
                            text = state.startDateTime.date.toDayMonthYearAbbreviatedString(),
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.size(16.dp))

                    Text(
                        text = stringResource(id = R.string.time),
                        fontSize = 18.sp
                    )

                    Spacer(modifier = Modifier.size(8.dp))

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = rememberRipple(bounded = true),
                            ) {
                                viewModel.handleEvent(HappeningDetailEvent.ShowTimePicker)
                            }
                            .padding(8.dp)

                    ) {
                        val startTimeString = state.startDateTime.time.toHoursMinutesString()
                        val endTimeString = state.endDateTime.time.toHoursMinutesString()
                        Text(
                            text = "$startTimeString - $endTimeString",
                            fontSize = 31.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    when (val suggestionsState = state.suggestionsState) {
                        is HappeningSuggestionsState.NoSuggestions -> {}
                        is HappeningSuggestionsState.Suggestions -> {
                            Spacer(Modifier.size(12.dp))
                            Suggestions(
                                suggestionRanges = suggestionsState.ranges,
                                onSuggestionClick = { startDateTime, endDateTime ->
                                    viewModel.handleEvent(
                                        HappeningDetailEvent.DateTimeChanged(
                                            startDateTime = startDateTime,
                                            endDateTime = endDateTime
                                        )
                                    )
                                }
                            )
                        }
                    }

                }

                Spacer(modifier = Modifier.weight(1f))

                if (state.startDateTime > CalendarRepository.nowDateTime()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = state.hasReminder,
                            onCheckedChange = {
                                viewModel.handleEvent(HappeningDetailEvent.ChangeHasReminder(it))
                            }
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(
                            stringResource(id = R.string.turn_alarm_or_not)
                        )
                    }

                    Spacer(modifier = Modifier.size(16.dp))
                }

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(12.dp),
                    onClick = {
                        viewModel.handleEvent(HappeningDetailEvent.SaveHappening)
                    }
                ) {
                    Text(
                        text =  stringResource(id = R.string.save),
                        fontWeight = FontWeight.Medium,
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}

