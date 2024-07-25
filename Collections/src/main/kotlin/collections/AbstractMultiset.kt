package collections

@Suppress("RemoveRedundantQualifierName")
abstract class AbstractMultiset<TElement>(
    private val base: MutableMap<TElement, MutableList<TElement>>,
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

    override fun remove(element: TElement): Boolean = this.remove(element, 1) > 0

    override fun remove(element: TElement, amount: Int): Int {
        AbstractMultiset.checkAmount(amount)

        val list = this.base[element]

        if (null === list) {
            return 0
        }

        val amountRemoved = this.deleteItems(element, list, amount)

        this.size -= amountRemoved
        ++(super.modCount)

        return amountRemoved
    }

    private fun deleteItems(element: TElement, list: MutableList<TElement>, amount: Int): Int {
        val amountRemoved = list.removeFromBack(amount)

        if (list.isEmpty()) {
            this.base.remove(element)
        }

        return amountRemoved
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
