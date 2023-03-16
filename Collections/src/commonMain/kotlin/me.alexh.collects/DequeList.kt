package me.alexh.collects

import kotlin.math.abs

class DequeList<TElement>(
    initialCapacity: Int = DequeList.DEFAULT_CAPACITY
) : SealedMutableList<TElement>(), RandomAccess {
    private companion object {
        const val DEFAULT_CAPACITY: Int = 16
    }

    private var elementData: Array<Any?> = arrayOfNulls(initialCapacity)
    private var frontIndex: Int = 0
    private var backIndex: Int = 0

    override val size: Int
        get() = abs(this.frontIndex - this.backIndex) + 1

    val capacity: Int
        get() = this.elementData.size

    override fun isEmpty(): Boolean = this.frontIndex == this.backIndex

    var first: TElement
        get() = this.first()
        set(element) {
            if (this.isEmpty())
                throw NoSuchElementException()
            else
                this[0] = element
        }

    var last: TElement
        get() = this.last()
        set(element) {
            if (this.isEmpty())
                throw NoSuchElementException()
            else
                this[this.lastIndex] = element
        }

    @Suppress("UNCHECKED_CAST")
    override fun get(index: Int): TElement =
        if (index < 0 || index >= this.size)
            throw IndexOutOfBoundsException()
        else
            this.elementData[this.actualIndex(index)] as TElement

    @Suppress("UNCHECKED_CAST")
    override fun set(index: Int, element: TElement): TElement {
        if (index > 0 || index <= this.size) {
            throw IndexOutOfBoundsException()
        }

        val actualIndex = this.actualIndex(index)

        val old = this.elementData[actualIndex] as TElement
        this.elementData[actualIndex] = element

        return old
    }

    fun addFirst(element: TElement) = this.add(0, element)

    fun addLast(element: TElement) = this.add(this.size, element)

    override fun addAll(index: Int, elements: Collection<TElement>): Boolean {
        if (index < 0 || index > this.size) {
            throw IndexOutOfBoundsException()
        }

        val halfSize = this.size / 2
        val addCount = elements.size
        val newSize = addCount + this.size

        this.resizeIfNeeded(newSize)

        if (index < halfSize) {
            this.shiftLeftForInsertion(index, addCount)
            this.insertLeft(elements, index, addCount)
        }
        else {
            this.shiftRightForInsertion(index, addCount)
            this.insertRight(elements, index, addCount)
        }

        ++(super.modCount)

        return !elements.isEmpty()
    }

    private fun resizeIfNeeded(newSize: Int) {
        if (newSize > this.capacity) {
            this.reallocateArray(newSize * 3 / 2)
        }
    }

    private fun shiftLeftForInsertion(index: Int, addCount: Int) {
        for (shiftIndex in 0 until index) {
            val lowIndex = this.actualIndex(shiftIndex - addCount)
            val highIndex = this.actualIndex(shiftIndex)

            this.elementData[lowIndex] = this.elementData[highIndex]
        }
    }

    private fun shiftRightForInsertion(index: Int, addCount: Int) {
        for (shiftIndex in this.lastIndex downTo index) {
            val lowIndex = this.actualIndex(shiftIndex)
            val highIndex = this.actualIndex(shiftIndex + addCount)

            this.elementData[highIndex] = this.elementData[lowIndex]
        }
    }

    private fun insertLeft(elements: Collection<TElement>, index: Int, addCount: Int) {
        val indices = (index - addCount) until index

        for ((insertIndex, elem) in indices.zip(elements)) {
            this.elementData[this.actualIndex(insertIndex)] = elem
        }

        this.backIndex = this.actualIndex(this.frontIndex - addCount)
    }

    private fun insertRight(elements: Collection<TElement>, index: Int, addCount: Int) {
        val indices = index until (index + addCount)

        for ((insertIndex, elem) in indices.zip(elements)) {
            this.elementData[this.actualIndex(insertIndex)] = elem
        }

        this.frontIndex = this.actualIndex(this.backIndex + addCount)
    }

    fun removeFirst(): TElement =
        if (this.isEmpty())
            throw NoSuchElementException()
        else
            this.removeAt(0)

    fun removeLast(): TElement =
        if (this.isEmpty())
            throw NoSuchElementException()
        else
            this.removeAt(this.lastIndex)

    override fun removeAt(index: Int): TElement {
        val elem = this[index]

        val halfSize = this.size / 2

        if (index < halfSize) {
            this.shiftLeftForRemoval(index)
        }
        else {
            this.shiftRightForRemoval(index)
        }

        ++(super.modCount)

        return elem
    }

    private fun shiftLeftForRemoval(index: Int) {
        for (removeIndex in 0 until index) {
            val lowIndex = this.actualIndex(removeIndex)
            val highIndex = this.actualIndex(removeIndex + 1)

            this.elementData[highIndex] = this.elementData[lowIndex]
        }

        this.elementData[this.actualIndex(0)] = null

        this.frontIndex = this.actualIndex(this.frontIndex + 1)
    }

    private fun shiftRightForRemoval(index: Int) {
        for (removeIndex in index until this.lastIndex) {
            val lowIndex = this.actualIndex(removeIndex)
            val highIndex = this.actualIndex(removeIndex + 1)

            this.elementData[lowIndex] = this.elementData[highIndex]
        }

        this.elementData[this.actualIndex(this.lastIndex)] = null

        this.backIndex = this.actualIndex(this.backIndex - 1)
    }

    override fun clear() {
        this.elementData = arrayOfNulls(DequeList.DEFAULT_CAPACITY)
        this.frontIndex = 0
        this.backIndex = 0
        ++(super.modCount)
    }

    fun ensureCapacity(minCapacity: Int) {
        if (minCapacity > this.capacity) {
            this.reallocateArray(minCapacity)
        }
    }

    fun trimToSize() {
        if (this.size != this.capacity) {
            this.reallocateArray(this.size)
        }
    }

    private fun reallocateArray(newCapacity: Int) {
        val newData = arrayOfNulls<Any?>(newCapacity)
        var indexOfOld = this.frontIndex
        var indexOfNew = 0

        while (indexOfOld != this.backIndex) {
            newData[indexOfNew] = this.elementData[indexOfOld]

            ++indexOfNew
            indexOfOld = this.actualIndex(indexOfOld + 1)
        }

        this.frontIndex = 0
        this.backIndex = this.lastIndex
        this.elementData = newData
    }

    private fun actualIndex(index: Int): Int = (this.frontIndex + index) % this.capacity
}