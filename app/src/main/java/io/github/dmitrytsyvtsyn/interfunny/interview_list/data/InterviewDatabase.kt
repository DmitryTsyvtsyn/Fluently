package io.github.dmitrytsyvtsyn.interfunny.interview_list.data

import android.annotation.SuppressLint
import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase

class InterviewDatabase(
    val id: Long,
    val eventId: Long,
    val reminderId: Long,
    val title: String,
    val startDate: Long,
    val endDate: Long
) {

    fun insert(database: SQLiteDatabase, contentValues: ContentValues) {
        contentValues.put(ID_EVENT_COLUMN, eventId)
        contentValues.put(ID_REMINDER_COLUMN, reminderId)
        contentValues.put(TITLE_COLUMN, title)
        contentValues.put(START_DATE_COLUMN, startDate)
        contentValues.put(END_DATE_COLUMN, endDate)

        database.insert(TABLE_NAME, null, contentValues)
    }

    fun update(database: SQLiteDatabase, contentValues: ContentValues) {
        contentValues.put(ID_EVENT_COLUMN, eventId)
        contentValues.put(ID_REMINDER_COLUMN, reminderId)
        contentValues.put(TITLE_COLUMN, title)
        contentValues.put(START_DATE_COLUMN, startDate)
        contentValues.put(END_DATE_COLUMN, endDate)

        database.update(TABLE_NAME, contentValues, "$ID_COLUMN = ?", arrayOf(id.toString()))
    }

    companion object {
        private const val TABLE_NAME = "interview_table"
        private const val ID_COLUMN = "id"
        private const val ID_EVENT_COLUMN = "event_id"
        private const val ID_REMINDER_COLUMN = "reminder_id"
        private const val TITLE_COLUMN = "title"
        private const val START_DATE_COLUMN = "start_date"
        private const val END_DATE_COLUMN = "end_date"

        fun deleteTable(database: SQLiteDatabase, id: Long) {
            database.delete(TABLE_NAME, "$ID_COLUMN = ?", arrayOf(id.toString()))
        }

        @SuppressLint("Range")
        fun fetchTable(database: SQLiteDatabase, id: Long): InterviewDatabase {
            database.query(
                TABLE_NAME,
                arrayOf(ID_EVENT_COLUMN, ID_REMINDER_COLUMN, TITLE_COLUMN, START_DATE_COLUMN, END_DATE_COLUMN),
                "$ID_COLUMN = ?", arrayOf(id.toString()), null, null, null
            ).use { cursor ->
                if (cursor.moveToNext()) {
                    val eventId = cursor.getLong(cursor.getColumnIndex(ID_EVENT_COLUMN))
                    val reminderId = cursor.getLong(cursor.getColumnIndex(ID_REMINDER_COLUMN))
                    val title = cursor.getString(cursor.getColumnIndex(TITLE_COLUMN))
                    val startDate = cursor.getLong(cursor.getColumnIndex(START_DATE_COLUMN))
                    val endDate = cursor.getLong(cursor.getColumnIndex(END_DATE_COLUMN))
                    return InterviewDatabase(id, eventId, reminderId, title, startDate, endDate)
                }
            }
            error("Not founded interview_table item by id $id")
        }

        @SuppressLint("Range")
        fun fetchTable(database: SQLiteDatabase): List<InterviewDatabase> {
            val items = mutableListOf<InterviewDatabase>()

            database.query(
                TABLE_NAME,
                arrayOf(ID_COLUMN, ID_EVENT_COLUMN, ID_REMINDER_COLUMN, TITLE_COLUMN, START_DATE_COLUMN, END_DATE_COLUMN),
                null, null, null, null, null
            ).use { cursor ->
                while (cursor.moveToNext()) {
                    val id = cursor.getLong(cursor.getColumnIndex(ID_COLUMN))
                    val eventId = cursor.getLong(cursor.getColumnIndex(ID_EVENT_COLUMN))
                    val reminderId = cursor.getLong(cursor.getColumnIndex(ID_REMINDER_COLUMN))
                    val title = cursor.getString(cursor.getColumnIndex(TITLE_COLUMN))
                    val startDate = cursor.getLong(cursor.getColumnIndex(START_DATE_COLUMN))
                    val endDate = cursor.getLong(cursor.getColumnIndex(END_DATE_COLUMN))
                    items.add(InterviewDatabase(id, eventId, reminderId, title, startDate, endDate))
                }
            }

            return items
        }

        fun createTable(database: SQLiteDatabase) {
            database.execSQL("""
                create table if not exists $TABLE_NAME (
                    $ID_COLUMN INTEGER primary key not null,
                    $ID_EVENT_COLUMN INTEGER not null,
                    $ID_REMINDER_COLUMN INTEGER not null,
                    $TITLE_COLUMN TEXT not null,
                    $START_DATE_COLUMN INTEGER not null,
                    $END_DATE_COLUMN INTEGER not null
                )
            """.trimIndent())
        }

    }
}