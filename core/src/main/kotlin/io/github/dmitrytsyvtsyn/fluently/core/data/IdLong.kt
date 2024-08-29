package io.github.dmitrytsyvtsyn.fluently.core.data

fun String?.toIdLong(): IdLong {
    val string = this
    return string?.toLongOrNull()?.let { value ->
        IdLong(value)
    } ?: IdLong.Empty
}

fun Long.toIdLong(): IdLong {
    val long = this
    return if (long == IdLong.Empty.value) {
        IdLong.Empty
    } else {
        IdLong(long)
    }
}

@JvmInline
value class IdLong(val value: Long) {
    val isEmpty: Boolean
        get() = value <= MIN_POSSIBLE_VALUE
    val isNotEmpty: Boolean
        get() = !isEmpty

    init {
        require(value >= MIN_POSSIBLE_VALUE) {
            "id must not be less than $MIN_POSSIBLE_VALUE"
        }
    }

    companion object {
        private const val MIN_POSSIBLE_VALUE = -1L

        val Empty = IdLong(MIN_POSSIBLE_VALUE)
    }
}