package collections

import java.io.Serializable

class OrderedMap<TKey, TValue>(
    private val map: MutableMap<TKey, MutableLinkedListNode<MutableEntry<TKey, TValue>>> = ChainMap(),
    private val list: MutableLinkedList<MutableEntry<TKey, TValue>> = BidirList()
) : AbstractMap<TKey, TValue>(), Serializable {
    private companion object {
        @Suppress("ConstPropertyName")
        const val serialVersionUID: Long = 1L
    }

    constructor(
        elements: Sequence<Map.Entry<TKey, TValue>>,
        map: MutableMap<TKey, MutableLinkedListNode<MutableEntry<TKey, TValue>>> = ChainMap(),
        list: MutableLinkedList<MutableEntry<TKey, TValue>> = BidirList()
    ) : this(elements.asIterable(), map, list)

    constructor(
        elements: Map<TKey, TValue>,
        map: MutableMap<TKey, MutableLinkedListNode<MutableEntry<TKey, TValue>>> = ChainMap(),
        list: MutableLinkedList<MutableEntry<TKey, TValue>> = BidirList()
    ) : this(elements.asIterable(), map, list)

    constructor(
        elements: Collection<Map.Entry<TKey, TValue>>,
        map: MutableMap<TKey, MutableLinkedListNode<MutableEntry<TKey, TValue>>> = ChainMap(),
        list: MutableLinkedList<MutableEntry<TKey, TValue>> = BidirList()
    ) : this(elements.asIterable(), map, list)

    constructor(
        elements: Iterable<Map.Entry<TKey, TValue>>,
        map: MutableMap<TKey, MutableLinkedListNode<MutableEntry<TKey, TValue>>> = ChainMap(),
        list: MutableLinkedList<MutableEntry<TKey, TValue>> = BidirList()
    ) : this(map, list) {
        for ((key, value) in elements) {
            this[key] = value
        }
    }

    override val size: Int
        get() = this.list.size

    override fun get(key: TKey): TValue? {
        val node = this.map[key]
        val entry = node?.item

        return entry?.value
    }

    override fun put(key: TKey, value: TValue): TValue? {
        val node = this.map[key]

        node?.let {
            val entry = it.item
            val oldValue = entry.value

            entry.setValue(value)

            return oldValue
        } ?: run {
            val newEntry = CustomEntry(key, value)

            this.list.addLast(newEntry)
            this.map[key] = this.list.tail!!

            return null
        }
    }

    override fun remove(key: TKey): TValue? {
        val node = this.map.remove(key)

        return node?.let {
            val entry = it.item

            this.list.remove(it)

            return entry.value
        }
    }

    override fun clear() {
        this.map.clear()
        this.list.clear()
    }

    override val entries: MutableSet<MutableEntry<TKey, TValue>>
        get() = object : AbstractEntrySet<TKey, TValue>(this@OrderedMap) {
            override fun iterator(): MutableIterator<MutableEntry<TKey, TValue>> = object : MutableIterator<MutableEntry<TKey, TValue>> {
                private val base: MutableIterator<MutableEntry<TKey, TValue>> = this@OrderedMap.list.iterator()
                private var lastItem: MutableEntry<TKey, TValue>? = null

                override fun hasNext(): Boolean = this.base.hasNext()

                override fun next(): MutableEntry<TKey, TValue> {
                    this.lastItem = this.base.next()

                    return this.lastItem!!
                }

                override fun remove() {
                    this.lastItem?.let {
                        this.base.remove()
                        this@OrderedMap.remove(it.key)

                        this.lastItem = null
                    }
                }
            }
        }
}

fun <TKey, TValue> Map<TKey, TValue>.toOrderedMap(): OrderedMap<TKey, TValue> = OrderedMap(this)

fun <TKey, TValue> Collection<Map.Entry<TKey, TValue>>.toOrderedMap(): OrderedMap<TKey, TValue> = OrderedMap(this)

fun <TKey, TValue> Iterable<Map.Entry<TKey, TValue>>.toOrderedMap(): OrderedMap<TKey, TValue> = OrderedMap(this)

fun <TKey, TValue> Sequence<Map.Entry<TKey, TValue>>.toOrderedMap(): OrderedMap<TKey, TValue> = OrderedMap(this)
