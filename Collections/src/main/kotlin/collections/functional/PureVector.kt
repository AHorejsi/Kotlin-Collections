package collections.functional

import collections.DefaultComparator
import collections.index
import collections.lastIndex
import collections.wrapGet
import java.io.Serializable

@Suppress("RemoveRedundantQualifierName")
sealed class PureVector<TElement> : PureList<TElement>, Serializable {
    companion object {
        @Suppress("ConstPropertyName")
        const val serialVersionUID: Long = 1L

        fun <TItem> empty(): PureVector<TItem> = PureVector.Empty()

        fun <TItem> single(only: TItem): PureVector<TItem> = PureVector.Singleton(only)
    }

    private class Empty<TItem> : PureVector<TItem>() {
        override val size: Int
            get() = 0

        override val head: TItem
            get() = throw NoSuchElementException()

        override operator fun get(index: Int): TItem = throw IndexOutOfBoundsException()

        override fun update(elements: Iterable<IndexedValue<TItem>>): PureVector<TItem> {
            if (elements.any()) {
                throw IndexOutOfBoundsException()
            }

            return this
        }

        override fun prependAll(elements: Collection<TItem>): PureVector<TItem> {
            if (elements.isEmpty()) {
                return this
            }

            val size = elements.size
            val iter = elements.iterator()
            val array = Array<Any?>(size) { iter.next() }

            return PureVector.Multiple(array, 0, size)
        }

        override fun appendAll(elements: Collection<TItem>): PureVector<TItem> = this.prependAll(elements)

        override fun insertAll(index: Int, elements: Collection<TItem>): PureVector<TItem> {
            if (0 != index) {
                throw IndexOutOfBoundsException()
            }

            return this.appendAll(elements)
        }

        override fun reverse(): PureVector<TItem> = this

        override fun rotate(amount: Int): PureVector<TItem> = this
    }

    private class Singleton<TItem>(override val head: TItem) : PureVector<TItem>() {
        override val size: Int
            get() = 1

        override val tail: PureVector<TItem>
            get() = PureVector.Empty()

        override operator fun get(index: Int): TItem =
            if (0 != index)
                throw IndexOutOfBoundsException()
            else
                this.head

        override fun update(elements: Iterable<IndexedValue<TItem>>): PureVector<TItem> {
            val indexed = elements.lastOrNull{ 0 == it.index }

            return if (null === indexed)
                this
            else
                PureVector.Singleton(indexed.value)
        }

        override fun reverse(): PureVector<TItem> = this

        override fun rotate(amount: Int): PureVector<TItem> = this
    }

    private class Multiple<TItem>(
        val items: Array<Any?>,
        val startIndex: Int,
        override val size: Int
    ) : PureVector<TItem>() {
        override fun get(index: Int): TItem =
            if (index < 0 || index >= this.size)
                throw IndexOutOfBoundsException()
            else
                @Suppress("UNCHECKED_CAST")
                this.items[this.actualIndex(index)] as TItem

        private fun actualIndex(index: Int): Int = (index + this.startIndex).mod(this.items.size)
    }

    private class Slice<TItem>(
        val base: PureVector<TItem>,
        val fromIndex: Int,
        val toIndex: Int
    ) : PureVector<TItem>() {
        override val size: Int
            get() = this.toIndex - this.fromIndex

        override operator fun get(index: Int): TItem {
            if (index < 0 || index >= this.size) {
                throw IndexOutOfBoundsException()
            }

            return this.base[index + this.fromIndex]
        }
    }

    private class Reversed<TItem>(val base: PureVector<TItem>) : PureVector<TItem>() {
        override val size: Int
            get() = this.base.size

        override operator fun get(index: Int): TItem = this.base[this.actualIndex(index)]

        private fun actualIndex(index: Int): Int = this.lastIndex - index

        override fun reverse(): PureVector<TItem> = this.base
    }

    private class Rotated<TItem>(
        val base: PureVector<TItem>,
        val amount: Int
    ) : PureVector<TItem>() {
        override val size: Int
            get() = this.base.size

        override operator fun get(index: Int): TItem {
            if (index < 0 || index >= this.size) {
                throw IndexOutOfBoundsException()
            }

            return this.base.wrapGet(index - this.amount)
        }

        override fun rotate(amount: Int): PureVector<TItem> {
            if (0 == amount.mod(this.size)) {
                return this
            }

            val newAmount = (amount + this.amount).mod(this.size)

            return if (0 == newAmount)
                this.base
            else
                PureVector.Rotated(this.base, newAmount)
        }
    }

    private class Transformed<TItem, TOther>(
        val base: PureVector<TItem>,
        val operation: (TItem) -> TOther
    ) : PureVector<TOther>() {
        private val used: Array<Any?> = arrayOfNulls(this.size)

        override val size: Int
            get() = this.base.size

        override operator fun get(index: Int): TOther {
            if (index < 0 || index >= this.size) {
                throw IndexOutOfBoundsException()
            }

            @Suppress("UNCHECKED_CAST")
            var item = this.used[index] as? TOther

            if (null === item) {
                item = this.operation(this.base[index])

                this.used[index] = item

                return item
            }

            return item
        }
    }

    override fun isEmpty(): Boolean = 0 == this.size

    override val head: TElement
        get() = this[0]

    override val tail: PureVector<TElement>
        get() = this.skipFirst(1)

    override fun set(index: Int, element: TElement): PureVector<TElement> {
        val indexed = IndexedValue(index, element)
        val vec = PureVector.Singleton(indexed)

        return this.update(vec)
    }

    override fun update(elements: Iterable<IndexedValue<TElement>>): PureVector<TElement> {
        if (!elements.any()) {
            return this
        }

        val base = arrayOfNulls<Any>(this.size)

        for ((index, item) in elements) {
            base[index] = item
        }

        for ((index, item) in this.withIndex()) {
            if (null === base[index]) {
                base[index] = item
            }
        }

        return PureVector.Multiple(base, 0, this.size)
    }

    override fun prepend(element: TElement): PureVector<TElement> {
        val vec = PureVector.Singleton(element)

        return this.prependAll(vec)
    }

    abstract override fun prependAll(elements: Collection<TElement>): PureVector<TElement>

    override fun append(element: TElement): PureVector<TElement> {
        val vec = PureVector.Singleton(element)

        return this.appendAll(vec)
    }

    abstract override fun appendAll(elements: Collection<TElement>): PureVector<TElement>

    override fun insert(index: Int, element: TElement): PureVector<TElement> {
        val vec = PureVector.Singleton(element)

        return this.insertAll(index, vec)
    }

    abstract override fun insertAll(index: Int, elements: Collection<TElement>): PureVector<TElement>

    override fun remove(element: TElement): PureVector<TElement> {
        val vec = PureVector.Singleton(element)

        return this.removeAll(vec)
    }

    override fun removeAll(elements: Collection<TElement>): PureVector<TElement> = this.sieve{ it in elements }

    override fun removeAt(index: Int): PureVector<TElement> {
        val vec = PureVector.Singleton(index)

        return this.removeAt(vec)
    }

    abstract override fun removeAt(indices: Iterable<Int>): PureVector<TElement>

    override operator fun contains(element: TElement): Boolean = -1 != this.indexOf(element)

    override fun indexOf(element: TElement): Int = this.index(0, element)

    override fun lastIndexOf(element: TElement): Int = this.lastIndex(this.lastIndex, element)

    override fun containsAll(elements: Collection<TElement>): Boolean = elements.all{ it in this }

    override fun drawFirst(amount: Int): PureVector<TElement> = this.subList(0, amount)

    override fun drawLast(amount: Int): PureVector<TElement> = this.drawFirst(this.size - amount)

    override fun skipFirst(amount: Int): PureVector<TElement> = this.subList(amount, this.size)

    override fun skipLast(amount: Int): PureVector<TElement> = this.skipFirst(this.size - amount)

    override fun split(index: Int): Pair<PureVector<TElement>, PureVector<TElement>> {
        val start = this.drawFirst(index)
        val end = this.skipFirst(index)

        return start to end
    }

    override fun subList(fromIndex: Int, toIndex: Int): PureVector<TElement> =
        PureVector.Slice(this, fromIndex, toIndex)

    override fun sort(): PureVector<TElement> {
        val default = DefaultComparator<TElement>()

        return this.sort(default::compare)
    }

    abstract override fun sort(comp: (TElement, TElement) -> Int): PureVector<TElement>

    override fun reverse(): PureVector<TElement> = PureVector.Reversed(this)

    override fun rotate(amount: Int): PureVector<TElement> =
        if (0 == amount.mod(this.size))
            this
        else
            PureVector.Rotated(this, amount)

    override fun <TOther> transform(operation: (TElement) -> TOther): PureVector<TOther> =
        PureVector.Transformed(this, operation)

    abstract override fun sieve(predicate: (TElement) -> Boolean): PureVector<TElement>

    abstract override fun partition(predicate: (TElement) -> Boolean): Pair<PureVector<TElement>, PureVector<TElement>>

    override fun iterator(): Iterator<TElement> = this.listIterator()

    override fun listIterator(): ListIterator<TElement> = this.listIterator(0)

    override fun listIterator(index: Int): ListIterator<TElement> = object : ListIterator<TElement> {
        private var currentIndex: Int = index

        override fun previousIndex(): Int = this.currentIndex - 1

        override fun nextIndex(): Int = this.currentIndex

        override fun hasPrevious(): Boolean = this.currentIndex > 0

        override fun hasNext(): Boolean = this.currentIndex < this@PureVector.size

        override fun previous(): TElement {
            if (!this.hasPrevious()) {
                throw NoSuchElementException()
            }

            val item = this@PureVector[this.nextIndex()]

            ++(this.currentIndex)

            return item
        }

        override fun next(): TElement {
            if (!this.hasNext()) {
                throw NoSuchElementException()
            }

            val item = this@PureVector[this.previousIndex()]

            --(this.currentIndex)

            return item
        }
    }
}
