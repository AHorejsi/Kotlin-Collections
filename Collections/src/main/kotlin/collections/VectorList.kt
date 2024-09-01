package collections

import java.io.Serializable
import kotlin.math.ceil
import kotlin.math.max

@Suppress("RemoveRedundantQualifierName")
class VectorList<TElement>(
    initialCapacity: Int = VectorList.DEFAULT_CAPACITY
) : AbstractList<TElement>(), RandomAccess, Serializable {
    companion object {
        @Suppress("ConstPropertyName")
        private const val serialVersionUID: Long = 1L

        const val DEFAULT_CAPACITY: Int = 16
    }

    init {
        checkIfNegativeCapacity(initialCapacity)
    }

    private var data: Array<Any?> = arrayOfNulls(max(initialCapacity, VectorList.DEFAULT_CAPACITY))

    override var size: Int = 0
        private set

    val capacity: Int
        get() = this.data.size

    override operator fun get(index: Int): TElement {
        checkIfIndexIsAccessible(index, this.size)

        @Suppress("UNCHECKED_CAST")
        return this.data[index] as TElement
    }

    override operator fun set(index: Int, element: TElement): TElement {
        val old = this[index]

        this.data[index] = element

        return old
    }

    override fun addAll(index: Int, elements: Collection<TElement>): Boolean {
        checkIfIndexCanBeInsertedAt(index, this.size)

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

    private fun insertElements(elements: Collection<TElement>, startIndex: Int) {
        for ((index, item) in elements.withIndex(startIndex)) {
            this.data[index] = item
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
        checkIfNegativeAmount(amount)

        val oldSize = this.size

        this.size = max(0, this.size - amount)
        ++(super.modCount)

        this.resizeIfNeededAfterRemoval()

        return oldSize - this.size
    }

    private fun shiftForRemoval(index: Int) {
        for (removalIndex in index until this.lastIndex) {
            this.data[removalIndex] = this.data[removalIndex + 1]
        }
    }

    override fun clear() {
        this.data = arrayOfNulls(VectorList.DEFAULT_CAPACITY)
        this.size = 0
        ++(super.modCount)
    }

    private fun resizeIfNeededAfterRemoval() {
        val capacityAboveMinimumThreshold = this.capacity > VectorList.DEFAULT_CAPACITY
        val halfOfSlotsAreEmpty = this.size <= ceil(this.capacity / 2.0).toInt()

        if (capacityAboveMinimumThreshold && halfOfSlotsAreEmpty) {
            this.reallocate(this.size * 3 / 2)
        }
    }

    fun ensureCapacity(newCapacity: Int) {
        checkIfNegativeCapacity(newCapacity)

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

fun <TElement> vectorListOf(): VectorList<TElement> =
    VectorList()

fun <TElement> vectorListOf(vararg elements: TElement): VectorList<TElement> =
    elements.toVectorList()

fun <TElement> Iterable<TElement>.toVectorList(): VectorList<TElement> {
    val size = this.count()
    val vec = VectorList<TElement>(size)

    vec.addAll(this)

    return vec
}

fun <TElement> Sequence<TElement>.toVectorList(): VectorList<TElement> {
    val vec = vectorListOf<TElement>()

    for (item in this) {
        vec.add(item)
    }

    return vec
}

fun <TElement> Array<out TElement>.toVectorList(): VectorList<TElement> {
    val vec = VectorList<TElement>(this.size)

    vec.addAll(this)

    return vec
}
