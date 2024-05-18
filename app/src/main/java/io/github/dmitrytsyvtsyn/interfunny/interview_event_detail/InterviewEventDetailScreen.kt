package io.github.dmitrytsyvtsyn.interfunny.interview_event_detail

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
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
import androidx.compose.ui.ExperimentalComposeUiApi
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
import io.github.dmitrytsyvtsyn.interfunny.interview_event_detail.viewmodel.InterviewEventDetailViewModel
import io.github.dmitrytsyvtsyn.interfunny.interview_event_detail.viewmodel.actions.InterviewEventDetailAction
import io.github.dmitrytsyvtsyn.interfunny.interview_event_detail.viewmodel.states.InterviewEventScheduledState
import java.text.SimpleDateFormat

private val dateFormat = SimpleDateFormat("dd MMM yyyy")
private val timeFormat = SimpleDateFormat("HH:mm")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InterviewEventDetailScreen(id: Long = -1) {
    val viewModel: InterviewEventDetailViewModel = viewModel()

    val navController = LocalNavController.current

    LaunchedEffect(key1 = Unit) {
        viewModel.init(id)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(id = R.string.new_interview),
                        textAlign = TextAlign.Center
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.popBackStack()
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
                    time = actionStateValue.date,
                    dismiss = viewModel::resetAction,
                    ok = { date ->
                        viewModel.dateChanged(date)
                        viewModel.resetAction()
                    }
                )
            }
            is InterviewEventDetailAction.TimePicker -> {
                InterviewTimeDialog(
                    dismiss = viewModel::resetAction,
                    ok = { startHours, startMinutes, endHours, endMinutes ->
                        viewModel.timeChanged(startHours, startMinutes, endHours, endMinutes)
                        viewModel.resetAction()
                    },
                    startHours = actionStateValue.startHours,
                    startMinutes = actionStateValue.startMinutes,
                    endHours = actionStateValue.endHours,
                    endMinutes = actionStateValue.endMinutes
                )
            }
            is InterviewEventDetailAction.Back -> {
                navController.popBackStack()
                viewModel.resetAction()
            }
        }

        Box(modifier = Modifier.padding(innerPadding)) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            ) {
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
                        text = dateFormat.format(state.startDate),
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
                        text = "${timeFormat.format(state.startDate)} - ${timeFormat.format(state.endDate)}",
                        fontSize = 31.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                when (val scheduledState = state.alreadyScheduledState) {
                    is InterviewEventScheduledState.Empty -> {}
                    is InterviewEventScheduledState.Content -> {
                        Spacer(Modifier.size(8.dp))
                        Text(
                            text = stringResource(id = R.string.already_scheduled_events, "${timeFormat.format(state.startDate)} - ${timeFormat.format(state.endDate)}"),
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(Modifier.size(8.dp))

                        val annotatedString = buildAnnotatedString {
                            append(stringResource(id = R.string.try_folowing_ranges))

                            scheduledState.freeRanges.forEach {
                                pushStringAnnotation("range", "${it.first}/${it.last}")

                                withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                                    append("${timeFormat.format(it.first)} - ${timeFormat.format(it.last)} ")
                                }

                                pop()
                            }
                        }

                        ClickableText(
                            text = annotatedString,
                            style = LocalTextStyle.current.copy(
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onBackground
                            ),
                        ) { offset ->
                            annotatedString.getStringAnnotations(tag = "range", start = offset, end = offset).firstOrNull()?.let {
                                val some = it.item.split("/")
                                val startDate = timeFormat.format(some.first().toLong())
                                val endDate = timeFormat.format(some.last().toLong())

                                val startHoursAndMinutes = startDate.split(":")
                                val endHoursAndMinutes = endDate.split(":")

                                viewModel.timeChanged(
                                    startHoursAndMinutes.first().toInt(),
                                    startHoursAndMinutes.last().toInt(),
                                    endHoursAndMinutes.first().toInt(),
                                    endHoursAndMinutes.last().toInt()
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

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

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(12.dp),
                    onClick = viewModel::save
                ) {
                    Text(
                        text = if (state.rescheduleInterview) {
                            stringResource(id = R.string.reschedule)
                        } else {
                            stringResource(id = R.string.save)
                        },
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
    time: Long = 0,
    dismiss: () -> Unit,
    ok: (Long) -> Unit
) {
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = time)
    val confirmEnabled = remember {
        derivedStateOf { datePickerState.selectedDateMillis != null }
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun InterviewTimeDialog(
    dismiss: () -> Unit,
    ok: (startHours: Int, startMinutes: Int, endHours: Int, endMinutes: Int) -> Unit,
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
                startTimeState.hour < endTimeState.hour -> true
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
                    ok.invoke(startTimeState.hour, startTimeState.minute, endTimeState.hour, endTimeState.minute)
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