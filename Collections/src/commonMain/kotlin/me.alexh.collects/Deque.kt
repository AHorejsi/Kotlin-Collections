package me.alexh.collects

class Deque<TElement>(
    initialCapacity: Int = Deque.DEFAULT_CAPACITY
) {
    private companion object {
        const val DEFAULT_CAPACITY: Int = 16
    }

    private var elementData: Array<Any?> = arrayOfNulls(initialCapacity)
    private var frontIndex: Int = 0
    private var endIndex: Int = 0

    val size: Int
        get() =
            if (this.frontIndex <= this.endIndex)
                this.endIndex - this.frontIndex + 1
            else
                this.capacity - (this.frontIndex - this.endIndex + 1)

    val capacity: Int
        get() = this.elementData.size

    fun isEmpty(): Boolean = this.frontIndex == this.endIndex

    fun addFirst(element: TElement) {
        this.resizeIfNeeded()

        this.frontIndex = this.index(this.frontIndex - 1)

        this.elementData[this.frontIndex] = element
    }

    fun addLast(element: TElement) {
        this.resizeIfNeeded()

        this.endIndex = this.index(this.endIndex + 1)

        this.elementData[this.endIndex] = element
    }

    private fun resizeIfNeeded() {
        if (this.size == this.capacity) {
            this.reallocateArray(this.capacity * 3 / 2)
        }
    }

    fun removeFirst(): TElement = this.tryRemoveFirst().getOrThrow()

    fun removeFirstOrNull(): TElement? = this.tryRemoveFirst().getOrNull()

    @Suppress("UNCHECKED_CAST")
    fun tryRemoveFirst(): Result<TElement> {
        if (this.isEmpty()) {
            return Result.failure(NoSuchElementException())
        }

        val elem = this.elementData[this.frontIndex] as TElement
        this.elementData[this.frontIndex] = null

        this.frontIndex = this.index(this.frontIndex + 1)

        return Result.success(elem)
    }

    fun removeLast(): TElement = this.tryRemoveLast().getOrThrow()

    fun removeLastOrNull(): TElement? = this.tryRemoveLast().getOrNull()

    @Suppress("UNCHECKED_CAST")
    fun tryRemoveLast(): Result<TElement> {
        if (this.isEmpty()) {
            return Result.failure(NoSuchElementException())
        }

        val elem = this.elementData[this.endIndex] as TElement
        this.elementData[this.endIndex] = null

        this.endIndex = this.index(this.endIndex - 1)

        return Result.success(elem)
    }

    private fun index(newIndex: Int): Int = (newIndex + this.capacity) % this.capacity

    fun first(): TElement = this.tryFirst().getOrThrow()

    fun firstOrNull(): TElement? = this.tryFirst().getOrNull()

    @Suppress("UNCHECKED_CAST")
    fun tryFirst(): Result<TElement> =
        if (this.isEmpty())
            Result.failure(NoSuchElementException())
        else
            Result.success(this.elementData[this.frontIndex] as TElement)

    fun last(): TElement = this.tryLast().getOrThrow()


    fun lastOrNull(): TElement? = this.tryLast().getOrNull()

    @Suppress("UNCHECKED_CAST")
    fun tryLast(): Result<TElement> =
        if (this.isEmpty())
            Result.failure(NoSuchElementException())
        else
            Result.success(this.elementData[this.endIndex] as TElement)

    fun ensureCapacity(minCapacity: Int) {
        if (minCapacity > this.capacity) {
            this.reallocateArray(minCapacity)
        }
    }

    fun trimToSize() {
        val size = this.size

        if (size != this.capacity) {
            this.reallocateArray(size)
        }
    }

    private fun reallocateArray(newCapacity: Int) {
        val newData = arrayOfNulls<Any?>(newCapacity)
        var indexOfNew = 0
        var indexOfOld = this.frontIndex

        while (indexOfOld != this.endIndex) {
            newData[indexOfNew] = this.elementData[indexOfOld]

            ++indexOfNew
            indexOfOld = this.index(indexOfOld + 1)
        }

        this.frontIndex = 0
        this.endIndex = this.elementData.lastIndex
        this.elementData = newData
    }
}
