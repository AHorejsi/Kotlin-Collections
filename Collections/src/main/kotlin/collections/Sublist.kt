package collections

import java.io.Serializable

@Suppress("RemoveRedundantQualifierName")
internal class Sublist<TElement>(
    private val list: MutableList<TElement>,
    private val fromIndex: Int,
    private var toIndex: Int
) : AbstractList<TElement>(), RandomAccess, Serializable {
    private companion object {
        @Suppress("ConstPropertyName")
        const val serialVersionUID: Long = 1L

        fun checkIfValidRange(fromIndex: Int, toIndex: Int) {
            if (fromIndex > toIndex) {
                throw IllegalArgumentException(
                    "fromIndex must be less than or equal to toIndex. " +
                    "fromIndex = $fromIndex, toIndex = $toIndex"
                )
            }
        }

        fun checkIndexSizeExcluded(index: Int, size: Int) {
            if (index < 0 || index >= size) {
                throw IndexOutOfBoundsException("0 <= index < size. index = $index, size = $size")
            }
        }

        fun checkIndexSizeIncluded(index: Int, size: Int) {
            if (index < 0 || index > size) {
                throw IndexOutOfBoundsException("0 <= index <= size. index = $index, size = $size")
            }
        }

        fun checkIfInBounds(fromIndex: Int, toIndex: Int, size: Int) {
            if (fromIndex < 0 || fromIndex >= size) {
                throw IndexOutOfBoundsException("0 <= fromIndex < size. fromIndex = $fromIndex, size = $size")
            }

            if (toIndex < 0 || toIndex > size) {
                throw IndexOutOfBoundsException("0 <= toIndex <= size. toIndex = $toIndex, size = $size")
            }
        }
    }

    init {
        Sublist.checkIfValidRange(this.fromIndex, this.toIndex)
        Sublist.checkIfInBounds(this.fromIndex, this.toIndex, this.list.size)
    }

    override val size: Int
        get() = this.toIndex - this.fromIndex

    override fun isEmpty(): Boolean = this.fromIndex == this.toIndex

    override fun get(index: Int): TElement {
        Sublist.checkIndexSizeExcluded(index, this.size)

        return this.list[index + this.fromIndex]
    }

    override fun set(index: Int, element: TElement): TElement {
        Sublist.checkIndexSizeExcluded(index, this.size)

        val actualIndex = index + this.fromIndex

        val old = this.list[actualIndex]
        this.list[actualIndex] = element

        return old
    }

    override fun addAll(index: Int, elements: Collection<TElement>): Boolean {
        Sublist.checkIndexSizeIncluded(index, this.size)

        val changed = this.list.addAll(index + this.fromIndex, elements)

        if (changed) {
            this.toIndex += elements.size
        }

        return changed
    }

    override fun removeAt(index: Int): TElement {
        Sublist.checkIndexSizeExcluded(index, this.size)

        val item = this.list.removeAt(index + this.fromIndex)
        --(this.toIndex)

        return item
    }

    override fun clear() {
        if (this.isEmpty()) {
            return
        }

        this.shiftForClearing()
        this.removeTrailingForClearing()

        this.toIndex = this.fromIndex
    }

    private fun shiftForClearing() {
        val size = this.size

        for (index in this.toIndex until this.list.lastIndex) {
            this.list[index - size] = this.list[index]
        }
    }

    private fun removeTrailingForClearing() {
        repeat(this.toIndex - this.fromIndex) {
            this.list.removeLast()
        }
    }

    override fun subList(fromIndex: Int, toIndex: Int): MutableList<TElement> =
        Sublist(this.list, fromIndex + this.fromIndex, toIndex + this.fromIndex)
}
