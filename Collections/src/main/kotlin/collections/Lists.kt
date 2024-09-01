package collections

import arrow.core.Option
import kotlin.math.min

val List<*>.isRandomAccess: Boolean
    get() = this is RandomAccess

fun <TElement> List<TElement>.tryGet(index: Int): Result<TElement> =
    runCatching { this[index] }

fun <TElement> MutableList<TElement>.trySet(index: Int, element: TElement): Result<TElement> =
    runCatching { this.set(index, element) }

fun <TElement> List<TElement>.safeGet(index: Int): Option<TElement> =
    this.tryGet(index).toOption()

fun <TElement> MutableList<TElement>.safeSet(index: Int, element: TElement): Option<TElement> =
    this.trySet(index, element).toOption()

fun <TElement> List<TElement>.wrapGet(index: Int): TElement {
    val actualIndex = index.mod(this.size)

    return this[actualIndex]
}

fun <TElement> MutableList<TElement>.wrapSet(index: Int, element: TElement): TElement {
    val actualIndex = index.mod(this.size)

    return this.set(actualIndex, element)
}

fun <TElement> MutableList<TElement>.swap(index1: Int, index2: Int) {
    if (index1 != index2) {
        this[index1] = this.set(index2, this[index1])
    }
}

fun <TElement> MutableList<TElement>.removeFromBack(amount: Int): Int =
    when (this) {
        is DequeList<TElement> -> this.removeFromBack(amount)
        is VectorList<TElement> -> this.removeFromBack(amount)
        else -> this.removeFromBackHelper(amount)
    }

private fun <TElement> MutableList<TElement>.removeFromBackHelper(amount: Int): Int {
    checkIfNegativeAmount(amount)

    if (amount >= this.size) {
        this.clear()
    }
    else {
        this.removeRange(this.size - amount, this.size)
    }

    return min(this.size, amount)
}

fun <TElement> MutableList<TElement>.removeRange(fromIndex: Int, toIndex: Int) =
    this.subList(fromIndex, toIndex).clear()

fun <TElement> List<TElement>.index(fromIndex: Int, element: @UnsafeVariance TElement): Int =
    this.index(fromIndex) { it == element }

fun <TElement> List<TElement>.index(fromIndex: Int, predicate: (TElement) -> Boolean): Int {
    checkIfIndexCanBeInsertedAt(fromIndex, this.size)

    if (this.isRandomAccess) {
        return this.searchWithIndexing(fromIndex, predicate)
    }

    val iter = this.listIterator(fromIndex)

    while (iter.hasNext()) {
        val item = iter.next()

        if (predicate(item)) {
            return iter.previousIndex()
        }
    }

    return -1
}

private fun <TElement> List<TElement>.searchWithIndexing(fromIndex: Int, predicate: (TElement) -> Boolean): Int {
    for (index in fromIndex until this.size) {
        val item = this[index]

        if (predicate(item)) {
            return index
        }
    }

    return -1
}

fun <TElement> List<TElement>.lastIndex(fromIndex: Int, element: @UnsafeVariance TElement): Int =
    this.lastIndex(fromIndex) { it == element }

fun <TElement> List<TElement>.lastIndex(fromIndex: Int, predicate: (TElement) -> Boolean): Int {
    checkIfIndexCanBeInsertedAt(fromIndex, this.size)

    if (this.isRandomAccess) {
        return this.searchBackwardWithIndexing(fromIndex, predicate)
    }

    val iter = this.listIterator(fromIndex)

    while (iter.hasPrevious()) {
        val item = iter.previous()

        if (predicate(item)) {
            return iter.nextIndex()
        }
    }

    return -1
}

private fun <TElement> List<TElement>.searchBackwardWithIndexing(fromIndex: Int, predicate: (TElement) -> Boolean): Int {
    for (index in (fromIndex - 1) downTo 0) {
        val item = this[index]

        if (predicate(item)) {
            return index
        }
    }

    return -1
}

fun <TElement> List<TElement>.isPermutationOf(other: List<TElement>): Boolean {
    if (this === other) {
        return true
    }

    if (this.size != other.size) {
        return false
    }

    val map = HashMap<TElement, Int>(this.size)

    for (item in this) {
        map.compute(item) { _, value -> 1 + (value ?: 0) }
    }

    for (item in other) {
        val newCount = map.compute(item) { _, value -> (value ?: 0) - 1 }

        if (null === newCount || newCount < 0) {
            return false
        }
    }

    return map.filter{ 0 == it.value }.isEmpty()
}

fun <TElement> MutableList<TElement>.next(comp: Comparator<TElement>? = null): Boolean =
    this.next(comp.function)

fun <TElement> MutableList<TElement>.next(comp: (TElement, TElement) -> Int): Boolean {
    var index1 = this.lastIndex - 1

    while (index1 >= 0) {
        if (comp(this[index1], this[index1 + 1]) < 0) {
            break
        }

        --index1
    }

    if (index1 < 0) {
        this.reverse()

        return false
    }

    var index2 = this.lastIndex

    while (index2 > index1) {
        if (comp(this[index2], this[index1]) > 0) {
            break
        }

        --index2
    }

    this.swap(index1, index2)
    this.subList(index1 + 1, this.size).reverse()

    return true
}

fun <TElement> MutableList<TElement>.prev(comp: Comparator<TElement>? = null): Boolean =
    this.prev(comp.function)

fun <TElement> MutableList<TElement>.prev(comp: (TElement, TElement) -> Int): Boolean =
    this.next(comp.reversed)

fun <TElement> compare(leftList: List<TElement>, rightList: List<TElement>, comp: Comparator<TElement>? = null): Int =
    compare(leftList, rightList, comp.function)

fun <TElement> compare(leftList: List<TElement>, rightList: List<TElement>, comp: (TElement, TElement) -> Int): Int {
    if (leftList === rightList) {
        return 0
    }

    val left = leftList.iterator()
    val right = rightList.iterator()

    while (left.hasNext() && right.hasNext()) {
        val leftElem = left.next()
        val rightElem = right.next()

        val comparison = comp(leftElem, rightElem)

        if (0 != comparison) {
            return comparison
        }
    }

    return leftList.size - rightList.size
}

fun <TElement> List<TElement>.isSorted(comp: Comparator<TElement>? = null): Boolean =
    this.isSorted(comp.function)

fun <TElement> List<TElement>.isSorted(comp: (TElement, TElement) -> Int): Boolean =
    this.size == this.isSortedUntil(comp)

fun <TElement> List<TElement>.isSortedUntil(comp: Comparator<TElement>? = null): Int =
    this.isSortedUntil(comp.function)

fun <TElement> List<TElement>.isSortedUntil(comp: (TElement, TElement) -> Int): Int {
    if (this.size <= 1) {
        return this.size
    }

    val iter = this.listIterator()
    var current = iter.next()

    while (iter.hasNext()) {
        val next = iter.next()

        if (!iter.hasNext()) {
            break
        }

        if (comp(current, next) > 0) {
            return iter.previousIndex()
        }

        current = next
    }

    return this.size
}
