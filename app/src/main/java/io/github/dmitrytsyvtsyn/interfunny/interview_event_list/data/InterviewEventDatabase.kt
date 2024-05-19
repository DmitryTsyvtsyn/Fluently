package io.github.dmitrytsyvtsyn.interfunny.interview_event_list.data

import android.annotation.SuppressLint
import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase

class InterviewEventDatabase(
    val id: Long,
    val eventId: Long,
    val reminderId: Long,
    val title: String,
    val startDate: Long,
    val endDate: Long
) {

    fun insert(database: SQLiteDatabase, contentValues: ContentValues) {
        contentValues.put(id_event_column, eventId)
        contentValues.put(id_reminder_column, reminderId)
        contentValues.put(title_column, title)
        contentValues.put(start_date_column, startDate)
        contentValues.put(end_date_column, endDate)

        database.insert(table_name, null, contentValues)
    }

    fun update(database: SQLiteDatabase, contentValues: ContentValues) {
        contentValues.put(id_event_column, eventId)
        contentValues.put(id_reminder_column, reminderId)
        contentValues.put(title_column, title)
        contentValues.put(start_date_column, startDate)
        contentValues.put(end_date_column, endDate)

        database.update(table_name, contentValues, "$id_column = ?", arrayOf(id.toString()))
    }

    companion object {
        private const val table_name = "interview_table"
        private const val id_column = "id"
        private const val id_event_column = "event_id"
        private const val id_reminder_column = "reminder_id"
        private const val title_column = "title"
        private const val start_date_column = "start_date"
        private const val end_date_column = "end_date"

        fun deleteTable(database: SQLiteDatabase, id: Long) {
            database.delete(table_name, "$id_column = ?", arrayOf(id.toString()))
        }

        @SuppressLint("Range")
        fun fetchTable(database: SQLiteDatabase, id: Long): InterviewEventDatabase {
            database.query(
                table_name,
                arrayOf(id_event_column, id_reminder_column, title_column, start_date_column, end_date_column),
                "$id_column = ?", arrayOf(id.toString()), null, null, null
            ).use { cursor ->
                if (cursor.moveToNext()) {
                    val eventId = cursor.getLong(cursor.getColumnIndex(id_event_column))
                    val reminderId = cursor.getLong(cursor.getColumnIndex(id_reminder_column))
                    val title = cursor.getString(cursor.getColumnIndex(title_column))
                    val startDate = cursor.getLong(cursor.getColumnIndex(start_date_column))
                    val endDate = cursor.getLong(cursor.getColumnIndex(end_date_column))
                    return InterviewEventDatabase(id, eventId, reminderId, title, startDate, endDate)
                }
            }
            error("Not founded coffee_table item by id $id")
        }

        @SuppressLint("Range")
        fun fetchTable(database: SQLiteDatabase): List<InterviewEventDatabase> {
            val items = mutableListOf<InterviewEventDatabase>()

            database.query(
                table_name,
                arrayOf(id_column, id_event_column, id_reminder_column, title_column, start_date_column, end_date_column),
                null, null, null, null, null
            ).use { cursor ->
                while (cursor.moveToNext()) {
                    val id = cursor.getLong(cursor.getColumnIndex(id_column))
                    val eventId = cursor.getLong(cursor.getColumnIndex(id_event_column))
                    val reminderId = cursor.getLong(cursor.getColumnIndex(id_reminder_column))
                    val title = cursor.getString(cursor.getColumnIndex(title_column))
                    val startDate = cursor.getLong(cursor.getColumnIndex(start_date_column))
                    val endDate = cursor.getLong(cursor.getColumnIndex(end_date_column))
                    items.add(InterviewEventDatabase(id, eventId, reminderId, title, startDate, endDate))
                }
            }

            return items
        }

        fun createTable(database: SQLiteDatabase) {
            database.execSQL("""
                create table if not exists $table_name (
                    $id_column INTEGER primary key not null,
                    $id_event_column INTEGER not null,
                    $id_reminder_column INTEGER not null,
                    $title_column TEXT not null,
                    $start_date_column INTEGER not null,
                    $end_date_column INTEGER not null
                )
            """.trimIndent())
        }

    }
}