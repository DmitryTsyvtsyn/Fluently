package io.github.dmitrytsyvtsyn.fluently.core.theme_settings_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.dmitrytsyvtsyn.fluently.core.R
import io.github.dmitrytsyvtsyn.fluently.core.navigation.LocalNavController
import io.github.dmitrytsyvtsyn.fluently.core.theme.FluentlyTheme
import io.github.dmitrytsyvtsyn.fluently.core.theme.composables.FluentlyIcon
import io.github.dmitrytsyvtsyn.fluently.core.theme.composables.FluentlyIconButton
import io.github.dmitrytsyvtsyn.fluently.core.theme.composables.FluentlyText
import io.github.dmitrytsyvtsyn.fluently.core.theme_settings_screen.viewmodel.SettingsEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeSettingsScreen() {
    val navController = LocalNavController.current
    val viewModel = LocalSettingsViewModel.current

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    FluentlyText(
                        text = stringResource(id = R.string.theme_settings),
                        textAlign = TextAlign.Center,
                        style = FluentlyTheme.typography.title1
                    )
                },
                navigationIcon = {
                    FluentlyIconButton(
                        onClick = {
                            navController.popBackStack()
                        }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_back),
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Spacer(modifier = Modifier.size(8.dp))

            FluentlyText(
                text = stringResource(id = R.string.choose_primary_color),
                modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                style = FluentlyTheme.typography.body2
            )

            Spacer(modifier = Modifier.size(8.dp))

            val state by viewModel.viewState.collectAsState()
            Row(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                state.themeColorVariants.forEach { themeColorVariant ->
                    val colors = if (isSystemInDarkTheme()) themeColorVariant.darkColors else themeColorVariant.lightColors
                    Box(
                        modifier = Modifier
                            .clickable {
                                viewModel.handleEvent(
                                    SettingsEvent.ChangeThemeColorVariant(
                                        themeColorVariant
                                    )
                                )
                            }
                            .size(56.dp)
                            .background(
                                color = colors.primaryColor,
                                shape = FluentlyTheme.shapes.small
                            )
                    ) {
                        if (state.themeColorVariant == themeColorVariant) {
                            FluentlyIcon(
                                painter = painterResource(id = R.drawable.ic_check),
                                contentDescription = "",
                                modifier = Modifier.align(Alignment.Center),
                                tint = FluentlyTheme.colors.onPrimaryColor
                            )
                        }
                    }
                }
            }
        }
    }
}