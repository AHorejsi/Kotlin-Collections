package collections

import java.io.Serializable

abstract class AbstractRandomAccessList<TElement> : AbstractList<TElement>(), RandomAccess {
    override fun add(element: TElement): Boolean {
        this.add(this.size, element)

        return true
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

    override fun subList(fromIndex: Int, toIndex: Int): MutableList<TElement> =
        RandomAccessSublist(this, fromIndex, toIndex)

    override fun listIterator(index: Int): MutableListIterator<TElement> = object : MutableListIterator<TElement> {
        init {
            checkIfIndexCanBeInsertedAt(index, this@AbstractRandomAccessList.size)
        }

        private var currentIndex: Int = index
        private var lastUsedIndex: Int? = null
        private var calledPreviousLast: Boolean = false
        private var modCount: Int = this@AbstractRandomAccessList.modCount

        override fun previousIndex(): Int {
            checkIfUnderlyingCollectionHasBeenModified(this.modCount, this@AbstractRandomAccessList.modCount)

            return this.currentIndex - 1
        }

        override fun nextIndex(): Int {
            checkIfUnderlyingCollectionHasBeenModified(this.modCount, this@AbstractRandomAccessList.modCount)

            return this.currentIndex
        }

        override fun hasPrevious(): Boolean =
            this.previousIndex() >= 0

        override fun hasNext(): Boolean =
            this.nextIndex() < this@AbstractRandomAccessList.size

        override fun previous(): TElement {
            checkIfPrev(this)

            val usedIndex = this.previousIndex()
            val item = this@AbstractRandomAccessList[usedIndex]

            this.lastUsedIndex = usedIndex
            this.calledPreviousLast = true
            --(this.currentIndex)

            return item
        }

        override fun next(): TElement {
            checkIfNext(this)

            val usedIndex = this.nextIndex()
            val item = this@AbstractRandomAccessList[usedIndex]

            this.lastUsedIndex = usedIndex
            this.calledPreviousLast = false
            ++(this.currentIndex)

            return item
        }

        override fun set(element: TElement) {
            checkIfUnderlyingCollectionHasBeenModified(this.modCount, this@AbstractRandomAccessList.modCount)

            this.lastUsedIndex?.let {
                this@AbstractRandomAccessList[it] = element
            } ?: noneToUse()
        }

        override fun remove() {
            checkIfUnderlyingCollectionHasBeenModified(this.modCount, this@AbstractRandomAccessList.modCount)

            this.lastUsedIndex?.let {
                this@AbstractRandomAccessList.removeAt(it)

                ++(this.modCount)
                this.lastUsedIndex = null

                if (it != this.nextIndex()) {
                    --(this.currentIndex)
                }
            } ?: noneToUse()
        }

        override fun add(element: TElement) {
            checkIfUnderlyingCollectionHasBeenModified(this.modCount, this@AbstractRandomAccessList.modCount)

            this@AbstractRandomAccessList.add(this.nextIndex(), element)

            this.lastUsedIndex?.let {
                if (this.calledPreviousLast) {
                    this.lastUsedIndex = it + 1
                }
            }

            ++(this.modCount)
        }
    }
}

internal class RandomAccessSublist<TElement>(
    private val base: MutableList<TElement>,
    private val fromIndex: Int,
    private var toIndex: Int
) : AbstractRandomAccessList<TElement>(), Serializable {
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

        val initialSize = this.base.size
        val actualIndex = index + this.fromIndex

        if (this.base.addAll(actualIndex, elements)) {
            this.toIndex += this.base.size - initialSize

            return true
        }

        return false
    }

    fun removeFromBack(amount: Int): Int {
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

    override fun subList(fromIndex: Int, toIndex: Int): MutableList<TElement> =
        RandomAccessSublist(this, fromIndex, toIndex)
}
