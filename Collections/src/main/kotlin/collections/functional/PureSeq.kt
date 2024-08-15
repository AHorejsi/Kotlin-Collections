package collections.functional

/*sealed class PureSeq<TElement> : PureList<TElement> {
    companion object {
        fun <TItem> empty(): PureSeq<TItem> = PureSeq.Empty()
    }

    private class Empty<TItem> : PureSeq<TItem>() {
        override fun isEmpty(): Boolean =
            true
    }

    override val head: TElement
        get() = this[0]

    override val tail: PureSeq<TElement>
        get() =
            if (this.isEmpty())
                throw NoSuchElementException()
            else
                this.subList(1, this.size)

    override fun isEmpty(): Boolean =
        false

    override fun set(index: Int, element: TElement): PureSeq<TElement> {
        val indexed = IndexedValue(index, element)
        val singleton = listOf(indexed)

        return this.setAll(singleton)
    }

    override fun setAll(elements: Collection<IndexedValue<TElement>>): PureSeq<TElement> {
        TODO("Not yet implemented")
    }

    override fun prepend(element: TElement): PureSeq<TElement> {
        val singleton = listOf(element)

        return this.prependAll(singleton)
    }

    override fun prependAll(elements: Collection<TElement>): PureSeq<TElement> {
        TODO("Not yet implemented")
    }

    override fun append(element: TElement): PureSeq<TElement> {
        val singleton = listOf(element)

        return this.appendAll(singleton)
    }

    override fun appendAll(elements: Collection<TElement>): PureSeq<TElement> {
        TODO("Not yet implemented")
    }

    override fun subList(fromIndex: Int, toIndex: Int): PureSeq<TElement> {
        TODO("Not yet implemented")
    }

    override fun insert(index: Int, element: TElement): PureSeq<TElement> {
        val singleton = listOf(element)

        return this.insertAll(index, singleton)
    }

    override fun insertAll(index: Int, elements: Collection<TElement>): PureSeq<TElement> {
        TODO("Not yet implemented")
    }
}*/
