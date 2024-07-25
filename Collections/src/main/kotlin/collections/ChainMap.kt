package collections

import java.io.Serializable

@Suppress("RemoveRedundantQualifierName")
class ChainMap<TKey, TValue>(
    private val initialCapacity: Int = ChainMap.DEFAULT_CAPACITY,
    private val loadFactor: Float = ChainMap.DEFAULT_LOAD_FACTOR,
    private val comparator: EqualityComparator<TKey> = ChainMap.DEFAULT_COMPARATOR
) : AbstractMap<TKey, TValue>(), Serializable {
    private companion object {
        @Suppress("ConstPropertyName")
        const val serialVersionUID: Long = 1L

        const val DEFAULT_CAPACITY: Int = 16
        const val DEFAULT_LOAD_FACTOR: Float = 0.75f
        val DEFAULT_COMPARATOR: EqualityComparator<Any?> = EqualityComparator.Default
    }

    init {
        require(this.initialCapacity >= 0)
        require(this.loadFactor > 0 && this.loadFactor <= 1)
    }

    constructor(
        elements: Sequence<Map.Entry<TKey, TValue>>,
        initialCapacity: Int = ChainMap.DEFAULT_CAPACITY,
        loadFactor: Float = ChainMap.DEFAULT_LOAD_FACTOR,
        comparator: EqualityComparator<TKey> = ChainMap.DEFAULT_COMPARATOR
    ) : this(elements.asIterable(), initialCapacity, loadFactor, comparator)

    constructor(
        elements: Map<TKey, TValue>,
        initialCapacity: Int = ChainMap.DEFAULT_CAPACITY,
        loadFactor: Float = ChainMap.DEFAULT_LOAD_FACTOR,
        comparator: EqualityComparator<TKey> = ChainMap.DEFAULT_COMPARATOR
    ) : this(elements.asIterable(), initialCapacity, loadFactor, comparator)

    constructor(
        elements: Collection<Map.Entry<TKey, TValue>>,
        initialCapacity: Int = ChainMap.DEFAULT_CAPACITY,
        loadFactor: Float = ChainMap.DEFAULT_LOAD_FACTOR,
        comparator: EqualityComparator<TKey> = ChainMap.DEFAULT_COMPARATOR
    ) : this(elements.asIterable(), initialCapacity, loadFactor, comparator)

    constructor(
        elements: Iterable<Map.Entry<TKey, TValue>>,
        initialCapacity: Int = ChainMap.DEFAULT_CAPACITY,
        loadFactor: Float = ChainMap.DEFAULT_LOAD_FACTOR,
        comparator: EqualityComparator<TKey> = ChainMap.DEFAULT_COMPARATOR
    ) : this(initialCapacity, loadFactor, comparator) {
        for ((key, value) in elements) {
            this[key] = value
        }
    }

    private var data: Array<Chain.Head<MutableEntry<TKey, TValue>>> =
        Array((this.initialCapacity / this.loadFactor).toInt()) { Chain.Head() }

    override var size: Int = 0
        private set

    val capacity: Int
        get() = this.data.size

    override operator fun get(key: TKey): TValue? {
        val (beforeNode, _) = this.findBucket(key, this.data)
        val entry = beforeNode.next?.item

        return entry?.value
    }

    override fun put(key: TKey, value: TValue): TValue? {
        this.rehashIfNeeded(1)

        val (beforeNode, _) = this.findBucket(key, this.data)
        val entry = beforeNode.next?.item

        entry?.let {
            val old = it.value

            it.setValue(value)

            return old
        } ?: run {
            val newEntry = CustomEntry(key, value)

            this.insertElement(newEntry, this.data)

            ++(this.size)
            ++(super.modCount)

            return null
        }
    }

    override fun putAll(from: Map<out TKey, TValue>) {
        this.rehashIfNeeded(from.size)

        super.putAll(from)
    }

    private fun rehashIfNeeded(amountToAdd: Int) {
        val newSize = this.size + amountToAdd
        val load = newSize.toFloat() / this.capacity

        if (load >= this.loadFactor) {
            val newCapacity = newSize * 2
            val newData = Array<Chain.Head<MutableEntry<TKey, TValue>>>(newCapacity) { Chain.Head() }

            for (item in this.entries) {
                this.insertElement(item, newData)
            }

            this.data = newData
        }
    }

    private fun insertElement(
        entry: MutableEntry<TKey, TValue>,
        itemData: Array<Chain.Head<MutableEntry<TKey, TValue>>>
    ) {
        val (beforeNode, index) = this.findBucket(entry.key, itemData)
        val node = beforeNode.next

        if (null !== node && !this.comparator.equals(node.item.key, entry.key)) {
            val head = itemData[index]
            val newNode = Chain.Valid(entry)

            newNode.next = head.next
            head.next = newNode
        }
    }

    override fun remove(key: TKey): TValue? {
        val (beforeNode, _) = this.findBucket(key, this.data)

        return this.removeNext(beforeNode)
    }

    private fun removeNext(beforeNode: Chain<MutableEntry<TKey, TValue>>): TValue? {
        val node = beforeNode.next

        node?.let {
            val entry = it.item
            val value = entry.value

            beforeNode.next = it.next

            --(this.size)
            ++(super.modCount)

            return value
        }

        return null
    }

    override fun clear() {
        this.data = Array((this.initialCapacity / this.loadFactor).toInt()) { Chain.Head() }
        this.size = 0
        ++(super.modCount)
    }

    private fun findBucket(
        key: TKey,
        itemData: Array<Chain.Head<MutableEntry<TKey, TValue>>>
    ): Pair<Chain<MutableEntry<TKey, TValue>>, Int> {
        val index = this.comparator.hashCode(key)
        var node: Chain<MutableEntry<TKey, TValue>> = itemData[index]
        var nextNode = node.next

        while (null !== nextNode) {
            val other = nextNode.item

            if (this.comparator.equals(key, other.key)) {
                break
            }

            node = nextNode
            nextNode = nextNode.next
        }

        return node to index
    }

    override val entries: MutableSet<MutableEntry<TKey, TValue>>
        get() = object : AbstractEntrySet<TKey, TValue>(this) {
            override fun iterator(): MutableIterator<MutableEntry<TKey, TValue>> = object : MutableIterator<MutableEntry<TKey, TValue>> {
                private var modCount: Int = this@ChainMap.modCount
                private var currentIndex: Int = this.findBucket(-1)
                private var currentNode: Chain<MutableEntry<TKey, TValue>> = this@ChainMap.data[this.currentIndex]
                private var lastNode: Chain<MutableEntry<TKey, TValue>>? = null

                private fun checkModCount() {
                    if (this.modCount != this@ChainMap.modCount) {
                        throw ConcurrentModificationException()
                    }
                }

                override fun hasNext(): Boolean {
                    this.checkModCount()

                    return this.exceedsCapacity(this.currentIndex)
                }

                private fun exceedsCapacity(index: Int): Boolean = index < this@ChainMap.capacity

                override fun next(): MutableEntry<TKey, TValue> {
                    if (!this.hasNext()) {
                        throw NoSuchElementException()
                    }

                    if (null === this.currentNode.next) {
                        this.currentIndex = this.findBucket(this.currentIndex)
                        this.currentNode = this@ChainMap.data[this.currentIndex]
                    }

                    val nextNode = this.currentNode.next!!
                    val item = nextNode.item

                    this.lastNode = this.currentNode
                    this.currentNode = nextNode

                    return item
                }

                private fun findBucket(startIndex: Int): Int {
                    var nextIndex = startIndex

                    do {
                        ++nextIndex
                    } while (this.exceedsCapacity(nextIndex) && null === this@ChainMap.data[nextIndex].next)

                    return nextIndex
                }

                override fun remove() {
                    this.checkModCount()

                    this.lastNode?.let {
                        this@ChainMap.removeNext(it)

                        ++(this.modCount)
                    } ?: throw IllegalStateException()
                }
            }
        }
}

fun <TKey, TValue> Map<TKey, TValue>.toChainMap(): ChainMap<TKey, TValue> = ChainMap(this)

fun <TKey, TValue> Collection<Map.Entry<TKey, TValue>>.toChainMap(): ChainMap<TKey, TValue> = ChainMap(this)

fun <TKey, TValue> Iterable<Map.Entry<TKey, TValue>>.toChainMap(): ChainMap<TKey, TValue> = ChainMap(this)

fun <TKey, TValue> Sequence<Map.Entry<TKey, TValue>>.toChainMap(): ChainMap<TKey, TValue> = ChainMap(this)
