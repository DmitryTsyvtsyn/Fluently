package io.github.dmitrytsyvtsyn.interfunny.interview_detail

import android.util.Log
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
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TimeInput
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
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
import io.github.dmitrytsyvtsyn.interfunny.R
import io.github.dmitrytsyvtsyn.interfunny.core.navigation.LocalNavController
import io.github.dmitrytsyvtsyn.interfunny.core.theme.components.DebounceIconButton
import io.github.dmitrytsyvtsyn.interfunny.interview_detail.viewmodel.InterviewDetailViewModel
import io.github.dmitrytsyvtsyn.interfunny.interview_detail.viewmodel.actions.InterviewEventDetailAction
import io.github.dmitrytsyvtsyn.interfunny.interview_detail.viewmodel.states.InterviewEventBusyState
import io.github.dmitrytsyvtsyn.interfunny.interview_list.CalendarRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InterviewDetailScreen(id: Long = -1, initialDate: Long = System.currentTimeMillis()) {
    val viewModel: InterviewDetailViewModel = viewModel()

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
                            viewModel.navigateToBack()
                        }
                    ) {
                        Icon(painter = painterResource(id = R.drawable.ic_back), contentDescription = "")
                    }
                },
                actions = {
                    IconButton(onClick = viewModel::save) {
                        Icon(painter = painterResource(id = R.drawable.ic_check), contentDescription = "")
                    }
                }
            )
        },
    ) { innerPadding ->
        val state by viewModel.state.collectAsState()
        val action = viewModel.action.collectAsState()

        when(val actionStateValue = action.value) {
            is InterviewEventDetailAction.Empty -> {}
            is InterviewEventDetailAction.DatePicker -> {
                InterviewDatePicker(
                    initialDate = actionStateValue.date,
                    minDate = CalendarRepository.dateMonthYearMillis(),
                    dismiss = viewModel::resetAction,
                    ok = { date ->
                        viewModel.dateChanged(date)
                        viewModel.resetAction()
                    }
                )
            }
            is InterviewEventDetailAction.TimePicker -> {
                InterviewTimePicker(
                    dismiss = viewModel::resetAction,
                    ok = { startHours, startMinutes, endHours, endMinutes, nextDay ->
                        viewModel.timeChanged(startHours, startMinutes, endHours, endMinutes, nextDay)
                        viewModel.resetAction()
                    },
                    startHours = actionStateValue.startHours,
                    startMinutes = actionStateValue.startMinutes,
                    endHours = actionStateValue.endHours,
                    endMinutes = actionStateValue.endMinutes
                )
            }
            is InterviewEventDetailAction.Back -> { navController.popBackStack() }
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
                            viewModel.titleChanged(title)
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
                                viewModel.navigateToDatePicker()
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
                                viewModel.navigateToTimePicker()
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
                                //text = stringResource(id = R.string.already_scheduled_events, "${CalendarRepository.formatHoursMinutes(busyState.startDate)} - ${CalendarRepository.formatHoursMinutes(busyState.endDate)}"),
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

                                    viewModel.timeChanged(
                                        startHoursAndMinutes.first().toInt(),
                                        startHoursAndMinutes.last().toInt(),
                                        endHoursAndMinutes.first().toInt(),
                                        endHoursAndMinutes.last().toInt(),
                                        nextDay = false
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
                                viewModel.changeHasReminder(it)
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
                    onClick = viewModel::save
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InterviewDatePicker(
    initialDate: Long = 0,
    minDate: Long = Long.MIN_VALUE,
    maxDate: Long = Long.MAX_VALUE,
    dismiss: () -> Unit,
    ok: (Long) -> Unit
) {
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialDate)
    val confirmEnabled = remember {
        derivedStateOf {
            val date = datePickerState.selectedDateMillis
            date != null && date >= minDate && date <= maxDate
        }
    }

    DatePickerDialog(
        onDismissRequest = dismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    ok.invoke(datePickerState.selectedDateMillis ?: error("DatePickerState.selectedDateMillis is null!"))
                },
                enabled = confirmEnabled.value
            ) {
                Text(stringResource(id = R.string.ok))
            }
        },
        dismissButton = {
            TextButton(onClick = dismiss) {
                Text(stringResource(id = R.string.cancel))
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InterviewTimePicker(
    dismiss: () -> Unit,
    ok: (startHours: Int, startMinutes: Int, endHours: Int, endMinutes: Int, nextDay: Boolean) -> Unit,
    startHours: Int,
    startMinutes: Int,
    endHours: Int,
    endMinutes: Int
) {
    val startTimeState = rememberTimePickerState(
        initialHour = startHours,
        initialMinute = startMinutes,
        is24Hour = true
    )
    val endTimeState = rememberTimePickerState(
        initialHour = endHours,
        initialMinute = endMinutes,
        is24Hour = true
    )
    val confirmEnabled = remember {
        derivedStateOf {
            when {
                startTimeState.hour < CalendarRepository.currentHours() -> false
                startTimeState.hour == CalendarRepository.currentHours() && startTimeState.minute < CalendarRepository.currentMinutes() -> false
                startTimeState.hour < endTimeState.hour -> true
                startTimeState.hour > endTimeState.hour -> true
                startTimeState.hour == endTimeState.hour -> {
                     startTimeState.minute < endTimeState.minute
                }
                else -> false
            }
        }
    }

    DatePickerDialog(
        onDismissRequest = dismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    ok.invoke(
                        startTimeState.hour,
                        startTimeState.minute,
                        endTimeState.hour,
                        endTimeState.minute,
                        startTimeState.hour > endTimeState.hour
                    )
                },
                enabled = confirmEnabled.value
            ) {
                Text(stringResource(id = R.string.ok))
            }
        },
        dismissButton = {
            TextButton(onClick = dismiss) {
                Text(stringResource(id = R.string.cancel))
            }
        }
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp),
        ) {
            val focusRequester = remember { FocusRequester() }

            Text(
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                text = stringResource(id = R.string.start_time),
                fontSize = 18.sp
            )

            Spacer(modifier = Modifier.size(16.dp))

            TimeInput(
                state = startTimeState,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .focusRequester(focusRequester),
            )

            Spacer(modifier = Modifier.size(16.dp))

            Text(
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                text = stringResource(id = R.string.end_time),
                fontSize = 18.sp
            )

            Spacer(modifier = Modifier.size(16.dp))

            TimeInput(state = endTimeState,  modifier = Modifier.align(Alignment.CenterHorizontally))

            LaunchedEffect(key1 = Unit) {
                focusRequester.requestFocus()
            }
        }
    }
}