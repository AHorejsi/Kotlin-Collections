package me.alexh.collects

inline fun <TElement> MutableIterable<TElement>.removeIf(predicate: Predicate<in TElement>): Boolean {
    val iter = this.iterator()
    var change = false

    while (iter.hasNext()) {
        val elem = iter.next()

        if (predicate(elem)) {
            iter.remove()

            change = true
        }
    }

    return change
}

fun <TElement> MutableIterable<TElement>.removeAmount(amount: Int, element: TElement): Int =
    if (this is MutableSet<TElement>)
        if (this.remove(element)) (1) else (0)
    else
        this.removeAmountIf(amount) { it == element }

inline fun <TElement> MutableIterable<TElement>.removeAmountIf(amount: Int, predicate: Predicate<in TElement>): Int {
    val iter = this.iterator()
    var amountRemoved = 0

    while (amountRemoved < amount && iter.hasNext()) {
        val item = iter.next()

        if (predicate(item)) {
            iter.remove()

            ++amountRemoved

            if (amountRemoved == amount) {
                break
            }
        }
    }

    return amountRemoved
}

fun <TElement> MutableIterable<TElement>.removeAll(element: TElement): Boolean =
    if (this is MutableSet<TElement>)
        this.remove(element)
    else
        this.removeIf{ it == element }
