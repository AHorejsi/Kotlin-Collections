package com.alexh.collects

class VectorList<E>(
    initialCapacity: Int = VectorList.DEFAULT_CAPACITY
) : AbstractRandomAccessList<E>(), RandomAccess
{
    private companion object {
        const val DEFAULT_CAPACITY = 16
    }

    private var data: Array<Any?> = arrayOfNulls(initialCapacity)

    constructor(other: Collection<E>) : this(other.size) {
        for ((index, elem) in other.withIndex()) {
            this.data[index] = elem
        }

        this.size = other.size
    }

    constructor(size: Int, value: E) : this(size) {
        for (count in 0 until size) {
            this.add(value)
        }
    }

    override fun get(index: Int): E {
        this.checkRangeExclusive(index)

        return this.data[index] as E
    }

    override fun set(index: Int, element: E): E {
        val old = this[index]
        this.data[index] = element

        return old
    }

    override fun addAll(index: Int, elements: Collection<E>): Boolean {
        this.checkRangeInclusive(index)
        this.expandIfNeeded(elements.size)

        if (index != this.size) {
            this.shiftForInsertion(index, elements.size)
        }

        for ((insertIndex, elem) in countUp(index, elements.size).zip(elements)) {
            this.data[insertIndex] = elem
        }

        this.size += elements.size
        ++(this.modifyCount)

        return true
    }

    private fun expandIfNeeded(otherSize: Int) {
        val newSize = this.size + otherSize

        if (newSize > this.data.size) {
            val newData = arrayOfNulls<Any?>(this.data.size * 2)

            for ((index, elem) in this.withIndex()) {
                newData[index] = elem
            }

            this.data = newData
        }
    }

    private fun shiftForInsertion(insertIndex: Int, otherSize: Int) {
        for (index in this.lastIndex downTo insertIndex) {
            this[index + otherSize] = this[index]
        }
    }

    private fun checkRangeInclusive(index: Int) {
        if (index < 0 || index > this.size) {
            throw IndexOutOfBoundsException()
        }
    }

    private fun checkRangeExclusive(index: Int) {
        if (index < 0 || index >= this.size) {
            throw IndexOutOfBoundsException()
        }
    }

    override fun removeAt(index: Int): E {
        val elem = this[index]
        this.shiftForRemoval(index)

        --(this.size)
        ++(this.modifyCount)

        return elem
    }

    private fun shiftForRemoval(index: Int) {
        for (swapIndex in index until this.lastIndex) {
            this.data[swapIndex] = this.data[swapIndex + 1]
        }

        this.data[this.lastIndex] = null
    }

    override fun clear() {
        this.data = arrayOfNulls(VectorList.DEFAULT_CAPACITY)
        this.size = 0
        ++(this.modifyCount)
    }
}
