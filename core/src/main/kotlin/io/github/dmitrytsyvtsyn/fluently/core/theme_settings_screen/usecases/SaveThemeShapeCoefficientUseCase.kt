package io.github.dmitrytsyvtsyn.fluently.core.theme_settings_screen.usecases

import io.github.dmitrytsyvtsyn.fluently.core.theme_settings_screen.data.SettingsRepository
import io.github.dmitrytsyvtsyn.fluently.core.theme_settings_screen.models.ThemeShapeCoefficient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SaveThemeShapeCoefficientUseCase(private val settingsRepository: SettingsRepository) {

    suspend fun execute(coefficient: ThemeShapeCoefficient) = withContext(Dispatchers.Default) {
        settingsRepository.saveThemeShapeCoefficient(coefficient.value)
    }

}