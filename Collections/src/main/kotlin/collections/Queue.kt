package collections

import java.io.Serializable

interface Queue<TElement> {
    val size: Int

    fun enqueue(element: TElement)

    fun dequeue(): TElement

    fun front(): TElement

    fun clear()
}

@Suppress("RemoveRedundantQualifierName")
class VectorQueue<TElement>(initialCapacity: Int = VectorQueue.DEFAULT_CAPACITY) : Queue<TElement>, Serializable {
    private companion object {
        @Suppress("ConstPropertyName")
        const val serialVersionUID: Long = 1L

        const val DEFAULT_CAPACITY: Int = 16
    }

    init {
        require(initialCapacity >= 0)
    }

    private var data: Array<Any?> = arrayOfNulls(initialCapacity)
    private var frontIndex: Int = 0

    override var size: Int = 0
        private set

    override fun enqueue(element: TElement) {
        if (this.data.size == this.size) {
            this.reallocate(this.size * 3 / 2)
        }

        this.data[(this.frontIndex + this.size) % this.data.size] = element
        ++(this.size)
    }

    override fun dequeue(): TElement {
        val item = this.front()

        this.frontIndex = (this.frontIndex + 1) % this.data.size
        --(this.size)

        return item
    }

    override fun front(): TElement =
        if (this.isEmpty())
            throw NoSuchElementException()
        else
            @Suppress("UNCHECKED_CAST")
            this.data[this.frontIndex] as TElement

    override fun clear() {
        this.size = 0
        this.frontIndex = 0
    }

    private fun reallocate(newCapacity: Int) {
        val newData = arrayOfNulls<Any>(newCapacity)

        for (index in 0 until this.size) {
            newData[index] = this.data[(this.frontIndex + index) % this.data.size]
        }

        this.data = newData
        this.frontIndex = 0
    }
}

private class QueueNode<TElement>(
    val item: TElement,
    var next: QueueNode<TElement>? = null
) : Serializable {
    private companion object {
        @Suppress("ConstPropertyName")
        const val serialVersionUID: Long = 1L
    }
}

class LinkedQueue<TElement> : Queue<TElement>, Serializable {
    private companion object {
        @Suppress("ConstPropertyName")
        const val serialVersionUID: Long = 1L
    }

    private var head: QueueNode<TElement>? = null
    private var tail: QueueNode<TElement>? = null

    override var size: Int = 0
        private set

    override fun enqueue(element: TElement) {
        val newNode = QueueNode(element)

        this.tail?.let {
            it.next = newNode
        } ?: run {
            this.head = newNode
        }

        this.tail = newNode
        ++(this.size)
    }

    override fun dequeue(): TElement =
        this.head?.let {
            this.head = it.next
            this.head ?: run { this.tail = null }
            --(this.size)

            return it.item
        } ?: throw NoSuchElementException()

    override fun front(): TElement = this.head?.item ?: throw NoSuchElementException()

    override fun clear() {
        this.head = null
        this.tail = null
        this.size = 0
    }
}

fun Queue<*>.isEmpty(): Boolean = 0 == this.size

fun <TElement> Queue<TElement>.tryDequeue(): Result<TElement> = runCatching { this.dequeue() }

fun <TElement> Queue<TElement>.tryFront(): Result<TElement> = runCatching { this.front() }
