package collections

import java.io.Serializable

class RbSet<TElement>(
    comparator: (TElement, TElement) -> Int
) : AbstractSortedSet<TElement>(RbMap(comparator)), Serializable {
    private companion object {
        @Suppress("ConstPropertyName")
        const val serialVersionUID: Long = 1L
    }

    constructor(compObj: Comparator<TElement>? = null) : this((compObj ?: DefaultComparator())::compare)

    constructor(
        vararg elements: TElement,
        compObj: Comparator<TElement>? = null
    ) : this(elements.asIterable(), compObj)

    constructor(
        elements: Collection<TElement>,
        compObj: Comparator<TElement>? = null
    ) : this(compObj) {
        this.addAll(elements)
    }

    constructor(
        elements: Iterable<TElement>,
        compObj: Comparator<TElement>? = null
    ) : this(compObj) {
        this.addAll(elements)
    }

    constructor(
        elements: Sequence<TElement>,
        compObj: Comparator<TElement>? = null
    ) : this(elements.asIterable(), compObj)
}

fun <TElement> Array<out TElement>.toRbSet(): RbSet<TElement> = RbSet(*this)

fun <TElement> Collection<TElement>.toRbSet(): RbSet<TElement> = RbSet(this)

fun <TElement> Iterable<TElement>.toRbSet(): RbSet<TElement> = RbSet(this)

fun <TElement> Sequence<TElement>.toRbSet(): RbSet<TElement> = RbSet(this)
