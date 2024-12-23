package collections

fun <TElement> MutableCollection<TElement>.insert(elements: Collection<TElement>): Int {
    val oldSize = this.size

    this.addAll(elements)

    return this.size - oldSize
}

fun <TElement> MutableCollection<TElement>.delete(elements: Collection<TElement>): Int {
    val oldSize = this.size

    if (this === elements) {
        this.clear()
    }
    else {
        elements.forEach(this::remove)
    }

    return oldSize - this.size
}

fun <TElement> MutableCollection<TElement>.keep(elements: Collection<TElement>): Int {
    val oldSize = this.size

    if (elements.isEmpty()) {
        this.clear()
    }
    else if (this !== elements) {
        this.removeIf{ it !in elements }
    }

    return oldSize - this.size
}
