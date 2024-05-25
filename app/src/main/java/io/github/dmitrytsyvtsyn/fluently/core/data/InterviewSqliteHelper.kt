package io.github.dmitrytsyvtsyn.fluently.core.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import io.github.dmitrytsyvtsyn.fluently.happening_list.data.InterviewDatabase

class InterviewSqliteHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    private val database by lazy { writableDatabase }
    private val contentValues = ContentValues()

    override fun onCreate(database: SQLiteDatabase) {
        InterviewDatabase.createTable(database)
    }

    fun fetch(): List<InterviewDatabase> {
        return InterviewDatabase.fetchTable(database)
    }

    fun fetch(id: Long): InterviewDatabase {
        return InterviewDatabase.fetchTable(database, id)
    }

    fun insert(item: InterviewDatabase) {
        contentValues.clear()
        item.insert(database, contentValues)
    }

    fun update(item: InterviewDatabase) {
        contentValues.clear()
        item.update(database, contentValues)
    }

    fun delete(id: Long) {
        InterviewDatabase.deleteTable(database, id)
    }

    override fun onUpgrade(database: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // no migrations
    }

    companion object {
        const val DATABASE_NAME = "interview_database"
        const val DATABASE_VERSION = 1
    }
}