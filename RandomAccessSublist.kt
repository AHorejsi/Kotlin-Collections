package com.alexh.collects

internal class RandomAccessSublist<E>
(
    private val underlying: MutableList<E>,
    private var startIndex: Int,
    private var endIndex: Int
) : AbstractRandomAccessList<E>(), RandomAccess
{
    override var size: Int = this.endIndex - this.startIndex

    override fun get(index: Int): E = this.underlying[index + this.startIndex]

    override fun set(index: Int, element: E): E = this.underlying.set(index + this.startIndex, element)

    override fun addAll(index: Int, elements: Collection<E>): Boolean {
        this.underlying.addAll(index + this.startIndex, elements)
        this.endIndex += elements.size
        ++(super.modifyCount)

        return true
    }

    override fun removeAt(index: Int): E {
        val elem = this[index + this.startIndex]

        this.underlying.removeAt(index + this.startIndex)
        --(this.endIndex)
        --(this.size)
        ++(super.modifyCount)

        return elem
    }

    override fun clear() {
        this.swapDownward()
        this.removeUnusedIndices()

        this.endIndex = this.startIndex

        ++(super.modifyCount)
    }

    private fun swapDownward() {
        var swapIndex = this.startIndex
        val distance = this.size

        while (swapIndex + distance != this.underlying.size) {
            this.underlying[swapIndex] = this.underlying[swapIndex + distance]
            ++swapIndex
        }
    }

    private fun removeUnusedIndices() {
        for (removeIndex in countDown(this.underlying.lastIndex, this.size)) {
            this.underlying.removeAt(removeIndex)
        }
    }

    override fun subList(fromIndex: Int, toIndex: Int): MutableList<E> {
        val actualFromIndex = fromIndex + this.startIndex
        val actualToIndex = toIndex + this.startIndex

        return this.underlying.subList(actualFromIndex, actualToIndex)
    }
}