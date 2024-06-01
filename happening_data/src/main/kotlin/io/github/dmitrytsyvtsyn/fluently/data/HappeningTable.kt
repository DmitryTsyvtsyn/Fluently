package io.github.dmitrytsyvtsyn.fluently.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "happening_table")
data class HappeningTable(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo("calendar_event_id")
    val calendarEventId: Long,
    @ColumnInfo("calendar_reminder_id")
    val calendarReminderId: Long,
    @ColumnInfo("title")
    val title: String,
    @ColumnInfo("start_date")
    val startDate: Long,
    @ColumnInfo("end_date")
    val endDate: Long
)