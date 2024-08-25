package io.github.dmitrytsyvtsyn.fluently.core.theme_settings_screen.viewmodel

import androidx.lifecycle.viewModelScope
import io.github.dmitrytsyvtsyn.fluently.core.di.DI
import io.github.dmitrytsyvtsyn.fluently.core.theme_settings_screen.data.SettingsRepository
import io.github.dmitrytsyvtsyn.fluently.core.theme_settings_screen.models.ThemeColorVariant
import io.github.dmitrytsyvtsyn.fluently.core.theme_settings_screen.models.ThemeShapeCoefficient
import io.github.dmitrytsyvtsyn.fluently.core.theme_settings_screen.usecases.FetchThemeColorVariantUseCase
import io.github.dmitrytsyvtsyn.fluently.core.theme_settings_screen.usecases.FetchThemeShapeCoefficientUseCase
import io.github.dmitrytsyvtsyn.fluently.core.theme_settings_screen.usecases.SaveThemeColorVariantUseCase
import io.github.dmitrytsyvtsyn.fluently.core.theme_settings_screen.usecases.SaveThemeShapeCoefficientUseCase
import io.github.dmitrytsyvtsyn.fluently.core.viewmodel.BaseViewModel
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.launch

class SettingsViewModel : BaseViewModel<SettingsEvent, SettingsViewState, SettingsSideEffect>(
    SettingsViewState(
        themeColorVariant = ThemeColorVariant.DEFAULT,
        themeColorVariants = ThemeColorVariant.entries.toPersistentList(),
        themeShapeCoefficient = ThemeShapeCoefficient.Default
    )
) {

    private val diComponent = object {
        private val repository = DI.get<SettingsRepository>()

        val saveThemeColorVariantUseCase = SaveThemeColorVariantUseCase(repository)
        val fetchThemeColorVariantUseCase = FetchThemeColorVariantUseCase(repository)

        val saveThemeShapeCoefficientUseCase = SaveThemeShapeCoefficientUseCase(repository)
        val fetchThemeShapeCoefficientUseCase = FetchThemeShapeCoefficientUseCase(repository)
    }

    init {
        handleEvent(SettingsEvent.Init)
    }

    override fun handleEvent(event: SettingsEvent) {
        when(event) {
            is SettingsEvent.Init -> handleEvent(event)
            is SettingsEvent.ChangeThemeColorVariant -> handleEvent(event)
            is SettingsEvent.ChangeThemeShapeCoefficient -> handleEvent(event)
        }
    }

    private fun handleEvent(event: SettingsEvent.Init) {
        viewModelScope.launch {
            val themeColorVariant = diComponent.fetchThemeColorVariantUseCase.execute()
            val themeShapeCoefficient = diComponent.fetchThemeShapeCoefficientUseCase.execute()
            setState {
                copy(
                    themeColorVariant = themeColorVariant,
                    themeShapeCoefficient = themeShapeCoefficient
                )
            }
        }
    }

    private fun handleEvent(event: SettingsEvent.ChangeThemeColorVariant) {
        val themeColorVariant = event.variant
        if (themeColorVariant == viewState.value.themeColorVariant) return

        viewModelScope.launch {
            diComponent.saveThemeColorVariantUseCase.execute(themeColorVariant)
            setState {
                copy(themeColorVariant = themeColorVariant)
            }
        }
    }

    private fun handleEvent(event: SettingsEvent.ChangeThemeShapeCoefficient) {
        val themeShapeCoefficient = event.coefficient
        if (themeShapeCoefficient == viewState.value.themeShapeCoefficient) return

        viewModelScope.launch {
            diComponent.saveThemeShapeCoefficientUseCase.execute(themeShapeCoefficient)
            setState {
                copy(themeShapeCoefficient = themeShapeCoefficient)
            }
        }
    }

}