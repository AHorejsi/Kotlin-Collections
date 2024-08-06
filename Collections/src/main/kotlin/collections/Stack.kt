package collections

import java.io.Serializable

interface Stack<TElement> {
    val size: Int

    fun push(element: TElement)

    fun pop(): TElement

    fun peek(): TElement

    fun clear()
}

@Suppress("RemoveRedundantQualifierName")
class VectorStack<TElement>(initialCapacity: Int = VectorStack.DEFAULT_CAPACITY) : Stack<TElement>, Serializable {
    private companion object {
        @Suppress("ConstPropertyName")
        const val serialVersionUID: Long = 1L

        const val DEFAULT_CAPACITY: Int = 16
    }

    init {
        require(initialCapacity >= 0) { "Initial capacity must be non-negative but was $initialCapacity" }
    }

    private var data: Array<Any?> = arrayOfNulls(initialCapacity)

    override var size: Int = 0
        private set

    override fun push(element: TElement) {
        if (this.data.size == this.size) {
            this.reallocate(this.size * 3 / 2)
        }

        this.data[this.size] = element
        ++(this.size)
    }

    override fun pop(): TElement {
        val item = this.peek()

        --(this.size)

        return item
    }

    override fun peek(): TElement =
        if (this.isEmpty())
            throw NoSuchElementException()
        else
            @Suppress("UNCHECKED_CAST")
            this.data[this.size - 1] as TElement

    override fun clear() {
        this.size = 0
    }

    private fun reallocate(newCapacity: Int) {
        val newData = arrayOfNulls<Any>(newCapacity)

        for (index in 0 until this.size) {
            newData[index] = this.data[index]
        }

        this.data = newData
    }
}

private class StackNode<TElement>(
    val item: TElement,
    var next: StackNode<TElement>?
) : Serializable {
    private companion object {
        @Suppress("ConstPropertyName")
        const val serialVersionUID: Long = 1L
    }
}

class LinkedStack<TElement> : Stack<TElement>, Serializable {
    private companion object {
        @Suppress("ConstPropertyName")
        const val serialVersionUID: Long = 1L
    }

    private var head: StackNode<TElement>? = null

    override var size: Int = 0
        private set

    override fun push(element: TElement) {
        this.head = StackNode(element, this.head)
        ++(this.size)
    }

    override fun pop(): TElement =
        this.head?.let {
            this.head = it.next
            --(this.size)

            return it.item
        } ?: throw NoSuchElementException()

    override fun peek(): TElement = this.head?.item ?: throw NoSuchElementException()

    override fun clear() {
        this.head = null
        this.size = 0
    }
}

fun Stack<*>.isEmpty(): Boolean = 0 == this.size

fun <TElement> Stack<TElement>.tryPop(): Result<TElement> = runCatching { this.pop() }

fun <TElement> Stack<TElement>.tryPeek(): Result<TElement> = runCatching { this.peek() }
