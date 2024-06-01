package io.github.dmitrytsyvtsyn.fluently.data

import androidx.room.Database
import androidx.room.RoomDatabase
import io.github.dmitrytsyvtsyn.fluently.data.HappeningDao
import io.github.dmitrytsyvtsyn.fluently.data.HappeningTable

@Database(version = 1, entities = [io.github.dmitrytsyvtsyn.fluently.data.HappeningTable::class])
abstract class FluentlyDatabase : RoomDatabase() {

    abstract fun happeningDao() : io.github.dmitrytsyvtsyn.fluently.data.HappeningDao

    companion object {
        const val NAME = "app_database.db"
    }
}