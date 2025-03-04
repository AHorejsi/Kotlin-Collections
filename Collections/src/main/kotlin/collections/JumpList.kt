package collections

import java.io.Serializable

class JumpList<TElement>(
    var jumper: (IndexedValue<TElement>, IndexedValue<TElement>) -> Boolean
) : AbstractCollection<TElement>(), SelfOrgList<TElement>, Serializable {
    companion object {
        @Suppress("ConstPropertyName")
        private const val serialVersionUID: Long = 1L

        @Suppress("UNUSED_PARAMETER", "FunctionName")
        fun <TKey> MOVE_TO_FRONT(
            past: IndexedValue<TKey>,
            now: IndexedValue<TKey>
        ): Boolean = false

        @Suppress("UNUSED_PARAMETER", "FunctionName")
        fun <TKey> TRANSPOSE(
            past: IndexedValue<TKey>,
            now: IndexedValue<TKey>
        ): Boolean = true
    }

    private val head: OrgNode<TElement> = OrgNode.Head(null)
    private var tail: OrgNode<TElement> = this.head

    override var size: Int = 0
        private set

    override val isRandomAccess: Boolean
        get() = false

    override fun add(element: TElement): Boolean {
        val newNode = OrgNode.Item(element, null)

        this.tail.next = newNode
        this.tail = newNode

        ++(this.size)
        ++(super.modCount)

        return true
    }

    override fun remove(element: TElement): Boolean {
        var node = this.head
        var next = node.next

        while (null !== next) {
            val current = next as OrgNode.Item<TElement>

            if (current.value == element) {
                this.deleteNextNode(node)

                return true
            }

            node = current
            next = current.next
        }

        return false
    }

    private fun deleteNextNode(node: OrgNode<TElement>) {
        if (node.next === this.tail) {
            this.tail = node
            this.tail.next = null
        }

        node.next = node.next?.next

        --(this.size)
        ++(super.modCount)
    }

    override fun clear() {
        this.head.next = null
        this.tail = this.head
        this.size = 0
        ++(super.modCount)
    }

    override fun find(predicate: (element: TElement) -> Boolean): IndexedValue<TElement>? {
        var node: OrgNode<TElement>? = this.head
        var nodeIndex = 0
        var current: OrgNode<TElement>? = this.head
        var currentIndex = 0

        while (null !== current && null !== node) {
            val itemNode = node.next as OrgNode.Item<TElement>
            val currentItem = current.next as OrgNode.Item<TElement>

            if (predicate(currentItem.value)) {
                this.jumpNode(node, current)

                return IndexedValue(nodeIndex, currentItem.value)
            }
            else {
                current = current.next
                ++currentIndex

                if (this.checkReorder(itemNode, nodeIndex, currentItem, currentIndex)) {
                    node = current
                    nodeIndex = currentIndex
                }
            }
        }

        ++(super.modCount)

        return null
    }

    override fun findAll(predicate: (TElement) -> Boolean): Sequence<IndexedValue<TElement>> = sequence {
        var node: OrgNode<TElement>? = this@JumpList.head
        var nodeIndex = 0
        var current: OrgNode<TElement>? = this@JumpList.head
        var currentIndex = 0

        while (null !== current && null !== node) {
            val itemNode = node.next as OrgNode.Item<TElement>
            val currentItem = current.next as OrgNode.Item<TElement>

            if (predicate(currentItem.value)) {
                val next = current.next?.next

                this@JumpList.jumpNode(node, current)
                yield(IndexedValue(nodeIndex, currentItem.value))

                current = next
                ++currentIndex
            }
            else {
                current = current.next
                ++currentIndex
            }

            if (this@JumpList.checkReorder(itemNode, nodeIndex, currentItem, currentIndex)) {
                node = current
                nodeIndex = currentIndex
            }
        }
    }

    private fun jumpNode(node: OrgNode<TElement>, current: OrgNode<TElement>) {
        node.next = node.next?.next
        current.next = current.next?.next

        current.next?.next = node.next
        node.next = current.next
    }

    private fun checkReorder(
        node: OrgNode.Item<TElement>,
        nodeIndex: Int,
        next: OrgNode.Item<TElement>,
        nextIndex: Int
    ): Boolean {
        val node1 = IndexedValue(nodeIndex, node.value)
        val next1 = IndexedValue(nextIndex, next.value)

        return this.jumper(node1, next1)
    }

    override operator fun contains(element: TElement): Boolean =
        super<SelfOrgList>.contains(element)

    override fun containsAll(elements: Collection<TElement>): Boolean =
        super<SelfOrgList>.containsAll(elements)

    override fun iterator(): MutableIterator<TElement> = object : MutableIterator<TElement> {
        private var current: OrgNode<TElement> = this@JumpList.head
        private var last: OrgNode<TElement>? = null
        private var modCount: Int = this@JumpList.modCount

        override fun hasNext(): Boolean {
            checkIfUnderlyingCollectionHasBeenModified(this.modCount, this@JumpList.modCount)

            return null !== this.current.next
        }

        override fun next(): TElement {
            checkIfUnderlyingCollectionHasBeenModified(this.modCount, this@JumpList.modCount)
            checkIfNext(this)

            val itemNode = this.current.next
            val item = (itemNode as OrgNode.Item<TElement>).value

            this.last = this.current
            this.current = itemNode

            return item
        }

        override fun remove() {
            this.last?.let {
                this@JumpList.deleteNextNode(it)

                this.last = null
                ++(this.modCount)
            } ?: noneToUse()
        }
    }
}
