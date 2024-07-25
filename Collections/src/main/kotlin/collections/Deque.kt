package collections

import java.io.Serializable

interface Deque<TElement> : Stack<TElement>, Queue<TElement> {
    override val size: Int

    override fun enqueue(element: TElement) = this.pushFront(element)

    override fun push(element: TElement) = this.pushBack(element)

    fun pushFront(element: TElement)

    fun pushBack(element: TElement)

    override fun dequeue(): TElement = this.popBack()

    override fun pop(): TElement = this.popBack()

    fun popFront(): TElement

    fun popBack(): TElement

    override fun peek(): TElement = this.back()

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
        require(initialCapacity >= 0)
    }

    private var data: Array<Any?> = arrayOfNulls(initialCapacity)
    private var frontIndex: Int = 0

    override var size: Int = 0
        private set

    override fun pushFront(element: TElement) {
        if (this.data.size == this.size) {
            this.reallocate()
        }

        this.frontIndex = (this.frontIndex - 1) % this.data.size
        this.data[this.frontIndex] = element
        ++(this.size)
    }

    override fun pushBack(element: TElement) {
        if (this.data.size == this.size) {
            this.reallocate()
        }

        this.data[(this.frontIndex + this.size) % this.data.size] = element
        ++(this.size)
    }

    override fun popFront(): TElement {
        val item = this.front()

        --(this.size)
        this.frontIndex = (this.frontIndex + 1) % this.data.size

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
            throw NoSuchElementException()
        else
            @Suppress("UNCHECKED_CAST")
            this.data[(this.frontIndex + index) % this.data.size] as TElement

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
        } ?: throw NoSuchElementException()

    override fun popBack(): TElement =
        this.tail?.let {
            this.tail = it.prev
            this.tail?.next = null
            this.tail ?: run { this.head = null }
            --(this.size)

            return it.item
        } ?: throw NoSuchElementException()

    override fun front(): TElement = this.at(this.head)

    override fun back(): TElement = this.at(this.tail)

    private fun at(node: DequeNode<TElement>?) = node?.item ?: throw NoSuchElementException()

    override fun clear() {
        this.head = null
        this.tail = null
        this.size = 0
    }
}

class UnrolledDeque<TElement>(private val nodeSize: Int) : Deque<TElement>, Serializable {
    private companion object {
        @Suppress("ConstPropertyName")
        const val serialVersionUID: Long = 1L
    }

    init {
        require(this.nodeSize > 0)
    }

    private val base: LinkedDeque<VectorDeque<TElement>> = LinkedDeque()

    override var size: Int = 0
        private set

    override fun pushFront(element: TElement) {
        var array = this.base.front()

        if (this.nodeSize == array.size) {
            array = VectorDeque(this.nodeSize)

            this.base.pushFront(array)
        }

        array.pushFront(element)

        ++(this.size)
    }

    override fun pushBack(element: TElement) {
        var array = this.base.back()

        if (this.nodeSize == array.size) {
            array = VectorDeque(this.nodeSize)

            this.base.pushBack(array)
        }

        array.pushBack(element)

        ++(this.size)
    }

    override fun popFront(): TElement {
        val array = this.base.front()
        val item = array.popFront()

        if (array.isEmpty()) {
            this.base.popFront()
        }

        --(this.size)

        return item
    }

    override fun popBack(): TElement {
        val array = this.base.back()
        val item = array.popBack()

        if (array.isEmpty()) {
            this.base.popBack()
        }

        --(this.size)

        return item
    }

    override fun front(): TElement = this.base.front().front()

    override fun back(): TElement = this.base.back().back()

    override fun clear() {
        this.base.clear()
        this.size = 0
    }
}

fun Deque<*>.isEmpty(): Boolean = 0 == this.size

fun <TElement> Deque<TElement>.tryPopFront(): Result<TElement> = runCatching { this.popFront() }

fun <TElement> Deque<TElement>.tryPopBack(): Result<TElement> = runCatching { this.popBack() }

fun <TElement> Deque<TElement>.tryFront(): Result<TElement> = runCatching { this.front() }

fun <TElement> Deque<TElement>.tryBack(): Result<TElement> = runCatching { this.back() }
