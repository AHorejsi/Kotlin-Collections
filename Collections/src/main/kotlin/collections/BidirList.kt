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
