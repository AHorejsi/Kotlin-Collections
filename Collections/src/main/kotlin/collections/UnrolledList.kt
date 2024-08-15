package collections

private class UnrolledNode<TElement>(capacity: Int) {
    private val data = arrayOfNulls<Any>(capacity)
    private var amount: Int = 0
    var prev: UnrolledNode<TElement>? = null
    var next: UnrolledNode<TElement>? = null

    @Suppress("UNCHECKED_CAST")
    operator fun get(relativeIndex: Int): TElement {
        checkIfIndexIsAccessible(relativeIndex, this.amount)

        return this.data[relativeIndex] as TElement
    }

    operator fun set(relativeIndex: Int, element: TElement) {
        checkIfIndexIsAccessible(relativeIndex, this.amount)

        this.data[relativeIndex] = element
    }

    fun isFull(): Boolean = this.data.size == this.amount

    fun append(item: TElement) {
        if (this.data.size == this.amount) {
            throw IllegalStateException("Node capacity has been reached")
        }

        this.data[this.amount] = item

        ++(this.amount)
    }
}

/*class UnrolledList<TElement>(private val nodeCap: Int) : AbstractList<TElement>() {
    private var head: UnrolledNode<TElement>? = null
    private var tail: UnrolledNode<TElement>? = null

    override var size: Int = 0
        private set

    override operator fun get(index: Int): TElement {
        checkIfIndexIsAccessible(index, this.size)

        val (node, relativeIndex) = this.findNode(index)

        return node[relativeIndex]
    }

    override fun set(index: Int, element: TElement): TElement {
        checkIfIndexIsAccessible(index, this.size)

        val (node, relativeIndex) = this.findNode(index)

        val old = node[relativeIndex]
        node[relativeIndex] = element

        return old
    }

    private fun findNode(targetIndex: Int): Pair<UnrolledNode<TElement>, Int> {
        var node = this.head!!
        var startIndex = 0

        for (ignore in 0 .. targetIndex step this.nodeCap) {
            node = node.next!!
            startIndex += this.nodeCap
        }

        return node to (targetIndex - startIndex)
    }

    override fun add(element: TElement): Boolean {
        val list = listOf(element)

        return this.addAll(list)
    }

    override fun addAll(elements: Collection<TElement>): Boolean {
        for (item in elements) {
            this.appendItem(item)
        }

        this.size += elements.size
        ++(super.modCount)

        return true
    }

    private fun appendItem(item: TElement) {
        if (this.isEmpty()) {
            this.initializeSingle(item)
        }
        else {
            this.getLastNode().append(item)
        }
    }

    private fun initializeSingle(item: TElement) {
        val newNode = UnrolledNode<TElement>(this.nodeCap)
        newNode.append(item)

        this.head = newNode
        this.tail = newNode
    }

    private fun getLastNode(): UnrolledNode<TElement> {
        var node = this.tail!!

        if (node.isFull()) {
            val newNode = UnrolledNode<TElement>(this.nodeCap)

            node.next = newNode
            newNode.prev = node

            node = newNode
        }

        return node
    }

    override fun addAll(index: Int, elements: Collection<TElement>): Boolean {
        checkIfIndexCanBeInsertedAt(index, this.size)

        if (index == this.size) {
            return this.addAll(elements)
        }
    }

    override fun clear() {
        this.size = 0
        this.head = null
        this.tail = null
        ++(super.modCount)
    }
}*/
