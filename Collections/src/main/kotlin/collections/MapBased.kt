package collections

import java.io.Serializable

private class MapBasedSet<TElement>(
    base: MutableMap<TElement, Unit>
) : AbstractSet<TElement>(base), Serializable {
    private companion object {
        @Suppress("ConstPropertyName")
        const val serialVersionUID: Long = 1L
    }
}

private class MapBasedSortedSet<TElement>(
    base: MutableSortedMap<TElement, Unit>
) : AbstractSortedSet<TElement>(base), Serializable {
    private companion object {
        @Suppress("ConstPropertyName")
        const val serialVersionUID: Long = 1L
    }
}

fun <TElement> asMutableSet(base: MutableMap<TElement, Unit>): MutableSet<TElement> =
    MapBasedSet(base)

fun <TElement> asMutableSortedSet(base: MutableSortedMap<TElement, Unit>): MutableSortedSet<TElement> =
    MapBasedSortedSet(base)


