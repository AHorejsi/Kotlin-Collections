package collections

abstract class AbstractCollection<TElement> : MutableCollection<TElement> {
    @Transient
    protected var modCount: Int = 0

    override fun isEmpty(): Boolean = 0 == this.size

    override fun addAll(elements: Collection<TElement>): Boolean = this.insert(elements) > 0

    override fun removeAll(elements: Collection<TElement>): Boolean = this.delete(elements) > 0

    override fun retainAll(elements: Collection<TElement>): Boolean {
        val oldSize = this.size
        val iter = this.iterator()

        while (iter.hasNext()) {
            val item = iter.next()

            if (item !in elements) {
                iter.remove()
            }
        }

        return oldSize > this.size
    }

    override fun containsAll(elements: Collection<@UnsafeVariance TElement>): Boolean =
        elements.all{ this.contains(it) }

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
