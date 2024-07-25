package collections

import java.io.Serializable

@Suppress("RemoveRedundantQualifierName")
class ChainSet<TElement>(
    initialCapacity: Int = ChainSet.DEFAULT_CAPACITY,
    loadFactor: Float = ChainSet.DEFAULT_LOAD_FACTOR,
    comparator: EqualityComparator<TElement> = ChainSet.DEFAULT_COMPARATOR
) : AbstractSet<TElement>(ChainMap(initialCapacity, loadFactor, comparator)), Serializable {
    private companion object {
        @Suppress("ConstPropertyName")
        const val serialVersionUID: Long = 1L

        const val DEFAULT_CAPACITY: Int = 16
        const val DEFAULT_LOAD_FACTOR: Float = 0.75f
        val DEFAULT_COMPARATOR: EqualityComparator<Any?> = EqualityComparator.Default
    }

    constructor(
        vararg elements: TElement,
        initialCapacity: Int = ChainSet.DEFAULT_CAPACITY,
        loadFactor: Float = ChainSet.DEFAULT_LOAD_FACTOR,
        comparator: EqualityComparator<TElement> = ChainSet.DEFAULT_COMPARATOR
    ) : this(initialCapacity, loadFactor, comparator) {
        this.addAll(elements)
    }

    constructor(
        elements: Collection<TElement>,
        initialCapacity: Int = ChainSet.DEFAULT_CAPACITY,
        loadFactor: Float = ChainSet.DEFAULT_LOAD_FACTOR,
        comparator: EqualityComparator<TElement> = ChainSet.DEFAULT_COMPARATOR
    ) : this(initialCapacity, loadFactor, comparator) {
        this.addAll(elements)
    }

    constructor(
        elements: Iterable<TElement>,
        initialCapacity: Int = ChainSet.DEFAULT_CAPACITY,
        loadFactor: Float = ChainSet.DEFAULT_LOAD_FACTOR,
        comparator: EqualityComparator<TElement> = ChainSet.DEFAULT_COMPARATOR
    ) : this(initialCapacity, loadFactor, comparator) {
        this.addAll(elements)
    }

    constructor(
        elements: Sequence<TElement>,
        initialCapacity: Int = ChainSet.DEFAULT_CAPACITY,
        loadFactor: Float = ChainSet.DEFAULT_LOAD_FACTOR,
        comparator: EqualityComparator<TElement> = ChainSet.DEFAULT_COMPARATOR
    ) : this(elements.asIterable(), initialCapacity, loadFactor, comparator)
}

fun <TElement> Iterable<TElement>.toChainSet(): ChainSet<TElement> = ChainSet(this)

fun <TElement> Collection<TElement>.toChainSet(): ChainSet<TElement> = ChainSet(this)

fun <TElement> Sequence<TElement>.toChainSet(): ChainSet<TElement> = ChainSet(this)

fun <TElement> Array<out TElement>.toChainSet(): ChainSet<TElement> = ChainSet(*this)