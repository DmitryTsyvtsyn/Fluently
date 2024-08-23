package io.github.dmitrytsyvtsyn.fluently.core.theme_settings_screen.viewmodel

import androidx.lifecycle.viewModelScope
import io.github.dmitrytsyvtsyn.fluently.core.di.DI
import io.github.dmitrytsyvtsyn.fluently.core.theme_settings_screen.data.SettingsRepository
import io.github.dmitrytsyvtsyn.fluently.core.theme_settings_screen.models.ThemeColorVariant
import io.github.dmitrytsyvtsyn.fluently.core.theme_settings_screen.usecases.FetchThemeColorVariantUseCase
import io.github.dmitrytsyvtsyn.fluently.core.theme_settings_screen.usecases.SaveThemeColorVariantUseCase
import io.github.dmitrytsyvtsyn.fluently.core.viewmodel.BaseViewModel
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.launch

class SettingsViewModel : BaseViewModel<SettingsEvent, SettingsViewState, SettingsSideEffect>(
    SettingsViewState(
        themeColorVariant = ThemeColorVariant.DEFAULT,
        themeColorVariants = ThemeColorVariant.entries.toPersistentList()
    )
) {

    private val diComponent = object {
        private val repository = DI.get<SettingsRepository>()
        val saveThemeColorVariantUseCase = SaveThemeColorVariantUseCase(repository)
        val fetchThemeColorVariantUseCase = FetchThemeColorVariantUseCase(repository)
    }

    init {
        handleEvent(SettingsEvent.Init)
    }

    override fun handleEvent(event: SettingsEvent) {
        when(event) {
            is SettingsEvent.Init -> handleEvent(event)
            is SettingsEvent.ChangeThemeColorVariant -> handleEvent(event)
        }
    }

    private fun handleEvent(event: SettingsEvent.Init) {
        viewModelScope.launch {
            val themeColorVariant = diComponent.fetchThemeColorVariantUseCase.execute()
            setState {
                copy(
                    themeColorVariant = themeColorVariant
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

}