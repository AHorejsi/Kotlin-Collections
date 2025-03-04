package collections

fun <TElement> Sequence<TElement>.withIndex(startIndex: Int): Sequence<IndexedValue<TElement>> {
    var currentIndex = startIndex
    val iter = this.iterator()

    return this.map{ IndexedValue(currentIndex++, iter.next()) }
}

fun Sequence<*>.atLeast(size: Int): Boolean =
    this.count() >= size

fun Sequence<*>.atMost(size: Int): Boolean =
    this.count() <= size

fun Sequence<*>.exactly(size: Int): Boolean =
    this.count() == size

fun <TElement> Sequence<TElement>.prepend(item: TElement): Sequence<TElement> = sequence {
    yield(item)
    yieldAll(this@prepend)
}

fun <TElement> Sequence<TElement>.append(item: TElement): Sequence<TElement> = sequence {
    yieldAll(this@append)
    yield(item)
}

fun <TElement> Sequence<TElement>.popFirst(): Sequence<TElement> = sequence {
    val iter = this@popFirst.iterator()

    iter.next()

    yieldAll(iter)
}

fun <TElement> Sequence<TElement>.popLast(): Sequence<TElement> = sequence {
    val iter = this@popLast.iterator()

    while (iter.hasNext()) {
        val item = iter.next()

        if (iter.hasNext()) {
            yield(item)
        }
    }
}

fun <TElement> Sequence<TElement>.find(element: @UnsafeVariance TElement): Sequence<TElement> =
    this.find{ it == element }

fun <TElement> Sequence<TElement>.find(predicate: (TElement) -> Boolean): Sequence<TElement> = sequence {
    for (item in this@find) {
        if (predicate(item)) {
            yield(item)
        }
    }
}

fun <TElement> Sequence<TElement>.reversed(): Sequence<TElement> {
    val iter = this.iterator()

    return reversedHelper(iter)
}

private fun <TElement> reversedHelper(iter: Iterator<TElement>): Sequence<TElement> = sequence {
    if (!iter.hasNext()) {
        return@sequence
    }

    val item = iter.next()
    val prev = reversedHelper(iter)

    yieldAll(prev)
    yield(item)
}

fun <TElement> Sequence<TElement>.splitAt(index: Int): Pair<Sequence<TElement>, Sequence<TElement>> {
    val left = this.take(index)
    val right = this.drop(index)

    return Pair(left, right)
}

fun <TElement> TElement.replicate(amount: Int): Sequence<TElement> = sequence {
    repeat(amount) {
        yield(this@replicate)
    }
}

fun <TElement> TElement.cycle(): Sequence<TElement> =
    sequenceOf(this).cycle()

fun <TElement> Sequence<TElement>.cycle(): Sequence<TElement> = sequence {
    while (true) {
        yieldAll(this@cycle)
    }
}

fun <TElement> Sequence<TElement>.cycle(amount: Int): Sequence<TElement> = sequence {
    repeat(amount) {
        yieldAll(this@cycle)
    }
}
