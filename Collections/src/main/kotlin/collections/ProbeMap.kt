package collections

import java.io.Serializable

@Suppress("RemoveRedundantQualifierName")
class ProbeMap<TKey, TValue>(
    private val hasher: Hasher = ProbeMap.DEFAULT_HASHER,
    private val comparator: EqualityComparator<TKey> = ProbeMap.DEFAULT_COMPARATOR
) : AbstractMap<TKey, TValue>(), Serializable {
    private companion object {
        @Suppress("ConstPropertyName")
        const val serialVersionUID: Long = 1L

        val DEFAULT_HASHER: Hasher = Hasher.Quadratic
        val DEFAULT_COMPARATOR: EqualityComparator<Any?> = EqualityComparator.Default
    }

    constructor(
        elements: Sequence<Map.Entry<TKey, TValue>>,
        hasher: Hasher = ProbeMap.DEFAULT_HASHER,
        comparator: EqualityComparator<TKey> = ProbeMap.DEFAULT_COMPARATOR
    ) : this(elements.asIterable(), hasher, comparator)

    constructor(
        elements: Map<TKey, TValue>,
        hasher: Hasher = ProbeMap.DEFAULT_HASHER,
        comparator: EqualityComparator<TKey> = ProbeMap.DEFAULT_COMPARATOR
    ) : this(elements.asIterable(), hasher, comparator)

    constructor(
        elements: Collection<Map.Entry<TKey, TValue>>,
        hasher: Hasher = ProbeMap.DEFAULT_HASHER,
        comparator: EqualityComparator<TKey> = ProbeMap.DEFAULT_COMPARATOR
    ) : this(elements.asIterable(), hasher, comparator)

    constructor(
        elements: Iterable<Map.Entry<TKey, TValue>>,
        hasher: Hasher = ProbeMap.DEFAULT_HASHER,
        comparator: EqualityComparator<TKey> = ProbeMap.DEFAULT_COMPARATOR
    ) : this(hasher, comparator) {
        for ((key, value) in elements) {
            this[key] = value
        }
    }

    private var data: Array<Probe<MutableEntry<TKey, TValue>>> =
        Array((this.hasher.initialCapacity / this.hasher.loadFactor).toInt()) { Probe.Empty }

    override var size: Int = 0
        private set

    val capacity: Int
        get() = this.data.size

    override operator fun get(key: TKey): TValue? {
        val index = this.findIndex(key)

        return index?.let {
            val probe = this.data[it] as Probe.Valid<MutableEntry<TKey, TValue>>
            val entry = probe.item

            return entry.value
        }
    }

    override fun put(key: TKey, value: TValue): TValue? {
        this.rehashIfNeeded(1)

        val oldValue = this.insertEntry(key, value, this.data)

        if (null === oldValue) {
            ++(this.size)
            ++(super.modCount)
        }

        return oldValue
    }

    override fun putAll(from: Map<out TKey, TValue>) {
        this.rehashIfNeeded(from.size)

        super.putAll(from)
    }

    private fun rehashIfNeeded(amountToAdd: Int) {
        val nextSize = this.size + amountToAdd
        val load = nextSize / this.capacity.toFloat()

        if (load >= this.hasher.loadFactor) {
            val newCapacity = this.hasher.findNextCapacity(nextSize - 1)

            this.reallocate(newCapacity)
        }
    }

    private fun insertEntry(
        key: TKey,
        value: TValue,
        itemData: Array<Probe<MutableEntry<TKey, TValue>>>
    ): TValue? {
        val startIndex = this.comparator.hashCode(key).mod(itemData.size)

        var index = startIndex
        var count = 1

        while (itemData[index] is Probe.Valid<MutableEntry<TKey, TValue>>) {
            val probe = itemData[index] as Probe.Valid<MutableEntry<TKey, TValue>>
            val existingEntry = probe.item

            if (this.comparator.equals(key, existingEntry.key)) {
                val oldValue = existingEntry.value

                existingEntry.setValue(value)

                return oldValue
            }

            ++count
            index = this.hasher.hashCode2(startIndex, count).mod(itemData.size)
        }

        val newEntry = CustomEntry(key, value)
        itemData[index] = Probe.Valid(newEntry)

        return null
    }

    override fun remove(key: TKey): TValue? {
        val index = this.findIndex(key)

        return this.removeByIndex(index)
    }

    private fun removeByIndex(index: Int?): TValue? {
        index?.let {
            val probe = this.data[it] as Probe.Valid<MutableEntry<TKey, TValue>>
            val entry = probe.item
            val value = entry.value

            this.data[it] = Probe.Deleted

            --(this.size)
            ++(super.modCount)

            return value
        }

        return null
    }

    override fun clear() {
        val capacity = (this.hasher.initialCapacity / this.hasher.loadFactor).toInt()

        this.data = Array(capacity) { Probe.Empty }
        this.size = 0
        ++(super.modCount)
    }

    override fun containsKey(key: TKey): Boolean = null !== this.findIndex(key)

    private fun findIndex(key: TKey): Int? {
        val startIndex = this.comparator.hashCode(key) % this.capacity

        var index = startIndex
        var count = 1

        while (this.data[index] !is Probe.Empty) {
            val probe = this.data[index]

            if (probe is Probe.Valid<MutableEntry<TKey, TValue>>) {
                val entry = probe.item

                if (this.comparator.equals(key, entry.key)) {
                    return index
                }
            }

            ++count
            index = this.hasher.hashCode2(startIndex, count) % this.capacity
        }

        return null
    }

    private fun reallocate(newCapacity: Int) {
        val newData = Array<Probe<MutableEntry<TKey, TValue>>>(newCapacity) { Probe.Empty }

        for (item in this.entries) {
            this.insertEntry(item.key, item.value, newData)
        }

        this.data = newData
    }

    override val entries: MutableSet<MutableEntry<TKey, TValue>>
        get() = object : AbstractEntrySet<TKey, TValue>(this) {
            override fun iterator(): MutableIterator<MutableEntry<TKey, TValue>> = object : MutableIterator<MutableEntry<TKey, TValue>> {
                private var modCount: Int = this@ProbeMap.modCount
                private var currentIndex: Int = this.findIndex(-1)
                private var lastIndex: Int? = null

                private fun checkModCount() {
                    if (this.modCount != this@ProbeMap.modCount) {
                        throw ConcurrentModificationException()
                    }
                }

                override fun hasNext(): Boolean {
                    this.checkModCount()

                    return this.exceedsCapacity(this.currentIndex)
                }

                private fun exceedsCapacity(index: Int): Boolean = index < this@ProbeMap.capacity

                override fun next(): MutableEntry<TKey, TValue> {
                    if (!this.hasNext()) {
                        throw NoSuchElementException()
                    }

                    val item = (this@ProbeMap.data[this.currentIndex] as Probe.Valid<MutableEntry<TKey, TValue>>).item

                    this.lastIndex = this.currentIndex
                    this.currentIndex = this.findIndex(this.currentIndex)

                    return item
                }

                private fun findIndex(startIndex: Int): Int {
                    var nextIndex = startIndex

                    do {
                        ++nextIndex
                    } while (this.exceedsCapacity(nextIndex) && this@ProbeMap.data[this.currentIndex] !is Probe.Valid<MutableEntry<TKey, TValue>>)

                    return nextIndex
                }

                override fun remove() {
                    this.checkModCount()

                    if (null === this@ProbeMap.removeByIndex(this.lastIndex)) {
                        throw IllegalStateException()
                    }
                    else {
                        ++(this.modCount)
                        this.lastIndex = null
                    }
                }
            }
        }
}

fun <TKey, TValue> Map<TKey, TValue>.toProbeMap(): ProbeMap<TKey, TValue> = ProbeMap(this)

fun <TKey, TValue> Collection<Map.Entry<TKey, TValue>>.toProbeMap(): ProbeMap<TKey, TValue> = ProbeMap(this)

fun <TKey, TValue> Iterable<Map.Entry<TKey, TValue>>.toProbeMap(): ProbeMap<TKey, TValue> = ProbeMap(this)

fun <TKey, TValue> Sequence<Map.Entry<TKey, TValue>>.toProbeMap(): ProbeMap<TKey, TValue> = ProbeMap(this)

