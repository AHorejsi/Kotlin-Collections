package collections

abstract class AbstractCollection<TElement> : MutableCollection<TElement> {
    @Transient
    protected var modCount: Int = 0

    override fun isEmpty(): Boolean = 0 == this.size

    override fun addAll(elements: Collection<TElement>): Boolean = this.insert(elements) > 0

    override fun removeAll(elements: Collection<TElement>): Boolean = this.delete(elements) > 0

    override fun retainAll(elements: Collection<TElement>): Boolean = this.keep(elements) > 0

    override fun containsAll(elements: Collection<@UnsafeVariance TElement>): Boolean =
        elements.all{ it in this }

    override fun toString(): String {
        if (this.isEmpty()) {
            return "[]"
        }

        val sb = StringBuilder()

        sb.append('[')
        for (item in this.asSequence().take(this.size - 1)) {
            sb.append("$item, ")
        }
        sb.append("${this.last()}]")

        return sb.toString()
    }
}
