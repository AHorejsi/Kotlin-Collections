package collections

import java.io.Serializable

class BidirList<TElement> : AbstractLinkedList<TElement>(), Serializable {
    private companion object {
        @Suppress("ConstPropertyName")
        const val serialVersionUID: Long = 1L
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

fun <TElement> bidirListOf(): BidirList<TElement> =
    BidirList()

fun <TElement> bidirListOf(vararg elements: TElement): BidirList<TElement> =
    elements.toBidirList()

fun <TElement> Iterable<TElement>.toBidirList(): BidirList<TElement> {
    val list = BidirList<TElement>()

    list.addAll(this)

    return list
}

fun <TElement> Sequence<TElement>.toBidirList(): BidirList<TElement> {
    val list = BidirList<TElement>()

    list.addAll(this)

    return list
}

fun <TElement> Array<out TElement>.toBidirList(): BidirList<TElement> {
    val list = BidirList<TElement>()

    list.addAll(this)

    return list
}
