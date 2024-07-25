package collections.functional

interface PureList<TElement> : List<TElement> {
    val head: TElement

    val tail: PureList<TElement>

    fun set(index: Int, element: TElement): PureList<TElement>

    fun update(elements: Iterable<IndexedValue<TElement>>): PureList<TElement>

    fun prepend(element: TElement): PureList<TElement>

    fun prependAll(elements: Collection<TElement>): PureList<TElement>

    fun append(element: TElement): PureList<TElement>

    fun appendAll(elements: Collection<TElement>): PureList<TElement>

    fun insert(index: Int, element: TElement): PureList<TElement>

    fun insertAll(index: Int, elements: Collection<TElement>): PureList<TElement>

    fun remove(element: TElement): PureList<TElement>

    fun removeAll(elements: Collection<TElement>): PureList<TElement>

    fun removeAt(index: Int): PureList<TElement>

    fun removeAt(indices: Iterable<Int>): PureList<TElement>

    fun drawFirst(amount: Int): PureList<TElement>

    fun drawLast(amount: Int): PureList<TElement>

    fun skipFirst(amount: Int): PureList<TElement>

    fun skipLast(amount: Int): PureList<TElement>

    fun split(index: Int): Pair<PureList<TElement>, PureList<TElement>>

    override fun subList(fromIndex: Int, toIndex: Int): PureList<TElement>

    fun reverse(): PureList<TElement>

    fun rotate(amount: Int): PureList<TElement>

    fun <TOther> transform(operation: (TElement) -> TOther): PureList<TOther>

    fun sieve(predicate: (TElement) -> Boolean): PureList<TElement>

    fun sort(): PureList<TElement>

    fun sort(comp: (TElement, TElement) -> Int): PureList<TElement>

    fun partition(predicate: (TElement) -> Boolean): Pair<PureList<TElement>, PureList<TElement>>
}

fun <TElement, TOther> pair(left: PureList<TElement>, right: PureList<TElement>): PureList<Pair<TElement, TOther>> {
    TODO()
}
