package collections

interface CounterSet<TElement> : Collection<TElement> {
    val cardinality: Int

    fun multiplicity(element: @UnsafeVariance TElement): Int

    fun distinctIterator(): Iterator<TElement>
}

interface MutableCounterSet<TElement> : CounterSet<TElement>, MutableCollection<TElement> {
    fun add(element: TElement, amount: Int): TElement

    fun remove(element: TElement, amount: Int): Int

    override fun distinctIterator(): MutableIterator<TElement>
}
