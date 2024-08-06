package collections

import arrow.core.Option

val List<*>.isRandomAccess: Boolean
    get() = this is RandomAccess

fun <TElement> List<TElement>.safeGet(index: Int): Option<TElement> = this.tryGet(index).toOption()

fun <TElement> MutableList<TElement>.safeSet(index: Int, element: TElement): Option<TElement> =
    this.trySet(index, element).toOption()

fun <TElement> List<TElement>.tryGet(index: Int): Result<TElement> = runCatching { this[index] }

fun <TElement> MutableList<TElement>.trySet(index: Int, element: TElement): Result<TElement> =
    runCatching { this.set(index, element) }

fun <TElement> List<TElement>.wrapGet(index: Int): TElement {
    val actualIndex = index.mod(this.size)

    return this[actualIndex]
}

fun <TElement> MutableList<TElement>.wrapSet(index: Int, element: TElement): TElement {
    val actualIndex = index.mod(this.size)

    return this.set(actualIndex, element)
}

fun <TElement> MutableList<TElement>.removeFromBack(amount: Int): Int {
    require(amount >= 0) { "Amount of elements to remove must be non-negative. Amount: $amount" }

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
                this.removeRange(this.size - amount, this.size)

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

fun <TElement> MutableList<TElement>.removeRange(fromIndex: Int, toIndex: Int) =
    this.subList(fromIndex, toIndex).clear()

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

fun <TElement> List<TElement>.indices(element: @UnsafeVariance TElement): Sequence<Int> = this.indices(0, element)

fun <TElement> List<TElement>.indices(predicate: (TElement) -> Boolean): Sequence<Int> = this.indices(0, predicate)

fun <TElement> List<TElement>.indices(fromIndex: Int, element: @UnsafeVariance TElement): Sequence<Int> =
    this.indices(fromIndex) { it == element }

fun <TElement> List<TElement>.indices(fromIndex: Int, predicate: (TElement) -> Boolean): Sequence<Int> =
    sequence {
        val iter = this@indices.listIterator(fromIndex)

        while (iter.hasNext()) {
            val item = iter.next()

            if (predicate(item)) {
                yield(iter.previousIndex())
            }
        }
    }

fun <TElement> compare(leftList: List<TElement>, rightList: List<TElement>, comp: (TElement, TElement) -> Int): Int {
    val leftIter = leftList.iterator()
    val rightIter = rightList.iterator()

    while (leftIter.hasNext() && rightIter.hasNext()) {
        val leftElem = leftIter.next()
        val rightElem = rightIter.next()

        val comparison = comp(leftElem, rightElem)

        if (0 != comparison) {
            return comparison
        }
    }

    return leftList.size - rightList.size
}
