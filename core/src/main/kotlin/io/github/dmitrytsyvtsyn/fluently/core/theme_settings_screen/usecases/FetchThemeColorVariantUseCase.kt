package io.github.dmitrytsyvtsyn.fluently.core.theme_settings_screen.usecases

import io.github.dmitrytsyvtsyn.fluently.core.theme_settings_screen.data.SettingsRepository
import io.github.dmitrytsyvtsyn.fluently.core.theme_settings_screen.models.ThemeColorVariant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FetchThemeColorVariantUseCase(private val settingsRepository: SettingsRepository) {

    suspend fun execute() = withContext(Dispatchers.Default) {
        val themeColorVariantInt = settingsRepository.readThemeColorVariant(ThemeColorVariant.DEFAULT.ordinal)
        ThemeColorVariant.entries[themeColorVariantInt]
    }

}