package collections

fun <TElement> Iterable<TElement>.withIndex(startIndex: Int): Iterable<IndexedValue<TElement>> =
    this.asSequence().withIndex(startIndex).asIterable()

fun Iterable<*>.hasAtLeast(size: Int): Boolean {
    val found =
        if (this is Collection<*>)
            this.size
        else
            this.countUpTo(size)

    return found >= size
}

fun Iterable<*>.hasAtMost(size: Int): Boolean {
    val found =
        if (this is Collection<*>)
            this.size
        else
            this.countUpTo(size)

    return found <= size
}

private fun Iterable<*>.countUpTo(size: Int): Int {
    checkIfNegativeAmount(size)

    val iter = this.iterator()
    var found = 0

    while (found < size && iter.hasNext()) {
        iter.next()
        ++found
    }

    return found
}

fun <TElement> MutableIterable<TElement>.removeFirstOf(element: @UnsafeVariance TElement): Boolean =
    when (this) {
        is MutableSet<TElement> -> this.remove(element)
        else -> this.removeFirstOf{ it == element }
    }

fun <TElement> MutableIterable<TElement>.removeFirstOf(predicate: (TElement) -> Boolean): Boolean =
    1 == this.removeAmount(1, predicate)

fun <TElement> MutableIterable<TElement>.removeAllOf(element: @UnsafeVariance TElement): Int =
    this.removeAllOf{ it == element }

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

fun <TElement> Iterable<TElement>.find(element: @UnsafeVariance TElement): Sequence<TElement> =
    this.asSequence().find(element)

fun <TElement> Iterable<TElement>.find(predicate: (TElement) -> Boolean): Sequence<TElement> =
    this.asSequence().find(predicate)

fun <TElement> Iterable<TElement>.tryFirst(): Result<TElement> =
    runCatching { this.first() }

fun <TElement> Iterable<TElement>.tryFirst(predicate: (TElement) -> Boolean): Result<TElement> =
    runCatching { this.first(predicate) }

fun <TElement> Iterable<TElement>.tryLast(): Result<TElement> =
    runCatching { this.last() }

fun <TElement> Iterable<TElement>.tryLast(predicate: (TElement) -> Boolean): Result<TElement> =
    runCatching { this.last(predicate) }

fun <TElement> Iterable<TElement>.asString(): String {
    if (!this.any()) {
        return "[]"
    }

    val sb = StringBuilder()
    val iter = this.iterator()
    var item: TElement

    sb.append("[")

    while (true) {
        item = iter.next()

        if (!iter.hasNext()) {
            break
        }

        sb.append(item)
        sb.append(", ")
    }

    sb.append(item)
    sb.append("]")

    return sb.toString()
}
