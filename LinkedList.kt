package com.alexh.collects

interface LinkedListNode<E> {
    val value: E

    val next: LinkedListNode<E>?

    val previous: LinkedListNode<E>?

    val list: LinkedList<E>?
}

class MutableLinkedListNode<E>(
    override var value: E,
    list: MutableLinkedList<E>? = null
) : LinkedListNode<E>
{
    override var list: MutableLinkedList<E>? = list
        internal set
    override var next: MutableLinkedListNode<E>? = null
        internal set
    override var previous: MutableLinkedListNode<E>? = null
        internal set
}

interface LinkedList<E> : Collection<E> {
    val first: LinkedListNode<E>?

    val last: LinkedListNode<E>?
}

interface MutableLinkedList<E> : LinkedList<E>, MutableCollection<E> {
    override val first: MutableLinkedListNode<E>?

    override val last: MutableLinkedListNode<E>?

    fun addFirst(element: E)

    fun addFirst(other: MutableLinkedListNode<E>)

    fun addLast(element: E)

    fun addLast(other: MutableLinkedListNode<E>)

    fun addBefore(node: MutableLinkedListNode<E>, element: E)

    fun addBefore(node: MutableLinkedListNode<E>, other: MutableLinkedListNode<E>)

    fun addAfter(node: MutableLinkedListNode<E>, element: E)

    fun addAfter(node: MutableLinkedListNode<E>, other: MutableLinkedListNode<E>)

    fun removeFirst()

    fun removeLast()

    fun remove(node: MutableLinkedListNode<E>)
}
