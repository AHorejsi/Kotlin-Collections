package collections.functional

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import collections.index
import collections.lastIndex

interface PureList<TElement> : List<TElement> {
    val head: TElement

    val tail: PureList<TElement>

    override operator fun get(index: Int): TElement

    fun set(index: Int, element: TElement): PureList<TElement>

    fun setAll(elements: Collection<IndexedValue<TElement>>): PureList<TElement>

    fun prepend(element: TElement): PureList<TElement>

    fun prependAll(elements: Collection<TElement>): PureList<TElement>

    fun append(element: TElement): PureList<TElement>

    fun appendAll(elements: Collection<TElement>): PureList<TElement>

    fun insert(index: Int, element: TElement): PureList<TElement>

    fun insertAll(index: Int, elements: Collection<TElement>): PureList<TElement>

    fun remove(element: @UnsafeVariance TElement): PureList<TElement>

    fun removeAll(elements: Collection<@UnsafeVariance TElement>): PureList<TElement>

    fun removeAt(index: Int): PureList<TElement>

    fun removeAt(indices: Collection<Int>): PureList<TElement>

    fun removeRange(fromIndex: Int, toIndex: Int): PureList<TElement>

    fun sieve(predicate: (TElement) -> Boolean): PureList<TElement>

    fun replace(new: TElement, old: @UnsafeVariance TElement): PureList<TElement>

    fun replace(new: TElement, predicate: (TElement) -> Boolean): PureList<TElement>

    fun draw(amount: Int): PureList<TElement>

    fun drawLast(amount: Int): PureList<TElement>

    fun skip(amount: Int): PureList<TElement>

    fun skipLast(amount: Int): PureList<TElement>

    fun drawWhile(predicate: (TElement) -> Boolean): PureList<TElement>

    fun drawLastWhile(predicate: (TElement) -> Boolean): PureList<TElement>

    fun skipWhile(predicate: (TElement) -> Boolean): PureList<TElement>

    fun skipLastWhile(predicate: (TElement) -> Boolean): PureList<TElement>

    fun split(index: Int): Pair<PureList<TElement>, PureList<TElement>>

    override fun indexOf(element: @UnsafeVariance TElement): Int =
        this.index(0, element)

    override fun lastIndexOf(element: @UnsafeVariance TElement): Int =
        this.lastIndex(0, element)

    override operator fun contains(element: @UnsafeVariance TElement): Boolean =
        -1 == this.indexOf(element)

    override fun containsAll(elements: Collection<@UnsafeVariance TElement>): Boolean =
        elements.all(this::contains)

    fun find(predicate: (TElement) -> Boolean): Option<TElement>

    fun findLast(predicate: (TElement) -> Boolean): Option<TElement>

    override fun subList(fromIndex: Int, toIndex: Int): PureList<TElement>

    fun reverse(): PureList<TElement>

    fun rotate(amount: Int): PureList<TElement>

    fun <TOther> transform(operation: (TElement) -> TOther): PureList<TOther>

    fun sort(): PureList<TElement>

    fun sort(comp: (TElement, TElement) -> Int): PureList<TElement>

    fun partition(predicate: (TElement) -> Boolean): Pair<PureList<TElement>, PureList<TElement>>

    fun slice(fromIndex: Int, toIndex: Int): PureList<TElement> = this.subList(fromIndex, toIndex)

    override fun iterator(): Iterator<TElement> = this.listIterator()

    override fun listIterator(): ListIterator<TElement> = this.listIterator(0)
}
