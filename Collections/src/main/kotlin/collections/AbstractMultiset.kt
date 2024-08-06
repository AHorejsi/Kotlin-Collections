package collections

@Suppress("RemoveRedundantQualifierName")
abstract class AbstractMultiset<TElement>(
    private val base: MutableMap<TElement, DequeList<TElement>>,
) : AbstractCollection<TElement>(), MutableMultiset<TElement> {
    private companion object {
        private fun checkAmount(amount: Int) {
            if (amount < 0) {
                throw IllegalArgumentException("Negative amount: $amount")
            }
        }
    }

    override var size: Int = 0
        protected set

    override val cardinality: Int
        get() = this.base.size

    override operator fun get(element: TElement): List<TElement> = this.base[element] ?: emptyList()

    override fun multiplicity(element: TElement): Int = this[element].size

    override fun add(element: TElement): Boolean {
        val list = this.base[element]

        if (null !== list) {
            list.add(element)
        }
        else {
            this.base[element] = dequeListOf(element)
        }

        ++(this.size)
        ++(super.modCount)

        return true
    }

    override fun remove(element: TElement): Boolean {
        val (_, changed) = this.remove(element, 1)

        return changed
    }

    override fun remove(element: TElement, amount: Int): Pair<Int, Boolean> {
        AbstractMultiset.checkAmount(amount)

        val list = this.base[element]

        return if (null === list)
            0 to false
        else
            this.deleteItems(element, list, amount)
    }

    private fun deleteItems(element: TElement, list: DequeList<TElement>, amount: Int): Pair<Int, Boolean> {
        val amountRemoved = list.removeFromFront(amount)
        var changed = false

        if (list.isEmpty()) {
            this.base.remove(element)
            changed = true
        }

        this.size -= amountRemoved
        ++(super.modCount)

        return amountRemoved to changed
    }

    override fun clear() {
        this.base.clear()
        this.size = 0
        ++(super.modCount)
    }

    override fun distinctIterator(): MutableIterator<TElement> = this.base.keys.iterator()

    override fun iterator(): MutableIterator<TElement> = object : MutableIterator<TElement> {
        private val base = this@AbstractMultiset.base.values.iterator()
        private var current = this.base.tryNext().map{ it.iterator() }.getOrDefault(mutableListOf<TElement>().iterator())

        override fun hasNext(): Boolean {
            if (this.current.hasNext()) {
                return true
            }

            if (this.base.hasNext()) {
                this.current = this.base.next().iterator()

                return true
            }

            return false
        }

        override fun next(): TElement {
            if (!this.hasNext()) {
                throw NoSuchElementException()
            }

            return this.current.next()
        }

        override fun remove() = this.current.remove()
    }
}
