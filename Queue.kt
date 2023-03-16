package com.alexh.collects

interface Queue<E> {
    val size: Int

    fun isEmpty(): Boolean

    val front: E

    fun enqueue(element: E)

    fun dequeue(): E
}

class ArrayQueue<E>(
    initialCapacity: Int = ArrayQueue.DEFAULT_CAPACITY
) : Queue<E> {
    private companion object {
        const val DEFAULT_CAPACITY = 16
    }

    private var data: Array<Any?> = arrayOfNulls(initialCapacity)
    private var frontIndex: Int = 0
    override var size: Int = 0
        private set

    override fun isEmpty(): Boolean = 0 == this.size

    override val front: E
        get() =
            if (this.isEmpty()) {
                throw NoSuchElementException()
            }
            else {
                this.data[this.frontIndex] as E
            }

    override fun enqueue(element: E) {
        if (this.data.size == this.size) {
            this.expand()
        }

        val insertIndex = (this.frontIndex + this.size) % this.data.size
        this.data[insertIndex] = element

        ++(this.size)
        ++(this.frontIndex)
    }

    private fun expand() {
        val newData = arrayOfNulls<Any?>(this.size * 2)
        var index = 0

        while (!this.isEmpty()) {
            newData[index] = this.dequeue()
            ++index
        }

        this.frontIndex = 0
        this.size = this.data.size
        this.data = newData
    }

    override fun dequeue(): E {
        val elem = this.front

        this.data[this.frontIndex] = null
        ++(this.frontIndex)
        --(this.size)

        return elem
    }
}

class LinkedQueue<E> : Queue<E> {
    private var head: QueueNode<E>? = null
    private var tail: QueueNode<E>? = null
    override var size: Int = 0
        private set

    override fun isEmpty(): Boolean = 0 == this.size

    override val front: E
        get() = this.head?.elem ?: throw NoSuchElementException()

    override fun enqueue(element: E) {
        val newNode = QueueNode(element)

        this.tail?.let {
            it.next = newNode
            this.tail = it.next
        } ?: run {
            this.head = newNode
            this.tail = this.head
        }

        ++(this.size)
    }

    override fun dequeue(): E {
        val elem = this.front

        this.head = this.head!!.next
        --(this.size)

        return elem
    }

    private data class QueueNode<E>(
        var elem: E,
        var next: QueueNode<E>? = null
    )
}
