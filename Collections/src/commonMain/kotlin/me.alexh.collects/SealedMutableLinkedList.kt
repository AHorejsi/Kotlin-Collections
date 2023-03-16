package me.alexh.collects

import kotlin.jvm.Transient

sealed class SealedMutableLinkedList<TElement> : MutableLinkedList<TElement>, SealedMutableCollection<TElement>() {
    @Transient
    protected var modCount: Int = 0
    override var first: MutableLinkedListNode<TElement>? = null
        protected set
    override var last: MutableLinkedListNode<TElement>? = null
        protected set
    override var size: Int = 0
        protected set

    override fun front(): MutableListIterator<TElement> = this.listIterator(true)

    override fun back(): MutableListIterator<TElement> = this.listIterator(false)

    private fun listIterator(useFront: Boolean): MutableListIterator<TElement> = object : MutableListIterator<TElement> {
        private var current: MutableLinkedListNode<TElement>? = if (useFront) (this@SealedMutableLinkedList.first) else (null)
        private var index: Int = if (useFront) (0) else (this@SealedMutableLinkedList.size)
        private var state: ListIteratorState = ListIteratorState.INITIALIZED
        private var modCount: Int = this@SealedMutableLinkedList.modCount

        override fun nextIndex(): Int {
            this.checkForConcurrentModification()

            return this.index
        }

        override fun previousIndex(): Int {
            this.checkForConcurrentModification()

            return this.index - 1
        }

        override fun hasNext(): Boolean = this.nextIndex() != this@SealedMutableLinkedList.size

        override fun hasPrevious(): Boolean = -1 != this.previousIndex()

        override fun next(): TElement {
            this.checkForConcurrentModification()

            this.current?.let {
                val elem = it.element

                this.current = it.next
                this.state = ListIteratorState.CALLED_NEXT

                return elem
            } ?: throw NoSuchElementException()
        }

        override fun previous(): TElement {
            this.checkForConcurrentModification()

            this.previousNode()?.let {
                val elem = it.element

                this.current = it
                this.state = ListIteratorState.CALLED_PREVIOUS

                return elem
            } ?: throw NoSuchElementException()
        }

        override fun set(element: TElement) {
            this.checkForConcurrentModification()

            val node = this.getLastAccessed()

            node.element = element
        }

        override fun remove() {
            this.checkForConcurrentModification()

            val node = this.getLastAccessed()

            this@SealedMutableLinkedList.remove(node)

            this.state = ListIteratorState.CALLED_REMOVE
            ++(this.modCount)
        }

        private fun getLastAccessed(): MutableLinkedListNode<TElement> =
            when (this.state) {
                ListIteratorState.INITIALIZED, ListIteratorState.CALLED_REMOVE -> throw IllegalStateException()
                ListIteratorState.CALLED_NEXT -> this.previousNode()!!
                ListIteratorState.CALLED_PREVIOUS -> this.current!!
            }

        override fun add(element: TElement) {
            this.checkForConcurrentModification()

            this@SealedMutableLinkedList.addBefore(this.current!!, element)

            ++(this.modCount)
        }

        private fun previousNode(): MutableLinkedListNode<TElement>? {
            val current = this.current

            return if (null === current)
                this@SealedMutableLinkedList.last
            else
                current.prev
        }

        private fun checkForConcurrentModification() {
            if (this.modCount != this@SealedMutableLinkedList.modCount) {
                throw ConcurrentModificationException()
            }
        }
    }
}
