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
