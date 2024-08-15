package collections

abstract class AbstractLinkedList<TElement> : AbstractCollection<TElement>(), MutableLinkedList<TElement> {
    override var head: MutableLinkedListNode<TElement>? = null
        protected set
    override var tail: MutableLinkedListNode<TElement>? = null
        protected set

    override var size: Int = 0
        protected set

    override fun add(element: TElement): Boolean {
        this.addLast(element)

        return true
    }

    override fun addAll(elements: Collection<TElement>): Boolean {
        this.addToBack(elements)

        return true
    }

    override fun addFirst(newNode: MutableLinkedListNode<TElement>) {
        this.head?.let {
            this.addBefore(it, newNode)
        } ?: run {
            this.makeFirstNode(newNode)
        }
    }

    override fun addLast(newNode: MutableLinkedListNode<TElement>) {
        this.tail?.let {
            this.addAfter(it, newNode)
        } ?: run {
            this.makeFirstNode(newNode)
        }
    }

    private fun makeFirstNode(newNode: MutableLinkedListNode<TElement>) {
        if (null !== newNode.list) {
            throw IllegalArgumentException()
        }

        this.head = newNode
        this.tail = newNode

        ++(this.size)
        ++(super.modCount)
    }

    override fun addBefore(node: MutableLinkedListNode<TElement>, newNode: MutableLinkedListNode<TElement>) {
        if (this !== node.list || null !== newNode.list) {
            throw IllegalArgumentException()
        }

        newNode.next = node
        newNode.prev = node.prev

        node.prev?.next = newNode
        node.prev = newNode

        if (node === this.head) {
            this.head = newNode
        }

        newNode.list = this

        ++(this.size)
        ++(super.modCount)
    }

    override fun addAfter(node: MutableLinkedListNode<TElement>, newNode: MutableLinkedListNode<TElement>) {
        if (this !== node.list || null !== newNode.list) {
            throw IllegalArgumentException()
        }

        newNode.next = node.next
        newNode.prev = node

        node.next?.prev = newNode
        node.next = newNode

        if (node === this.tail) {
            this.tail = newNode
        }

        ++(this.size)
        ++(super.modCount)
    }

    override fun addToFront(elements: Collection<TElement>) {
        val (node, iter) = this.makeHeadIfNeeded(elements)

        while (iter.hasNext()) {
            val item = iter.next()

            this.addBefore(node, item)
        }
    }

    private fun makeHeadIfNeeded(elements: Collection<TElement>): Pair<MutableLinkedListNode<TElement>, Iterator<TElement>> {
        val iter = elements.iterator()

        this.head?.let {
            return it to iter
        } ?: run {
            val first = iter.next()
            this.addFirst(first)

            return this.head!! to iter
        }
    }

    override fun addToBack(elements: Collection<TElement>) {
        val (node, iter) = this.makeTailIfNeeded(elements)

        while (iter.hasNext()) {
            val item = iter.next()

            this.addBefore(node, item)
        }
    }

    private fun makeTailIfNeeded(elements: Collection<TElement>): Pair<MutableLinkedListNode<TElement>, Iterator<TElement>> {
        val iter = elements.iterator()

        this.tail?.let {
            return it to iter
        } ?: run {
            val first = iter.next()
            this.addLast(first)

            return this.tail!! to iter
        }
    }

    override fun remove(element: TElement): Boolean {
        val node = this.findAmount(element)

        if (null !== node) {
            this.remove(node)

            return true
        }

        return false
    }

    override fun removeFirst(): MutableLinkedListNode<TElement> =
        this.head?.let {
            this.remove(it)

            return it
        } ?: throw NoSuchElementException()

    override fun removeLast(): MutableLinkedListNode<TElement> =
        this.tail?.let {
            this.remove(it)

            return it
        } ?: throw NoSuchElementException()

    override fun remove(node: MutableLinkedListNode<TElement>) {
        if (this !== node.list) {
            throw IllegalArgumentException()
        }

        node.prev?.next = node.next
        node.next?.prev = node.prev

        when (node) {
            this.head -> {
                this.head = node.next
                node.prev = null
            }
            this.tail -> {
                this.tail = node.prev
                node.next = null
            }
        }

        node.list = null

        --(this.size)
        ++(super.modCount)
    }

    override fun clear() {
        this.head = null
        this.tail = null
        this.size = 0
        ++(super.modCount)
    }

    override fun removeFromFront(amount: Int): Int {
        var amountRemoved = 0

        while (amountRemoved < amount && !this.isEmpty()) {
            this.remove(this.head!!)

            ++amountRemoved
        }

        return amountRemoved
    }

    override fun removeFromBack(amount: Int): Int {
        var amountRemoved = 0

        while (amountRemoved < amount && !this.isEmpty()) {
            this.remove(this.tail!!)

            ++amountRemoved
        }

        return amountRemoved
    }

    override operator fun contains(element: @UnsafeVariance TElement): Boolean = null !== this.findAmount(element)

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }

        if (other !is LinkedList<*> || this.size != other.size) {
            return false
        }

        var thisNode = this.head
        var otherNode = other.head

        while (null !== thisNode && null !== otherNode) {
            if (thisNode.item != otherNode.item) {
                return false
            }

            thisNode = thisNode.next
            otherNode = otherNode.next
        }

        return true
    }

    override fun hashCode(): Int {
        var hashValue = 31
        var node = this.head

        while (null !== node) {
            hashValue = 31 * hashValue + node.item.hashCode()

            node = node.next
        }

        return hashValue
    }

    override fun listIterator(index: Int): MutableListIterator<TElement> {
        val node = this.getNodeAt(index)

        return this.makeListIterator(index, node)
    }

    override fun listIterator(node: LinkedListNode<TElement>): MutableListIterator<TElement> {
        if (this !== node.list) {
            throw IllegalArgumentException()
        }

        val index = this.findIndex(node)

        return this.makeListIterator(index, node as MutableLinkedListNode<TElement>)
    }

    override fun listIterator(node: MutableLinkedListNode<TElement>): MutableListIterator<TElement> {
        if (this !== node.list) {
            throw IllegalArgumentException()
        }

        val index = this.findIndex(node)

        return this.makeListIterator(index, node)
    }

    private fun findIndex(node: LinkedListNode<TElement>): Int {
        var index = 0
        var current = this.head

        while (null !== current) {
            if (node === current) {
                return index
            }

            ++index
            current = current.next
        }

        throw NoSuchElementException()
    }

    private fun makeListIterator(index: Int, node: MutableLinkedListNode<TElement>): MutableListIterator<TElement> =
        object : MutableListIterator<TElement> {
            private var modCount: Int = this@AbstractLinkedList.modCount
            private var currentIndex: Int = index
            private var lastNode: MutableLinkedListNode<TElement>? = null
            private var currentNode: MutableLinkedListNode<TElement>? = node

            private fun checkModCount() {
                if (this.modCount != this@AbstractLinkedList.modCount) {
                    throw ConcurrentModificationException()
                }
            }

            override fun previousIndex(): Int {
                this.checkModCount()

                return this.currentIndex - 1
            }

            override fun nextIndex(): Int {
                this.checkModCount()

                return this.currentIndex
            }

            override fun hasPrevious(): Boolean = this.currentNode !== this@AbstractLinkedList.head

            override fun hasNext(): Boolean = null !== this.currentNode

            override fun previous(): TElement {
                if (!this.hasPrevious()) {
                    throw NoSuchElementException()
                }

                --(this.currentIndex)
                this.currentNode = this.currentNode?.prev ?: this@AbstractLinkedList.tail
                this.lastNode = this.currentNode

                return this.currentNode!!.item
            }

            override fun next(): TElement {
                if (!this.hasNext()) {
                    throw NoSuchElementException()
                }

                val current = this.currentNode!!
                val item = current.item

                ++(this.currentIndex)
                this.lastNode = current
                this.currentNode = current.next

                return item
            }

            override fun set(element: TElement) {
                this.checkModCount()

                this.lastNode?.let {
                    it.item = element
                } ?: throw IllegalStateException()
            }

            override fun remove() {
                this.checkModCount()

                this.lastNode?.let {
                    this@AbstractLinkedList.remove(it)

                    ++(this.modCount)
                    this.lastNode = null
                } ?: throw IllegalStateException()
            }

            override fun add(element: TElement) {
                this.checkModCount()

                this.currentNode?.let {
                    this@AbstractLinkedList.addBefore(it, element)

                    this.currentNode = it.prev
                } ?: run {
                    this@AbstractLinkedList.addLast(element)

                    this.currentNode = this@AbstractLinkedList.tail
                }

                ++(this.modCount)
            }
        }
}
