package me.alexh.collects

import kotlin.jvm.Transient

private class CountNode<TElement>(
    var element: TElement,
    var amount: Int = 0,
    var next: CountNode<TElement>? = null,
    var prev: CountNode<TElement>? = null
)

class CountList<TElement> : SealedMutableCollection<TElement>(), SelfOrgList<TElement> {
    @Transient
    private var modCount: Int = 0
    private var head: CountNode<TElement>? = null
    private var tail: CountNode<TElement>? = null
    override var size: Int = 0
        private set

    override fun get(index: Int): TElement =
        if (index < 0 || index >= this.size)
            throw IndexOutOfBoundsException()
        else
            this.getNode(index).element

    private fun getNode(targetIndex: Int): CountNode<TElement> {
        var currentNode = this.head
        var currentIndex = 0

        while (currentIndex != targetIndex) {
            ++currentIndex
            currentNode = currentNode!!.next
        }

        return currentNode!!
    }

    override fun add(element: TElement): Boolean {
        val newNode = CountNode(element)

        newNode.prev = this.tail

        this.tail?.let {
            it.next = newNode
            this.tail = newNode
        } ?: run {
            this.head = newNode
            this.tail = newNode
        }

        ++(this.size)
        ++(this.modCount)

        return true
    }

    override fun find(predicate: Predicate<in TElement>): ListIterator<TElement> {
        if (super.isEmpty()) {
            return this.back()
        }

        val iter = this.front()

        while (iter.hasNext()) {
            val item = iter.next()

            if (predicate(item)) {
                iter.previous()

                break
            }
        }

        return iter
    }
}
