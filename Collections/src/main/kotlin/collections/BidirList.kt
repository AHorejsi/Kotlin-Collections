package collections

import java.io.Serializable

class BidirList<TElement>() : AbstractLinkedList<TElement>(), Serializable {
    private companion object {
        @Suppress("ConstPropertyName")
        const val serialVersionUID: Long = 1L
    }

    constructor(elements: Sequence<TElement>) : this(elements.asIterable())

    constructor(elements: Collection<TElement>) : this() {
        this.addAll(elements)
    }

    constructor(elements: Iterable<TElement>) : this() {
        this.addAll(elements)
    }

    constructor(vararg elements: TElement) : this() {
        this.addAll(elements)
    }

    override fun addFirst(element: TElement) {
        val newNode = MutableLinkedListNode(element)

        super.addFirst(newNode)
    }

    override fun addLast(element: TElement) {
        val newNode = MutableLinkedListNode(element)

        super.addLast(newNode)
    }

    override fun addBefore(node: MutableLinkedListNode<TElement>, element: TElement) {
        val newNode = MutableLinkedListNode(element)

        super.addBefore(node, newNode)
    }

    override fun addAfter(node: MutableLinkedListNode<TElement>, element: TElement) {
        val newNode = MutableLinkedListNode(element)

        super.addAfter(node, newNode)
    }
}

fun <TElement> bidirListOf(): BidirList<TElement> = BidirList()

fun <TElement> bidirListOf(vararg elements: TElement): BidirList<TElement> = elements.toBidirList()

fun <TElement> Iterable<TElement>.toBidirList(): BidirList<TElement> = BidirList(this)

fun <TElement> Sequence<TElement>.toBidirList(): BidirList<TElement> = BidirList(this)

fun <TElement> Collection<TElement>.toBidirList(): BidirList<TElement> = BidirList(this)

fun <TElement> Array<out TElement>.toBidirList(): BidirList<TElement> = BidirList(*this)
