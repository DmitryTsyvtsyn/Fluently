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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
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

    val viewModel: HappeningListViewModel = viewModel()

    val state by viewModel.viewState.collectAsState()

    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle ?: error("HappeningListScreen.currentBackStackEntry.savedStateHandle is null!")
    val newSelectedDate = savedStateHandle
        .getStateFlow(Screens.HappeningDatePickerDialog.RESULT_KEY, -1L)
        .collectAsState()

    LaunchedEffect(key1 = newSelectedDate) {
        val date = newSelectedDate.value
        if (date > 0) {
            viewModel.pushEvent(HappeningListEvent.ChangeDate(date))
        }
    }

    LaunchedEffect(key1 = "side_effects") {
        viewModel.effect.onEach { sideEffect ->
            when (sideEffect) {
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
                onClick = {
                    viewModel.pushEvent(HappeningListEvent.ShowHappeningAdding)
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
                        viewModel.pushEvent(HappeningListEvent.ChangeDateByPageIndex(it))
                    }
                }

                LaunchedEffect(pagerState) {
                    snapshotFlow { pagerState.isScrollInProgress }.collect {
                        if (!pagerState.isScrollInProgress) {
                            viewModel.pushEvent(HappeningListEvent.ChangePagesByPageIndex(pagerState.currentPage))
                        }
                    }
                }

                val nowDate = CalendarRepository.nowDate()
                HappeningTabs(
                    tabs = persistentListOf(
                        HappeningTabModel(
                            title = formatDate(date = CalendarRepository.minusDays(state.date, 1), nowDate = nowDate),
                            onClick = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(pagerState.currentPage - 1)
                                }
                            }
                        ),
                        HappeningTabModel(
                            title = formatDate(date = state.date, nowDate = nowDate),
                            onClick = {
                                viewModel.pushEvent(HappeningListEvent.ShowDatePicker)
                            }
                        ),
                        HappeningTabModel(
                            title = formatDate(date = CalendarRepository.plusDays(state.date, 1), nowDate = nowDate),
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
                    val interviews = pages[index].items
                    if (interviews.isEmpty()) {
                        HappeningEmptyList()
                    } else {
                        val context = LocalContext.current
                        HappeningList(
                            events = interviews,
                            onClick = { id ->
                                viewModel.pushEvent(HappeningListEvent.ShowHappeningEditing(id))
                            },
                            onRemove = { id, eventId, reminderId ->
                                viewModel.pushEvent(HappeningListEvent.RemoveHappening(id, eventId, reminderId))
                            },
                            onView = { eventId ->
                                val uri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventId)
                                context.startActivity(Intent(Intent.ACTION_VIEW).setData(uri))
                            },
                            onNextDay = {
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
