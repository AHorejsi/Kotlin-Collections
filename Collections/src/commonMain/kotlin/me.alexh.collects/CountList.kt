package me.alexh.collects

import kotlin.jvm.Transient

class Count<TElement>(
    var element: TElement,
    var amount: Int = 0,
    internal var next: Count<TElement>? = null,
    internal var prev: Count<TElement>? = null
)

class CountList<TElement> : SealedMutableCollection<TElement>(), SelfOrgList<TElement> {
    @Transient
    private var modCount: Int = 0
    private var head: Count<TElement>? = null
    private var tail: Count<TElement>? = null
    override var size: Int = 0
        private set

    override fun get(index: Int): TElement = this.getCount(index).element

    fun getCount(index: Int): Count<TElement> {
        if (index < 0 || index >= this.size) {
            throw IndexOutOfBoundsException()
        }

        var currentNode = this.head
        var currentIndex = 0

        while (currentIndex != index) {
            ++currentIndex
            currentNode = currentNode!!.next
        }

        return currentNode!!
    }

    override fun add(element: TElement): Boolean {
        val newNode = Count(element)

        this.tail?.let {
            it.next = newNode
            newNode.prev = it

            this.tail = newNode
        } ?: run {
            this.head = newNode
            this.tail = newNode
        }

        ++(this.size)
        ++(this.modCount)

        return true
    }

    override fun count(predicate: Predicate<in TElement>): Int {
        val iter = this.iteratorCount()
        var total = 0

        while (iter.hasNext()) {
            val counter = iter.next()

            if (predicate(counter.element)) {
                ++total
                ++(counter.amount)
            }
        }

        return total
    }

    override fun find(predicate: Predicate<in TElement>): ListIterator<TElement> {
        val node = this.head

        while (null !== node) {
            if (predicate(node.element)) {
                ++(node.amount)

                return this.listIterator(this.countListIterator(node))
            }
        }

        return this.back()
    }

    override fun remove(element: TElement): Boolean {
        val iter = this.iterator()

        while (iter.hasNext()) {
            val item = iter.next()

            if (item == element) {
                iter.remove()

                return true
            }
        }

        return false
    }

    override fun clear() {
        this.head = null
        this.tail = null
        this.size = 0
        ++(this.modCount)
    }

    fun iteratorCount(): MutableIterator<Count<TElement>> = object : MutableIterator<Count<TElement>> {
        private var current: Count<TElement>? = this@CountList.head
        private var state: IteratorState = IteratorState.INITIALIZED

        override fun hasNext(): Boolean = null !== this.current

        override fun next(): Count<TElement> {
            if (!this.hasNext()) {
                throw NoSuchElementException()
            }
            else {
                val node = this.current!!

                this.state = IteratorState.CALLED_NEXT
                this.current = node.next

                return node
            }
        }

        override fun remove() {
            val node = this.getLastAccessed()
            val prevNode = node.prev
            val nextNode = node.next

            prevNode?.next = nextNode
            nextNode?.prev = prevNode

            --(this@CountList.size)
            ++(this@CountList.modCount)
        }

        private fun getLastAccessed(): Count<TElement> =
            when (this.state) {
                IteratorState.INITIALIZED, IteratorState.CALLED_REMOVE -> throw IllegalStateException()
                IteratorState.CALLED_NEXT -> this.current!!.prev!!
            }
    }

    override fun iterator(): MutableIterator<TElement> = object : MutableIterator<TElement> {
        private val iter: MutableIterator<Count<TElement>> = this@CountList.iteratorCount()

        override fun hasNext(): Boolean = this.iter.hasNext()

        override fun next(): TElement = this.iter.next().element

        override fun remove() = this.iter.remove()
    }

    fun frontCount(): ListIterator<Count<TElement>> = this.countListIterator(this.head)

    fun backCount(): ListIterator<Count<TElement>> = this.countListIterator(this.tail)

    private fun countListIterator(node: Count<TElement>?): ListIterator<Count<TElement>> = object : ListIterator<Count<TElement>> {
        private var index: Int = if (node === this@CountList.head) (0) else (this@CountList.size - 1)
        private var current: Count<TElement>? = node

        override fun previousIndex(): Int = this.index - 1

        override fun nextIndex(): Int = this.index

        override fun hasPrevious(): Boolean = -1 != this.previousIndex()

        override fun hasNext(): Boolean = this@CountList.size != this.nextIndex()

        override fun previous(): Count<TElement> {
            if (!this.hasPrevious()) {
                throw NoSuchElementException()
            }

            val current = this.current!!

            ++(this.index)
            this.current = current.next

            return current
        }

        override fun next(): Count<TElement> {
            if (!this.hasNext()) {
                throw NoSuchElementException()
            }

            val current = this.current!!

            --(this.index)
            this.current = current.prev

            return current.prev!!
        }
    }

    override fun front(): ListIterator<TElement> = this.listIterator(this.frontCount())

    override fun back(): ListIterator<TElement> = this.listIterator(this.backCount())

    private fun listIterator(iter: ListIterator<Count<TElement>>): ListIterator<TElement> = object : ListIterator<TElement> {
        private val iter: ListIterator<Count<TElement>> = iter

        override fun hasNext(): Boolean = this.iter.hasNext()

        override fun hasPrevious(): Boolean = this.iter.hasPrevious()

        override fun nextIndex(): Int = this.iter.nextIndex()

        override fun previousIndex(): Int = this.iter.previousIndex()

        override fun next(): TElement = this.iter.next().element

        override fun previous(): TElement = this.iter.previous().element
    }
}
