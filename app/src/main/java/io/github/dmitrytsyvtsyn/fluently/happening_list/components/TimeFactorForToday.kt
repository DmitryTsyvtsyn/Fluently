package io.github.dmitrytsyvtsyn.fluently.happening_list.components

@JvmInline
value class TimeFactorForToday(val value: Float) {
    val isValid: Boolean
        get() = this >= MinFactor && this <= MaxFactor

    operator fun compareTo(other: TimeFactorForToday): Int {
        if (value == other.value) return 0
        if (value < other.value) return -1

        return 1
    }

    companion object {
        private const val HOURS_FOR_DAY_IN_MILLIS = 24 * 60 * 60 * 1000
        val MinFactor = TimeFactorForToday(0f)
        val MaxFactor = TimeFactorForToday(1f)
        val Invalid = TimeFactorForToday(-1f)

        fun from(time: Int): TimeFactorForToday {
            if (time < 0) return MinFactor
            if (time > HOURS_FOR_DAY_IN_MILLIS) return MaxFactor
            return TimeFactorForToday(time.toFloat() / HOURS_FOR_DAY_IN_MILLIS)
        }
    }
}