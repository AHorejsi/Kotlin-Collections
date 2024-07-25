package collections

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import kotlin.math.min

val List<*>.isRandomAccess: Boolean
    get() = this is RandomAccess

fun <TElement> List<TElement>.safeGet(index: Int): Option<TElement> =
    if (index < 0 || index >= this.size)
        None
    else
        Some(this[index])

fun <TElement> MutableList<TElement>.safeSet(index: Int, element: TElement): Option<TElement> =
    if (index < 0 || index >= this.size)
        None
    else
        Some(this.set(index, element))

fun <TElement> List<TElement>.tryGet(index: Int): Result<TElement> = runCatching { this[index] }

fun <TElement> MutableList<TElement>.trySet(index: Int, element: TElement): Result<TElement> =
    runCatching { this.set(index, element) }

fun <TElement> List<TElement>.wrapGet(index: Int): TElement = this[index.mod(this.size)]

fun <TElement> MutableList<TElement>.wrapSet(index: Int, element: TElement): TElement {
    val actualIndex = index.mod(this.size)

    return this.set(actualIndex, element)
}

fun <TElement> MutableList<TElement>.removeFromBack(amount: Int): Int {
    require(amount >= 0) { "Amount of elements to remove must be positive. Amount: $amount" }

    if (0 == amount) {
        return 0
    }

    return when (this) {
        is DequeList<TElement> -> this.removeFromBack(amount)
        is VectorList<TElement> -> this.removeFromBack(amount)
        else -> {
            if (amount >= this.size) {
                return clearElements(this)
            }
            else {
                repeat(amount) {
                    this.removeLast()
                }

                return amount
            }
        }
    }
}

private fun <TElement> clearElements(list: MutableList<TElement>): Int {
    val amountRemoved = list.size

    list.clear()

    return amountRemoved
}

fun <TElement> List<TElement>.index(fromIndex: Int, element: @UnsafeVariance TElement): Int =
    this.index(fromIndex) { it == element }

fun <TElement> List<TElement>.index(fromIndex: Int, predicate: (TElement) -> Boolean): Int {
    val iter = this.listIterator(fromIndex)

    while (iter.hasNext()) {
        val item = iter.next()

        if (predicate(item)) {
            return iter.previousIndex()
        }
    }

    return -1
}

fun <TElement> List<TElement>.lastIndex(fromIndex: Int, element: @UnsafeVariance TElement): Int =
    this.lastIndex(fromIndex) { it == element }

fun <TElement> List<TElement>.lastIndex(fromIndex: Int, predicate: (TElement) -> Boolean): Int {
    val iter = this.listIterator(fromIndex + 1)

    while (iter.hasPrevious()) {
        val item = iter.previous()

        if (predicate(item)) {
            return iter.nextIndex()
        }
    }

    return -1
}

fun <TElement> compare(leftList: List<TElement>, rightList: List<TElement>, comp: (TElement, TElement) -> Int): Int {
    val leftSize = leftList.size
    val rightSize = rightList.size
    val smallerSize = min(leftSize, rightSize)

    for (index in 0 until smallerSize) {
        val comparison = comp(leftList[index], rightList[index])

        if (0 != comparison) {
            return comparison
        }
    }

    return leftSize.compareTo(rightSize)
}
