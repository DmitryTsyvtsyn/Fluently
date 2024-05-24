package io.github.dmitrytsyvtsyn.interfunny.theme_settings.viewmodel

import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.dmitrytsyvtsyn.interfunny.core.di.DI
import io.github.dmitrytsyvtsyn.interfunny.core.theme.ThemeContrast
import io.github.dmitrytsyvtsyn.interfunny.theme_settings.data.SettingsRepository
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SettingsViewModel : ViewModel() {

    private val repository = SettingsRepository(DI.preferences)

    private val _state = MutableStateFlow(
        SettingsViewState(
            contrast = ThemeContrast.LIGHT,
            contrasts = ThemeContrast.entries.filter { contrast ->
                if (contrast != ThemeContrast.DYNAMIC) true
                else Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
            }.toPersistentList()
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