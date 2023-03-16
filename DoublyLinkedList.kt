package com.alexh.collects

class DoublyLinkedList<E>() : AbstractLinkedList<E>() {
    constructor(elements: Collection<E>) : this() {
        for (elem in elements) {
            this.addLast(elem)
        }
    }

    constructor(size: Int, value: E) : this() {
        for (count in 0 until size) {
            this.add(value)
        }
    }

    override fun addFirst(element: E) {
        val newNode = MutableLinkedListNode(element, this)
        this.addFirst(newNode)
    }

    override fun addLast(element: E) {
        val newNode = MutableLinkedListNode(element, this)
        this.addLast(newNode)
    }

    override fun addBefore(node: MutableLinkedListNode<E>, element: E) {
        val newNode = MutableLinkedListNode(element, this)
        this.addBefore(node, newNode)
    }

    override fun addAfter(node: MutableLinkedListNode<E>, element: E) {
        val newNode = MutableLinkedListNode(element, this)
        this.addAfter(node, newNode)
    }

    override fun remove(node: MutableLinkedListNode<E>) {
        if (node.list !== this) {
            throw IllegalArgumentException("Node does not belong to this list")
        }

        when (node) {
            this.first -> {
                this.first = this.first!!.next
                this.first!!.previous = null
            }
            this.last -> {
                this.last = this.last!!.previous
                this.last!!.next = null
            }
            else -> {
                node.next = node.next!!.next
                node.next!!.previous = node
            }
        }

        node.list = null

        --(this.size)
    }
}