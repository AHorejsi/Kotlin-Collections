package collections

fun <TElement> Iterable<TElement>.withIndex(startIndex: Int): Iterable<IndexedValue<TElement>> =
    this.asSequence().withIndex(startIndex).asIterable()

fun <TElement> Iterable<*>.hasAtLeast(size: Int): Boolean =
    this.asSequence().hasAtLeast(size)

fun <TElement> MutableIterable<TElement>.removeFirstOf(element: @UnsafeVariance TElement): Boolean =
    when (this) {
        is MutableSet<TElement> -> this.remove(element)
        is MutableMultiset<TElement> -> 1 == this.remove(element, 1).first
        else -> this.removeFirstOf{ it == element }
    }

inline fun <TElement> MutableIterable<TElement>.removeFirstOf(predicate: (TElement) -> Boolean): Boolean =
    1 == this.removeAmount(1, predicate)

fun <TElement> MutableIterable<TElement>.removeAmount(amount: Int, element: @UnsafeVariance TElement): Int =
    when (this) {
        is MutableSet<TElement> -> this.removeAmount(amount, element)
        is MutableMultiset<TElement> -> this.remove(element, amount).first
        else -> this.removeAmount(amount) { it == element }
    }

inline fun <TElement> MutableIterable<TElement>.removeAmount(amount: Int, predicate: (TElement) -> Boolean): Int {
    require(amount >= 0)

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

fun <TElement> MutableIterable<TElement>.removeAllOf(element: @UnsafeVariance TElement): Int =
    this.removeAmount(this.count(), element)

inline fun <TElement> MutableIterable<TElement>.removeAllOf(predicate: (TElement) -> Boolean): Int =
    this.removeAmount(this.count(), predicate)

fun <TElement> Iterable<TElement>.count(element: @UnsafeVariance TElement): Int =
    when (this) {
        is Set<TElement> -> this.count(element)
        is Multiset<TElement> -> this.multiplicity(element)
        else -> {
            var amountFound = 0

            for (item in this) {
                if (item == element) {
                    ++amountFound
                }
            }

            amountFound
        }
    }

fun <TElement> Iterable<TElement>.tryFirst(): Result<TElement> = runCatching { this.first() }

inline fun <TElement> Iterable<TElement>.tryFirst(predicate: (TElement) -> Boolean): Result<TElement> =
    runCatching { this.first(predicate) }

fun <TElement> Iterable<TElement>.tryLast(): Result<TElement> = runCatching { this.last() }

inline fun <TElement> Iterable<TElement>.tryLast(predicate: (TElement) -> Boolean): Result<TElement> =
    runCatching { this.last(predicate) }
