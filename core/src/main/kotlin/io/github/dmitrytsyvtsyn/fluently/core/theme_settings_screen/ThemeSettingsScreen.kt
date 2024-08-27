package io.github.dmitrytsyvtsyn.fluently.core.theme_settings_screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
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
import io.github.dmitrytsyvtsyn.fluently.core.theme.composables.FluentlyCenterAlignedTopAppBar
import io.github.dmitrytsyvtsyn.fluently.core.theme.composables.FluentlyIcon
import io.github.dmitrytsyvtsyn.fluently.core.theme.composables.FluentlyIconButton
import io.github.dmitrytsyvtsyn.fluently.core.theme.composables.FluentlyScaffold
import io.github.dmitrytsyvtsyn.fluently.core.theme.composables.FluentlySlider
import io.github.dmitrytsyvtsyn.fluently.core.theme.composables.FluentlyText
import io.github.dmitrytsyvtsyn.fluently.core.theme.composables.FluentlyTextButton
import io.github.dmitrytsyvtsyn.fluently.core.theme_settings_screen.composables.ThemeSelectionColorsRow
import io.github.dmitrytsyvtsyn.fluently.core.theme_settings_screen.models.ThemeShapeCoefficient
import io.github.dmitrytsyvtsyn.fluently.core.theme_settings_screen.models.toThemeShapeCoefficient
import io.github.dmitrytsyvtsyn.fluently.core.theme_settings_screen.viewmodel.SettingsEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ThemeSettingsScreen() {
    val navController = LocalNavController.current
    val viewModel = LocalSettingsViewModel.current

    FluentlyScaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            FluentlyCenterAlignedTopAppBar(
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
                        FluentlyIcon(
                            painter = painterResource(id = R.drawable.ic_back),
                            contentDescription = stringResource(R.string.back),
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
                text = stringResource(id = R.string.choose_color),
                modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                style = FluentlyTheme.typography.body2
            )

            Spacer(modifier = Modifier.size(8.dp))

            val state by viewModel.viewState.collectAsState()

            ThemeSelectionColorsRow(
                themeColorVariant = state.themeColorVariant,
                themeColorVariants = state.themeColorVariants,
                onClick = { variant ->
                    viewModel.handleEvent(SettingsEvent.ChangeThemeColorVariant(variant))
                }
            )

            Spacer(modifier = Modifier.size(12.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                FluentlyText(
                    text = stringResource(id = R.string.choose_shape),
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                    style = FluentlyTheme.typography.body2
                )
                FluentlyTextButton(
                    onClick = {
                        viewModel.handleEvent(SettingsEvent.ChangeThemeShapeCoefficient(ThemeShapeCoefficient.Default))
                    },
                    enabled = state.themeShapeCoefficient != ThemeShapeCoefficient.Default
                ) {
                    FluentlyText(
                        text = stringResource(id = R.string.reset),
                        color = FluentlyTheme.colors.primaryColor
                    )
                }
            }

            Spacer(modifier = Modifier.size(4.dp))

            FluentlySlider(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                value = state.themeShapeCoefficient.value,
                onValueChange = { value ->
                    viewModel.handleEvent(SettingsEvent.ChangeThemeShapeCoefficient(value.toThemeShapeCoefficient()))
                },
            )
        }
    }
}