package io.github.dmitrytsyvtsyn.interfunny.interview_event_list

import android.content.ContentUris
import android.content.Intent
import android.provider.CalendarContract
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.dmitrytsyvtsyn.interfunny.R
import io.github.dmitrytsyvtsyn.interfunny.core.navigation.LocalNavController
import io.github.dmitrytsyvtsyn.interfunny.core.navigation.Screens
import io.github.dmitrytsyvtsyn.interfunny.interview_event_detail.InterviewDatePicker
import io.github.dmitrytsyvtsyn.interfunny.interview_event_list.components.InterviewTabs
import io.github.dmitrytsyvtsyn.interfunny.interview_event_list.components.TimelineListItem
import io.github.dmitrytsyvtsyn.interfunny.interview_event_list.viewmodel.InterviewEventListViewModel
import io.github.dmitrytsyvtsyn.interfunny.interview_event_list.viewmodel.actions.InterviewEventListAction
import io.github.dmitrytsyvtsyn.interfunny.interview_event_list.viewmodel.states.InterviewEventListItemState
import io.github.dmitrytsyvtsyn.interfunny.interview_event_list.viewmodel.states.InterviewEventModel
import io.github.dmitrytsyvtsyn.interfunny.interview_event_list.viewmodel.states.InterviewEventStatus
import kotlinx.collections.immutable.PersistentList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InterviewEventListScreen() {
    val navController = LocalNavController.current

    val viewModel: InterviewEventListViewModel = viewModel()

    val state by viewModel.state.collectAsState()
    val action = viewModel.action.collectAsState(InterviewEventListAction.Empty)

    val context = LocalContext.current

    when (val actionStateValue = action.value) {
        is InterviewEventListAction.Empty -> {}
        is InterviewEventListAction.Detail -> {
            navController.navigate("${Screens.InterviewEventDetailScreen.name}/${actionStateValue.id}")
            viewModel.resetAction()
        }
        is InterviewEventListAction.ShowDatePicker -> {
            InterviewDatePicker(
                time = actionStateValue.date,
                dismiss = { viewModel.resetAction() },
                ok = { date ->
                    viewModel.changeDate(date)
                    viewModel.resetAction()
                }
            )
        }
        is InterviewEventListAction.ShowDropdownMenu -> {
            DropdownMenu(
                expanded = true,
                onDismissRequest = { viewModel.resetAction() }
            ) {
                DropdownMenuItem(
                    text = {  Text(stringResource(id = R.string.remove_event)) },
                    onClick = {
                        viewModel.removeInterviewEvent(actionStateValue.id, actionStateValue.eventId, actionStateValue.reminderId)
                        viewModel.resetAction()
                    }
                )
                DropdownMenuItem(
                    text = { Text(stringResource(id = R.string.show_event)) },
                    onClick = {
                        val uri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, actionStateValue.eventId)
                        context.startActivity(Intent(Intent.ACTION_VIEW).setData(uri))
                        viewModel.resetAction()
                    }
                )
            }
        }
    }

    LaunchedEffect(key1 = navController.currentBackStackEntry) {
        viewModel.init()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.interviews))
                },
                actions = {
                    IconButton(onClick = {
                        navController.navigate(Screens.InterviewThemeSettingsScreen.name)
                    }) {
                        Icon(painter = painterResource(id = R.drawable.ic_settings), contentDescription = "")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                shape = RoundedCornerShape(24.dp),
                onClick = {
                    viewModel.addInterviewEvent()
                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_add),
                    contentDescription = ""
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            content = {
                InterviewTabs(
                    prevDate = CalendarRepository.formatDateMonth(state.prevDate),
                    prevClick = viewModel::backDay,
                    nowDate = if (CalendarRepository.formatDateMonth(state.date) == CalendarRepository.formatDateMonth(System.currentTimeMillis())) {
                        stringResource(id = R.string.today_day)
                    } else {
                        CalendarRepository.formatDateMonthWeek(state.date)
                    },
                    nowClick = viewModel::showDatePicker,
                    nextDate = CalendarRepository.formatDateMonth(state.nextDate),
                    nextClick = viewModel::forwardDay
                )

                val events = state.filteredEvents

                if (events.isEmpty()) {
                    InterviewEmptyList()
                } else {
                    InterviewEventList(
                        events = state.filteredEvents,
                        onClick = { id ->
                            viewModel.navigateToDetail(id)
                        },
                        onRemove = { id, eventId, reminderId ->
                            viewModel.removeInterviewEvent(id, eventId, reminderId)
                        },
                        onView = { eventId ->
                            val uri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventId)
                            context.startActivity(Intent(Intent.ACTION_VIEW).setData(uri))
                        },
                        onNextDay = viewModel::forwardDay
                    )
                }
            }
        )
    }
}

@Composable
private fun InterviewEmptyList() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.size(32.dp))

        Image(
            painter = painterResource(id = R.drawable.ic_relax),
            contentDescription = "",
            modifier = Modifier
                .width(300.dp)
                .padding(start = 16.dp, end = 16.dp)
                .clip(RoundedCornerShape(32.dp)),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.size(16.dp))

        Text(
            text = stringResource(id = R.string.today_not_interviews),
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp),
            textAlign = TextAlign.Center,
            lineHeight = 30.sp,
            fontSize = 23.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun InterviewEventList(
    events: PersistentList<InterviewEventListItemState>,
    onClick: (id: Long) -> Unit,
    onRemove: (id: Long, eventId: Long, reminderId: Long) -> Unit,
    onView: (eventId: Long) -> Unit,
    onNextDay: () -> Unit
) {
    val listState = rememberLazyListState()

    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        state = listState,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(events.size) { index ->
            when (val listItemState = events[index]) {
                is InterviewEventListItemState.Content -> {
                    InterviewContentListItem(
                        model = listItemState.model,
                        onClick = onClick,
                        onRemove = onRemove,
                        onView = onView,
                        onNextDay = onNextDay
                    )
                }
                is InterviewEventListItemState.Title -> {
                    Text(
                        text = listItemState.value,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                is InterviewEventListItemState.Timeline -> {
                    TimelineListItem(timeline = listItemState)
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun InterviewContentListItem(
    model: InterviewEventModel,
    onClick: (id: Long) -> Unit,
    onRemove: (id: Long, eventId: Long, reminderId: Long) -> Unit,
    onView: (eventId: Long) -> Unit,
    onNextDay: () -> Unit
) {
    val dropdownExpanded = remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .alpha(
                when (model.status) {
                    InterviewEventStatus.ACTUAL -> 1f
                    InterviewEventStatus.PASSED -> 0.5f
                }
            )
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.tertiaryContainer,
                shape = RoundedCornerShape(8.dp)
            )
            .combinedClickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(bounded = true),
                onClick = { onClick.invoke(model.id) },
                onLongClick = { dropdownExpanded.value = true }
            )
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = model.title,
                fontSize = 19.sp
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                text = "${CalendarRepository.formatHoursMinutes(model.startDate)} - ${CalendarRepository.formatHoursMinutes(model.endDate)}",
                fontSize = 26.sp,
                textDecoration = if (model.status != InterviewEventStatus.ACTUAL) TextDecoration.LineThrough else null,
                fontWeight = FontWeight.Medium
            )
//            Box {
//
//                Text(
//                    text = stringResource(id = R.string.tomorrow),
//                    text = stringResource(id = R.string.yesterday)
//                    fontSize = 14.sp
//                )
//            }

            if (dropdownExpanded.value) {
                DropdownMenu(
                    expanded = true,
                    onDismissRequest = { dropdownExpanded.value = false }
                ) {
                    DropdownMenuItem(
                        text = {  Text(stringResource(id = R.string.remove_event)) },
                        onClick = {
                            onRemove.invoke(model.id, model.eventId, model.reminderId)
                            dropdownExpanded.value = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(id = R.string.show_event)) },
                        onClick = {
                            onView.invoke(model.eventId)
                            dropdownExpanded.value = false
                        }
                    )
                }
            }
        }
        Row(modifier = Modifier.align(Alignment.TopEnd), verticalAlignment = Alignment.CenterVertically) {
            if (model.reminderId >= 0) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_notification),
                    contentDescription = ""
                )
                Spacer(modifier = Modifier.size(4.dp))
            }

            Text(
                text = formatFloatingHours(hours = (model.endDate - model.startDate) / 3_600_000f),
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
//        if (CalendarRepository.time(model.endDate) < CalendarRepository.time(model.startDate)) {
//            IconButton(
//                modifier = Modifier.align(Alignment.BottomEnd),
//                onClick = { onNextDay.invoke() }
//            ) {
//                Icon(
//                    painterResource(id = R.drawable.ic_forward),
//                    contentDescription = ""
//                )
//            }
//        }
    }
}
