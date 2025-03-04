package collections

fun <TElement> MutableCollection<TElement>.insert(elements: Collection<TElement>): Int {
    val oldSize = this.size

    this.addAll(elements)

    return this.size - oldSize
}

fun <TElement> MutableCollection<TElement>.delete(elements: Collection<TElement>): Int {
    val oldSize = this.size

    @Suppress("ConvertArgumentToSet")
    this.removeAll(elements)

    return oldSize - this.size
}

fun <TElement> MutableCollection<TElement>.keep(elements: Collection<TElement>): Int {
    val oldSize = this.size

    @Suppress("ConvertArgumentToSet")
    this.retainAll(elements)

    return oldSize - this.size
}
