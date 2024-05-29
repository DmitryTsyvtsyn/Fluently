package io.github.dmitrytsyvtsyn.fluently.happening_list

import android.content.ContentUris
import android.content.Intent
import android.provider.CalendarContract
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.dmitrytsyvtsyn.fluently.R
import io.github.dmitrytsyvtsyn.fluently.core.navigation.LocalNavController
import io.github.dmitrytsyvtsyn.fluently.core.navigation.Screens
import io.github.dmitrytsyvtsyn.fluently.happening_list.components.HappeningEmptyList
import io.github.dmitrytsyvtsyn.fluently.happening_list.components.HappeningList
import io.github.dmitrytsyvtsyn.fluently.happening_list.components.HappeningTabModel
import io.github.dmitrytsyvtsyn.fluently.happening_list.components.HappeningTabs
import io.github.dmitrytsyvtsyn.fluently.happening_list.viewmodel.HappeningListEvent
import io.github.dmitrytsyvtsyn.fluently.happening_list.viewmodel.HappeningListViewModel
import io.github.dmitrytsyvtsyn.fluently.happening_list.viewmodel.HappeningListSideEffect
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HappeningListScreen() {
    val navController = LocalNavController.current
    val context = LocalContext.current

    val viewModel: HappeningListViewModel = viewModel()

    val state by viewModel.viewState.collectAsState()

    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle ?: error("HappeningListScreen.currentBackStackEntry.savedStateHandle is null!")
    val newSelectedDate = savedStateHandle
        .getStateFlow(Screens.HappeningDatePickerDialog.RESULT_KEY, -1L)
        .collectAsState()

    LaunchedEffect(key1 = newSelectedDate.value) {
        val date = newSelectedDate.value
        if (date > 0) {
            viewModel.handleEvent(HappeningListEvent.ChangeDate(date))
        }
    }

    LaunchedEffect(key1 = "side_effects") {
        viewModel.effect.onEach { sideEffect ->
            when (sideEffect) {
                is HappeningListSideEffect.ShowCalendarEvent -> {
                    val uri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, sideEffect.id)
                    context.startActivity(Intent(Intent.ACTION_VIEW).setData(uri))
                }
                is HappeningListSideEffect.ShowDetail -> {
                    navController.navigate("${Screens.HappeningDetailScreen.NAME}/${sideEffect.id}/${sideEffect.date}")
                }
                is HappeningListSideEffect.ShowDatePicker -> {
                    navController.navigate("${Screens.HappeningDatePickerDialog.NAME}?${Screens.HappeningDatePickerDialog.INITIAL_DATE}=${sideEffect.date}")
                }
            }
        }.collect()
    }

    LaunchedEffect(key1 = navController.currentBackStackEntry) {
        viewModel.handleEvent(HappeningListEvent.FetchHappenings(state.currentDate))
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> {
                    viewModel.handleEvent(HappeningListEvent.SubscribeTimeUpdates)
                }
                Lifecycle.Event.ON_STOP -> {
                    viewModel.handleEvent(HappeningListEvent.UnsubscribeTimeUpdates)
                }
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
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
                        navController.navigate(Screens.HappeningThemeSettingsScreen.NAME)
                    }) {
                        Icon(painter = painterResource(id = R.drawable.ic_settings), contentDescription = "")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                shape = RoundedCornerShape(24.dp),
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary,
                onClick = {
                    viewModel.handleEvent(HappeningListEvent.ShowHappeningAdding)
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
                val pagerState = remember(key1 = state.pages) {
                    object : PagerState(state.currentPage) {
                        override val pageCount: Int = state.pages.size
                    }
                }
                val coroutineScope = rememberCoroutineScope()

                LaunchedEffect(pagerState) {
                    snapshotFlow { pagerState.currentPage }.collect {
                        viewModel.handleEvent(HappeningListEvent.ChangeDateByPageIndex(it))
                    }
                }

                LaunchedEffect(pagerState) {
                    snapshotFlow { pagerState.isScrollInProgress }.collect {
                        if (!pagerState.isScrollInProgress) {
                            viewModel.handleEvent(HappeningListEvent.ChangePagesByPageIndex(pagerState.currentPage))
                        }
                    }
                }

                HappeningTabs(
                    tabs = persistentListOf(
                        HappeningTabModel(
                            title = formatDate(date = CalendarRepository.minusDays(state.currentDate, 1), nowDate = state.nowDate),
                            onClick = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(pagerState.currentPage - 1)
                                }
                            }
                        ),
                        HappeningTabModel(
                            title = formatDate(date = state.currentDate, nowDate = state.nowDate),
                            onClick = {
                                viewModel.handleEvent(HappeningListEvent.ShowDatePicker)
                            }
                        ),
                        HappeningTabModel(
                            title = formatDate(date = CalendarRepository.plusDays(state.currentDate, 1), nowDate = state.nowDate),
                            onClick = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                }
                            }
                        )
                    )
                )

                val pages = state.pages
                HorizontalPager(state = pagerState) { index ->
                    val page = pages[index]
                    val items = page.items
                    if (items.isEmpty()) {
                        HappeningEmptyList()
                    } else {
                        HappeningList(
                            listItemStates = items,
                            onClick = { id ->
                                viewModel.handleEvent(HappeningListEvent.ShowHappeningEditing(id))
                            },
                            onRemove = { id, eventId, reminderId ->
                                viewModel.handleEvent(HappeningListEvent.RemoveHappening(id, eventId, reminderId))
                            },
                            onView = { eventId ->
                                viewModel.handleEvent(HappeningListEvent.ShowCalendarEvent(eventId))
                            },
                            goToYesterday = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(pagerState.currentPage - 1)
                                }
                            },
                            goToTomorrow = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                }
                            }
                        )
                    }
                }
            }
        )
    }
}
