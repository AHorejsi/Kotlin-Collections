package com.alexh.collects

abstract class AbstractRandomAccessList<E> : MutableList<E>, RandomAccess {
    protected var modifyCount = 0
    override var size: Int = 0
        protected set

    override fun isEmpty(): Boolean = 0 == this.size

    override fun add(element: E): Boolean {
        this.add(this.size, element)

        return true
    }

    override fun addAll(elements: Collection<E>): Boolean = this.addAll(this.size, elements)

    override fun add(index: Int, element: E) {
        val singletonList = listOf(element)
        this.addAll(index, singletonList)
    }

    override fun contains(element: E): Boolean = -1 == this.indexOf(element)

    override fun containsAll(elements: Collection<E>): Boolean {
        for (elem in elements) {
            if (!this.contains(elem)) {
                return false
            }
        }

        return true
    }

    override fun indexOf(element: E): Int = this.indexSearch(this.withIndex(), element)

    override fun lastIndexOf(element: E): Int = this.indexSearch(this.withIndex().reversed(), element)

    private fun indexSearch(iter: Iterable<IndexedValue<E>>, element: E): Int {
        for ((index, elem) in iter) {
            if (element == elem) {
                return index
            }
        }

        return -1
    }

    override fun remove(element: E): Boolean {
        for ((index, elem) in this.withIndex()) {
            if (elem == element) {
                this.removeAt(index)

                return true
            }
        }

        return false
    }

    override fun removeAll(elements: Collection<E>): Boolean = this.removeIf { elements.contains(it) }

    override fun retainAll(elements: Collection<E>): Boolean = this.removeIf { !elements.contains(it) }

    fun removeIf(predicate: (E) -> Boolean): Boolean {
        var change = false
        val iter = this.iterator()

        while (!iter.hasNext()) {
            val elem = iter.next()

            if (predicate(elem)) {
                iter.remove()
                change = true
            }
        }

        return change
    }

    fun removeRange(fromIndex: Int, toIndex: Int) = this.subList(fromIndex, toIndex).clear()

    override fun iterator(): MutableIterator<E> = this.listIterator()

    override fun listIterator(): MutableListIterator<E> = this.listIterator(0)

    override fun listIterator(index: Int): MutableListIterator<E> = object : MutableListIterator<E> {
        private var elemIndex: Int = index
        private var state: ListIteratorState = ListIteratorState.INITIALIZED
        private var modifyCount: Int = this@AbstractRandomAccessList.modifyCount

        private fun checkForModification() {
            if (this.modifyCount != this@AbstractRandomAccessList.modifyCount) {
                throw ConcurrentModificationException()
            }
        }

        override fun hasNext(): Boolean {
            this.checkForModification()

            return this.elemIndex != this@AbstractRandomAccessList.size
        }

        override fun hasPrevious(): Boolean {
            this.checkForModification()

            return 0 != this.elemIndex
        }

        override fun nextIndex(): Int {
            this.checkForModification()

            return this.elemIndex
        }

        override fun previousIndex(): Int {
            this.checkForModification()

            return this.elemIndex - 1
        }

        override fun next(): E {
            this.checkForModification()

            if (!this.hasNext()) {
                throw NoSuchElementException()
            }

            val value = this@AbstractRandomAccessList[index]

            ++(this.elemIndex)
            this.state = ListIteratorState.CALLED_NEXT

            return value
        }

        override fun previous(): E {
            this.checkForModification()

            if (!this.hasPrevious()) {
                throw NoSuchElementException()
            }

            val value = this@AbstractRandomAccessList[index - 1]

            --(this.elemIndex)
            this.state = ListIteratorState.CALLED_PREVIOUS

            return value
        }

        override fun set(element: E) {
            this.checkForModification()

            when (this.state) {
                ListIteratorState.INITIALIZED,
                ListIteratorState.CALLED_MUTATION -> throw IllegalStateException()
                ListIteratorState.CALLED_NEXT -> this@AbstractRandomAccessList[this.elemIndex - 1] = element
                ListIteratorState.CALLED_PREVIOUS -> this@AbstractRandomAccessList[this.elemIndex] = element
            }

        }

        override fun remove() {
            this.checkForModification()

            when (this.state) {
                ListIteratorState.INITIALIZED,
                ListIteratorState.CALLED_MUTATION -> throw IllegalStateException()
                ListIteratorState.CALLED_NEXT -> this@AbstractRandomAccessList.removeAt(this.elemIndex - 1)
                ListIteratorState.CALLED_PREVIOUS -> this@AbstractRandomAccessList.removeAt(this.elemIndex)
            }

            this.state = ListIteratorState.CALLED_MUTATION
            ++(this.modifyCount)
        }

        override fun add(element: E) {
            this.checkForModification()

            this@AbstractRandomAccessList.add(this.elemIndex - 1, element)

            this.state = ListIteratorState.CALLED_MUTATION
            ++(this.modifyCount)
        }
    }

    override fun subList(fromIndex: Int, toIndex: Int): MutableList<E> {
        this.checkRangeInclusive(fromIndex)
        this.checkRangeInclusive(toIndex)
        this.checkFromTo(fromIndex, toIndex)

        return RandomAccessSublist(this, fromIndex, toIndex)
    }

    private fun checkRangeInclusive(index: Int) {
        if (index < 0 || index > this.size) {
            throw IndexOutOfBoundsException()
        }
    }

    private fun checkFromTo(fromIndex: Int, toIndex: Int) {
        if (fromIndex < toIndex) {
            throw IllegalArgumentException()
        }
    }
}
