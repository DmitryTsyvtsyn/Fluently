package io.github.dmitrytsyvtsyn.fluently.data

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import io.github.dmitrytsyvtsyn.fluently.data.database.HappeningDao
import io.github.dmitrytsyvtsyn.fluently.data.database.HappeningTable

@Database(
    version = 2,
    entities = [HappeningTable::class],
    autoMigrations = [
        AutoMigration(1, 2)
    ]
)
abstract class FluentlyDatabase : RoomDatabase() {

    abstract fun happeningDao() : HappeningDao

    companion object {
        const val NAME = "app_database.db"
    }
}