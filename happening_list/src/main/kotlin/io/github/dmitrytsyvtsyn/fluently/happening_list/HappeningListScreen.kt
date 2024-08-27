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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.dmitrytsyvtsyn.fluently.core.datetime.minus
import io.github.dmitrytsyvtsyn.fluently.core.datetime.plus
import io.github.dmitrytsyvtsyn.fluently.core.datetime.toEpochMillis
import io.github.dmitrytsyvtsyn.fluently.core.datetime.toLocalDateTime
import io.github.dmitrytsyvtsyn.fluently.core.navigation.LocalNavController
import io.github.dmitrytsyvtsyn.fluently.core.navigation.ThemeSettingsDestination
import io.github.dmitrytsyvtsyn.fluently.core.navigation.navigate
import io.github.dmitrytsyvtsyn.fluently.core.theme.FluentlyTheme
import io.github.dmitrytsyvtsyn.fluently.core.theme.composables.FluentlyCenterAlignedTopAppBar
import io.github.dmitrytsyvtsyn.fluently.core.theme.composables.FluentlyFloatingActionButton
import io.github.dmitrytsyvtsyn.fluently.core.theme.composables.FluentlyIconButton
import io.github.dmitrytsyvtsyn.fluently.core.theme.composables.FluentlyScaffold
import io.github.dmitrytsyvtsyn.fluently.core.theme.composables.FluentlyText
import io.github.dmitrytsyvtsyn.fluently.happening_detail.navigation.HappeningDetailDestination
import io.github.dmitrytsyvtsyn.fluently.happening_list.composables.HappeningWeekend
import io.github.dmitrytsyvtsyn.fluently.happening_list.composables.HappeningList
import io.github.dmitrytsyvtsyn.fluently.happening_list.models.HappeningTabModel
import io.github.dmitrytsyvtsyn.fluently.happening_list.composables.HappeningTabs
import io.github.dmitrytsyvtsyn.fluently.happening_list.composables.toDateMonthString
import io.github.dmitrytsyvtsyn.fluently.happening_list.viewmodel.HappeningListEvent
import io.github.dmitrytsyvtsyn.fluently.happening_list.viewmodel.HappeningListViewModel
import io.github.dmitrytsyvtsyn.fluently.happening_list.viewmodel.HappeningListSideEffect
import io.github.dmitrytsyvtsyn.fluently.happening_pickers.navigation.HappeningDatePickerDestination
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
internal fun HappeningListScreen() {
    val navController = LocalNavController.current
    val context = LocalContext.current

    val viewModel: HappeningListViewModel = viewModel()

    val state by viewModel.viewState.collectAsState()

    val newSelectedDate = HappeningDatePickerDestination.fetchResult(navController).collectAsState()
    LaunchedEffect(key1 = newSelectedDate.value) {
        val date = newSelectedDate.value
        if (date > 0) {
            viewModel.handleEvent(HappeningListEvent.ChangeDate(date.toLocalDateTime()))
        }
    }

    LaunchedEffect(key1 = "side_effects") {
        viewModel.effect.onEach { sideEffect ->
            when (sideEffect) {
                is HappeningListSideEffect.ShowCalendar -> {
                    val uri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, sideEffect.id.value)
                    context.startActivity(Intent(Intent.ACTION_VIEW).setData(uri))
                }
                is HappeningListSideEffect.ShowDetail -> {
                    navController.navigate(HappeningDetailDestination.Params(
                        id = sideEffect.id,
                        initialDate = sideEffect.dateTime.toEpochMillis()
                    ))
                }
                is HappeningListSideEffect.ShowDatePicker -> {
                    navController.navigate(
                        HappeningDatePickerDestination.Params(
                        initialDate = sideEffect.dateTime.toEpochMillis()
                    ))
                }
            }
        }.collect()
    }

    LaunchedEffect(key1 = navController.currentBackStackEntry) {
        viewModel.handleEvent(HappeningListEvent.FetchHappenings(state.currentDateTime))
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

    FluentlyScaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = FluentlyTheme.colors.backgroundColor,
        topBar = {
            FluentlyCenterAlignedTopAppBar(
                title = {
                    FluentlyText(
                        text = stringResource(id = R.string.interviews),
                        style = FluentlyTheme.typography.title1
                    )
                },
                actions = {
                    FluentlyIconButton(
                        onClick = {
                            navController.navigate(ThemeSettingsDestination.Params())
                        }
                    ) {
                        Icon(painter = painterResource(id = R.drawable.ic_settings), contentDescription = "")
                    }
                }
            )
        },
        floatingActionButton = {
            FluentlyFloatingActionButton(
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

                val coroutineScope = rememberCoroutineScope()
                HappeningTabs(
                    tabs = persistentListOf(
                        HappeningTabModel(
                            title = state.currentDateTime.minus(1, DateTimeUnit.DAY).toDateMonthString(nowDateTime = state.nowDateTime),
                            onClick = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(pagerState.currentPage - 1)
                                }
                            }
                        ),
                        HappeningTabModel(
                            title = state.currentDateTime.toDateMonthString(nowDateTime = state.nowDateTime),
                            onClick = {
                                viewModel.handleEvent(HappeningListEvent.ShowDatePicker)
                            }
                        ),
                        HappeningTabModel(
                            title = state.currentDateTime.plus(1, DateTimeUnit.DAY).toDateMonthString(nowDateTime = state.nowDateTime),
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
                        HappeningWeekend()
                    } else {
                        HappeningList(
                            items = items,
                            onClick = { happening ->
                                viewModel.handleEvent(HappeningListEvent.EditHappening(happening))
                            },
                            onRemove = { happening ->
                                viewModel.handleEvent(HappeningListEvent.RemoveHappening(happening))
                            },
                            onView = { happening ->
                                viewModel.handleEvent(HappeningListEvent.ShowCalendar(happening))
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
