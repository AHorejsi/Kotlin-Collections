package collections

import java.io.Serializable

private class SeqNode<TItem>(
    var data: TItem,
    var prev: SeqNode<TItem>? = null,
    var next: SeqNode<TItem>? = null
)

class SequentialList<TElement> : AbstractList<TElement>(), Serializable {
    private companion object {
        @Suppress("ConstPropertyName")
        private const val serialVersionUID = 1L
    }

    private var head: SeqNode<TElement>? = null
    private var tail: SeqNode<TElement>? = null

    override var size: Int = 0
        private set

    override operator fun get(index: Int): TElement {
        val node = this.getNode(index)

        return node.data
    }

    override operator fun set(index: Int, element: TElement): TElement {
        val node = this.getNode(index)

        val old = node.data
        node.data = element

        return old
    }

    private fun getNode(index: Int): SeqNode<TElement> {
        checkIfIndexIsAccessible(index, this.size)

        return if (index <= this.size / 2)
            this.getNodeFromFront(index)
        else
            this.getNodeFromBack(index)
    }

    private fun getNodeFromFront(index: Int): SeqNode<TElement> {
        var node = this.head

        repeat(index) {
            node = node!!.next
        }

        return node!!
    }

    private fun getNodeFromBack(index: Int): SeqNode<TElement> {
        var node = this.tail

        repeat(this.size - index) {
            node = node!!.prev
        }

        return node!!
    }

    override fun add(element: TElement): Boolean {
        val newNode = SeqNode(element)

        if (null === this.head) {
            this.head = newNode
        }
        else {
            newNode.prev = this.tail

            this.tail!!.next = newNode
        }

        this.tail = newNode

        ++(this.size)
        ++(super.modCount)

        return true
    }

    override fun add(index: Int, element: TElement) {
        val singleton = listOf(element)

        this.addAll(index, singleton)
    }

    override fun addAll(elements: Collection<TElement>): Boolean =
        this.insert(elements) > 0

    override fun addAll(index: Int, elements: Collection<TElement>): Boolean {
        checkIfIndexCanBeInsertedAt(index, this.size)

        if (index == this.size) {
            return this.addAll(elements)
        }

        if (elements.isEmpty()) {
            return false
        }

        val node = this.getNode(index)

        for (item in elements) {
            this.insertNewNode(node, item)
        }

        ++(super.modCount)

        return true
    }

    private fun insertNewNode(node: SeqNode<TElement>, item: TElement) {
        val newNode = SeqNode(item)

        newNode.next = node
        newNode.prev = node.prev

        node.prev = newNode

        ++(this.size)
    }

    override fun removeAt(index: Int): TElement {
        val node = this.getNode(index)

        this.deleteNode(node)

        return node.data
    }

    private fun deleteNode(node: SeqNode<TElement>) {
        if (node === this.head) {
            this.head = this.head!!.next
            this.head!!.prev = null
        }
        else if (node === this.tail) {
            this.tail = this.tail!!.prev
            this.tail!!.next = null
        }

        node.next?.prev = node.prev
        node.prev?.next = node.next

        --(this.size)
        ++(super.modCount)
    }

    override fun remove(element: TElement): Boolean {
        var node = this.head

        while (null !== node) {
            if (element == node.data) {
                this.deleteNode(node)

                return true
            }

            node = node.next
        }

        return false
    }

    override fun clear() {
        this.head = null
        this.tail = null
        this.size = 0
        ++(super.modCount)
    }

    override operator fun contains(element: TElement): Boolean =
        -1 != this.indexOf(element)

    override fun indexOf(element: TElement): Int =
        this.index(0, element)

    override fun lastIndexOf(element: TElement): Int =
        this.lastIndex(this.size, element)

    override fun iterator(): MutableIterator<TElement> =
        this.listIterator()

    override fun listIterator(): MutableListIterator<TElement> =
        this.listIterator(0)

    override fun listIterator(index: Int): MutableListIterator<TElement> = object : MutableListIterator<TElement> {
        init {
            checkIfIndexCanBeInsertedAt(index, this@SequentialList.size)
        }

        private var modCount: Int = this@SequentialList.modCount
        private var currentIndex: Int = index
        private var lastNode: SeqNode<TElement>? = null
        private var currentNode: SeqNode<TElement>? =
            if (index == this@SequentialList.size)
                null
            else
                this@SequentialList.getNode(index)

        override fun previousIndex(): Int {
            checkIfUnderlyingCollectionHasBeenModified(this.modCount, this@SequentialList.modCount)

            return this.currentIndex - 1
        }

        override fun nextIndex(): Int {
            checkIfUnderlyingCollectionHasBeenModified(this.modCount, this@SequentialList.modCount)

            return this.currentIndex
        }

        override fun hasPrevious(): Boolean =
            this.previousIndex() >= 0

        override fun hasNext(): Boolean =
            this.nextIndex() < this@SequentialList.size

        override fun previous(): TElement {
            checkIfPrev(this)

            val prevNode = this.currentNode!!.prev!!
            val item = prevNode.data

            this.currentNode = prevNode
            this.lastNode = prevNode
            --(this.currentIndex)

            return item
        }

        override fun next(): TElement {
            checkIfNext(this)

            val node = this.currentNode!!
            val item = node.data

            this.currentNode = node.next
            this.lastNode = node
            ++(this.currentIndex)

            return item
        }

        override fun set(element: TElement) =
            this.lastNode?.let {
                it.data = element
            } ?: noneToUse()

        override fun remove() =
            this.lastNode?.let {
                this@SequentialList.deleteNode(it)

                ++(this.modCount)
                this.lastNode = null
            } ?: noneToUse()

        override fun add(element: TElement) {
            this.currentNode?.let {
                this@SequentialList.insertNewNode(it, element)
            }
        }
    }
}
