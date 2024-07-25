package collections

import java.io.Serializable

@Suppress("RemoveRedundantQualifierName")
class ChainMultiset<TElement>(
    initialCapacity: Int = ChainMultiset.DEFAULT_CAPACITY,
    loadFactor: Float = ChainMultiset.DEFAULT_LOAD_FACTOR,
    comparator: EqualityComparator<TElement> = ChainMultiset.DEFAULT_COMPARATOR
) : AbstractMultiset<TElement>(ChainMap(initialCapacity, loadFactor, comparator)), Serializable {
    private companion object {
        @Suppress("ConstPropertyName")
        const val serialVersionUID: Long = 1L

        const val DEFAULT_CAPACITY: Int = 16
        const val DEFAULT_LOAD_FACTOR: Float = 0.75f
        val DEFAULT_COMPARATOR: EqualityComparator<Any?> = EqualityComparator.Default
    }

    constructor(
        vararg elements: TElement,
        initialCapacity: Int = ChainMultiset.DEFAULT_CAPACITY,
        loadFactor: Float = ChainMultiset.DEFAULT_LOAD_FACTOR,
        comparator: EqualityComparator<TElement> = ChainMultiset.DEFAULT_COMPARATOR
    ) : this(initialCapacity, loadFactor, comparator) {
        this.addAll(elements)
    }

    constructor(
        elements: Collection<TElement>,
        initialCapacity: Int = ChainMultiset.DEFAULT_CAPACITY,
        loadFactor: Float = ChainMultiset.DEFAULT_LOAD_FACTOR,
        comparator: EqualityComparator<TElement> = ChainMultiset.DEFAULT_COMPARATOR
    ) : this(initialCapacity, loadFactor, comparator) {
        this.addAll(elements)
    }

    constructor(
        elements: Iterable<TElement>,
        initialCapacity: Int = ChainMultiset.DEFAULT_CAPACITY,
        loadFactor: Float = ChainMultiset.DEFAULT_LOAD_FACTOR,
        comparator: EqualityComparator<TElement> = ChainMultiset.DEFAULT_COMPARATOR
    ) : this(initialCapacity, loadFactor, comparator) {
        this.addAll(elements)
    }

    constructor(
        elements: Sequence<TElement>,
        initialCapacity: Int = ChainMultiset.DEFAULT_CAPACITY,
        loadFactor: Float = ChainMultiset.DEFAULT_LOAD_FACTOR,
        comparator: EqualityComparator<TElement> = ChainMultiset.DEFAULT_COMPARATOR
    ) : this(elements.asIterable(), initialCapacity, loadFactor, comparator)
}

fun <TElement> Iterable<TElement>.toChainMultiset(): ChainMultiset<TElement> = ChainMultiset(this)

fun <TElement> Collection<TElement>.toChainMultiset(): ChainMultiset<TElement> = ChainMultiset(this)

fun <TElement> Sequence<TElement>.toChainMultiset(): ChainMultiset<TElement> = ChainMultiset(this)

fun <TElement> Array<out TElement>.toChainMultiset(): ChainMultiset<TElement> = ChainMultiset(*this)
