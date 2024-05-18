package io.github.dmitrytsyvtsyn.interfunny.core

import android.app.Application
import io.github.dmitrytsyvtsyn.interfunny.core.di.DI

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        DI.init(applicationContext)
    }

}