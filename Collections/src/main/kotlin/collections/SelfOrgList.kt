package collections

internal sealed class OrgNode<TElement> {
    class Head<TKey>(override var next: OrgNode<TKey>?) : OrgNode<TKey>()
    class Item<TKey>(var value: TKey, override var next: OrgNode<TKey>?) : OrgNode<TKey>()

    abstract var next: OrgNode<TElement>?
}

interface SelfOrgList<TElement> : MutableCollection<TElement> {
    fun at(index: Int): TElement {
        if (index < 0 || index >= this.size) {
            throw IndexOutOfBoundsException()
        }

        val iter = this.iterator()

        repeat(index - 1) {
            iter.next()
        }

        return iter.next()
    }

    override fun addAll(elements: Collection<TElement>): Boolean {
        for (item in elements) {
            this.add(item)
        }

        return true
    }

    override fun removeAll(elements: Collection<TElement>): Boolean {
        val oldSize = this.size

        elements.forEach(this::remove)

        return oldSize > this.size
    }

    override fun retainAll(elements: Collection<TElement>): Boolean {
        var changed = false
        val iter = this.iterator()

        while (iter.hasNext()) {
            val item = iter.next()

            if (item !in elements) {
                iter.remove()

                changed = true
            }
        }

        return changed
    }

    fun find(element: @UnsafeVariance TElement): IndexedValue<TElement>? = this.find{ it == element }

    fun find(predicate: (element: TElement) -> Boolean): IndexedValue<TElement>?

    fun findAll(elements: Collection<@UnsafeVariance TElement>): Sequence<IndexedValue<TElement>> = this.findAll{ it in elements }

    fun findAll(predicate: (element: TElement) -> Boolean): Sequence<IndexedValue<TElement>>

    override operator fun contains(element: @UnsafeVariance TElement): Boolean = this.contains{ it == element }

    fun contains(predicate: (TElement) -> Boolean): Boolean = null !== this.find(predicate)

    override fun containsAll(elements: Collection<@UnsafeVariance TElement>): Boolean {
        var foundAll = true

        for (item in elements) {
            if (item !in this) {
                foundAll = false
            }
        }

        return foundAll
    }
}
