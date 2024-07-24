package io.github.dmitrytsyvtsyn.fluently.core.datetime

import kotlinx.datetime.DateTimePeriod
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.char
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toDateTimePeriod
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

fun Long.toLocalDateTime(): LocalDateTime {
    val millis = this
    return Instant.fromEpochMilliseconds(millis).toLocalDateTime(TimeZone.currentSystemDefault())
}

fun LocalDateTime.toEpochMillis(): Long {
    return toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds()
}

fun LocalDateTime.plus(value: Int, unit: DateTimeUnit = DateTimeUnit.DAY): LocalDateTime {
    val systemTimeZone = TimeZone.currentSystemDefault()
    return toInstant(systemTimeZone)
        .plus(value, unit, systemTimeZone)
        .toLocalDateTime(systemTimeZone)
}

fun LocalDateTime.minus(value: Int, unit: DateTimeUnit = DateTimeUnit.DAY): LocalDateTime {
    val systemTimeZone = TimeZone.currentSystemDefault()
    return toInstant(systemTimeZone)
        .minus(value, unit, systemTimeZone)
        .toLocalDateTime(systemTimeZone)
}

fun LocalDateTime.minus(other: LocalDateTime): DateTimePeriod {
    val systemTimezone = TimeZone.currentSystemDefault()
    val duration = toInstant(systemTimezone).minus(other.toInstant(systemTimezone))
    return duration.toDateTimePeriod()
}

operator fun DateTimePeriod.compareTo(other: DateTimePeriod): Int {
    val current = this
    if (current.hours != other.hours) return current.hours - other.hours
    if (current.minutes != other.minutes) return current.minutes - other.minutes
    if (current.seconds != other.seconds) return current.seconds - other.seconds
    if (current.nanoseconds != other.nanoseconds) return current.nanoseconds - other.nanoseconds
    return 0
}

fun LocalTime.toHoursMinutesString(): String {
    val currentTime = this
    return currentTime.format(hoursMinutesFormat)
}

private val hoursMinutesFormat = LocalTime.Format {
    hour()
    char(':')
    minute()
}