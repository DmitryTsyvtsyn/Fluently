package io.github.dmitrytsyvtsyn.fluently.core.theme_settings_screen.usecases

import io.github.dmitrytsyvtsyn.fluently.core.theme_settings_screen.data.SettingsRepository
import io.github.dmitrytsyvtsyn.fluently.core.theme_settings_screen.models.ThemeShapeCoefficient
import io.github.dmitrytsyvtsyn.fluently.core.theme_settings_screen.models.toThemeShapeCoefficient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FetchThemeShapeCoefficientUseCase(private val settingsRepository: SettingsRepository) {

    suspend fun execute() = withContext(Dispatchers.Default) {
        settingsRepository.readThemeShapeCoefficient(ThemeShapeCoefficient.Default.value)
            .toThemeShapeCoefficient()
    }

}