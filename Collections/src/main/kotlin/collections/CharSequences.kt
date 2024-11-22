package collections

import kotlin.math.min

fun StringBuilder.duplicate(amount: Int) {
    checkIfNegativeAmount(amount)

    val length = this.length

    repeat(amount) {
        for (index in 0 until length) {
            this.append(this[index])
        }
    }
}

fun CharSequence.interspersed(separator: CharSequence): String {
    val sb = StringBuilder(this)

    sb.intersperse(separator)

    return sb.toString()
}

fun StringBuilder.intersperse(separator: CharSequence) {
    if (separator.isEmpty()) {
        return
    }

    var index = 1
    val length = separator.length

    while (index < this.length) {
        this.insert(index, separator)

        index += length + 1
    }
}

fun CharSequence.startsWithIgnoreCase(prefix: CharSequence): Boolean {
    val length = prefix.length

    if (length > this.length) {
        return false
    }

    for (index in 0 until length) {
        val char = this[index].lowercaseChar()
        val prefixChar = prefix[index].lowercaseChar()

        if (char != prefixChar) {
            return false
        }
    }

    return true
}

fun CharSequence.endsWithIgnoreCase(suffix: CharSequence): Boolean {
    if (suffix.length > this.length) {
        return false
    }

    var index = this.lastIndex
    var suffixIndex = suffix.lastIndex

    while (suffixIndex >= 0) {
        val char = this[index].lowercaseChar()
        val suffixChar = suffix[suffixIndex].lowercaseChar()

        if (char != suffixChar) {
            return false
        }

        --index
        --suffixIndex
    }

    return true
}

fun compareIgnoreCase(str1: CharSequence?, str2: CharSequence?): Int {
    if (null === str1 && null === str2)
        return 0
    else if (null === str1)
        return -1
    else if (null === str2)
        return 1
    else {
        val length1 = str1.length
        val length2 = str2.length

        val smallerLength = min(length1, length2)

        for (index in 0 until smallerLength) {
            val char1 = str1[index].lowercaseChar()
            val char2 = str2[index].lowercaseChar()

            val comp = char1 - char2

            if (0 != comp) {
                return comp
            }
        }

        return length1 - length2
    }
}
