package io.github.dmitrytsyvtsyn.fluently.core.di

import org.koin.core.Koin
import org.koin.core.qualifier.Qualifier

object DI {

    private var _koin: Koin? = null
    val koin: Koin
        get() = requireNotNull(_koin)

    inline fun <reified T : Any> get(qualifier: Qualifier? = null): T {
        return koin.get<T>(qualifier)
    }

    fun init(koin: Koin) {
        _koin = koin
    }

}