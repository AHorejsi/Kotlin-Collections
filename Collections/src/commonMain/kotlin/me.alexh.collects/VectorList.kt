package me.alexh.collects

class VectorList<TElement>(
    initialCapacity: Int = VectorList.DEFAULT_CAPACITY
) : SealedMutableList<TElement>(), RandomAccess {
    private companion object {
        const val DEFAULT_CAPACITY: Int = 16
    }

    private var elementData: Array<Any?> = arrayOfNulls(initialCapacity)
    override var size: Int = 0
        private set

    val capacity: Int
        get() = this.elementData.size

    @Suppress("UNCHECKED_CAST")
    override operator fun get(index: Int): TElement =
        if (index < 0 || index >= this.size)
            throw IndexOutOfBoundsException()
        else
            this.elementData[index] as TElement

    override operator fun set(index: Int, element: TElement): TElement {
        val old = this[index]
        this.elementData[index] = element

        return old
    }

    override fun addAll(index: Int, elements: Collection<TElement>): Boolean {
        if (index < 0 || index > this.size) {
            throw IndexOutOfBoundsException()
        }

        val addCount = elements.size
        val newSize = addCount + this.size

        this.resizeIfNeeded(newSize)
        this.shiftForInsertionIfNeeded(index, addCount)
        this.doInsert(elements, index, addCount)

        this.size += addCount
        ++(super.modCount)

        return !elements.isEmpty()
    }

    private fun resizeIfNeeded(newSize: Int) {
        if (newSize > this.capacity) {
            this.reallocateArray(newSize * 3 / 2)
        }
    }

    private fun shiftForInsertionIfNeeded(index: Int, addCount: Int) {
        for (shiftIndex in this.lastIndex downTo index) {
            this.elementData[shiftIndex + addCount] = this.elementData[shiftIndex]
        }
    }

    private fun doInsert(elements: Collection<TElement>, index: Int, addCount: Int) {
        val indices = index until (index + addCount)

        for ((insertIndex, elem) in indices.zip(elements)) {
            this.elementData[insertIndex] = elem
        }
    }

    override fun removeAt(index: Int): TElement {
        val elem = this[index]

        this.shiftForRemoval(index)

        --(this.size)
        ++(super.modCount)

        return elem
    }

    private fun shiftForRemoval(index: Int) {
        for (removeIndex in index until this.lastIndex) {
            this.elementData[removeIndex] = this.elementData[removeIndex + 1]
        }

        this.elementData[this.lastIndex] = null
    }

    override fun clear() {
        this.elementData = arrayOfNulls(VectorList.DEFAULT_CAPACITY)
        this.size = 0
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
        val newElementData = arrayOfNulls<Any?>(newCapacity)

        for ((index, elem) in this.withIndex()) {
            newElementData[index] = elem
        }

        this.elementData = newElementData
    }
}
