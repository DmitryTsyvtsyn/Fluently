package io.github.dmitrytsyvtsyn.fluently.core.theme

import io.github.dmitrytsyvtsyn.fluently.R

enum class ThemeContrast(val title: Int, val description: Int) {
    DYNAMIC(R.string.dynamic, R.string.dynamic_contrast_description),
    LIGHT(R.string.light, R.string.light_contrast_description),
    MEDIUM(R.string.medium, R.string.medium_contrast_description),
    HIGH(R.string.high, R.string.high_contrast_description)
}