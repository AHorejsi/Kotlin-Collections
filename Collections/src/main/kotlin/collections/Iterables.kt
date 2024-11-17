package collections

fun <TElement> Iterable<TElement>.withIndex(startIndex: Int): Iterable<IndexedValue<TElement>> =
    this.asSequence().withIndex(startIndex).asIterable()

fun Iterable<*>.atLeast(size: Int): Boolean {
    val found =
        if (this is Collection<*>)
            this.size
        else
            this.countUpTo(size)

    return found >= size
}

fun Iterable<*>.atMost(size: Int): Boolean {
    val found =
        if (this is Collection<*>)
            this.size
        else
            this.countUpTo(size)

    return found <= size
}

fun Iterable<*>.exactly(size: Int): Boolean {
    val found =
        if (this is Collection<*>)
            this.size
        else
            this.countUpTo(size)

    return found == size
}

private fun Iterable<*>.countUpTo(size: Int): Int {
    checkIfNegativeAmount(size)

    val iter = this.iterator()
    var amount = 0

    while (amount < size && iter.hasNext()) {
        iter.next()

        ++amount
    }

    return amount
}

fun <TElement> MutableIterable<TElement>.removeAllOf(element: @UnsafeVariance TElement): Int =
    when (this) {
        is MutableSet<TElement> -> if (this.remove(element)) 1 else 0
        else -> this.removeAllOf{ it == element }
    }

fun <TElement> MutableIterable<TElement>.removeAllOf(predicate: (TElement) -> Boolean): Int {
    var amountRemoved = 0
    val iter = this.iterator()

    while (iter.hasNext()) {
        val item = iter.next()

        if (predicate(item)) {
            iter.remove()
            ++amountRemoved
        }
    }

    return amountRemoved
}

fun <TElement> MutableIterable<TElement>.removeAmount(amount: Int, element: @UnsafeVariance TElement): Int =
    when (this) {
        is MutableSet<TElement> -> this.removeAmount(amount, element)
        else -> this.removeAmount(amount) { it == element }
    }

fun <TElement> MutableIterable<TElement>.removeAmount(amount: Int, predicate: (TElement) -> Boolean): Int {
    checkIfNegativeAmount(amount)

    if (0 == amount) {
        return 0
    }

    var amountRemoved = 0
    val iter = this.iterator()

    while (amountRemoved < amount && iter.hasNext()) {
        val item = iter.next()

        if (predicate(item)) {
            iter.remove()

            ++amountRemoved
        }
    }

    return amountRemoved
}

fun <TElement> Iterable<TElement>.tryFirst(): Result<TElement> =
    runCatching{ this.first() }

fun <TElement> Iterable<TElement>.tryFirst(predicate: (TElement) -> Boolean): Result<TElement> =
    runCatching{ this.first(predicate) }

fun <TElement> Iterable<TElement>.tryLast(): Result<TElement> =
    runCatching{ this.last() }

fun <TElement> Iterable<TElement>.tryLast(predicate: (TElement) -> Boolean): Result<TElement> =
    runCatching{ this.last(predicate) }

fun <TElement> Iterable<TElement>.sortedWith(comp: (TElement, TElement) -> Int): List<TElement> {
    val comparator = Comparator(comp)

    return this.sortedWith(comparator)
}

fun <TElement> Iterable<TElement>.splitAt(index: Int): Pair<List<TElement>, List<TElement>> {
    val iter = this.iterator()
    val left = mutableListOf<TElement>()
    val right = mutableListOf<TElement>()

    var count = 0

    while (count < index && iter.hasNext()) {
        val item = iter.next()

        left.add(item)

        ++count
    }

    while (iter.hasNext()) {
        val item = iter.next()

        right.add(item)
    }

    return Pair(left, right)
}
