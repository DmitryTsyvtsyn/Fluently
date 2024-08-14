package io.github.dmitrytsyvtsyn.fluently

import android.app.Application
import android.content.Context
import androidx.room.Room
import io.github.dmitrytsyvtsyn.fluently.core.di.DI
import io.github.dmitrytsyvtsyn.fluently.core.di.coreModule
import io.github.dmitrytsyvtsyn.fluently.data.FluentlyDatabase
import io.github.dmitrytsyvtsyn.fluently.data.di.dataModule
import org.koin.core.context.startKoin
import org.koin.dsl.module

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        val appModule = module {
            factory { applicationContext }
            single { get<Context>().getSharedPreferences("shared_preferences", MODE_PRIVATE) }
            single {
                Room.databaseBuilder(
                    get<Context>(),
                    FluentlyDatabase::class.java,
                    FluentlyDatabase.NAME
                ).build()
            }
            factory { get<FluentlyDatabase>().happeningDao() }
        }

        val koinApplication = startKoin {
            modules(
                appModule,
                coreModule,
                dataModule
            )
        }

        DI.init(koinApplication.koin)
    }

}