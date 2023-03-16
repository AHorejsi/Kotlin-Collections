package me.alexh.collects

fun <TElement> MutableCollection<TElement>.addAll(vararg elements: TElement): Boolean = this.addAll(elements.iterator())

fun <TElement> MutableCollection<TElement>.addAll(elements: Iterable<TElement>): Boolean =
    if (elements is Collection<TElement>) {
        this.addAll(elements)
    }
    else {
        this.addAll(elements.iterator())
    }

fun <TElement> MutableCollection<TElement>.addAll(iter: Iterator<TElement>): Boolean {
    var change = false

    while (iter.hasNext()) {
        val elem = iter.next()

        if (this.add(elem)) {
            change = true
        }
    }

    return change
}
