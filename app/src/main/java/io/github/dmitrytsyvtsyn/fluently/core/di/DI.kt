package io.github.dmitrytsyvtsyn.fluently.core.di

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.room.Room
import io.github.dmitrytsyvtsyn.fluently.core.data.FluentlyDatabase
import io.github.dmitrytsyvtsyn.fluently.core.data.PlatformCalendarAPI

object DI {

    private var _database: FluentlyDatabase? = null
    val database: FluentlyDatabase
        get() = requireNotNull(_database)

    private var _platformAPI: PlatformCalendarAPI? = null
    val platformAPI: PlatformCalendarAPI
        get() = requireNotNull(_platformAPI)

    private var _preferences: SharedPreferences? = null
    val preferences: SharedPreferences
        get() = requireNotNull(_preferences)

    fun init(context: Context) {
        _database = Room.databaseBuilder(context, FluentlyDatabase::class.java, FluentlyDatabase.NAME).build()
        _platformAPI = PlatformCalendarAPI(context)
        _preferences = context.getSharedPreferences("shared_preferences", MODE_PRIVATE)
    }

}