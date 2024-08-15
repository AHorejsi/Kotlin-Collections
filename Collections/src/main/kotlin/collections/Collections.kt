package collections

fun <TElement> MutableCollection<TElement>.insert(elements: Collection<TElement>): Int {
    val oldSize = this.size

    elements.forEach(this::add)

    return this.size - oldSize
}

fun <TElement> MutableCollection<TElement>.delete(elements: Collection<TElement>): Int {
    if (this === elements) {
        val oldSize = this.size

        this.clear()

        return oldSize
    }

    val oldSize = this.size

    elements.forEach(this::remove)

    return oldSize - this.size
}

fun <TElement> MutableCollection<TElement>.keep(elements: Collection<TElement>): Int {
    if (this === elements) {
        return 0
    }

    val oldSize = this.size

    this.removeIf{ it !in elements }

    return oldSize - this.size
}
