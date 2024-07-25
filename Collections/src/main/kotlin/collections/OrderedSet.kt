package collections

import java.io.Serializable

class OrderedSet<TElement>(
    map: MutableMap<TElement, MutableLinkedListNode<MutableEntry<TElement, Unit>>> = ChainMap(),
    list: MutableLinkedList<MutableEntry<TElement, Unit>> = BidirList()
) : AbstractSet<TElement>(OrderedMap(map, list)), Serializable {
    private companion object {
        @Suppress("ConstPropertyName")
        const val serialVersionUID: Long = 1L
    }

    constructor(
        elements: Sequence<TElement>,
        map: MutableMap<TElement, MutableLinkedListNode<MutableEntry<TElement, Unit>>> = ChainMap(),
        list: MutableLinkedList<MutableEntry<TElement, Unit>> = BidirList()
    ) : this(elements.asIterable(), map, list)

    constructor(
        vararg elements: TElement,
        map: MutableMap<TElement, MutableLinkedListNode<MutableEntry<TElement, Unit>>> = ChainMap(),
        list: MutableLinkedList<MutableEntry<TElement, Unit>> = BidirList()
    ) : this(map, list) {
        this.addAll(elements)
    }

    constructor(
        elements: Collection<TElement>,
        map: MutableMap<TElement, MutableLinkedListNode<MutableEntry<TElement, Unit>>> = ChainMap(),
        list: MutableLinkedList<MutableEntry<TElement, Unit>> = BidirList()
    ) : this(map, list) {
        this.addAll(elements)
    }

    constructor(
        elements: Iterable<TElement>,
        map: MutableMap<TElement, MutableLinkedListNode<MutableEntry<TElement, Unit>>> = ChainMap(),
        list: MutableLinkedList<MutableEntry<TElement, Unit>> = BidirList()
    ) : this(map, list) {
        this.addAll(elements)
    }
}

fun <TElement> Iterable<TElement>.toOrderedSet(): OrderedSet<TElement> = OrderedSet(this)

fun <TElement> Collection<TElement>.toOrderedSet(): OrderedSet<TElement> = OrderedSet(this)

fun <TElement> Sequence<TElement>.toOrderedSet(): OrderedSet<TElement> = OrderedSet(this)

fun <TElement> Array<out TElement>.toOrderedSet(): OrderedSet<TElement> = OrderedSet(*this)
