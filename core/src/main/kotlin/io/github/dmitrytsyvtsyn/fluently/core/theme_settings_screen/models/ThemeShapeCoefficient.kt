package io.github.dmitrytsyvtsyn.fluently.core.theme_settings_screen.models

@JvmInline
value class ThemeShapeCoefficient(val value: Float) {

    init {
        require(value in MIN..MAX) {
            "ThemeShapeCoefficient ($value) is not correct"
        }
    }

    companion object {
        private const val MIN = 0f
        private const val MAX = 1f

        val Default = ThemeShapeCoefficient(0.5f)
    }
}

fun Float.toThemeShapeCoefficient() = ThemeShapeCoefficient(this)