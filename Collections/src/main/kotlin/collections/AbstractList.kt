package collections

import java.io.Serializable

abstract class AbstractList<TElement> : AbstractCollection<TElement>(), MutableList<TElement> {
    override fun add(index: Int, element: TElement) {
        val list = listOf(element)

        this.addAll(index, list)
    }

    override fun remove(element: @UnsafeVariance TElement): Boolean {
        val iter = this.iterator()

        while (iter.hasNext()) {
            val current = iter.next()

            if (element == current) {
                iter.remove()

                return true
            }
        }

        return false
    }

    override operator fun contains(element: @UnsafeVariance TElement): Boolean =
        -1 != this.indexOf(element)

    override fun indexOf(element: @UnsafeVariance TElement): Int =
        this.index(0, element)

    override fun lastIndexOf(element: @UnsafeVariance TElement): Int =
        this.lastIndex(this.size, element)

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }

        if (other !is List<*> || this.size != other.size) {
            return false
        }

        val left = this.iterator()
        val right = other.iterator()

        while (left.hasNext()) {
            val leftItem = left.next()
            val rightItem = right.next()

            if (leftItem != rightItem) {
                return false
            }
        }

        return true
    }

    override fun hashCode(): Int {
        val modifier = 31
        var hashValue = modifier * this.size

        for (item in this) {
            hashValue = modifier * hashValue + item.hashCode()
        }

        return hashValue
    }

    override fun subList(fromIndex: Int, toIndex: Int): MutableList<TElement> =
        if (0 == fromIndex && this.size == toIndex)
            this
        else
            Sublist(this, fromIndex, toIndex, super.modCount)

    override fun iterator(): MutableIterator<TElement> =
        this.listIterator()

    override fun listIterator(): MutableListIterator<TElement> =
        this.listIterator(0)
}

internal class Sublist<TElement>(
    private val base: AbstractList<TElement>,
    private val fromIndex: Int,
    private var toIndex: Int,
    baseModCount: Int
) : AbstractList<TElement>(), Serializable {
    private companion object {
        @Suppress("ConstPropertyName")
        private const val serialVersionUID = 1L
    }

    init {
        checkIfRangeInBounds(this.fromIndex, this.toIndex, this.base.size)
        checkIfValidRange(this.fromIndex, this.toIndex)

        super.modCount = baseModCount
    }

    override val size: Int
        get() {
            checkIfUnderlyingCollectionHasBeenModified(super.modCount, this.base.modCount)

            return this.toIndex - this.fromIndex
        }

    override fun isEmpty(): Boolean {
        checkIfUnderlyingCollectionHasBeenModified(super.modCount, this.base.modCount)

        return this.fromIndex == this.toIndex
    }

    override fun get(index: Int): TElement {
        checkIfUnderlyingCollectionHasBeenModified(super.modCount, this.base.modCount)
        checkIfIndexIsAccessible(index, this.size)

        return this.base[index + this.fromIndex]
    }

    override fun set(index: Int, element: TElement): TElement {
        checkIfUnderlyingCollectionHasBeenModified(super.modCount, this.base.modCount)
        checkIfIndexIsAccessible(index, this.size)

        return this.base.set(index + this.fromIndex, element)
    }

    override fun add(element: TElement): Boolean {
        checkIfUnderlyingCollectionHasBeenModified(super.modCount, this.base.modCount)

        this.base.add(this.toIndex, element)

        ++(this.toIndex)

        return true
    }

    override fun addAll(index: Int, elements: Collection<TElement>): Boolean {
        checkIfUnderlyingCollectionHasBeenModified(super.modCount, this.base.modCount)
        checkIfIndexCanBeInsertedAt(index, this.size)

        val initialSize = this.base.size
        val actualIndex = index + this.fromIndex

        if (this.base.addAll(actualIndex, elements)) {
            this.toIndex += this.base.size - initialSize

            return true
        }

        return false
    }

    override fun removeAt(index: Int): TElement {
        checkIfUnderlyingCollectionHasBeenModified(super.modCount, this.base.modCount)
        checkIfIndexIsAccessible(index, this.size)

        val item = this.base.removeAt(index + this.fromIndex)

        --(this.toIndex)

        return item
    }

    fun removeFromBack(amount: Int): Int {
        checkIfUnderlyingCollectionHasBeenModified(super.modCount, this.base.modCount)

        val oldSize = this.size

        if (amount >= oldSize) {
            this.clear()
        }
        else {
            this.base.removeRange(oldSize - amount, oldSize)

            this.toIndex -= amount
        }

        return oldSize - this.size
    }

    override fun clear() {
        checkIfUnderlyingCollectionHasBeenModified(super.modCount, this.base.modCount)

        if (this.isEmpty()) {
            return
        }

        this.shiftForClearing()
        this.base.removeFromBack(this.size)

        this.toIndex = this.fromIndex
    }

    private fun shiftForClearing() {
        val size = this.size

        for (index in this.toIndex until this.base.size) {
            this.base[index - size] = this.base[index]
        }
    }

    override fun listIterator(index: Int): MutableListIterator<TElement> = object : MutableListIterator<TElement> {
        init {
            checkIfUnderlyingCollectionHasBeenModified(this@Sublist.modCount, this@Sublist.base.modCount)
        }

        private val iter = this@Sublist.base.listIterator(index + this@Sublist.fromIndex)

        override fun previousIndex(): Int =
            this.iter.previousIndex() - this@Sublist.fromIndex

        override fun nextIndex(): Int =
            this.iter.nextIndex() - this@Sublist.fromIndex

        override fun hasPrevious(): Boolean =
            this.previousIndex() >= 0

        override fun hasNext(): Boolean =
            this.nextIndex() < this@Sublist.size

        override fun previous(): TElement {
            checkIfPrev(this)

            return this.iter.previous()
        }

        override fun next(): TElement {
            checkIfNext(this)

            return this.iter.next()
        }

        override fun set(element: TElement) =
            this.iter.set(element)

        override fun remove() =
            this.iter.remove()

        override fun add(element: TElement) =
            this.iter.add(element)
    }
}
