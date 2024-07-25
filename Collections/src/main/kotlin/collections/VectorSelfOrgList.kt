package collections

import java.io.Serializable

@Suppress("RemoveRedundantQualifierName")
class VectorSelfOrgList<TElement>(
    initialCapacity: Int = VectorSelfOrgList.DEFAULT_CAPACITY
) : SelfOrgList<TElement>, RandomAccess, Serializable {
    private companion object {
        @Suppress("ConstPropertyName")
        const val serialVersionUID: Long = 1L

        const val DEFAULT_CAPACITY: Int = 16
    }

    private val data: MutableList<TElement> = ArrayList(initialCapacity)

    override val size: Int
        get() = this.data.size

    override fun isEmpty(): Boolean = this.data.isEmpty()

    override fun at(index: Int): TElement {
        if (index < 0 || index >= this.size) {
            throw IndexOutOfBoundsException()
        }

        return this.data[index]
    }

    override fun add(element: TElement): Boolean = this.data.add(element)

    override fun addAll(elements: Collection<TElement>): Boolean = this.data.addAll(elements)

    override fun remove(element: TElement): Boolean = this.data.remove(element)

    override fun clear() = this.data.clear()

    override fun find(predicate: (element: TElement) -> Boolean): IndexedValue<TElement>? {
        val index = this.data.index(0, predicate)

        return if (-1 == index)
            null
        else {
            this.reorder(index)
        }
    }

    override fun findAll(predicate: (element: TElement) -> Boolean): Sequence<IndexedValue<TElement>> = sequence {
        var index = -1

        do {
            index = this@VectorSelfOrgList.data.index(index + 1, predicate)

            if (index > 0) {
                val result = this@VectorSelfOrgList.reorder(index)

                yield(result)
            }
        } while (-1 != index)
    }

    private fun reorder(index: Int): IndexedValue<TElement> {
        val item = this.data[index]

        if (0 == index) {
            return IndexedValue(index, item)
        }
        else {
            val prevIndex = index - 1

            val temp = this.data[prevIndex]
            this.data[prevIndex] = this.data[index]
            this.data[index] = temp

            return IndexedValue(prevIndex, item)
        }
    }

    override fun iterator(): MutableIterator<TElement> = this.data.iterator()
}
