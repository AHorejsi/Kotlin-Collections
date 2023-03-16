package me.alexh.collects

import kotlin.jvm.Transient

class ChainSet<TElement>(
    initialCapacity: Int = ChainSet.DEFAULT_CAPACITY,
    val maxLoadFactor: Float = ChainSet.DEFAULT_MAX_LOAD_FACTOR,
    val hasher: Hasher<TElement> = DefaultHasher()
) : SealedMutableCollection<TElement>(), MutableSet<TElement> {
    private companion object {
        const val DEFAULT_CAPACITY: Int = 16
        const val DEFAULT_MAX_LOAD_FACTOR: Float = 0.75F
    }

    @Transient
    private var modCount: Int = 0
    private var elementData: Array<MutableLinkedList<TElement>> = Array(initialCapacity) { emptyMutableLinkedList() }
    override var size: Int = 0
        private set

    val capacity: Int
        get() = this.elementData.size

    val loadFactor: Float
        get() = this.size.toFloat() / this.capacity.toFloat()

    override fun add(element: TElement): Boolean {
        if (this.loadFactor > this.maxLoadFactor) {
            this.resize(this.capacity * 2)
        }

        val (node, bucket) = this.findNode(element, this.elementData)

        if (null === node) {
            bucket.addLast(element)

            ++(this.size)
            ++(this.modCount)

            return true
        }

        return false
    }

    private fun resize(newCapacity: Int) {
        val newData = Array<MutableLinkedList<TElement>>(newCapacity) { emptyMutableLinkedList() }

        for (element in this) {
            val (_, bucket) = this.findNode(element, newData)

            bucket.addLast(element)
        }

        this.elementData = newData
    }

    override operator fun contains(element: TElement): Boolean {
        val (node, _) = this.findNode(element, this.elementData)

        return null !== node
    }

    override fun remove(element: TElement): Boolean {
        val (node, bucket) = this.findNode(element, this.elementData)

        if (null !== node) {
            bucket.remove(node)

            --(this.size)
            ++(this.modCount)

            return true
        }

        return false
    }

    private fun findNode(element: TElement, elementData: Array<MutableLinkedList<TElement>>): Pair<MutableLinkedListNode<TElement>?, MutableLinkedList<TElement>> {
        val cap = this.capacity
        val hash = this.hasher.hashCode(element)
        val index = hash % cap
        val bucket = elementData[index]

        var node = bucket.first

        while (null !== node) {
            if (this.hasher.equals(node.element, element)) {
                break
            }

            node = node.next
        }

        return node to bucket
    }

    override fun clear() {
        this.elementData = Array(ChainSet.DEFAULT_CAPACITY) { emptyMutableLinkedList() }
        this.size = 0
        ++(this.modCount)
    }

    override fun iterator(): MutableIterator<TElement> = object : MutableIterator<TElement> {
        private val modCount: Int = this@ChainSet.modCount
        private var state: IteratorState = IteratorState.INITIALIZED
        private var bucketIndex: Int = -1
        private var bucketNode: MutableLinkedListNode<TElement>? = null
        private var lastNode: MutableLinkedListNode<TElement>? = null
        private var listOfLastNode: MutableLinkedList<TElement>? = null

        init {
            this.moveToNext()
        }

        override fun hasNext(): Boolean {
            this.checkForConcurrentModification()

            return this.bucketIndex == this@ChainSet.capacity
        }

        override fun next(): TElement {
            if (!this.hasNext()) {
                throw NoSuchElementException()
            }

            val bucketNode = this.bucketNode!!
            val elem = bucketNode.element

            this.lastNode = bucketNode
            this.listOfLastNode = bucketNode.source
            this.state = IteratorState.CALLED_NEXT

            this.moveToNext()

            return elem
        }

        private fun moveToNext() {
            if (null !== this.bucketNode!!.next) {
                this.bucketNode = this.bucketNode!!.next
            }
            else {
                this.bucketIndex = this.findNextNonemptyBucket(this.bucketIndex + 1)
                this.bucketNode =
                    if (this.bucketIndex == this@ChainSet.capacity)
                        null
                    else
                        this@ChainSet.elementData[this.bucketIndex].first
            }
        }

        private fun findNextNonemptyBucket(bucketIndex: Int): Int {
            var nextBucketIndex = bucketIndex

            while (this@ChainSet.elementData[nextBucketIndex].isEmpty()) {
                ++nextBucketIndex
            }

            return nextBucketIndex
        }

        override fun remove() {
            this.checkForConcurrentModification()

            if (IteratorState.CALLED_NEXT != this.state) {
                throw IllegalStateException()
            }

            this.listOfLastNode!!.remove(this.lastNode!!)
            --(this@ChainSet.size)

            this.state = IteratorState.CALLED_REMOVE
        }

        private fun checkForConcurrentModification() {
            if (this.modCount != this@ChainSet.modCount) {
                throw ConcurrentModificationException()
            }
        }
    }
}
