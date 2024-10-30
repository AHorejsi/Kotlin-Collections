package collections

fun <TElement> MutableCollection<TElement>.insert(elements: Collection<TElement>): Int {
    val oldSize = this.size

    if (this === elements) {
        return this.selfInsert()
    }
    else {
        elements.forEach(this::add)

        return this.size - oldSize
    }
}

private fun <TElement> MutableCollection<TElement>.selfInsert(): Int =
    when (this) {
        is MutableList<TElement> -> {
            this.insertListIntoSelf()
        }
        is MutableLinkedList<TElement> -> {
            this.insertLinkedListIntoSelf()
        }
        is MutableSet<TElement> -> 0
        else -> this.insertFromCopy()
    }

private fun <TElement> MutableList<TElement>.insertListIntoSelf(): Int {
    if (!this.isRandomAccess) {
        return this.insertFromCopy()
    }

    val oldSize = this.size

    for (index in 0 until oldSize) {
        this.add(this[index])
    }

    return oldSize
}

private fun <TElement> MutableCollection<TElement>.insertFromCopy(): Int {
    val copy = this.toList()

    copy.forEach(this::add)

    return copy.size
}

private fun <TElement> MutableLinkedList<TElement>.insertLinkedListIntoSelf(): Int {
    val oldSize = this.size

    var current = this.head

    repeat(oldSize) {
        val node = current!!

        this.addLast(node.item)

        current = node.next
    }

    return oldSize
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
