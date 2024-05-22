package io.github.dmitrytsyvtsyn.interfunny.core.data

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.provider.CalendarContract
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.TimeZone

class PlatformCalendarAPI(context: Context) {

    private val contentResolver = context.contentResolver

    suspend fun insertEvent(
        title: String,
        startDate: Long,
        endDate: Long,
        hasReminder: Boolean = false,
    ) = withContext(Dispatchers.Default) {

        val values = ContentValues().apply {
            put(CalendarContract.Events.DTSTART, startDate)
            put(CalendarContract.Events.DTEND, endDate)
            put(CalendarContract.Events.TITLE, title)
            put(CalendarContract.Events.DESCRIPTION, "")
            put(CalendarContract.Events.CALENDAR_ID, fetchCalendarPrimaryId())
            put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id)
        }

        val uri = contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)
        val eventId = uri?.lastPathSegment?.toLong() ?: -1

        if (hasReminder) {
            values.clear()
            with(values) {
                put(CalendarContract.Reminders.MINUTES, 1)
                put(CalendarContract.Reminders.EVENT_ID, eventId)
                put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT)
            }
            val reminderId = contentResolver.insert(CalendarContract.Reminders.CONTENT_URI, values)?.lastPathSegment?.toLong() ?: -1
            eventId to reminderId
        } else {
            eventId to -1L
        }
    }

    suspend fun updateEvent(
        eventId: Long,
        reminderId: Long,
        title: String,
        startDate: Long,
        endDate: Long,
        hasReminder: Boolean = false,
    ) = withContext(Dispatchers.Default) {
        val values = ContentValues().apply {
            put(CalendarContract.Events.TITLE, title)
            put(CalendarContract.Events.DTSTART, startDate)
            put(CalendarContract.Events.DTEND, endDate)
        }

        contentResolver.update(ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventId), values, null, null)

        when {
            reminderId >= 0 && !hasReminder -> {
                contentResolver.delete(ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, reminderId), null, null)
                -1
            }
            reminderId < 0 && hasReminder -> {
                values.clear()
                with(values) {
                    put(CalendarContract.Reminders.MINUTES, 1)
                    put(CalendarContract.Reminders.EVENT_ID, eventId)
                    put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT)
                }
                val reminderResult = contentResolver.insert(CalendarContract.Reminders.CONTENT_URI, values)

                reminderResult?.lastPathSegment?.toLong() ?: -1
            }
            else -> reminderId
        }
    }

    suspend fun removeEvent(eventId: Long, reminderId: Long) = withContext(Dispatchers.Default) {
        val eventRows = contentResolver.delete(ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventId), null, null)
        val reminderRows = if (reminderId >= 0) {
            contentResolver.delete(ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, reminderId), null, null)
        } else {
            0
        }
        eventRows > 0 || reminderRows > 0
    }

    @SuppressLint("Range")
    private fun fetchCalendarPrimaryId() : Long {
        var cursor = contentResolver.query(
            CalendarContract.Calendars.CONTENT_URI,
            arrayOf(CalendarContract.Calendars._ID),
            CalendarContract.Calendars.VISIBLE + " = 1 AND " + CalendarContract.Calendars.IS_PRIMARY + "=1",
            null,
            CalendarContract.Calendars._ID + " ASC"
        )

        if (cursor != null && cursor.count <= 0) {
            cursor = contentResolver.query(
                CalendarContract.Calendars.CONTENT_URI,
                arrayOf(CalendarContract.Calendars._ID),
                CalendarContract.Calendars.VISIBLE + " = 1",
                null,
                CalendarContract.Calendars._ID + " ASC"
            )
        }

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                val id = cursor.getString(cursor.getColumnIndex(CalendarContract.Calendars._ID))

                cursor.close()

                return id.toLongOrNull() ?: -1
            }
        }

        return -1
    }

}