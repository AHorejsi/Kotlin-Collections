package collections

class DefaultComparator<TElement> : Comparator<TElement> {
    override fun compare(p0: TElement, p1: TElement): Int {
        @Suppress("UNCHECKED_CAST")
        val comp = p0 as Comparable<TElement>

        return comp.compareTo(p1)
    }
}

class FuncComparator<TElement>(private val func: (TElement, TElement) -> Int) : Comparator<TElement> {
    override fun compare(p0: TElement, p1: TElement): Int = this.func(p0, p1)
}

fun <TElement> reverseOrder(comp: (TElement, TElement) -> Int): (TElement, TElement) -> Int {
    return { left, right -> comp(right, left) }
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
