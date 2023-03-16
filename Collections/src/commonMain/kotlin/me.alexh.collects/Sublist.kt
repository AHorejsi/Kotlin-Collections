package me.alexh.collects

internal class Sublist<TElement>(
    private val whole: MutableList<TElement>,
    private var fromIndex: Int,
    private var toIndex: Int
) : SealedMutableList<TElement>() {
    override val size: Int
        get() = this.toIndex - this.fromIndex

    override fun get(index: Int): TElement =
        if (index < 0 || index >= this.size)
            throw IndexOutOfBoundsException()
        else
            this.whole[index + this.fromIndex]

    override fun set(index: Int, element: TElement): TElement =
        if (index < 0 || index >= this.size)
            throw IndexOutOfBoundsException()
        else
            this.whole.set(index + this.fromIndex, element)

    override fun addAll(index: Int, elements: Collection<TElement>): Boolean {
        if (index < 0 || index > this.size) {
            throw IndexOutOfBoundsException()
        }

        this.addAll(index + this.fromIndex, elements)

        this.toIndex += elements.size
        ++(super.modCount)

        return true
    }

    override fun removeAt(index: Int): TElement {
        if (index < 0 || index >= this.size) {
            throw IndexOutOfBoundsException()
        }

        val elem = this.whole.removeAt(index + this.fromIndex)

        --(this.toIndex)
        ++(super.modCount)

        return elem
    }

    override fun clear() {
        this.whole.removeRange(this.fromIndex, this.toIndex)

        this.toIndex = this.fromIndex
        ++(super.modCount)
    }

    override fun subList(fromIndex: Int, toIndex: Int): MutableList<TElement> {
        val size = this.size

        if (fromIndex < 0 || fromIndex >= size || toIndex < 0 || toIndex > size) {
            throw IndexOutOfBoundsException()
        }
        if (toIndex < fromIndex) {
            throw IllegalArgumentException()
        }

        return Sublist(this.whole, fromIndex + this.fromIndex, toIndex + this.fromIndex)
    }
}