package io.github.dmitrytsyvtsyn.fluently.core.data

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.provider.CalendarContract
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.TimeZone

internal class PlatformCalendarAPIImpl(context: Context): PlatformCalendarAPI {

    private val contentResolver = context.contentResolver

    override suspend fun insertEventWithReminder(
        title: String,
        startDate: Long,
        endDate: Long
    ) = withContext(Dispatchers.Default) {
        val values = ContentValues()
        with(values) {
            put(CalendarContract.Events.DTSTART, startDate)
            put(CalendarContract.Events.DTEND, endDate)
            put(CalendarContract.Events.TITLE, title)
            put(CalendarContract.Events.DESCRIPTION, "")
            put(CalendarContract.Events.CALENDAR_ID, fetchCalendarPrimaryId().value)
            put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id)
        }
        val eventId = contentResolver.insert(
            CalendarContract.Events.CONTENT_URI,
            values
        )?.lastPathSegment.toIdLong()

        with(values) {
            clear()
            put(CalendarContract.Reminders.MINUTES, 1)
            put(CalendarContract.Reminders.EVENT_ID, eventId.value)
            put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT)
        }
        val reminderId = contentResolver.insert(
            CalendarContract.Reminders.CONTENT_URI,
            values
        )?.lastPathSegment.toIdLong()

        InsertEventResult(eventId, reminderId)
    }

    override suspend fun updateEventWithReminder(
        eventId: IdLong,
        title: String,
        startDate: Long,
        endDate: Long
    ) = withContext(Dispatchers.Default) {
        if (eventId.isEmpty) return@withContext false
        val values = ContentValues()
        with(values) {
            put(CalendarContract.Events.TITLE, title)
            put(CalendarContract.Events.DTSTART, startDate)
            put(CalendarContract.Events.DTEND, endDate)
        }
        val updatedRowCount = contentResolver.update(eventId.toEventsContentUri(), values, null, null)
        updatedRowCount > 0
    }

    override suspend fun removeEventWithReminder(eventId: IdLong, reminderId: IdLong) =
        withContext(Dispatchers.Default) {
            val eventRows = if (eventId.isNotEmpty) {
                contentResolver.delete(eventId.toEventsContentUri(), null, null)
            } else {
                0
            }

            val reminderRows = if (reminderId.isNotEmpty) {
                contentResolver.delete(reminderId.toRemindersContentUri(), null, null)
            } else {
                0
            }

            eventRows > 0 || reminderRows > 0
        }

    private fun fetchCalendarPrimaryId() : IdLong {
        var cursor = contentResolver.query(
            CalendarContract.Calendars.CONTENT_URI,
            arrayOf(CalendarContract.Calendars._ID),
            CalendarContract.Calendars.VISIBLE + " = 1 AND " + CalendarContract.Calendars.IS_PRIMARY + " = 1",
            null,
            CalendarContract.Calendars._ID + " ASC"
        )

        if (cursor == null) return IdLong.Empty

        if (cursor.count <= 0) {
            cursor = contentResolver.query(
                CalendarContract.Calendars.CONTENT_URI,
                arrayOf(CalendarContract.Calendars._ID),
                CalendarContract.Calendars.VISIBLE + " = 1",
                null,
                CalendarContract.Calendars._ID + " ASC"
            )
        }

        if (cursor == null) return IdLong.Empty

        if (cursor.moveToFirst()) {
            val calendarIdColumnIndex = cursor.getColumnIndex(CalendarContract.Calendars._ID)
            if (calendarIdColumnIndex < 0) return IdLong.Empty

            val id = cursor.getString(calendarIdColumnIndex)

            cursor.close()

            return id.toIdLong()
        }

        return IdLong.Empty
    }

    private fun IdLong.toEventsContentUri() =
        ContentUris.withAppendedId(
            CalendarContract.Events.CONTENT_URI,
            value
        )

    private fun IdLong.toRemindersContentUri() =
        ContentUris.withAppendedId(
            CalendarContract.Events.CONTENT_URI,
            value
        )

}