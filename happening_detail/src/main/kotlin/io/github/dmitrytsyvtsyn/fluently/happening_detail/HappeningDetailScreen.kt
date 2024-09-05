package io.github.dmitrytsyvtsyn.fluently.happening_detail

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.dmitrytsyvtsyn.fluently.core.compose.configurations
import io.github.dmitrytsyvtsyn.fluently.core.datetime.DateTimeExtensions
import io.github.dmitrytsyvtsyn.fluently.core.datetime.toEpochMillis
import io.github.dmitrytsyvtsyn.fluently.core.datetime.toEpochMillisInUTC
import io.github.dmitrytsyvtsyn.fluently.core.datetime.toHoursMinutesString
import io.github.dmitrytsyvtsyn.fluently.core.datetime.toLocalDateTime
import io.github.dmitrytsyvtsyn.fluently.core.navigation.LocalNavController
import io.github.dmitrytsyvtsyn.fluently.core.navigation.navigate
import io.github.dmitrytsyvtsyn.fluently.core.theme.FluentlyTheme
import io.github.dmitrytsyvtsyn.fluently.core.theme.composables.FluentlyButton
import io.github.dmitrytsyvtsyn.fluently.core.theme.composables.FluentlyCenterAlignedTopAppBar
import io.github.dmitrytsyvtsyn.fluently.core.theme.composables.FluentlyCheckbox
import io.github.dmitrytsyvtsyn.fluently.core.theme.composables.FluentlyIconButton
import io.github.dmitrytsyvtsyn.fluently.core.theme.composables.FluentlyScaffold
import io.github.dmitrytsyvtsyn.fluently.core.theme.composables.FluentlyText
import io.github.dmitrytsyvtsyn.fluently.core.theme.composables.FluentlyTextField
import io.github.dmitrytsyvtsyn.fluently.happening_detail.composables.Suggestions
import io.github.dmitrytsyvtsyn.fluently.happening_detail.composables.rememberCalendarPermissionsRequester
import io.github.dmitrytsyvtsyn.fluently.happening_detail.composables.toDayMonthYearAbbreviatedString
import io.github.dmitrytsyvtsyn.fluently.happening_detail.navigation.HappeningDetailDestination
import io.github.dmitrytsyvtsyn.fluently.happening_detail.viewmodel.HappeningDetailEvent
import io.github.dmitrytsyvtsyn.fluently.happening_detail.viewmodel.HappeningDetailViewModel
import io.github.dmitrytsyvtsyn.fluently.happening_detail.viewmodel.HappeningDetailSideEffect
import io.github.dmitrytsyvtsyn.fluently.happening_detail.viewmodel.HappeningSuggestionsState
import io.github.dmitrytsyvtsyn.fluently.happening_pickers.navigation.HappeningDatePickerDestination
import io.github.dmitrytsyvtsyn.fluently.happening_pickers.navigation.HappeningTimePickerDestination
import kotlinx.datetime.LocalTime
import io.github.dmitrytsyvtsyn.fluently.core.R as CoreRes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HappeningDetailScreen(params: HappeningDetailDestination.Params) {
    val viewModel: HappeningDetailViewModel = viewModel()

    LaunchedEffect(key1 = Unit) {
        viewModel.handleEvent(HappeningDetailEvent.Init(params.id, params.initialDate.toLocalDateTime()))
    }

    val navController = LocalNavController.current
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
        viewModel.effect.collect { sideEffect ->
            when (sideEffect) {
                is HappeningDetailSideEffect.DatePicker -> {
                    navController.navigate(
                        HappeningDatePickerDestination.Params(
                            initialDate = sideEffect.initialDate.toEpochMillisInUTC(),
                            minDate = sideEffect.minDate.toEpochMillisInUTC()
                        )
                    )
                }
                is HappeningDetailSideEffect.TimePicker -> {
                    navController.navigate(
                        HappeningTimePickerDestination.Params(
                            startHours = sideEffect.startTime.hour,
                            startMinutes = sideEffect.startTime.minute,
                            endHours = sideEffect.endTime.hour,
                            endMinutes = sideEffect.endTime.minute,
                            dateInMillis = sideEffect.date.toEpochMillis()
                        )
                    )
                }
                is HappeningDetailSideEffect.Back -> {
                    navController.popBackStack()
                }
                is HappeningDetailSideEffect.CheckCalendarPermission -> {
                    calendarPermissionsRequester.requestPermissions()
                }
            }
        }
    }

    BackHandler {
        viewModel.handleEvent(HappeningDetailEvent.Back)
    }

    val state by viewModel.viewState.collectAsState()
    FluentlyScaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            FluentlyCenterAlignedTopAppBar(
                title = {
                    FluentlyText(
                        modifier = Modifier.fillMaxWidth(),
                        text = if (params.id.isNotEmpty) {
                            stringResource(id = R.string.editing_interview)
                        } else {
                            stringResource(id = R.string.new_interview)
                        },
                        textAlign = TextAlign.Center,
                        style = FluentlyTheme.typography.title1
                    )
                },
                navigationIcon = {
                    FluentlyIconButton(
                        onClick = {
                            viewModel.handleEvent(HappeningDetailEvent.Back)
                        },
                        enabled = state.isBackNavigationButtonEnabled
                    ) {
                        Icon(painter = painterResource(id = CoreRes.drawable.ic_back), contentDescription = "")
                    }
                },
                actions = {
                    FluentlyIconButton(
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
        val configuration = LocalConfiguration.current
        Box(modifier = Modifier.padding(innerPadding)) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .configurations(
                        configuration = configuration,
                        landscape = Modifier.verticalScroll(rememberScrollState())
                    ),
            ) {
                FluentlyTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusable(true),
                    placeholder = {
                        FluentlyText(
                            text = stringResource(id = R.string.interview_company_name)
                        )
                    },
                    singleLine = true,
                    isError = state.titleError,
                    value = state.title,
                    onValueChange = { title ->
                        viewModel.handleEvent(HappeningDetailEvent.TitleChanged(title))
                    }
                )

                if (state.titleError) {
                    Spacer(modifier = Modifier.size(8.dp))
                    FluentlyText(
                        text = stringResource(id = R.string.title_must_not_be_less_three_symbols),
                        style = FluentlyTheme.typography.caption4,
                        color = FluentlyTheme.colors.errorColor
                    )
                }

                Spacer(modifier = Modifier.size(24.dp))

                FluentlyText(
                    text = stringResource(id = R.string.date),
                    style = FluentlyTheme.typography.body2
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
                    FluentlyText(
                        text = state.startDateTime.date.toDayMonthYearAbbreviatedString(),
                        style = FluentlyTheme.typography.body3
                    )
                }

                Spacer(modifier = Modifier.size(16.dp))

                FluentlyText(
                    text = stringResource(id = R.string.time),
                    style = FluentlyTheme.typography.body2
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
                    FluentlyText(
                        text = "$startTimeString - $endTimeString",
                        style = FluentlyTheme.typography.body3
                    )
                }

                if (state.timeActualError) {
                    Spacer(modifier = Modifier.size(8.dp))
                    FluentlyText(
                        text = stringResource(id = R.string.date_time_must_be_actual),
                        style = FluentlyTheme.typography.caption4,
                        color = FluentlyTheme.colors.errorColor
                    )
                }

                if (state.timePeriodError) {
                    Spacer(modifier = Modifier.size(8.dp))
                    FluentlyText(
                        text = stringResource(id = R.string.date_time_must_be_different),
                        style = FluentlyTheme.typography.caption4,
                        color = FluentlyTheme.colors.errorColor
                    )
                }

                when (val suggestionsState = state.suggestionsState) {
                    is HappeningSuggestionsState.NoSuggestions -> {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                    is HappeningSuggestionsState.Suggestions -> {
                        Spacer(Modifier.size(12.dp))
                        Suggestions(
                            modifier = Modifier
                                .fillMaxWidth()
                                .configurations(
                                    configuration = configuration,
                                    portrait = Modifier
                                        .weight(1f)
                                        .verticalScroll(rememberScrollState())
                                ),
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
                        Spacer(Modifier.size(12.dp))
                    }
                }

                if (state.startDateTime > DateTimeExtensions.nowDateTime()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FluentlyCheckbox(
                            checked = state.hasReminder,
                            onCheckedChange = {
                                viewModel.handleEvent(HappeningDetailEvent.ChangeHasReminder(it))
                            }
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        FluentlyText(
                            text = stringResource(id = R.string.turn_alarm_or_not),
                            style = FluentlyTheme.typography.caption3
                        )
                    }

                    Spacer(modifier = Modifier.size(16.dp))
                }

                FluentlyButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        viewModel.handleEvent(HappeningDetailEvent.SaveHappening)
                    }
                ) {
                    FluentlyText(
                        text =  stringResource(id = R.string.save),
                        color = Color.Unspecified,
                        style = FluentlyTheme.typography.caption3
                    )
                }
            }
        }
    }
}

