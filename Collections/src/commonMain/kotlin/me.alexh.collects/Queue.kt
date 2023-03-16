package me.alexh.collects

class Queue<TElement>(
    initialCapacity: Int = Queue.DEFAULT_CAPACITY
) {
    private companion object {
        const val DEFAULT_CAPACITY: Int = 16
        val EMPTY_EXCEPTION: RuntimeException = NoSuchElementException()
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

    fun enqueue(element: TElement) {
        this.resizeIfNeeded()

        this.endIndex = this.indexToRight(this.endIndex)

        this.elementData[this.endIndex] = element
    }

    private fun resizeIfNeeded() {
        if (this.size == this.capacity) {
            this.reallocateArray(this.capacity * 3 / 2)
        }
    }

    fun dequeue(): TElement = this.tryDequeue().getOrThrow()

    fun dequeueOrNull(): TElement? = this.tryDequeue().getOrNull()

    @Suppress("UNCHECKED_CAST")
    fun tryDequeue(): Result<TElement> {
        if (this.isEmpty()) {
            return Result.failure(Queue.EMPTY_EXCEPTION)
        }

        val elem = this.elementData[this.frontIndex] as TElement
        this.elementData[this.frontIndex] = null

        this.frontIndex = this.indexToRight(this.frontIndex)

        return Result.success(elem)
    }

    fun front(): TElement = this.tryFront().getOrThrow()

    fun frontOrNull(): TElement? = this.tryFront().getOrNull()

    @Suppress("UNCHECKED_CAST")
    fun tryFront(): Result<TElement> =
        if (this.isEmpty())
            Result.failure(Queue.EMPTY_EXCEPTION)
        else
            Result.success(this.elementData[this.frontIndex] as TElement)

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
        var indexOfOld = this.frontIndex
        var indexOfNew = 0

        while (indexOfOld != this.endIndex) {
            newData[indexOfNew] = this.elementData[indexOfOld]

            ++indexOfNew
            indexOfOld = this.indexToRight(indexOfOld)
        }

        this.frontIndex = 0
        this.endIndex = this.size - 1
        this.elementData = newData
    }

    private fun indexToRight(index: Int): Int = (index + 1) % this.capacity
}