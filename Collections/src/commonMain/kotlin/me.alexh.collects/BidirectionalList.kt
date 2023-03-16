package me.alexh.collects

class BidirectionalList<TElement> : SealedMutableLinkedList<TElement>() {
    override fun addFirst(newNode: MutableLinkedListNode<TElement>) =
        super.first?.let {
            this.addBefore(it, newNode)
        } ?: run {
            this.insertIntoEmptyList(newNode)
        }

    override fun addLast(newNode: MutableLinkedListNode<TElement>) =
        super.last?.let {
            this.addAfter(it, newNode)
        } ?: run {
            this.insertIntoEmptyList(newNode)
        }

    private fun insertIntoEmptyList(newNode: MutableLinkedListNode<TElement>) {
        super.first = newNode
        super.first = newNode

        super.size = 1
        ++(super.modCount)
    }

    override fun addBefore(node: MutableLinkedListNode<TElement>, newNode: MutableLinkedListNode<TElement>) {
        if (null !== newNode.source || this !== node.source) {
            throw IllegalArgumentException()
        }

        node.insertBefore(newNode)

        newNode.source = this

        ++(super.size)
        ++(super.modCount)
    }

    override fun addAfter(node: MutableLinkedListNode<TElement>, newNode: MutableLinkedListNode<TElement>) {
        if (null !== newNode.source || this !== node.source) {
            throw IllegalArgumentException()
        }

        node.insertAfter(newNode)

        newNode.source = this

        ++(super.size)
        ++(super.modCount)
    }

    override fun spliceBefore(node: MutableLinkedListNode<TElement>, other: MutableLinkedList<TElement>) {
        node.spliceBefore(other)

        super.size += other.size
        ++(super.modCount)

        other.clear()

        this.reassignSource(other)
    }

    override fun spliceAfter(node: MutableLinkedListNode<TElement>, other: MutableLinkedList<TElement>) {
        node.spliceAfter(other)

        super.size += other.size
        ++(super.modCount)

        other.clear()

        this.reassignSource(other)
    }

    override fun splice(other: MutableLinkedList<TElement>) =
        super.last?.let {
            this.spliceAfter(it, other)
        } ?: run {
            super.first = other.first
            super.last = other.last
            super.size = other.size
            ++(super.modCount)

            other.clear()

            this.reassignSource(other)
        }

    private fun reassignSource(other: MutableLinkedList<TElement>) {
        var node = other.first

        while (null !== node) {
            node.source = this

            node = node.next
        }
    }

    override fun remove(node: MutableLinkedListNode<TElement>) {
        if (this !== node.source) {
            throw IllegalArgumentException()
        }

        node.remove()

        --(super.size)
        ++(super.modCount)
    }

    override fun clear() {
        this.unassignSource()

        super.first = null
        super.last = null
        super.size = 0
        ++(super.modCount)
    }

    private fun unassignSource() {
        val node = this.first

        while (null != node) {
            node.source = null
        }
    }
}
