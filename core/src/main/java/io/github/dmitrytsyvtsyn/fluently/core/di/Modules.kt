package io.github.dmitrytsyvtsyn.fluently.core.di

import io.github.dmitrytsyvtsyn.fluently.core.data.PlatformCalendarAPI
import io.github.dmitrytsyvtsyn.fluently.core.theme_settings_screen.data.SettingsRepository
import org.koin.dsl.module

val coreModule = module {
    factory { SettingsRepository(get()) }
    single { PlatformCalendarAPI(get()) }
}