package collections

abstract class AbstractSortedSet<TElement>(
    private val base: MutableSortedMap<TElement, Unit>
) : AbstractSet<TElement>(base), MutableSortedSet<TElement> {
    override fun first(): TElement = this.base.firstKey()

    override fun last(): TElement = this.base.lastKey()

    override fun lesser(element: TElement, inclusive: Boolean): TElement = this.base.lesserKey(element, inclusive)

    override fun greater(element: TElement, inclusive: Boolean): TElement = this.base.greaterKey(element, inclusive)

    override fun removeFirst(): TElement = this.base.removeFirst().key

    override fun removeLast(): TElement = this.base.removeLast().key

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }

        if (other !is SortedSet<*> || this.size != other.size) {
            return false
        }

        val leftSeq = this.asSequence()
        val rightSeq = other.asSequence()

        for ((left, right) in leftSeq.zip(rightSeq)) {
            if (left != right) {
                return false
            }
        }

        return true
    }

    override fun hashCode(): Int {
        var hashValue = 31 * this.size

        for (item in this) {
            hashValue = 31 * hashValue + item.hashCode()
        }

        return hashValue
    }
}
