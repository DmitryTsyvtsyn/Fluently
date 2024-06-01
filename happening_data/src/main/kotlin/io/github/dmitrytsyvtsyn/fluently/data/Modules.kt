package io.github.dmitrytsyvtsyn.fluently.data

import org.koin.dsl.module

val dataModule = module {
    factory { HappeningRepository(get(), get()) }
}