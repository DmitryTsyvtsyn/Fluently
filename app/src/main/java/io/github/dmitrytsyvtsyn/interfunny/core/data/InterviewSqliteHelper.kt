package io.github.dmitrytsyvtsyn.interfunny.core.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import io.github.dmitrytsyvtsyn.interfunny.interview_event_list.data.InterviewEventDatabase

class InterviewSqliteHelper(context: Context) : SQLiteOpenHelper(context, databaseName, null, databaseVersion) {

    private val database by lazy { writableDatabase }
    private val contentValues = ContentValues()

    override fun onCreate(database: SQLiteDatabase) {
        InterviewEventDatabase.createTable(database)
    }

    fun fetch(): List<InterviewEventDatabase> {
        return InterviewEventDatabase.fetchTable(database)
    }

    fun fetch(id: Long): InterviewEventDatabase {
        return InterviewEventDatabase.fetchTable(database, id)
    }

    fun insert(item: InterviewEventDatabase) {
        contentValues.clear()
        item.insert(database, contentValues)
    }

    fun update(item: InterviewEventDatabase) {
        contentValues.clear()
        item.update(database, contentValues)
    }

    fun delete(id: Long) {
        InterviewEventDatabase.deleteTable(database, id)
    }

    override fun onUpgrade(database: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // no migrations
    }

    companion object {
        const val databaseName = "interview_database"
        const val databaseVersion = 1
    }
}