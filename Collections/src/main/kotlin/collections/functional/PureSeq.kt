package collections.functional

/*import collections.empty

sealed class PureSeq<TElement> : PureList<TElement> {
    companion object {
        fun <TItem> empty(): PureSeq<TItem> = PureSeq.Empty()

        fun <TItem> single(only: TItem): PureSeq<TItem> = PureSeq.Singleton(only)
    }

    private class Empty<TItem> : PureSeq<TItem>() {
        override fun isEmpty(): Boolean =
            true
    }

    private class Singleton<TItem>(
        override val head: TItem
    ) : PureSeq<TItem>() {
        override fun isEmpty(): Boolean =
            false
    }

    override val head: TElement
        get() = this[0]

    override val tail: PureSeq<TElement>
        get() =
            when (this.size) {
                0 -> empty("Empty Seq")
                1 -> PureSeq.empty()
                2 -> PureSeq.single(this.head)
                else -> this.subList(1, this.size)
            }

    override fun isEmpty(): Boolean =
        false

    override fun prepend(element: TElement): PureSeq<TElement> {
        val singleton = PureSeq.single(element)

        return this.prependAll(singleton)
    }

    override fun prependAll(elements: Collection<TElement>): PureSeq<TElement> {
        TODO("Not yet implemented")
    }

    override fun append(element: TElement): PureSeq<TElement> {
        val singleton = PureSeq.single(element)

        return this.appendAll(singleton)
    }

    override fun appendAll(elements: Collection<TElement>): PureSeq<TElement> {
        TODO("Not yet implemented")
    }

    override fun insert(index: Int, element: TElement): PureSeq<TElement> {
        val singleton = PureSeq.single(element)

        return this.insertAll(index, singleton)
    }

    override fun insertAll(index: Int, elements: Collection<TElement>): PureSeq<TElement> {
        TODO("Not yet implemented")
    }

    override fun subList(fromIndex: Int, toIndex: Int): PureSeq<TElement> {
        TODO("Not yet implemented")
    }
}*/
