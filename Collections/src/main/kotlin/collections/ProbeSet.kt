package collections

import java.io.Serializable

@Suppress("RemoveRedundantQualifierName")
class ProbeSet<TElement>(
    hasher: Hasher = ProbeSet.DEFAULT_HASHER,
    comparator: EqualityComparator<TElement> = ProbeSet.DEFAULT_COMPARATOR
) : AbstractSet<TElement>(ProbeMap(hasher, comparator)), Serializable {
    private companion object {
        @Suppress("ConstPropertyName")
        const val serialVersionUID: Long = 1L

        val DEFAULT_HASHER: Hasher = Hasher.Quadratic
        val DEFAULT_COMPARATOR: EqualityComparator<Any?> = EqualityComparator.Default
    }

    constructor(
        vararg elements: TElement,
        hasher: Hasher = ProbeSet.DEFAULT_HASHER,
        comparator: EqualityComparator<TElement> = ProbeSet.DEFAULT_COMPARATOR
    ) : this(hasher, comparator) {
        this.addAll(elements)
    }

    constructor(
        elements: Collection<TElement>,
        hasher: Hasher = ProbeSet.DEFAULT_HASHER,
        comparator: EqualityComparator<TElement> = ProbeSet.DEFAULT_COMPARATOR
    ) : this(hasher, comparator) {
        this.addAll(elements)
    }

    constructor(
        elements: Iterable<TElement>,
        hasher: Hasher = ProbeSet.DEFAULT_HASHER,
        comparator: EqualityComparator<TElement> = ProbeSet.DEFAULT_COMPARATOR
    ) : this(hasher, comparator) {
        this.addAll(elements)
    }

    constructor(
        elements: Sequence<TElement>,
        hasher: Hasher = ProbeSet.DEFAULT_HASHER,
        comparator: EqualityComparator<TElement> = ProbeSet.DEFAULT_COMPARATOR
    ) : this(elements.asIterable(), hasher, comparator)
}

fun <TElement> Iterable<TElement>.toProbeSet(): ProbeSet<TElement> = ProbeSet(this)

fun <TElement> Collection<TElement>.toProbeSet(): ProbeSet<TElement> = ProbeSet(this)

fun <TElement> Sequence<TElement>.toProbeSet(): ProbeSet<TElement> = ProbeSet(this)

fun <TElement> Array<out TElement>.toProbeSet(): ProbeSet<TElement> = ProbeSet(*this)
