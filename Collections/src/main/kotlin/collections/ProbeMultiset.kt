package collections

import java.io.Serializable

@Suppress("RemoveRedundantQualifierName")
class ProbeMultiset<TElement>(
    hasher: Hasher = ProbeMultiset.DEFAULT_HASHER,
    comparator: EqualityComparator<TElement> = ProbeMultiset.DEFAULT_COMPARATOR
) : AbstractMultiset<TElement>(ProbeMap(hasher, comparator)), Serializable {
    private companion object {
        @Suppress("ConstPropertyName")
        const val serialVersionUID: Long = 1L

        val DEFAULT_HASHER: Hasher = Hasher.Quadratic
        val DEFAULT_COMPARATOR: EqualityComparator<Any?> = EqualityComparator.Default
    }

    constructor(
        vararg elements: TElement,
        hasher: Hasher = ProbeMultiset.DEFAULT_HASHER,
        comparator: EqualityComparator<TElement> = ProbeMultiset.DEFAULT_COMPARATOR
    ) : this(hasher, comparator) {
        this.addAll(elements)
    }

    constructor(
        elements: Collection<TElement>,
        hasher: Hasher = ProbeMultiset.DEFAULT_HASHER,
        comparator: EqualityComparator<TElement> = ProbeMultiset.DEFAULT_COMPARATOR
    ) : this(hasher, comparator) {
        this.addAll(elements)
    }

    constructor(
        elements: Iterable<TElement>,
        hasher: Hasher = ProbeMultiset.DEFAULT_HASHER,
        comparator: EqualityComparator<TElement> = ProbeMultiset.DEFAULT_COMPARATOR
    ) : this(hasher, comparator) {
        this.addAll(elements)
    }

    constructor(
        elements: Sequence<TElement>,
        hasher: Hasher = ProbeMultiset.DEFAULT_HASHER,
        comparator: EqualityComparator<TElement> = ProbeMultiset.DEFAULT_COMPARATOR
    ) : this(elements.asIterable(), hasher, comparator)
}

fun <TElement> Iterable<TElement>.toProbeMultiset(): ProbeMultiset<TElement> = ProbeMultiset(this)

fun <TElement> Collection<TElement>.toProbeMultiset(): ProbeMultiset<TElement> = ProbeMultiset(this)

fun <TElement> Sequence<TElement>.toProbeMultiset(): ProbeMultiset<TElement> = ProbeMultiset(this)

fun <TElement> Array<out TElement>.toProbeMultiset(): ProbeMultiset<TElement> = ProbeMultiset(*this)