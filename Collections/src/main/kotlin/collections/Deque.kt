package collections

import java.io.Serializable

interface Deque<TElement> : Stack<TElement>, Queue<TElement> {
    override val size: Int

    override fun enqueue(element: TElement) =
        this.pushFront(element)

    override fun push(element: TElement) =
        this.pushBack(element)

    fun pushFront(element: TElement)

    fun pushBack(element: TElement)

    override fun dequeue(): TElement =
        this.popBack()

    override fun pop(): TElement =
        this.popBack()

    fun popFront(): TElement

    fun popBack(): TElement

    override fun peek(): TElement =
        this.back()

    override fun front(): TElement

    fun back(): TElement

    override fun clear()
}

@Suppress("RemoveRedundantQualifierName")
class VectorDeque<TElement>(initialCapacity: Int = VectorDeque.DEFAULT_CAPACITY) : Deque<TElement>, Serializable {
    private companion object {
        @Suppress("ConstPropertyName")
        const val serialVersionUID: Long = 1L

        const val DEFAULT_CAPACITY: Int = 16
    }

    init {
        checkIfNegativeCapacity(initialCapacity)
    }

    private var data: Array<Any?> = arrayOfNulls(initialCapacity)
    private var frontIndex: Int = 0

    override var size: Int = 0
        private set

    override fun pushFront(element: TElement) {
        if (this.data.size == this.size) {
            this.reallocate()
        }

        this.frontIndex = this.actualIndex(-1)
        this.data[this.frontIndex] = element
        ++(this.size)
    }

    override fun pushBack(element: TElement) {
        if (this.data.size == this.size) {
            this.reallocate()
        }

        this.data[this.actualIndex(this.size)] = element
        ++(this.size)
    }

    override fun popFront(): TElement {
        val item = this.front()

        --(this.size)
        this.frontIndex = this.actualIndex(1)

        return item
    }

    override fun popBack(): TElement {
        val item = this.back()

        --(this.size)

        return item
    }

    override fun front(): TElement = this.at(0)

    override fun back(): TElement = this.at(this.size - 1)

    private fun at(index: Int): TElement =
        if (this.isEmpty())
            empty(VectorDeque::class)
        else
            @Suppress("UNCHECKED_CAST")
            this.data[this.actualIndex(index)] as TElement

    private fun actualIndex(index: Int): Int =
        (this.frontIndex + index).mod(this.data.size)

    override fun clear() {
        this.size = 0
        this.frontIndex = 0
    }

    private fun reallocate() {
        val newData = arrayOfNulls<Any>(this.data.size * 3 / 2)

        for (index in 0 until this.size) {
            newData[index] = this.data[(this.frontIndex + index) % this.data.size]
        }

        this.data = newData
        this.frontIndex = 0
    }
}

private class DequeNode<TElement>(
    val item: TElement,
    var prev: DequeNode<TElement>?,
    var next: DequeNode<TElement>?
) : Serializable {
    private companion object {
        @Suppress("ConstPropertyName")
        const val serialVersionUID: Long = 1L
    }
}

class LinkedDeque<TElement> : Deque<TElement>, Serializable {
    private companion object {
        @Suppress("ConstPropertyName")
        const val serialVersionUID: Long = 1L
    }

    private var head: DequeNode<TElement>? = null
    private var tail: DequeNode<TElement>? = null

    override var size: Int = 0
        private set

    override fun pushFront(element: TElement) {
        val newNode = DequeNode(element, null, this.head)

        this.head?.let {
            it.prev = newNode
        } ?: run {
            this.tail = newNode
        }

        this.head = newNode
        ++(this.size)
    }

    override fun pushBack(element: TElement) {
        val newNode = DequeNode(element, this.tail, null)

        this.tail?.let {
            it.next = newNode
        } ?: run {
            this.head = newNode
        }

        this.tail = newNode
        ++(this.size)
    }

    override fun popFront(): TElement =
        this.head?.let {
            this.head = it.next
            this.head?.prev = null
            this.head ?: run { this.tail = null }
            --(this.size)

            return it.item
        } ?: empty(LinkedDeque::class)

    override fun popBack(): TElement =
        this.tail?.let {
            this.tail = it.prev
            this.tail?.next = null
            this.tail ?: run { this.head = null }
            --(this.size)

            return it.item
        } ?: empty(LinkedDeque::class)

    override fun front(): TElement =
        this.head?.let {
            return it.item
        } ?: empty(LinkedDeque::class)

    override fun back(): TElement =
        this.tail?.let {
            return it.item
        } ?: empty(LinkedDeque::class)

    override fun clear() {
        this.head = null
        this.tail = null
        this.size = 0
    }
}

fun Deque<*>.isEmpty(): Boolean =
    0 == this.size

fun <TElement> Deque<TElement>.tryPopFront(): Result<TElement> =
    runCatching{ this.popFront() }

fun <TElement> Deque<TElement>.popFrontOrNull(): TElement? =
    this.tryPopFront().getOrNull()

fun <TElement> Deque<TElement>.tryPopBack(): Result<TElement> =
    runCatching{ this.popBack() }

fun <TElement> Deque<TElement>.popBackOrNull(): TElement? =
    this.tryPopBack().getOrNull()

fun <TElement> Deque<TElement>.tryFront(): Result<TElement> =
    runCatching{ this.front() }

fun <TElement> Deque<TElement>.frontOrNull(): TElement? =
    this.tryFront().getOrNull()

fun <TElement> Deque<TElement>.tryBack(): Result<TElement> =
    runCatching{ this.back() }

fun <TElement> Deque<TElement>.backOrNull(): TElement? =
    this.tryBack().getOrNull()
