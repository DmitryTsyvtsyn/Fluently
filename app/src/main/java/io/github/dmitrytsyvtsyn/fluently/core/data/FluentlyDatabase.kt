package io.github.dmitrytsyvtsyn.fluently.core.data

import androidx.room.Database
import androidx.room.RoomDatabase
import io.github.dmitrytsyvtsyn.fluently.happening_list.data.HappeningDao
import io.github.dmitrytsyvtsyn.fluently.happening_list.data.HappeningTable

@Database(version = 1, entities = [HappeningTable::class])
abstract class FluentlyDatabase : RoomDatabase() {

    abstract fun happeningDao() : HappeningDao

    companion object {
        const val NAME = "app_database.db"
    }
}