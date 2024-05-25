package io.github.dmitrytsyvtsyn.fluently.core.di

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import io.github.dmitrytsyvtsyn.fluently.core.data.InterviewSqliteHelper
import io.github.dmitrytsyvtsyn.fluently.core.data.PlatformCalendarAPI

object DI {

    private var _sqliteHelper: InterviewSqliteHelper? = null
    val sqliteHelper: InterviewSqliteHelper
        get() = requireNotNull(_sqliteHelper)

    private var _platformAPI: PlatformCalendarAPI? = null
    val platformAPI: PlatformCalendarAPI
        get() = requireNotNull(_platformAPI)

    private var _preferences: SharedPreferences? = null
    val preferences: SharedPreferences
        get() = requireNotNull(_preferences)

    fun init(context: Context) {
        _sqliteHelper = InterviewSqliteHelper(context)
        _platformAPI = PlatformCalendarAPI(context)
        _preferences = context.getSharedPreferences("shared_preferences", MODE_PRIVATE)
    }

}