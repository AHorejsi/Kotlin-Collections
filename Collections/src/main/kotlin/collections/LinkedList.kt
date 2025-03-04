package collections

import java.io.Serializable

open class LinkedListNode<out TElement> internal constructor(
    open val item: TElement
) : Serializable {
    private companion object {
        @Suppress("ConstPropertyName")
        const val serialVersionUID: Long = 1L
    }

    open val list: LinkedList<TElement>? = null

    open val prev: LinkedListNode<TElement>? = null
    open val next: LinkedListNode<TElement>? = null
}

class MutableLinkedListNode<TElement> internal constructor(
    override var item: TElement
) : LinkedListNode<TElement>(item), Serializable {
    private companion object {
        @Suppress("ConstPropertyName")
        const val serialVersionUID: Long = 1L
    }

    override var list: MutableLinkedList<TElement>? = null
        internal set

    override var prev: MutableLinkedListNode<TElement>? = null
        internal set
    override var next: MutableLinkedListNode<TElement>? = null
        internal set
}

sealed interface LinkedList<out TElement> : Collection<TElement> {
    val head: LinkedListNode<TElement>?

    val tail: LinkedListNode<TElement>?

    fun front(): TElement =
        this.head?.let {
            return it.item
        } ?: empty(LinkedList::class)

    fun back(): TElement =
        this.tail?.let {
            return it.item
        } ?: empty(LinkedList::class)

    fun listIterator(): ListIterator<TElement> =
        this.listIterator(0)

    fun listIterator(index: Int): ListIterator<TElement>

    fun listIterator(node: LinkedListNode<@UnsafeVariance TElement>): ListIterator<TElement>
}

sealed interface MutableLinkedList<TElement> : LinkedList<TElement>, MutableCollection<TElement> {
    override val head: MutableLinkedListNode<TElement>?

    override val tail: MutableLinkedListNode<TElement>?

    fun addFirst(element: TElement)

    fun addFirst(newNode: MutableLinkedListNode<TElement>)

    fun addLast(element: TElement)

    fun addLast(newNode: MutableLinkedListNode<TElement>)

    fun addBefore(node: MutableLinkedListNode<TElement>, element: TElement)

    fun addBefore(node: MutableLinkedListNode<TElement>, newNode: MutableLinkedListNode<TElement>)

    fun addAfter(node: MutableLinkedListNode<TElement>, element: TElement)

    fun addAfter(node: MutableLinkedListNode<TElement>, newNode: MutableLinkedListNode<TElement>)

    fun addToFront(elements: Collection<TElement>)

    fun addToBack(elements: Collection<TElement>)

    fun removeFirst(): MutableLinkedListNode<TElement>

    fun removeLast(): MutableLinkedListNode<TElement>

    fun remove(node: MutableLinkedListNode<TElement>)

    fun removeFromFront(amount: Int): Int

    fun removeFromBack(amount: Int): Int

    override fun iterator(): MutableIterator<TElement> =
        this.listIterator()

    override fun listIterator(): MutableListIterator<TElement> =
        this.listIterator(0)

    override fun listIterator(index: Int): MutableListIterator<TElement>

    override fun listIterator(node: LinkedListNode<TElement>): MutableListIterator<TElement>

    fun listIterator(node: MutableLinkedListNode<TElement>): MutableListIterator<TElement>
}

fun <TElement> LinkedList<TElement>.getNodeAt(index: Int): LinkedListNode<TElement> {
    checkIfIndexIsAccessible(index, this.size)

    val lastIndex = this.size - index

    return if (index < lastIndex)
        leftIndexing(this, index)
    else
        rightIndexing(this, lastIndex)
}

private fun <TElement> leftIndexing(list: LinkedList<TElement>, count: Int): LinkedListNode<TElement> {
    var node = list.head!!

    repeat(count) {
        node = node.next!!
    }

    return node
}

private fun <TElement> rightIndexing(list: LinkedList<TElement>, count: Int): LinkedListNode<TElement> {
    var node = list.tail!!

    repeat(count) {
        node = node.prev!!
    }

    return node
}

fun <TElement> LinkedList<TElement>.tryGetNodeAt(index: Int): Result<LinkedListNode<TElement>> =
    runCatching{ this.getNodeAt(index) }

fun <TElement> MutableLinkedList<TElement>.getNodeAt(index: Int): MutableLinkedListNode<TElement> {
    checkIfIndexIsAccessible(index, this.size)

    val lastIndex = this.size - index

    return if (index < lastIndex)
        leftIndexing(this, index)
    else
        rightIndexing(this, lastIndex)
}

private fun <TElement> leftIndexing(list: MutableLinkedList<TElement>, count: Int): MutableLinkedListNode<TElement> {
    var node = list.head!!

    repeat(count) {
        node = node.next!!
    }

    return node
}

private fun <TElement> rightIndexing(list: MutableLinkedList<TElement>, count: Int): MutableLinkedListNode<TElement> {
    var node = list.tail!!

    repeat(count) {
        node = node.prev!!
    }

    return node
}

fun <TElement> MutableLinkedList<TElement>.tryGetNodeAt(index: Int): Result<MutableLinkedListNode<TElement>> =
    runCatching{ this.getNodeAt(index) }

fun <TElement> MutableLinkedList<TElement>.setItemAt(index: Int, element: TElement): TElement {
    val node = this.getNodeAt(index)

    val old = node.item

    node.item = element

    return old
}

fun <TElement> MutableLinkedList<TElement>.trySetItemAt(index: Int, element: TElement): Result<TElement> =
    runCatching{ this.setItemAt(index, element) }
