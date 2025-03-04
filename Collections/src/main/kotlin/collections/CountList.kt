package collections

import java.io.Serializable

data class SearchCounter<TKey>(
    var amount: Int,
    val item: TKey
) : Serializable {
    private companion object {
        @Suppress("ConstPropertyName")
        const val serialVersionUID: Long = 1L
    }
}

class CountList<TElement> : AbstractCollection<TElement>(), SelfOrgList<TElement>, Serializable {
    private companion object {
        @Suppress("ConstPropertyName")
        const val serialVersionUID: Long = 1L
    }

    private val jump: JumpList<SearchCounter<TElement>> =
        JumpList{ past, now ->
            past.value.amount > now.value.amount
        }

    override val size: Int
        get() = this.jump.size

    override val isRandomAccess: Boolean
        get() = this.jump.isRandomAccess

    override fun isEmpty(): Boolean =
        this.jump.isEmpty()

    override fun add(element: TElement): Boolean {
        val counter = SearchCounter(0, element)

        return this.jump.add(counter)
    }

    override fun remove(element: TElement): Boolean =
        1 == this.jump.removeAmount(1) { it.item == element }

    override fun clear() =
        this.jump.clear()

    override operator fun contains(element: TElement): Boolean =
        super<SelfOrgList>.contains(element)

    override fun containsAll(elements: Collection<TElement>): Boolean =
        super<SelfOrgList>.containsAll(elements)

    override fun find(predicate: (TElement) -> Boolean): IndexedValue<TElement>? =
        this.amount(predicate)?.let { IndexedValue(it.index, it.value.item) }

    fun amount(element: @UnsafeVariance TElement): IndexedValue<SearchCounter<TElement>>? =
        this.amount{ it == element }

    fun amount(predicate: (TElement) -> Boolean): IndexedValue<SearchCounter<TElement>>? {
        val search = this.jump.find{ predicate(it.item) }

        this.countUp(search)

        return search
    }

    override fun findAll(predicate: (TElement) -> Boolean): Sequence<IndexedValue<TElement>> =
        this.amountAll(predicate).map{ IndexedValue(it.index, it.value.item) }

    fun amountAll(elements: Collection<@UnsafeVariance TElement>): Sequence<IndexedValue<SearchCounter<TElement>>> =
        this.amountAll{ it in elements }

    fun amountAll(predicate: (TElement) -> Boolean): Sequence<IndexedValue<SearchCounter<TElement>>> = sequence {
        for (indexed in this@CountList.jump.withIndex()) {
            val counter = indexed.value

            if (predicate(counter.item)) {
                this@CountList.countUp(indexed)

                yield(indexed)
            }
        }
    }

    private fun countUp(indexed: IndexedValue<SearchCounter<TElement>>?) {
        if (null === indexed) {
            return
        }

        val counter = indexed.value

        ++(counter.amount)
    }

    override fun iterator(): MutableIterator<TElement> = object : MutableIterator<TElement> {
        private val iter = this@CountList.jump.iterator()

        override fun hasNext(): Boolean =
            this.iter.hasNext()

        override fun next(): TElement =
            this.iter.next().item

        override fun remove() =
            this.iter.remove()
    }
}