package collections

fun <TElement> MutableCollection<TElement>.insert(elements: Collection<TElement>): Int {
    val oldSize = this.size

    elements.forEach(this::add)

    return this.size - oldSize
}

fun <TElement> MutableCollection<TElement>.delete(elements: Collection<TElement>): Int {
    val oldSize = this.size

    elements.forEach(this::remove)

    return oldSize - this.size
}

fun <TElement> MutableCollection<TElement>.keep(elements: Collection<TElement>): Int {
    val oldSize = this.size
    val iter = this.iterator()

    while (iter.hasNext()) {
        val item = iter.next()

        if (item !in elements) {
            iter.remove()
        }
    }

    return oldSize - this.size
}

fun <TElement> Collection<TElement>.find(amount: Int, predicate: (TElement) -> Boolean): Sequence<TElement> = sequence {
    require(amount >= 0) { "Amount to find must be non-negative. Amount: $amount" }

    if (0 == amount) {
        return@sequence
    }

    var amountFound = 0
    val iter = this@find.iterator()

    while (iter.hasNext()) {
        val item = iter.next()

        if (predicate(item)) {
            yield(item)

            ++amountFound

            if (amountFound == amount) {
                break
            }
        }
    }
}
