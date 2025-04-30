package collections

val <TElement> Comparator<TElement>?.nonnull: Comparator<TElement>
    get() = this ?: inOrder()

val <TElement> Comparator<TElement>?.function: (TElement, TElement) -> Int
    get() = this.nonnull::compare

val <TElement> ((TElement, TElement) -> Int).reversed: (TElement, TElement) -> Int
    get() {
        return { left, right -> this(right, left) }
    }

fun <TElement> inOrder(): Comparator<TElement> =
    Comparator { p0, p1 ->
        @Suppress("UNCHECKED_CAST")
        (p0 as Comparable<TElement>).compareTo(p1)
    }

fun <TElement> nullFirst(comp: (TElement, TElement) -> Int): (TElement, TElement) -> Int {
    return { left, right ->
        when (left to right) {
            null to null -> 0
            null to right -> -1
            left to null -> 1
            else -> comp(left, right)
        }
    }
}
