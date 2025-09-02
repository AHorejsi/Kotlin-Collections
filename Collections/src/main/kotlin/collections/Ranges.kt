package collections

import kotlin.math.abs

fun IntRange.count(): Int =
    this.last - this.first + 1

fun LongRange.count(): Long =
    this.last - this.first + 1L

fun IntProgression.count(): Int {
    if (this is IntRange) {
        return this.count()
    }

    if (this.isEmpty()) {
        return 0
    }

    val gap = abs(this.first - this.last) + 1
    val step = abs(this.step)

    val quot = gap / step
    val rem = gap % step

    return quot + rem
}

fun LongProgression.count(): Long {
    if (this is LongRange) {
        return this.count()
    }

    if (this.isEmpty()) {
        return 0L
    }

    val gap = abs(this.first - this.last) + 1L
    val step = abs(this.step)

    val quot = gap / step
    val rem = gap % step

    return quot + rem
}

infix fun Int.up(amount: Int): IntRange {
    checkIfNegativeAmount(amount)

    return if (0 == amount)
        IntRange.EMPTY
    else
        this until checkForOverflowOnAddition(this, amount)
}

infix fun Long.up(amount: Long): LongRange {
    checkIfNegativeAmount(amount)

    return if (0L == amount)
        LongRange.EMPTY
    else
        this until checkForOverflowOnAddition(this, amount)
}

infix fun Int.down(amount: Int): IntProgression {
    checkIfNegativeAmount(amount)

    return if (0 == amount)
        IntRange.EMPTY
    else
        this downTo checkForOverflowOnSubtraction(this, amount) + 1
}

infix fun Long.down(amount: Long): LongProgression {
    checkIfNegativeAmount(amount)

    return if (0L == amount)
        LongRange.EMPTY
    else
        this downTo checkForOverflowOnSubtraction(this, amount) + 1L
}

infix fun Int.move(amount: Int): IntProgression =
    if (amount >= 0)
        this up amount
    else
        this down -amount

infix fun Long.move(amount: Long): LongProgression =
    if (amount >= 0)
        this up amount
    else
        this down -amount

operator fun IntRange.get(index: Int): Int =
    this.elementAt(index)

operator fun LongRange.get(index: Long): Long =
    this.elementAt(index)

operator fun IntProgression.get(index: Int): Int =
    this.elementAt(index)

operator fun LongProgression.get(index: Long): Long =
    this.elementAt(index)

fun IntRange.elementAt(index: Int): Int {
    val first = this.first
    val last = this.last

    val result =
        if (first < last)
            first + index
        else
            last - index

    return if (result < first || result > last)
        outOfBoundsWithoutSize(index)
    else
        result
}

fun LongRange.elementAt(index: Long): Long {
    val first = this.first
    val last = this.last

    val result =
        if (first < last)
            first + index
        else
            last - index

    return if (result < first || result > last)
        outOfBoundsWithoutSize(index)
    else
        result
}

fun IntProgression.elementAt(index: Int): Int {
    if (this is IntRange) {
        return this.elementAt(index)
    }

    val factor = abs(this.step)

    val first = this.first
    val last = this.last

    val (start, end, result) =
        if (first < last)
            Triple(first, last, first + factor * index)
        else
            Triple(last, first, first - factor * index)

    return if (result < start || result > end)
        outOfBoundsWithoutSize(index)
    else
        result
}

fun LongProgression.elementAt(index: Long): Long {
    if (this is LongRange) {
        return this.elementAt(index)
    }

    val factor = abs(this.step)

    val first = this.first
    val last = this.last

    val (start, end, result) =
        if (first < last)
            Triple(first, last, first + factor * index)
        else
            Triple(last, first, first - factor * index)

    return if (result < start || result > end)
        outOfBoundsWithoutSize(index)
    else
        result
}

fun IntProgression.indexOf(value: Int): Int {
    val comp =
        if (this.first < this.last)
            inOrder<Int>()
        else
            reverseOrder()

    var startIndex = 0
    var endIndex = this.count() - 1

    while (startIndex <= endIndex) {
        val midIndex = (startIndex + endIndex) / 2
        val midItem = this.elementAt(midIndex)

        val comparison = comp.compare(value, midItem)

        if (comparison < 0) {
            endIndex = midIndex - 1
        }
        else if (comparison > 0) {
            startIndex = midIndex + 1
        }
        else {
            return midIndex
        }
    }

    return -1
}

fun LongProgression.indexOf(value: Long): Long {
    val comp =
        if (this.first < this.last)
            inOrder<Long>()
        else
            reverseOrder()

    var startIndex = 0L
    var endIndex = this.count() - 1L

    while (startIndex <= endIndex) {
        val midIndex = (startIndex + endIndex) / 2
        val midItem = this.elementAt(midIndex)

        val comparison = comp.compare(value, midItem)

        if (comparison < 0) {
            endIndex = midIndex - 1L
        }
        else if (comparison > 0) {
            startIndex = midIndex + 1L
        }
        else {
            return midIndex
        }
    }

    return -1
}

operator fun IntProgression.contains(value: Int): Boolean =
    -1 != this.indexOf(value)

operator fun LongProgression.contains(value: Long): Boolean =
    -1L != this.indexOf(value)
