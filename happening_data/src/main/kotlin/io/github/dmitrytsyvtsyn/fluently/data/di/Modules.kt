package io.github.dmitrytsyvtsyn.fluently.data.di

import io.github.dmitrytsyvtsyn.fluently.data.HappeningRepository
import io.github.dmitrytsyvtsyn.fluently.data.HappeningRepositoryImpl
import org.koin.dsl.module

val dataModule = module {
    factory<HappeningRepository> { HappeningRepositoryImpl(get(), get()) }
}