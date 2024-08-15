package collections

import arrow.core.Option

val List<*>.isRandomAccess: Boolean
    get() = this is RandomAccess

fun <TElement> List<TElement>.safeGet(index: Int): Option<TElement> =
    this.tryGet(index).toOption()

fun <TElement> MutableList<TElement>.safeSet(index: Int, element: TElement): Option<TElement> =
    this.trySet(index, element).toOption()

fun <TElement> List<TElement>.tryGet(index: Int): Result<TElement> =
    runCatching { this[index] }

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

fun <TElement> MutableList<TElement>.swap(index1: Int, index2: Int) {
    if (index1 != index2) {
        val temp = this[index1]
        this[index1] = this[index2]
        this[index2] = temp
    }
}

fun <TElement> MutableList<TElement>.removeFromBack(amount: Int): Int {
    return when (this) {
        is DequeList<TElement> -> this.removeFromBack(amount)
        is VectorList<TElement> -> this.removeFromBack(amount)
        else -> {
            checkIfNegativeAmount(amount)

            if (amount >= this.size) {
                return clearElements(this)
            }

            this.removeRange(this.size - amount, this.size)

            return amount
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

fun <TElement> List<TElement>.indices(element: @UnsafeVariance TElement): Sequence<Int> =
    this.indices(0, element)

fun <TElement> List<TElement>.indices(predicate: (TElement) -> Boolean): Sequence<Int> =
    this.indices(0, predicate)

fun <TElement> List<TElement>.indices(fromIndex: Int, element: @UnsafeVariance TElement): Sequence<Int> =
    this.indices(fromIndex) { it == element }

fun <TElement> List<TElement>.indices(fromIndex: Int, predicate: (TElement) -> Boolean): Sequence<Int> =
    if (this.isRandomAccess)
        this.searchIndicesWithIndexing(fromIndex, predicate)
    else
        this.searchIndicesWithIterator(fromIndex, predicate)

private fun <TElement> List<TElement>.searchIndicesWithIndexing(
    fromIndex: Int,
    predicate: (TElement) -> Boolean
): Sequence<Int> = sequence {
    for (index in fromIndex until this@searchIndicesWithIndexing.size) {
        val item = this@searchIndicesWithIndexing[index]

        if (predicate(item)) {
            yield(index)
        }
    }
}

private fun <TElement> List<TElement>.searchIndicesWithIterator(
    fromIndex: Int,
    predicate: (TElement) -> Boolean
): Sequence<Int> = sequence {
    val iter = this@searchIndicesWithIterator.listIterator(fromIndex)

    while (iter.hasNext()) {
        val item = iter.next()

        if (predicate(item)) {
            yield(iter.previousIndex())
        }
    }
}

fun <TElement> List<TElement>.isPermutationOf(other: List<TElement>): Boolean {
    if (this.size != other.size) {
        return false
    }

    val set = asMutableSet<TElement>(HashMap(this.size))

    set.addAll(this)

    @Suppress("ConvertArgumentToSet")
    set.removeAll(other)

    return set.isEmpty()
}

/*fun <TElement> MutableList<TElement>.next(comp: Comparator<TElement>? = null): Boolean =
    this.next(compare.function)

fun <TElement> MutableList<TElement>.next(comp: (TElement, TElement) -> Int): Boolean {

}

fun <TElement> MutableList<TElement>.next(comp: Comparator<TElement>? = null): Boolean =
    this.prev(compare.function)

fun <TElement> MutableList<TElement>.prev(comp: (TElement, TElement) -> Int): Boolean {

}*/

fun <TElement> compare(leftList: List<TElement>, rightList: List<TElement>, comp: Comparator<TElement>? = null): Int =
    compare(leftList, rightList, comp.function)

fun <TElement> compare(leftList: List<TElement>, rightList: List<TElement>, comp: (TElement, TElement) -> Int): Int {
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
    for (index in 0 until this.lastIndex) {
        val current = this[index]
        val next = this[index + 1]

        if (comp(current, next) > 0) {
            return index + 1
        }
    }

    return this.size
}

/*fun <TElement> MutableList<TElement>.asSorted(comp: (TElement, TElement) -> Int): List<TElement> {

}*/
