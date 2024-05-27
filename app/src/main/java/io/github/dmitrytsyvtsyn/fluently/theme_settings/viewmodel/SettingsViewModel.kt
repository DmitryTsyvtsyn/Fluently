package io.github.dmitrytsyvtsyn.fluently.theme_settings.viewmodel

import android.os.Build
import androidx.lifecycle.viewModelScope
import io.github.dmitrytsyvtsyn.fluently.core.di.DI
import io.github.dmitrytsyvtsyn.fluently.core.theme.ThemeContrast
import io.github.dmitrytsyvtsyn.fluently.core.viewmodel.BaseViewModel
import io.github.dmitrytsyvtsyn.fluently.theme_settings.data.SettingsRepository
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.launch

class SettingsViewModel : BaseViewModel<SettingsEvent, SettingsViewState, SettingsSideEffect>(
    SettingsViewState(
        contrast = ThemeContrast.LIGHT,
        contrasts = ThemeContrast.entries.filter { contrast ->
            if (contrast != ThemeContrast.DYNAMIC) true
            else Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
        }.toPersistentList()
    )
) {

    private val repository = SettingsRepository(DI.preferences)

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
                    setState { copy(contrast = event.contrast) }
                }
            }
        }
    }

}