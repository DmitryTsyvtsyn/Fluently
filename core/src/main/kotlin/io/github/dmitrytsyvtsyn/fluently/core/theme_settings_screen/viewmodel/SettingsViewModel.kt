package io.github.dmitrytsyvtsyn.fluently.core.theme_settings_screen.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.dmitrytsyvtsyn.fluently.core.coroutines.update
import io.github.dmitrytsyvtsyn.fluently.core.di.DI
import io.github.dmitrytsyvtsyn.fluently.core.theme_settings_screen.data.SettingsRepository
import io.github.dmitrytsyvtsyn.fluently.core.theme_settings_screen.models.ThemeColorVariant
import io.github.dmitrytsyvtsyn.fluently.core.theme_settings_screen.models.ThemeShapeCoefficient
import io.github.dmitrytsyvtsyn.fluently.core.theme_settings_screen.usecases.FetchThemeColorVariantUseCase
import io.github.dmitrytsyvtsyn.fluently.core.theme_settings_screen.usecases.FetchThemeShapeCoefficientUseCase
import io.github.dmitrytsyvtsyn.fluently.core.theme_settings_screen.usecases.SaveThemeColorVariantUseCase
import io.github.dmitrytsyvtsyn.fluently.core.theme_settings_screen.usecases.SaveThemeShapeCoefficientUseCase
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class SettingsViewModel : ViewModel() {

    private val _viewState = MutableStateFlow(
        SettingsViewState(
            themeColorVariant = ThemeColorVariant.DEFAULT,
            themeColorVariants = ThemeColorVariant.entries.toPersistentList(),
            themeShapeCoefficient = ThemeShapeCoefficient.Default
        )
    )
    val viewState: StateFlow<SettingsViewState> = _viewState

    private val _effect = MutableSharedFlow<SettingsSideEffect>()
    val effect = _effect.asSharedFlow()

    private val diComponent = object {
        private val repository = DI.get<SettingsRepository>()

        val saveThemeColorVariantUseCase = SaveThemeColorVariantUseCase(repository)
        val fetchThemeColorVariantUseCase = FetchThemeColorVariantUseCase(repository)

        val saveThemeShapeCoefficientUseCase = SaveThemeShapeCoefficientUseCase(repository)
        val fetchThemeShapeCoefficientUseCase = FetchThemeShapeCoefficientUseCase(repository)
    }

    fun handleEvent(event: SettingsEvent) {
        when(event) {
            is SettingsEvent.Init -> handleEvent(event)
            is SettingsEvent.ChangeThemeColorVariant -> handleEvent(event)
            is SettingsEvent.ChangeThemeShapeCoefficient -> handleEvent(event)
            is SettingsEvent.Back -> handleEvent(event)
        }
    }

    private fun handleEvent(event: SettingsEvent.Init) {
        viewModelScope.launch {
            val themeColorVariant = diComponent.fetchThemeColorVariantUseCase.execute()
            val themeShapeCoefficient = diComponent.fetchThemeShapeCoefficientUseCase.execute()
            _viewState.update {
                copy(
                    themeColorVariant = themeColorVariant,
                    themeShapeCoefficient = themeShapeCoefficient,
                    isBackNavigationButtonEnabled = true
                )
            }
        }
    }

    private fun handleEvent(event: SettingsEvent.ChangeThemeColorVariant) {
        val themeColorVariant = event.variant
        if (themeColorVariant == viewState.value.themeColorVariant) return

        viewModelScope.launch {
            diComponent.saveThemeColorVariantUseCase.execute(themeColorVariant)
            _viewState.update { copy(themeColorVariant = themeColorVariant) }
        }
    }

    private fun handleEvent(event: SettingsEvent.ChangeThemeShapeCoefficient) {
        val themeShapeCoefficient = event.coefficient
        if (themeShapeCoefficient == viewState.value.themeShapeCoefficient) return

        viewModelScope.launch {
            diComponent.saveThemeShapeCoefficientUseCase.execute(themeShapeCoefficient)
            _viewState.update { copy(themeShapeCoefficient = themeShapeCoefficient) }
        }
    }

    private fun handleEvent(event: SettingsEvent.Back) {
        viewModelScope.launch {
            _viewState.update { copy(isBackNavigationButtonEnabled = false) }
            _effect.emit(SettingsSideEffect.Back)
        }
    }

}