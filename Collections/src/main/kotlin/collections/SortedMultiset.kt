package collections

interface SortedMultiset<TElement> : Multiset<TElement> {
    fun first(): TElement

    fun last(): TElement

    fun lesser(element: TElement, inclusive: Boolean): TElement

    fun greater(element: TElement, inclusive: Boolean): TElement
}

interface MutableSortedMultiset<TElement> : SortedMultiset<TElement>, MutableMultiset<TElement> {
    fun removeFirst(amount: Int = 1): List<TElement>

    fun removeLast(amount: Int = 1): List<TElement>
}
