package collections

import java.io.Serializable

class MapBasedSet<TElement>(
    base: MutableMap<TElement, Unit>
) : AbstractSet<TElement>(base), Serializable {
    private companion object {
        @Suppress("ConstPropertyName")
        const val serialVersionUID: Long = 1L
    }
}

class MapBasedSortedSet<TElement>(
    base: MutableSortedMap<TElement, Unit>
) : AbstractSortedSet<TElement>(base), Serializable {
    private companion object {
        @Suppress("ConstPropertyName")
        const val serialVersionUID: Long = 1L
    }
}
