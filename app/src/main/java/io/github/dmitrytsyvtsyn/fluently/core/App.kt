package io.github.dmitrytsyvtsyn.fluently.core

import android.app.Application
import io.github.dmitrytsyvtsyn.fluently.core.di.DI

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        DI.init(applicationContext)
    }

}