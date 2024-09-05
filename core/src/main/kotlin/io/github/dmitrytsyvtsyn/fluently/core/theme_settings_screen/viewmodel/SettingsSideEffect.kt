package io.github.dmitrytsyvtsyn.fluently.core.theme_settings_screen.viewmodel

sealed interface SettingsSideEffect {

    data object Back : SettingsSideEffect

}