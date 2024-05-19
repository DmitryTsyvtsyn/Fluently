package io.github.dmitrytsyvtsyn.interfunny.theme_settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.dmitrytsyvtsyn.interfunny.core.di.DI
import io.github.dmitrytsyvtsyn.interfunny.core.theme.ThemeContrast
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SettingsViewModel : ViewModel() {

    private val repository = SettingsRepository(DI.preferences)

    private val _state = MutableStateFlow(
        SettingsViewState(
            contrast = ThemeContrast.LIGHT,
            contrasts = ThemeContrast.entries.toPersistentList()
        )
    )
    val state: StateFlow<SettingsViewState> = _state

    init {
        viewModelScope.launch {
            _state.value = _state.value.copy(contrast = repository.readContrast())
        }
    }

    fun changeContrast(contrast: ThemeContrast) {
        if (_state.value.contrast == contrast) return

        viewModelScope.launch {
            repository.saveContrast(contrast)
            _state.value = _state.value.copy(contrast = contrast)
        }
    }

}