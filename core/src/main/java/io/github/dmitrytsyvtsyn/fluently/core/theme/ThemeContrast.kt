package io.github.dmitrytsyvtsyn.fluently.core.theme

import android.os.Build
import io.github.dmitrytsyvtsyn.fluently.core.R
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList

enum class ThemeContrast(val title: Int, val description: Int) {
    DYNAMIC(R.string.dynamic, R.string.dynamic_contrast_description),
    LIGHT(R.string.light, R.string.light_contrast_description),
    MEDIUM(R.string.medium, R.string.medium_contrast_description),
    HIGH(R.string.high, R.string.high_contrast_description);

    companion object {
        val entriesWithDynamicIfPossible: PersistentList<ThemeContrast>
            get() = entries.filter { contrast ->
                if (contrast != DYNAMIC) true
                else Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
            }.toPersistentList()
    }
}