package me.alexh.collects

import kotlin.jvm.Transient

private class CountNode<TElement>(
    var element: TElement,
    var amount: Int = 0,
    var next: CountNode<TElement>? = null,
    val prev: CountNode<TElement>? = null
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
}
