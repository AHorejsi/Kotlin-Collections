package collections

abstract class AbstractCollection<TElement> : MutableCollection<TElement> {
    @Transient
    internal var modCount: Int = 0

    override fun isEmpty(): Boolean =
        0 == this.size

    override fun addAll(elements: Collection<TElement>): Boolean {
        var change = false

        for (item in elements) {
            if (this.add(item)) {
                change = true
            }
        }

        return change
    }

    override fun removeAll(elements: Collection<@UnsafeVariance TElement>): Boolean =
        this.delete(elements) > 0

    override fun retainAll(elements: Collection<@UnsafeVariance TElement>): Boolean =
        this.keep(elements) > 0

    override fun containsAll(elements: Collection<@UnsafeVariance TElement>): Boolean =
        elements.all(this::contains)

    override fun toString(): String {
        if (this.isEmpty()) {
            return "[]"
        }

        val sb = StringBuilder()
        val iter = this.iterator()
        var elem: TElement

        sb.append('[')

        while (true) {
            elem = iter.next()

            sb.append(elem)

            if (!iter.hasNext()) {
                break
            }

            sb.append(',')
            sb.append(' ')
        }

        sb.append(']')

        return sb.toString()
    }
}
