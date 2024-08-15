package collections

import kotlin.math.abs

fun IntProgression.count(): Int {
    if (this.isEmpty()) {
        return 0
    }

    val gap = abs(this.first - this.last) + 1
    val step = abs(this.step)

    val quot = gap.div(step)
    val rem = gap.rem(step)

    return quot + rem
}

fun LongProgression.count(): Long {
    if (this.isEmpty()) {
        return 0
    }

    val gap = abs(this.first - this.last) + 1
    val step = abs(this.step)

    val quot = gap.div(step)
    val rem = gap.rem(step)

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

    if (0 == amount) {
        return IntRange.EMPTY
    }

    var end = checkForOverflowOnSubtraction(this, amount)
    end = checkForOverflowOnAddition(end, 1)

    return this downTo end
}

infix fun Long.down(amount: Long): LongProgression {
    checkIfNegativeAmount(amount)

    if (0L == amount) {
        return LongRange.EMPTY
    }

    var end = checkForOverflowOnSubtraction(this, amount)
    end = checkForOverflowOnAddition(end, 1L)

    return this downTo end
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
