package me.alexh.collects

import kotlin.jvm.Transient

sealed class SealedMutableList<TElement> : MutableList<TElement>, SealedMutableCollection<TElement>() {
    @Transient
    protected var modCount: Int = 0

    override fun add(element: TElement): Boolean {
        this.add(this.size, element)

        return true
    }

    override fun add(index: Int, element: TElement) {
        val singletonList = listOf(element)

        this.addAll(index, singletonList)
    }

    override fun addAll(elements: Collection<TElement>): Boolean = this.addAll(this.size, elements)

    override operator fun contains(element: @UnsafeVariance TElement): Boolean = -1 != this.indexOf(element)

    override fun indexOf(element: @UnsafeVariance TElement): Int = this.indexOf(element, 0)

    override fun lastIndexOf(element: @UnsafeVariance TElement): Int = this.lastIndexOf(element, this.lastIndex)

    override fun remove(element: @UnsafeVariance TElement): Boolean {
        for (index in this.indices) {
            if (element == this[index]) {
                this.removeAt(index)

                return true
            }
        }

        return false
    }

    override fun subList(fromIndex: Int, toIndex: Int): MutableList<TElement> =
        when {
            fromIndex < 0 || fromIndex >= this.size || toIndex < 0 || toIndex > this.size -> throw IndexOutOfBoundsException()
            toIndex < fromIndex -> throw IllegalArgumentException()
            else -> Sublist(this, fromIndex, toIndex)
        }

    override fun iterator(): MutableIterator<TElement> = this.listIterator()

    override fun listIterator(): MutableListIterator<TElement> = this.listIterator(0)

    override fun listIterator(index: Int): MutableListIterator<TElement> = object : MutableListIterator<TElement> {
        private var index: Int = index
        private var modCount: Int = this@SealedMutableList.modCount
        private var state: ListIteratorState = ListIteratorState.INITIALIZED

        override fun nextIndex(): Int {
            this.checkForConcurrentModification()

            return this.index
        }

        override fun previousIndex(): Int {
            this.checkForConcurrentModification()

            return this.index - 1
        }

        override fun hasNext(): Boolean = this.nextIndex() != this@SealedMutableList.size

        override fun hasPrevious(): Boolean = -1 != this.previousIndex()

        override fun next(): TElement {
            if (!this.hasNext()) {
                throw NoSuchElementException()
            }

            val elem = this@SealedMutableList[this.nextIndex()]

            ++(this.index)
            this.state = ListIteratorState.CALLED_NEXT

            return elem
        }

        override fun previous(): TElement {
            if (!this.hasPrevious()) {
                throw NoSuchElementException()
            }

            val elem = this@SealedMutableList[this.previousIndex()]

            --(this.index)
            this.state = ListIteratorState.CALLED_PREVIOUS

            return elem
        }

        override fun set(element: TElement) {
            this.checkForConcurrentModification()

            val elemIndex = this.getLastAccessed()

            this@SealedMutableList[elemIndex] = element
        }

        override fun remove() {
            this.checkForConcurrentModification()

            val elemIndex = this.getLastAccessed()

            this@SealedMutableList.removeAt(elemIndex)

            ++(this.modCount)
            this.state = ListIteratorState.CALLED_REMOVE
        }

        private fun getLastAccessed(): Int =
            when (this.state) {
                ListIteratorState.INITIALIZED, ListIteratorState.CALLED_REMOVE -> throw IllegalStateException()
                ListIteratorState.CALLED_NEXT -> this.previousIndex()
                ListIteratorState.CALLED_PREVIOUS -> this.nextIndex()
            }

        override fun add(element: TElement) {
            this.checkForConcurrentModification()

            this@SealedMutableList.add(this.previousIndex(), element)

            ++(this.modCount)
        }

        private fun checkForConcurrentModification() {
            if (this.modCount != this@SealedMutableList.modCount) {
                throw ConcurrentModificationException()
            }
        }
    }
}
