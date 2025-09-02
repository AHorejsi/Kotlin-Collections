package collections

val List<*>.isRandomAccess: Boolean
    get() = this is RandomAccess

fun <TElement> List<TElement>.tryGet(index: Int): Result<TElement> =
    runCatching{ this[index] }

fun <TElement> MutableList<TElement>.trySet(index: Int, element: TElement): Result<TElement> =
    runCatching{ this.set(index, element) }

fun <TElement> List<TElement>.wrapGet(index: Int): TElement {
    checkIfEmptyListForWrappedIndexing(this)

    val actualIndex = index.mod(this.size)

    return this[actualIndex]
}

fun <TElement> MutableList<TElement>.wrapSet(index: Int, element: TElement): TElement {
    checkIfEmptyListForWrappedIndexing(this)

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

fun <TElement> MutableList<TElement>.resize(newSize: Int, supply: () -> TElement): Int {
    checkIfNegativeAmount(newSize)

    val oldSize = this.size

    while (this.size < newSize) {
        this.add(supply())
    }

    while (this.size > newSize) {
        this.removeLast()
    }

    return this.size - oldSize
}

fun <TElement> MutableList<TElement>.removeFromBack(amount: Int): Int =
    when (this) {
        is DequeList<TElement> -> this.removeFromBack(amount)
        is VectorList<TElement> -> this.removeFromBack(amount)
        is RandomAccessSublist<TElement> -> this.removeFromBack(amount)
        else -> this.removeFromBackHelper(amount)
    }

private fun <TElement> MutableList<TElement>.removeFromBackHelper(amount: Int): Int {
    checkIfNegativeAmount(amount)

    val sizeBeforeRemoval = this.size

    if (amount >= this.size) {
        this.clear()
    }
    else {
        repeat(amount) {
            this.removeLast()
        }
    }

    return sizeBeforeRemoval - this.size
}

fun <TElement> MutableList<TElement>.removeRange(fromIndex: Int, toIndex: Int) =
    this.subList(fromIndex, toIndex).clear()

fun <TElement> List<TElement>.index(fromIndex: Int, element: @UnsafeVariance TElement): Int =
    this.index(fromIndex) { it == element }

fun <TElement> List<TElement>.index(fromIndex: Int, predicate: (TElement) -> Boolean): Int {
    checkIfIndexCanBeInsertedAt(fromIndex, this.size)

    if (this.isRandomAccess) {
        return searchWithIndexing(this, fromIndex, predicate)
    }

    val iter = this.listIterator(fromIndex)

    while (iter.hasNext()) {
        val elem = iter.next()

        if (predicate(elem)) {
            return iter.previousIndex()
        }
    }

    return -1
}

private fun <TElement> searchWithIndexing(list: List<TElement>, fromIndex: Int, predicate: (TElement) -> Boolean): Int {
    for (index in fromIndex until list.size) {
        val element = list[index]

        if (predicate(element)) {
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
        return searchBackwardWithIndexing(this, fromIndex, predicate)
    }

    val iter = this.listIterator(fromIndex)

    while (iter.hasPrevious()) {
        val elem = iter.previous()

        if (predicate(elem)) {
            return iter.nextIndex()
        }
    }

    return -1
}

private fun <TElement> searchBackwardWithIndexing(
    list: List<TElement>,
    fromIndex: Int,
    predicate: (TElement) -> Boolean
): Int {
    for (index in (fromIndex - 1) downTo 0) {
        val element = list[index]

        if (predicate(element)) {
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

    val counter = HashMap<TElement, Int>(this.size)

    for (item in this) {
        counter.compute(item) { _, value -> 1 + (value ?: 0) }
    }

    for (item in other) {
        val newCount = counter.compute(item) { _, value -> (value ?: 0) - 1 }

        if (null === newCount || newCount < 0) {
            return false
        }
    }

    return counter.all{ 0 == it.value }
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

fun <TElement> List<TElement>.separationPoint(predicate: (TElement) -> Boolean): Int? {
    var index = 0

    while (index < this.size) {
        val item = this[index]

        if (!predicate(item)) {
            break
        }

        ++index
    }

    val separationIndex = index

    while (index < this.size) {
        val item = this[index]

        if (predicate(item)) {
            return null
        }

        ++index
    }

    return separationIndex
}

fun <TElement> MutableList<TElement>.separate(predicate: (TElement) -> Boolean): Int {
    var partitionPoint = 0

    for (index in this.indices) {
        val item = this[index]

        if (predicate(item)) {
            this.swap(index, partitionPoint)
            ++partitionPoint
        }
    }

    return partitionPoint
}

fun <TElement> MutableList<TElement>.stableSeparate(predicate: (TElement) -> Boolean): Int {
    val (yes, no) = this.partition(predicate)
    var index = 0

    for (item in yes) {
        this[index] = item
        ++index
    }

    for (item in no) {
        this[index] = item
        ++index
    }

    return yes.size
}

fun <TElement> MutableList<TElement>.rotate(amount: Int) {
    if (this.size <= 1) {
        return
    }

    val actualAmount = amount % this.size

    if (0 != actualAmount) {
        val rotationCount =
            if (actualAmount < 0)
                this.size + actualAmount
            else
                actualAmount

        rotateRight(this, rotationCount)
    }
}

private fun <TElement> rotateRight(list: MutableList<TElement>, amount: Int) {
    val end = list.size - amount

    val left = VectorQueue<TElement>(end)
    val right = VectorQueue<TElement>(amount)

    for (index in 0 until end) {
        left.enqueue(list[index])
    }

    for (index in end until list.size) {
        right.enqueue(list[index])
    }

    doRotation(list, left, right)
}

private fun <TElement> doRotation(list: MutableList<TElement>, left: Queue<TElement>, right: Queue<TElement>) {
    var index = 0

    while (!right.isEmpty()) {
        list[index] = right.dequeue()
        ++index
    }

    while (!left.isEmpty()) {
        list[index] = left.dequeue()
        ++index
    }
}

fun <TElement> MutableList<TElement>.intersperse(separator: TElement) {
    if (this.size <= 1) {
        return
    }

    val iter = this.listIterator()

    while (iter.hasNext()) {
        iter.next()

        if (!iter.hasNext()) {
            break
        }

        iter.add(separator)
    }
}

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

    val iter = this.iterator()

    var current = iter.next()
    var index = 0

    while (iter.hasNext()) {
        val elem = iter.next()

        if (!iter.hasNext()) {
            break
        }

        if (comp(current, elem) > 0) {
            return index + 1
        }

        current = elem
        ++index
    }

    return this.size
}
