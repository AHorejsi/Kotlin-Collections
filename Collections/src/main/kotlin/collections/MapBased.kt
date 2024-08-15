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

private class MapBasedMultiset<TElement>(
    base: MutableMap<TElement, DequeList<TElement>>
) : AbstractMultiset<TElement>(base), Serializable {
    private companion object {
        @Suppress("ConstPropertyName")
        const val serialVersionUID: Long = 1L
    }
}

fun <TElement> asMutableSet(map: MutableMap<TElement, Unit>): MutableSet<TElement> =
    MapBasedSet(map)

fun <TElement> asMutableSet(map: MutableSortedMap<TElement, Unit>): MutableSortedSet<TElement> =
    MapBasedSortedSet(map)

fun <TElement> asMutableMultiset(map: MutableMap<TElement, DequeList<TElement>>): MutableMultiset<TElement> =
    MapBasedMultiset(map)
