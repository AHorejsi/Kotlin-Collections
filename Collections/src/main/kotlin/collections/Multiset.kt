package collections

interface Multiset<TElement> : Collection<TElement>{
    val cardinality: Int

    operator fun get(element: TElement): List<TElement>

    fun multiplicity(element: TElement): Int

    fun distinctIterator(): Iterator<TElement>
}

interface MutableMultiset<TElement> : Multiset<TElement>, MutableCollection<TElement> {
    fun remove(element: TElement, amount: Int): Pair<Int, Boolean>

    override operator fun contains(element: TElement): Boolean = this.multiplicity(element) > 0

    override fun distinctIterator(): MutableIterator<TElement>
}
