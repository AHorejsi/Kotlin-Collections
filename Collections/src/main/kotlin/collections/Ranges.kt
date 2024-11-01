package collections

import kotlin.math.abs

fun IntProgression.count(): Int {
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
        this.up(amount)
    else
        this.down(-amount)

infix fun Long.move(amount: Long): LongProgression =
    if (amount >= 0)
        this.up(amount)
    else
        this.down(-amount)
