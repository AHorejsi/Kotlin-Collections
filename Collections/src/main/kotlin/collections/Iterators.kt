package collections

fun <TElement> ListIterator<TElement>.tryPrevious(): Result<TElement> = runCatching { this.previous() }

fun <TElement> Iterator<TElement>.tryNext(): Result<TElement> = runCatching { this.next() }

fun <TElement> MutableListIterator<TElement>.trySet(element: TElement): Result<Unit> = runCatching { this.set(element) }

fun MutableIterator<*>.tryRemove(): Result<Unit> = runCatching { this.remove() }

fun <TElement> MutableListIterator<TElement>.tryAdd(element: TElement): Result<Unit> = runCatching { this.add(element) }

fun <TElement> MutableIterator<TElement>.removeFirst(element: @UnsafeVariance TElement): Boolean {
    val func = { other: TElement -> other == element }

    return this.removeFirst(func)
}

inline fun <TElement> MutableIterator<TElement>.removeFirst(predicate: (TElement) -> Boolean): Boolean =
    1 == this.removeAmountFirst(1, predicate)

fun <TElement> MutableIterator<TElement>.removeAmountFirst(amount: Int, element: @UnsafeVariance TElement): Int {
    val func = { other: TElement -> other == element }

    return this.removeAmountFirst(amount, func)
}

inline fun <TElement> MutableIterator<TElement>.removeAmountFirst(amount: Int, predicate: (TElement) -> Boolean): Int {
    require(amount >= 0) { "Amount must be non-negative. Specified Amount: $amount" }

    var amountRemoved = 0

    while (amountRemoved < amount && this.hasNext()) {
        val item = this.next()

        if (predicate(item)) {
            ++amountRemoved

            this.remove()
        }
    }

    return amountRemoved
}

fun <TElement> MutableListIterator<TElement>.removeLast(element: @UnsafeVariance TElement): Boolean {
    val func = { other: TElement -> other == element }

    return this.removeLast(func)
}

inline fun <TElement> MutableListIterator<TElement>.removeLast(predicate: (TElement) -> Boolean): Boolean =
    1 == this.removeAmountLast(1, predicate)

fun <TElement> MutableListIterator<TElement>.removeAmountLast(amount: Int, element: @UnsafeVariance TElement): Int {
    val func = { other: TElement -> other == element }

    return this.removeAmountLast(amount, func)
}

inline fun <TElement> MutableListIterator<TElement>.removeAmountLast(amount: Int, predicate: (TElement) -> Boolean): Int {
    require(amount >= 0) { "Amount must be non-negative. Specified Amount: $amount" }

    var amountRemoved = 0

    while (amountRemoved < amount && this.hasPrevious()) {
        val item = this.previous()

        if (predicate(item)) {
            ++amountRemoved

            this.remove()
        }
    }

    return amountRemoved
}