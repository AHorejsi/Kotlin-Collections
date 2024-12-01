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

    fun find(element: @UnsafeVariance TElement): IndexedValue<TElement>? =
        this.find{ it == element }

    fun find(predicate: (TElement) -> Boolean): IndexedValue<TElement>?

    fun findAll(elements: Collection<@UnsafeVariance TElement>): Sequence<IndexedValue<TElement>> =
        this.findAll(elements::contains)

    fun findAll(predicate: (TElement) -> Boolean): Sequence<IndexedValue<TElement>>

    override operator fun contains(element: @UnsafeVariance TElement): Boolean =
        null !== this.find{ it == element }

    override fun containsAll(elements: Collection<@UnsafeVariance TElement>): Boolean {
        var foundAll = true

        for (item in elements) {
            foundAll = foundAll && item in this
        }

        return foundAll
    }
}
