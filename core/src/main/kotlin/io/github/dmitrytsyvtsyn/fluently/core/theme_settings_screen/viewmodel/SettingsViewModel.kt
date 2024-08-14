package io.github.dmitrytsyvtsyn.fluently.core.theme_settings_screen.viewmodel

import androidx.lifecycle.viewModelScope
import io.github.dmitrytsyvtsyn.fluently.core.di.DI
import io.github.dmitrytsyvtsyn.fluently.core.theme_settings_screen.data.SettingsRepository
import io.github.dmitrytsyvtsyn.fluently.core.viewmodel.BaseViewModel
import kotlinx.coroutines.launch

class SettingsViewModel : BaseViewModel<SettingsEvent, SettingsViewState, SettingsSideEffect>(SettingsViewState()) {

    private val repository = DI.get<SettingsRepository>()

    init {
        viewModelScope.launch {
            val contrast = repository.readContrast()
            setState { copy(contrast = contrast) }
        }
    }

    override fun handleEvent(event: SettingsEvent) {
        when(event) {
            is SettingsEvent.ChangeContrast -> {
                if (event.contrast == viewState.value.contrast) return

                viewModelScope.launch {
                    repository.saveContrast(event.contrast)
                    setState {
                        copy(contrast = event.contrast)
                    }
                }
            }
        }
    }

}