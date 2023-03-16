package me.alexh.collects

interface SelfOrgList<TElement> : MutableCollection<TElement> {
    operator fun get(index: Int): TElement

    override operator fun contains(element: TElement): Boolean = this.contains{ it == element }

    fun contains(predicate: Predicate<in TElement>): Boolean = -1 != this.indexOf(predicate)

    fun find(element: @UnsafeVariance TElement): ListIterator<TElement> = this.find{ it == element }

    fun find(predicate: Predicate<in TElement>): ListIterator<TElement>

    fun indexOf(element: @UnsafeVariance TElement): Int = this.indexOf{ it == element }

    fun indexOf(predicate: Predicate<in TElement>): Int = this.find(predicate).nextIndex()

    fun count(element: @UnsafeVariance TElement): Int = this.count{ it == element }

    fun count(predicate: Predicate<in TElement>): Int

    fun front(): ListIterator<TElement>

    fun back(): ListIterator<TElement>
}
