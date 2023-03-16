package com.alexh.collects

interface Stack<E> {
    val size: Int

    fun isEmpty(): Boolean

    val top: E

    fun push(element: E)

    fun pop(): E
}

class ArrayStack<E>(
    initialCapacity: Int = ArrayStack.DEFAULT_CAPACITY
) : Stack<E> {
    private companion object {
        const val DEFAULT_CAPACITY = 16
    }

    private var data: Array<Any?> = arrayOfNulls(initialCapacity)
    override var size: Int = 0
        private set

    override fun isEmpty(): Boolean = 0 == this.size

    override val top: E
        get() =
            if (this.isEmpty()) {
                throw NoSuchElementException()
            }
            else {
                this.data[this.size - 1] as E
            }


    override fun push(element: E) {
        if (this.data.size == this.size) {
            this.expand()
        }

        this.data[this.size] = element
        ++(this.size)
    }

    private fun expand() {
        val newData = arrayOfNulls<Any?>(this.size * 2)

        for ((index, elem) in this.data.withIndex()) {
            newData[index] = elem
        }

        this.data = newData
    }

    override fun pop(): E {
        val elem = this.top

        this.data[this.size - 1] = null
        --(this.size)

        return elem
    }
}

class LinkedStack<E> : Stack<E> {
    private var head: StackNode<E>? = null
    override var size: Int = 0
        private set

    override fun isEmpty(): Boolean = 0 == this.size

    override val top: E
        get() = this.head?.elem ?: throw NoSuchElementException()

    override fun push(element: E) {
        val newNode = StackNode(element, this.head)
        this.head = newNode

        ++(this.size)
    }

    override fun pop(): E {
        val elem = this.top

        this.head = this.head!!.next
        --(this.size)

        return elem
    }

    private data class StackNode<T>(
        var elem: T,
        var next: StackNode<T>?
    )
}