package collections

abstract class AbstractList<TElement> : AbstractCollection<TElement>(), MutableList<TElement>, RandomAccess {
    override fun add(element: TElement): Boolean {
        this.add(this.size, element)

        return true
    }

    override fun add(index: Int, element: TElement) {
        val list = listOf(element)

        this.addAll(index, list)
    }

    override fun addAll(elements: Collection<TElement>): Boolean = this.addAll(this.size, elements)

    override fun remove(element: TElement): Boolean {
        val index = this.indexOf(element)

        if (-1 == index) {
            return false
        }

        this.removeAt(index)

        return true
    }

    override operator fun contains(element: @UnsafeVariance TElement): Boolean = -1 != this.indexOf(element)

    override fun indexOf(element: @UnsafeVariance TElement): Int = this.index(0, element)

    override fun lastIndexOf(element: @UnsafeVariance TElement): Int = this.lastIndex(this.lastIndex, element)

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }

        if (other !is List<*> || this.size != other.size) {
            return false
        }

        for (index in this.indices) {
            if (this[index] != other[index]) {
                return false
            }
        }

        return true
    }

    override fun hashCode(): Int {
        var hashValue = 31 * this.size

        for (index in this.indices) {
            hashValue = 31 * hashValue + this[index].hashCode()
        }

        return hashValue
    }

    override fun subList(fromIndex: Int, toIndex: Int): MutableList<TElement> =
        Sublist(this, fromIndex, toIndex)

    override fun iterator(): MutableIterator<TElement> = this.listIterator()

    override fun listIterator(): MutableListIterator<TElement> = this.listIterator(0)

    override fun listIterator(index: Int): MutableListIterator<TElement> = object : MutableListIterator<TElement> {
        init {
            if (index < 0 || index > this@AbstractList.size) {
                throw IndexOutOfBoundsException("0 <= index <= size. Index = $index, size = ${this@AbstractList.size}")
            }
        }

        private var currentIndex: Int = index
        private var lastUsedIndex: Int? = null
        private var modCount: Int = this@AbstractList.modCount

        private fun checkModCount() {
            if (this.modCount != this@AbstractList.modCount) {
                throw ConcurrentModificationException("Modifications made to underlying List")
            }
        }

        override fun previousIndex(): Int {
            this.checkModCount()

            return this.currentIndex - 1
        }

        override fun nextIndex(): Int {
            this.checkModCount()

            return this.currentIndex
        }

        override fun hasPrevious(): Boolean = this.previousIndex() >= 0

        override fun hasNext(): Boolean = this.nextIndex() < this@AbstractList.size

        override fun previous(): TElement {
            if (!this.hasPrevious()) {
                throw NoSuchElementException("ListIterator at its beginning")
            }

            val usedIndex = this.previousIndex()
            val item = this@AbstractList[usedIndex]

            this.lastUsedIndex = usedIndex
            --(this.currentIndex)

            return item
        }

        override fun next(): TElement {
            if (!this.hasNext()) {
                throw NoSuchElementException("ListIterator at its end")
            }

            val usedIndex = this.nextIndex()
            val item = this@AbstractList[usedIndex]

            this.lastUsedIndex = usedIndex
            ++(this.currentIndex)

            return item
        }

        override fun set(element: TElement) {
            this.checkModCount()

            this.lastUsedIndex?.let {
                this@AbstractList[it] = element
            } ?: throw IllegalStateException("No item to set")
        }

        override fun remove() {
            this.checkModCount()

            this.lastUsedIndex?.let {
                this@AbstractList.removeAt(it)
                --(this.currentIndex)

                ++(this.modCount)
                this.lastUsedIndex = null
            } ?: throw IllegalStateException("No item to remove")
        }

        override fun add(element: TElement) {
            this.checkModCount()

            this@AbstractList.add(this.nextIndex(), element)

            ++(this.modCount)
        }
    }
}