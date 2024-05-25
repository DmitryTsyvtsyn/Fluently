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
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.dmitrytsyvtsyn.fluently.R
import io.github.dmitrytsyvtsyn.fluently.core.navigation.LocalNavController
import io.github.dmitrytsyvtsyn.fluently.core.navigation.Screens
import io.github.dmitrytsyvtsyn.fluently.core.theme.components.DebounceIconButton
import io.github.dmitrytsyvtsyn.fluently.happening_detail.viewmodel.HappeningDetailEvent
import io.github.dmitrytsyvtsyn.fluently.happening_detail.viewmodel.HappeningDetailViewModel
import io.github.dmitrytsyvtsyn.fluently.happening_detail.viewmodel.HappeningDetailSideEffect
import io.github.dmitrytsyvtsyn.fluently.happening_detail.viewmodel.InterviewEventBusyState
import io.github.dmitrytsyvtsyn.fluently.happening_list.CalendarRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HappeningDetailScreen(id: Long = -1, initialDate: Long = System.currentTimeMillis()) {
    val viewModel: HappeningDetailViewModel = viewModel()

    val navController = LocalNavController.current

    LaunchedEffect(key1 = Unit) {
        viewModel.init(id, initialDate)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = if (id >= 0) stringResource(id = R.string.editing_interview) else stringResource(id = R.string.new_interview),
                        textAlign = TextAlign.Center
                    )
                },
                navigationIcon = {
                    DebounceIconButton(
                        onClick = {
                            viewModel.pushEvent(HappeningDetailEvent.Back)
                        }
                    ) {
                        Icon(painter = painterResource(id = R.drawable.ic_back), contentDescription = "")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        viewModel.pushEvent(HappeningDetailEvent.SaveHappening)
                    }) {
                        Icon(painter = painterResource(id = R.drawable.ic_check), contentDescription = "")
                    }
                }
            )
        },
    ) { innerPadding ->
        val state by viewModel.viewState.collectAsState()

        val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle ?: error("HappeningDetailScreen.currentBackStackEntry.savedStateHandle is null!")

        val newSelectedDate = savedStateHandle
            .getStateFlow(Screens.HappeningDatePickerDialog.RESULT_KEY, -1L)
            .collectAsState()
        LaunchedEffect(key1 = newSelectedDate.value) {
            val date = newSelectedDate.value
            if (date > 0) {
                viewModel.pushEvent(HappeningDetailEvent.DateChanged(date))
            }
        }

        val newSelectedTime = savedStateHandle
            .getStateFlow(Screens.HappeningTimePickerDialog.RESULT_KEY, Screens.HappeningTimePickerDialog.Result())
            .collectAsState()
        LaunchedEffect(key1 = newSelectedTime.value) {
            val time = newSelectedTime.value
            if (time.isNotEmpty) {
                viewModel.pushEvent(HappeningDetailEvent.TimeChanged(time.startHours, time.startMinutes, time.endHours, time.endMinutes))
            }
        }

        LaunchedEffect(key1 = "side_effects") {
            viewModel.effect.onEach { sideEffect ->
                when (sideEffect) {
                    is HappeningDetailSideEffect.DatePicker -> {
                        navController.navigate("${Screens.HappeningDatePickerDialog.NAME}?${Screens.HappeningDatePickerDialog.INITIAL_DATE}=${sideEffect.date}&${Screens.HappeningDatePickerDialog.MIN_DATE}=${CalendarRepository.dateMonthYearMillis()}")
                    }
                    is HappeningDetailSideEffect.TimePicker -> {
                        navController.navigate("${Screens.HappeningTimePickerDialog.NAME}?${Screens.HappeningTimePickerDialog.START_HOURS}=${sideEffect.startHours}&${Screens.HappeningTimePickerDialog.START_MINUTES}=${sideEffect.startMinutes}&${Screens.HappeningTimePickerDialog.END_HOURS}=${sideEffect.endHours}&${Screens.HappeningTimePickerDialog.END_MINUTES}=${sideEffect.endMinutes}")
                    }
                    is HappeningDetailSideEffect.Back -> {
                        navController.popBackStack()
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
                            viewModel.pushEvent(HappeningDetailEvent.TitleChanged(title))
                        })

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
                                viewModel.pushEvent(HappeningDetailEvent.ShowDatePicker)
                             }
                            .padding(8.dp)

                    ) {
                        Text(
                            text = CalendarRepository.formatDateMonthYear(state.startDate),
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
                                viewModel.pushEvent(HappeningDetailEvent.ShowTimePicker)
                            }
                            .padding(8.dp)

                    ) {
                        Text(
                            text = "${CalendarRepository.formatHoursMinutes(state.startDate)} - ${CalendarRepository.formatHoursMinutes(state.endDate)}",
                            fontSize = 31.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    when (val busyState = state.busyState) {
                        is InterviewEventBusyState.NotBusy -> {}
                        is InterviewEventBusyState.BusyWithSuggestions -> {
                            Spacer(Modifier.size(8.dp))
                            Text(
                                text = stringResource(id = R.string.already_scheduled_events),
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium
                            )

                            Spacer(Modifier.size(8.dp))

                            val annotatedString = buildAnnotatedString {
                                append(stringResource(id = R.string.try_folowing_ranges))
                                append(" ")

                                val suggestionRanges = busyState.suggestionRanges
                                suggestionRanges.forEachIndexed { index, range ->
                                    pushStringAnnotation("range", "${range.first}/${range.last}")

                                    withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                                        val trailingSymbol = if (index != suggestionRanges.lastIndex) "\n" else ""

                                        val now = System.currentTimeMillis()
                                        val startDate = range.first
                                        val endDate = range.last

                                        val startDateString = CalendarRepository.formatDateMonthYearBrief(startDate)
                                        val nowDateString = CalendarRepository.formatDateMonthYearBrief(now)
                                        val endDateString = CalendarRepository.formatDateMonthYearBrief(endDate)

                                        val dates = if (startDateString == endDateString) {
                                            if (startDateString == nowDateString) {
                                                stringResource(id = R.string.at_today)
                                            } else {
                                                stringResource(id = R.string.at_date, nowDateString)
                                            }
                                        } else {
                                            stringResource(id = R.string.at_dates, startDateString, endDateString)
                                        }

                                        stringResource(id = R.string.at_date, )
                                        stringResource(id = R.string.at_date)

                                        append("${index + 1}. ${CalendarRepository.formatHoursMinutes(startDate)} - ${CalendarRepository.formatHoursMinutes(endDate)} $dates $trailingSymbol")
                                    }

                                    pop()
                                }
                            }

                            ClickableText(
                                text = annotatedString,
                                style = LocalTextStyle.current.copy(
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    lineHeight = 28.sp
                                ),
                            ) { offset ->
                                annotatedString.getStringAnnotations(tag = "range", start = offset, end = offset).firstOrNull()?.let {
                                    val some = it.item.split("/")
                                    val startDate = CalendarRepository.formatHoursMinutes(some.first().toLong())
                                    val endDate = CalendarRepository.formatHoursMinutes(some.last().toLong())

                                    val startHoursAndMinutes = startDate.split(":")
                                    val endHoursAndMinutes = endDate.split(":")

                                    viewModel.pushEvent(
                                        HappeningDetailEvent.TimeChanged(
                                            startHoursAndMinutes.first().toInt(),
                                            startHoursAndMinutes.last().toInt(),
                                            endHoursAndMinutes.first().toInt(),
                                            endHoursAndMinutes.last().toInt()
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                if (state.startDate > System.currentTimeMillis()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = state.hasReminder,
                            onCheckedChange = {
                                viewModel.pushEvent(HappeningDetailEvent.ChangeHasReminder(it))
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
                        viewModel.pushEvent(HappeningDetailEvent.SaveHappening)
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

