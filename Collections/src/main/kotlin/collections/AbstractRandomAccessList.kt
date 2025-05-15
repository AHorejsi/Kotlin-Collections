package collections

import java.io.Serializable

abstract class AbstractRandomAccessList<TElement> : AbstractList<TElement>(), RandomAccess {
    override fun remove(element: @UnsafeVariance TElement): Boolean {
        val index = this.indexOf(element)

        if (-1 == index) {
            return false
        }

        this.removeAt(index)

        return true
    }

    override fun removeAll(elements: Collection<TElement>): Boolean {
        val values =
            if (elements is RandomAccessSublist && elements.hasBaseOf(this))
                elements.toList()
            else
                elements

        @Suppress("ConvertArgumentToSet")
        return super.removeAll(values)
    }

    override fun retainAll(elements: Collection<TElement>): Boolean {
        val values =
            if (elements is RandomAccessSublist && elements.hasBaseOf(this))
                elements.toList()
            else
                elements

        @Suppress("ConvertArgumentToSet")
        return super.retainAll(values)
    }

    override fun subList(fromIndex: Int, toIndex: Int): MutableList<TElement> =
        if (0 == fromIndex && this.size == toIndex)
            this
        else
            RandomAccessSublist(this, fromIndex, toIndex)

    override fun listIterator(index: Int): MutableListIterator<TElement> = object : MutableListIterator<TElement> {
        init {
            checkIfIndexCanBeInsertedAt(index, this@AbstractRandomAccessList.size)
        }

        private var currentIndex: Int = index
        private var lastUsedIndex: Int? = null
        private var calledAdd: Boolean = false
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
            --(this.currentIndex)
            this.calledAdd = false

            return item
        }

        override fun next(): TElement {
            checkIfNext(this)

            val usedIndex = this.nextIndex()
            val item = this@AbstractRandomAccessList[usedIndex]

            this.lastUsedIndex = usedIndex
            ++(this.currentIndex)
            this.calledAdd = false

            return item
        }

        override fun set(element: TElement) {
            checkIfUnderlyingCollectionHasBeenModified(this.modCount, this@AbstractRandomAccessList.modCount)
            checkIfCalledAddOnListIterator(this.calledAdd)

            this.lastUsedIndex?.let {
                this@AbstractRandomAccessList[it] = element
            } ?: noneToUse()
        }

        override fun remove() {
            checkIfUnderlyingCollectionHasBeenModified(this.modCount, this@AbstractRandomAccessList.modCount)
            checkIfCalledAddOnListIterator(this.calledAdd)

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

            this@AbstractRandomAccessList.add(this.currentIndex, element)

            ++(this.currentIndex)
            this.calledAdd = true
            ++(this.modCount)
        }
    }
}

internal class RandomAccessSublist<TElement>(
    private val base: AbstractRandomAccessList<TElement>,
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

        super.modCount = this.base.modCount
    }

    override val size: Int
        get() {
            checkIfUnderlyingCollectionHasBeenModified(super.modCount, this.base.modCount)

            return this.toIndex - this.fromIndex
        }

    internal fun hasBaseOf(other: List<TElement>): Boolean {
        var list = this.base

        while (list is RandomAccessSublist) {
            list = list.base
        }

        return list === other
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

    override fun addAll(index: Int, elements: Collection<TElement>): Boolean {
        checkIfUnderlyingCollectionHasBeenModified(super.modCount, this.base.modCount)
        checkIfIndexCanBeInsertedAt(index, this.size)

        val initialSize = elements.size
        val actualIndex = index + this.fromIndex

        if (this.base.addAll(actualIndex, elements)) {
            this.toIndex += initialSize
            ++(super.modCount)

            return true
        }

        return false
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
            ++(super.modCount)
        }

        return oldSize - this.size
    }

    override fun removeAt(index: Int): TElement {
        checkIfUnderlyingCollectionHasBeenModified(super.modCount, this.base.modCount)
        checkIfIndexIsAccessible(index, this.size)

        val item = this.base.removeAt(index + this.fromIndex)

        --(this.toIndex)
        ++(super.modCount)

        return item
    }

    override fun clear() {
        checkIfUnderlyingCollectionHasBeenModified(super.modCount, this.base.modCount)

        if (this.isEmpty()) {
            return
        }

        this.shiftForClearing()
        this.base.removeFromBack(this.size)

        this.toIndex = this.fromIndex
        ++(super.modCount)
    }

    private fun shiftForClearing() {
        val size = this.size

        for (index in this.toIndex until this.base.size) {
            this.base[index - size] = this.base[index]
        }
    }
}
