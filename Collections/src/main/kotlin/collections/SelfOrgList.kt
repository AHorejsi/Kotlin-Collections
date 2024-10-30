package collections

internal sealed class OrgNode<TElement> {
    class Head<TKey>(override var next: OrgNode<TKey>?) : OrgNode<TKey>()
    class Item<TKey>(var value: TKey, override var next: OrgNode<TKey>?) : OrgNode<TKey>()

    abstract var next: OrgNode<TElement>?
}

interface SelfOrgList<TElement> : MutableCollection<TElement> {
    val isRandomAccess: Boolean

    fun at(index: Int): TElement {
        checkIfIndexIsAccessible(index, this.size)

        val iter = this.iterator()

        repeat(index - 1) {
            iter.next()
        }

        return iter.next()
    }

    override fun addAll(elements: Collection<TElement>): Boolean =
        this.insert(elements) > 0

    override fun removeAll(elements: Collection<TElement>): Boolean =
        this.delete(elements) > 0

    override fun retainAll(elements: Collection<TElement>): Boolean =
        this.keep(elements) > 0

    fun find(element: @UnsafeVariance TElement): IndexedValue<TElement>? =
        this.find{ it == element }

    fun find(predicate: (element: TElement) -> Boolean): IndexedValue<TElement>?

    fun findAll(elements: Collection<@UnsafeVariance TElement>): Sequence<IndexedValue<TElement>> =
        this.findAll(elements::contains)

    fun findAll(predicate: (element: TElement) -> Boolean): Sequence<IndexedValue<TElement>>

    override operator fun contains(element: @UnsafeVariance TElement): Boolean =
        this.contains{ it == element }

    fun contains(predicate: (TElement) -> Boolean): Boolean =
        null !== this.find(predicate)

    override fun containsAll(elements: Collection<@UnsafeVariance TElement>): Boolean {
        var foundAll = true

        for (item in elements) {
            foundAll = item in this
        }

        return foundAll
    }
}
