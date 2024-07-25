package collections

import java.io.Serializable

@Suppress("RemoveRedundantQualifierName")
class VectorList<TElement>(
    initialCapacity: Int = VectorList.DEFAULT_CAPACITY
) : AbstractList<TElement>(), RandomAccess, Serializable {
    private companion object {
        @Suppress("ConstPropertyName")
        const val serialVersionUID: Long = 1L

        const val DEFAULT_CAPACITY: Int = 16

        fun checkCapacity(capacity: Int) {
            if (capacity < 0) {
                throw IllegalArgumentException("Capacity must be greater than 0. Capacity = $capacity")
            }
        }

        fun checkIndexWithSizeExcluded(index: Int, size: Int) {
            if (index < 0 || index >= size) {
                throw IndexOutOfBoundsException("0 <= index < size. Index = $index, size = $size")
            }
        }

        fun checkIndexWithSizeIncluded(index: Int, size: Int) {
            if (index < 0 || index > size) {
                throw IndexOutOfBoundsException("0 <= index <= size. Index = $index, size = $size")
            }
        }
    }

    init {
        VectorList.checkCapacity(initialCapacity)
    }

    private var data: Array<Any?> = arrayOfNulls(initialCapacity)

    override var size: Int = 0
        private set

    val capacity: Int
        get() = this.data.size

    override operator fun get(index: Int): TElement {
        VectorList.checkIndexWithSizeExcluded(index, this.size)

        @Suppress("UNCHECKED_CAST")
        return this.data[index] as TElement
    }

    override operator fun set(index: Int, element: TElement): TElement {
        val old = this[index]

        this.data[index] = element

        return old
    }

    override fun addAll(index: Int, elements: Collection<TElement>): Boolean {
        VectorList.checkIndexWithSizeIncluded(index, this.size)

        val amountToAdd = elements.size
        val newSize = this.size + amountToAdd

        this.resizeIfNeededAfterInsertion(newSize)
        this.shiftForInsertion(index, amountToAdd)
        this.insertElements(elements, index)

        this.size = newSize
        ++(super.modCount)

        return true
    }

    private fun resizeIfNeededAfterInsertion(newSize: Int) {
        if (newSize > this.capacity) {
            this.reallocate(newSize * 3 / 2)
        }
    }

    private fun shiftForInsertion(insertIndex: Int, amountToAdd: Int) {
        for (index in this.lastIndex downTo insertIndex) {
            this.data[index + amountToAdd] = this.data[index]
        }
    }

    private fun insertElements(elements: Collection<TElement>, index: Int) {
        var insertIndex = index

        for (item in elements) {
            this.data[insertIndex] = item
            ++insertIndex
        }
    }

    override fun removeAt(index: Int): TElement {
        val item = this[index]

        this.shiftForRemoval(index)

        --(this.size)
        ++(super.modCount)

        this.resizeIfNeededAfterRemoval()

        return item
    }

    fun removeFromBack(amount: Int): Int {
        if (amount >= this.size) {
            val oldSize = this.size
            this.clear()

            return oldSize
        }
        else {
            this.size -= amount
            ++(super.modCount)

            this.resizeIfNeededAfterRemoval()

            return amount
        }
    }

    private fun shiftForRemoval(index: Int) {
        for (removalIndex in index until this.lastIndex) {
            this.data[removalIndex] = this.data[removalIndex + 1]
        }
    }

    private fun resizeIfNeededAfterRemoval() {
        if (this.size <= this.capacity / 2) {
            this.reallocate(if (0 == this.size) VectorList.DEFAULT_CAPACITY else this.size * 3 / 2)
        }
    }

    override fun clear() {
        this.size = 0
        ++(super.modCount)

        this.resizeIfNeededAfterRemoval()
    }

    fun ensureCapacity(newCapacity: Int) {
        VectorList.checkCapacity(newCapacity)

        if (newCapacity > this.capacity) {
            this.reallocate(newCapacity)
        }
    }

    fun trimToSize() {
        if (this.size < this.capacity) {
            this.reallocate(this.size)
        }
    }

    private fun reallocate(newCapacity: Int) {
        val newData = arrayOfNulls<Any>(newCapacity)

        for (index in this.indices) {
            newData[index] = this.data[index]
        }

        this.data = newData
    }
}

fun <TElement> vectorListOf(): VectorList<TElement> = VectorList()

fun <TElement> vectorListOf(vararg elements: TElement): VectorList<TElement> = elements.toVectorList()

fun <TElement> Iterable<TElement>.toVectorList(): VectorList<TElement> {
    val size = this.count()
    val vec = VectorList<TElement>(size)

    for (item in this) {
        vec.add(item)
    }

    return vec
}

fun <TElement> Sequence<TElement>.toVectorList(): VectorList<TElement> {
    val vec = VectorList<TElement>()

    for (item in this) {
        vec.add(item)
    }

    return vec
}

fun <TElement> Collection<TElement>.toVectorList(): VectorList<TElement> {
    val vec = VectorList<TElement>(this.size)

    vec.addAll(this)

    return vec
}

fun <TElement> Array<out TElement>.toVectorList(): VectorList<TElement> {
    val vec = VectorList<TElement>(this.size)

    vec.addAll(this)

    return vec
}
