package collections

abstract class AbstractSortedMap<TKey, TValue>(
    private val comparator: (TKey, TKey) -> Int,
    protected val nodeMaker: (MutableEntry<TKey, TValue>) -> BstNode<TKey, TValue>
) : AbstractMap<TKey, TValue>(), MutableSortedMap<TKey, TValue> {
    protected val tree: BinarySearchTree<TKey, TValue> by
        lazy(LazyThreadSafetyMode.PUBLICATION) { BinarySearchTree(this.comparator) }

    override var size: Int = 0
        protected set

    override fun first(): Map.Entry<TKey, TValue> {
        val node = this.tree.firstNode()

        return this.getEntry(node)
    }

    override fun last(): Map.Entry<TKey, TValue> {
        val node = this.tree.lastNode()

        return this.getEntry(node)
    }

    override fun lesser(key: TKey, inclusive: Boolean): Map.Entry<TKey, TValue> {
        val node = this.tree.lesserNode(key, inclusive)

        return this.getEntry(node)
    }

    override fun greater(key: TKey, inclusive: Boolean): Map.Entry<TKey, TValue> {
        val node = this.tree.greaterNode(key, inclusive)

        return this.getEntry(node)
    }

    private fun getEntry(node: BstNode<TKey, TValue>?): Map.Entry<TKey, TValue> {
        if (null === node) {
            throw NoSuchElementException()
        }

        return node.entry
    }

    override fun get(key: TKey): TValue? {
        val (_, node) = this.tree.findNode(key)

        return node?.value
    }

    protected abstract fun balanceTreeAfterInsertion(node: BstNode<TKey, TValue>)

    protected abstract fun removeNode(node: BstNode<TKey, TValue>)

    override fun put(key: TKey, value: TValue): TValue? {
        val (parent, node) = this.tree.findNode(key)

        node?.let {
            val old = it.value

            it.entry.setValue(value)

            return old
        } ?: run {
            val entry = CustomEntry(key, value)
            val newNode = this.nodeMaker(entry)

            this.tree.addLeaf(parent, newNode)

            ++(this.size)
            ++(super.modCount)

            this.balanceTreeAfterInsertion(newNode)

            return null
        }
    }

    override fun removeFirst(): MutableEntry<TKey, TValue> {
        val node = this.tree.firstNode()

        return this.deleteNode(node)
    }

    override fun removeLast(): MutableEntry<TKey, TValue> {
        val node = this.tree.lastNode()

        return this.deleteNode(node)
    }

    override fun remove(key: TKey): TValue? {
        val (_, node) = this.tree.findNode(key)

        return node?.let { this.deleteNode(it).value }
    }

    private fun deleteNode(node: BstNode<TKey, TValue>?): MutableEntry<TKey, TValue> =
        node?.let {
            this.removeNode(it)

            return it.entry
        } ?: throw NoSuchElementException()

    override fun clear() {
        this.tree.deleteTree()
        this.size = 0
        ++(super.modCount)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }

        if (other !is SortedMap<*, *> || this.size != other.size) {
            return false
        }

        val leftSeq = this.asSequence()
        val rightSeq = other.asSequence()

        for ((left, right) in leftSeq.zip(rightSeq)) {
            if (left != right) {
                return false
            }
        }

        return true
    }

    override fun hashCode(): Int {
        var hashValue = 31

        for ((key, value) in this) {
            hashValue = 31 * hashValue + key.hashCode()
            hashValue = 31 * hashValue + value.hashCode()
        }

        return hashValue
    }

    override val entries: MutableSet<MutableEntry<TKey, TValue>>
        get() = object : AbstractEntrySet<TKey, TValue>(this) {
            override fun iterator(): MutableIterator<MutableEntry<TKey, TValue>> = object : MutableIterator<MutableEntry<TKey, TValue>> {
                private var modCount: Int = this@AbstractSortedMap.modCount
                private var lastNode: BstNode<TKey, TValue>? = null
                private val stack: Stack<BstNode<TKey, TValue>> = VectorStack(this@AbstractSortedMap.size)

                init {
                    this.insertLeftmostBranch(this@AbstractSortedMap.tree.root)
                }

                private fun checkModCount() {
                    if (this.modCount != this@AbstractSortedMap.modCount) {
                        throw ConcurrentModificationException()
                    }
                }

                override fun hasNext(): Boolean {
                    this.checkModCount()

                    return !this.stack.isEmpty()
                }

                override fun next(): MutableEntry<TKey, TValue> {
                    if (!this.hasNext()) {
                        throw NoSuchElementException()
                    }

                    val node = this.stack.pop()

                    this.lastNode = node
                    this.insertLeftmostBranch(node.right)

                    return node.entry
                }

                private fun insertLeftmostBranch(node: BstNode<TKey, TValue>?) {
                    var current = node

                    while (null !== current) {
                        this.stack.push(current)

                        current = current.left
                    }
                }

                override fun remove() {
                    this.checkModCount()

                    this.lastNode?.let {
                        this@AbstractSortedMap.removeNode(it)

                        ++(this.modCount)
                        this.lastNode = null
                    }
                }
            }
        }
}
