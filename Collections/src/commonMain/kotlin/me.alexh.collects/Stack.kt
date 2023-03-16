package me.alexh.collects

class Stack<TElement>(
    initialCapacity: Int = Stack.DEFAULT_CAPACITY
) {
    private companion object {
        const val DEFAULT_CAPACITY: Int = 16
        val EMPTY_EXCEPTION: RuntimeException = NoSuchElementException()
    }

    private var elementData: Array<Any?> = arrayOfNulls(initialCapacity)
    var size: Int = 0
        private set

    val capacity: Int
        get() = this.elementData.size

    fun isEmpty(): Boolean = 0 == this.size

    fun push(element: TElement) {
        this.resizeIfNeeded()

        this.elementData[this.size] = element

        ++(this.size)
    }

    private fun resizeIfNeeded() {
        if (this.size == this.capacity) {
            this.reallocateArray(this.capacity * 3 / 2)
        }
    }

    fun pop(): TElement = this.tryPop().getOrThrow()

    fun popOrNull(): TElement? = this.tryPop().getOrNull()

    @Suppress("UNCHECKED_CAST")
    fun tryPop(): Result<TElement> {
        if (this.isEmpty()) {
            return Result.failure(Stack.EMPTY_EXCEPTION)
        }

        val endIndex = this.elementData.lastIndex

        val elem = this.elementData[endIndex] as TElement
        this.elementData[endIndex] = null

        --(this.size)

        return Result.success(elem)
    }

    fun peek(): TElement = this.tryPeek().getOrThrow()

    fun peekOrNull(): TElement? = this.tryPeek().getOrNull()

    @Suppress("UNCHECKED_CAST")
    fun tryPeek(): Result<TElement> =
        if (this.isEmpty())
            Result.failure(Stack.EMPTY_EXCEPTION)
        else
            Result.success(this.elementData[this.elementData.lastIndex] as TElement)

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

        for (index in 0 until this.size) {
            newData[index] = this.elementData[index]
        }

        this.elementData = newData
    }
}
