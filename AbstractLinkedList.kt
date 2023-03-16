package com.alexh.collects

abstract class AbstractLinkedList<E> : MutableLinkedList<E> {
    protected var modifyCount = 0
    override var size: Int = 0
        protected set
    override var first: MutableLinkedListNode<E>? = null
        protected set
    override var last: MutableLinkedListNode<E>? = null
        protected set

    override fun isEmpty(): Boolean = 0 == this.size

    override fun add(element: E): Boolean {
        this.addLast(element)

        return true
    }

    override fun addAll(elements: Collection<E>): Boolean {
        for (elem in elements) {
            this.add(elem)
        }

        return true
    }

    override fun addFirst(other: MutableLinkedListNode<E>) {
        this.first?.let {
            it.previous = other
            other.next = it
            this.first = it.previous
        } ?: run {
            this.first = other
            this.last = this.first
        }

        other.list = this
        ++(this.size)
        ++(this.modifyCount)
    }

    override fun addLast(other: MutableLinkedListNode<E>) {
        this.last?.let {
            it.next = other
            other.previous = it
            this.last = it.next
        } ?: run {
            this.last = other
            this.first = this.last
        }

        other.list = this
        ++(this.size)
        ++(this.modifyCount)
    }

    override fun addBefore(node: MutableLinkedListNode<E>, other: MutableLinkedListNode<E>) {
        if (node.list !== this) {
            throw IllegalArgumentException()
        }

        if (node == this.first) {
            this.addFirst(other)
        }
        else {
            other.next = node
            other.previous = node.previous
            node.previous!!.next = other
            node.previous = other

            other.list = this
            ++(this.modifyCount)
            ++(this.size)
        }
    }

    override fun addAfter(node: MutableLinkedListNode<E>, other: MutableLinkedListNode<E>) {
        if (node.list !== this) {
            throw IllegalArgumentException()
        }

        if (node == this.last) {
            this.addLast(other)
        }
        else {
            other.next = node.next
            other.previous = node
            node.next!!.previous = other
            node.next = other

            other.list = this
            ++(this.modifyCount)
            ++(this.size)
        }
    }

    override fun contains(element: E): Boolean {
        for (elem in this) {
            if (elem == element) {
                return true
            }
        }

        return false
    }

    override fun containsAll(elements: Collection<E>): Boolean {
        for (elem in elements) {
            if (!this.contains(elem)) {
                return false
            }
        }

        return true
    }

    override fun remove(element: E): Boolean {
        val singletonList = listOf(element)
        return this.removeAll(singletonList)
    }

    override fun removeFirst() {
        this.first?.let {
            this.remove(it)
        } ?: run {
            throw NoSuchElementException()
        }
    }

    override fun removeLast() {
        this.last?.let {
            this.remove(it)
        } ?: run {
            throw NoSuchElementException()
        }
    }

    override fun removeAll(elements: Collection<E>): Boolean {
        var change = false
        var node = this.first

        while (node !== null) {
            if (elements.contains(node.value)) {
                val nextNode = node.next

                this.remove(node)
                change = true

                node = nextNode
            }
            else {
                node = node.next
            }
        }

        return change
    }

    override fun retainAll(elements: Collection<E>): Boolean {
        var change = false
        var node = this.first

        while (node !== null) {
            if (!elements.contains(node.value)) {
                this.remove(node)
                change = true
            }

            node = node.next
        }

        return change
    }

    override fun clear() {
        this.first = null
        this.last = null
        this.size = 0
    }

    override fun iterator(): MutableIterator<E> = object : MutableIterator<E> {
        private var prev: MutableLinkedListNode<E>? = null
        private var node: MutableLinkedListNode<E>? = this@AbstractLinkedList.first
        private var state: IteratorState = IteratorState.INITIALIZED
        private var modifyCount: Int = this@AbstractLinkedList.modifyCount

        private fun checkForModification() {
            if (this.modifyCount != this@AbstractLinkedList.modifyCount) {
                throw ConcurrentModificationException()
            }
        }

        override fun hasNext(): Boolean {
            this.checkForModification()

            return null !== this.node
        }

        override fun next(): E {
            this.checkForModification()

            if (!this.hasNext()) {
                throw NoSuchElementException()
            }

            val elem = this.node!!.value

            this.prev = this.node
            this.node = this.node!!.next
            this.state = IteratorState.CALLED_NEXT

            return elem
        }

        override fun remove() {
            this.checkForModification()

            if (IteratorState.CALLED_NEXT != this.state) {
                throw NoSuchElementException()
            }

            this@AbstractLinkedList.remove(this.prev!!)
            this.state = IteratorState.CALLED_REMOVE
            this.prev = null
            ++(this.modifyCount)
        }
    }
}