package collections

abstract class AbstractSet<TElement>(
    private val base: MutableMap<TElement, Unit>
) : AbstractCollection<TElement>(), MutableSet<TElement> {
    override val size: Int
        get() = this.base.size

    override fun add(element: TElement): Boolean =
        null === this.base.putIfAbsent(element, Unit)

    override fun remove(element: TElement): Boolean =
        null !== this.base.remove(element)

    override fun clear() =
        this.base.clear()

    override operator fun contains(element: TElement): Boolean =
        this.base.containsKey(element)

    override fun iterator(): MutableIterator<TElement> =
        this.base.keys.iterator()

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }

        if (other !is Set<*> || this.size != other.size) {
            return false
        }

        for (item in this) {
            if (item !in other) {
                return false
            }
        }

        return true
    }

    override fun hashCode(): Int {
        val modifier = 31
        var hashValue = modifier * this.size

        for (item in this) {
            hashValue += item.hashCode()
        }

        return hashValue
    }
}
