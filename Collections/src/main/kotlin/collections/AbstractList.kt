package collections

import java.io.Serializable

abstract class AbstractList<TElement> : AbstractCollection<TElement>(), MutableList<TElement>, RandomAccess {
    override fun add(element: TElement): Boolean {
        this.add(this.size, element)

        return true
    }

    override fun add(index: Int, element: TElement) {
        val list = listOf(element)

        this.addAll(index, list)
    }

    override fun addAll(elements: Collection<TElement>): Boolean =
        this.addAll(this.size, elements)

    override fun remove(element: @UnsafeVariance TElement): Boolean {
        val index = this.indexOf(element)

        if (-1 == index) {
            return false
        }

        this.removeAt(index)

        return true
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
        Sublist(this, fromIndex, toIndex)

    override fun iterator(): MutableIterator<TElement> =
        this.listIterator()

    override fun listIterator(): MutableListIterator<TElement> =
        this.listIterator(0)

    override fun listIterator(index: Int): MutableListIterator<TElement> = object : MutableListIterator<TElement> {
        init {
            checkIfIndexCanBeInsertedAt(index, this@AbstractList.size)
        }

        private var currentIndex: Int = index
        private var lastUsedIndex: Int? = null
        private var calledPreviousLast: Boolean = false
        private var modCount: Int = this@AbstractList.modCount

        override fun previousIndex(): Int {
            checkIfUnderlyingCollectionHasBeenModified(this.modCount, this@AbstractList.modCount)

            return this.currentIndex - 1
        }

        override fun nextIndex(): Int {
            checkIfUnderlyingCollectionHasBeenModified(this.modCount, this@AbstractList.modCount)

            return this.currentIndex
        }

        override fun hasPrevious(): Boolean =
            this.previousIndex() >= 0

        override fun hasNext(): Boolean =
            this.nextIndex() < this@AbstractList.size

        override fun previous(): TElement {
            checkIfPrev(this)

            val usedIndex = this.previousIndex()
            val item = this@AbstractList[usedIndex]

            this.lastUsedIndex = usedIndex
            this.calledPreviousLast = true
            --(this.currentIndex)

            return item
        }

        override fun next(): TElement {
            checkIfNext(this)

            val usedIndex = this.nextIndex()
            val item = this@AbstractList[usedIndex]

            this.lastUsedIndex = usedIndex
            this.calledPreviousLast = false
            ++(this.currentIndex)

            return item
        }

        override fun set(element: TElement) {
            checkIfUnderlyingCollectionHasBeenModified(this.modCount, this@AbstractList.modCount)

            this.lastUsedIndex?.let {
                this@AbstractList[it] = element
            } ?: noneToUse()
        }

        override fun remove() {
            checkIfUnderlyingCollectionHasBeenModified(this.modCount, this@AbstractList.modCount)

            this.lastUsedIndex?.let {
                this@AbstractList.removeAt(it)

                ++(this.modCount)
                this.lastUsedIndex = null

                if (it != this.nextIndex()) {
                    --(this.currentIndex)
                }
            } ?: noneToUse()
        }

        override fun add(element: TElement) {
            checkIfUnderlyingCollectionHasBeenModified(this.modCount, this@AbstractList.modCount)

            this@AbstractList.add(this.nextIndex(), element)

            this.lastUsedIndex?.let {
                if (this.calledPreviousLast) {
                    this.lastUsedIndex = it + 1
                }
            }

            ++(this.modCount)
        }
    }
}

internal class Sublist<TElement>(
    private val base: MutableList<TElement>,
    private val fromIndex: Int,
    private var toIndex: Int
) : AbstractList<TElement>(), RandomAccess, Serializable {
    private companion object {
        @Suppress("ConstPropertyName")
        const val serialVersionUID: Long = 1L
    }

    init {
        checkIfRangeInBounds(this.fromIndex, this.toIndex, this.base.size)
        checkIfValidRange(this.fromIndex, this.toIndex)
    }

    override val size: Int
        get() = this.toIndex - this.fromIndex

    override fun isEmpty(): Boolean =
        this.fromIndex == this.toIndex

    override fun get(index: Int): TElement {
        checkIfIndexIsAccessible(index, this.size)

        return this.base[index + this.fromIndex]
    }

    override fun set(index: Int, element: TElement): TElement {
        checkIfIndexIsAccessible(index, this.size)

        return this.base.set(index + this.fromIndex, element)
    }

    override fun addAll(index: Int, elements: Collection<TElement>): Boolean {
       checkIfIndexCanBeInsertedAt(index, this.size)

        val initialSize = elements.size
        val actualIndex = index + this.fromIndex

        if (this.base.addAll(actualIndex, elements)) {
            this.toIndex += initialSize

            return true
        }

        return false
    }

    override fun removeAt(index: Int): TElement {
        checkIfIndexIsAccessible(index, this.size)

        val item = this.base.removeAt(index + this.fromIndex)

        --(this.toIndex)

        return item
    }

    override fun clear() {
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

    override fun subList(fromIndex: Int, toIndex: Int): MutableList<TElement> {
        checkIfRangeInBounds(fromIndex, toIndex, this.size)
        checkIfValidRange(fromIndex, toIndex)

        return Sublist(this.base, fromIndex + this.fromIndex, toIndex + this.fromIndex)
    }
}
