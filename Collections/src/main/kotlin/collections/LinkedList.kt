package collections

import java.io.Serializable

open class LinkedListNode<out TElement>(
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

class MutableLinkedListNode<TElement>(
    override var item: TElement,
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

    fun listIterator(): ListIterator<TElement> = this.listIterator(0)

    fun listIterator(index: Int): ListIterator<TElement>

    fun listIterator(node: LinkedListNode<@UnsafeVariance TElement>): ListIterator<TElement>
}

sealed interface MutableLinkedList<TElement> : LinkedList<TElement>, Deque<TElement>, MutableCollection<TElement> {
    override val head: MutableLinkedListNode<TElement>?

    override val tail: MutableLinkedListNode<TElement>?

    override fun front(): TElement = this.head?.item ?: throw NoSuchElementException()

    override fun back(): TElement = this.tail?.item ?: throw NoSuchElementException()

    fun addFirst(element: TElement)

    override fun pushFront(element: TElement) = this.addFirst(element)

    fun addFirst(newNode: MutableLinkedListNode<TElement>)

    fun addLast(element: TElement)

    override fun pushBack(element: TElement) = this.addLast(element)

    fun addLast(newNode: MutableLinkedListNode<TElement>)

    fun addBefore(node: MutableLinkedListNode<TElement>, element: TElement)

    fun addBefore(node: MutableLinkedListNode<TElement>, newNode: MutableLinkedListNode<TElement>)

    fun addAfter(node: MutableLinkedListNode<TElement>, element: TElement)

    fun addAfter(node: MutableLinkedListNode<TElement>, newNode: MutableLinkedListNode<TElement>)

    fun addToFront(elements: Collection<TElement>)

    fun addToBack(elements: Collection<TElement>)

    fun removeFirst(): MutableLinkedListNode<TElement>

    override fun popFront(): TElement = this.removeFirst().item

    fun removeLast(): MutableLinkedListNode<TElement>

    override fun popBack(): TElement = this.removeLast().item

    fun remove(node: MutableLinkedListNode<TElement>)

    fun removeFromFront(amount: Int): Int

    fun removeFromBack(amount: Int): Int

    override fun iterator(): MutableIterator<TElement> = this.listIterator()

    override fun listIterator(): MutableListIterator<TElement> = this.listIterator(0)

    override fun listIterator(index: Int): MutableListIterator<TElement>

    override fun listIterator(node: LinkedListNode<TElement>): MutableListIterator<TElement>

    fun listIterator(node: MutableLinkedListNode<TElement>): MutableListIterator<TElement>
}

fun <TElement> Iterable<TElement>.toMutableLinkedList(): MutableLinkedList<TElement> = this.toBidirList()

fun <TElement> Sequence<TElement>.toMutableLinkedList(): MutableLinkedList<TElement> = this.toBidirList()

fun <TElement> Collection<TElement>.toMutableLinkedList(): MutableLinkedList<TElement> = this.toBidirList()

fun <TElement> Array<out TElement>.toMutableLinkedList(): MutableLinkedList<TElement> = this.toBidirList()

fun <TElement> LinkedList<TElement>.getNodeAt(index: Int): LinkedListNode<TElement> {
    if (index < 0 || index >= this.size) {
        throw IndexOutOfBoundsException()
    }

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
    runCatching { this.getNodeAt(index) }

fun <TElement> MutableLinkedList<TElement>.getNodeAt(index: Int): MutableLinkedListNode<TElement> {
    if (index < 0 || index >= this.size) {
        throw IndexOutOfBoundsException()
    }

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
    runCatching { this.getNodeAt(index) }

fun <TElement> MutableLinkedList<TElement>.setItemAt(index: Int, element: TElement): TElement {
    val node = this.getNodeAt(index)

    val old = node.item

    node.item = element

    return old
}

fun <TElement> MutableLinkedList<TElement>.trySetItemAt(index: Int, element: TElement): Result<TElement> =
    runCatching { this.setItemAt(index, element) }

fun <TElement> LinkedList<TElement>.find(element: @UnsafeVariance TElement): LinkedListNode<TElement>? =
    this.find { it == element }

inline fun <TElement> LinkedList<TElement>.find(predicate: (TElement) -> Boolean): LinkedListNode<TElement>? {
    var node = this.head

    while (null !== node) {
        if (predicate(node.item)) {
            break
        }

        node = node.next
    }

    return node
}

fun <TElement> MutableLinkedList<TElement>.find(element: @UnsafeVariance TElement): MutableLinkedListNode<TElement>? {
    var node = this.head

    while (null !== node) {
        if (element == node.item) {
            break
        }

        node = node.next
    }

    return node
}

inline fun <TElement> MutableLinkedList<TElement>.find(predicate: (TElement) -> Boolean): MutableLinkedListNode<TElement>? {
    var node = this.head

    while (null !== node) {
        if (predicate(node.item)) {
            break
        }

        node = node.next
    }

    return node
}

fun <TElement> LinkedList<TElement>.findLast(element: @UnsafeVariance TElement): LinkedListNode<TElement>? =
    this.findLast { it == element }

inline fun <TElement> LinkedList<TElement>.findLast(predicate: (TElement) -> Boolean): LinkedListNode<TElement>? {
    var node = this.tail

    while (null !== node) {
        if (predicate(node.item)) {
            break
        }

        node = node.prev
    }

    return node
}

fun <TElement> MutableLinkedList<TElement>.findLast(element: @UnsafeVariance TElement): MutableLinkedListNode<TElement>? {
    var node = this.tail

    while (null !== node) {
        if (element == node.item) {
            break
        }

        node = node.prev
    }

    return node
}

inline fun <TElement> MutableLinkedList<TElement>.findLast(predicate: (TElement) -> Boolean): MutableLinkedListNode<TElement>? {
    var node = this.tail

    while (null !== node) {
        if (predicate(node.item)) {
            break
        }

        node = node.prev
    }

    return node
}
