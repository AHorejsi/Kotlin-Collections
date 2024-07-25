package collections

import kotlin.random.Random

@Suppress("RemoveRedundantQualifierName")
class ArraySegment<TElement> internal constructor(
    private val base: Array<TElement>,
    private val fromIndex: Int = 0,
    private val toIndex: Int = base.size
) : Iterable<TElement> {
    private companion object {
        fun checkIfValidRange(fromIndex: Int, toIndex: Int) {
            if (fromIndex > toIndex) {
                throw IllegalArgumentException(
                    "fromIndex must be less than or equal to toIndex. " +
                    "fromIndex = $fromIndex, toIndex = $toIndex"
                )
            }
        }

        fun checkIfRangeInBounds(fromIndex: Int, toIndex: Int, size: Int) {
            if (fromIndex < 0 || fromIndex >= size) {
                throw IndexOutOfBoundsException("0 <= fromIndex < size. fromIndex = $fromIndex, size = $size")
            }

            if (toIndex < 0 || toIndex > size) {
                throw IndexOutOfBoundsException("0 <= toIndex <= size. toIndex = $toIndex, size = $size")
            }
        }

        private fun checkIndexSizeExcluded(index: Int, size: Int) {
            if (index < 0 || index >= size) {
                throw IndexOutOfBoundsException("0 <= index < size. index = $index, size = $size")
            }
        }
    }

    init {
        ArraySegment.checkIfValidRange(this.fromIndex, this.toIndex)
        ArraySegment.checkIfRangeInBounds(this.fromIndex, this.toIndex, this.size)
    }

    internal constructor(slice: ArraySegment<TElement>, fromIndex: Int, toIndex: Int)
        : this(slice.base, fromIndex + slice.fromIndex, toIndex + slice.fromIndex)
    {
        ArraySegment.checkIfRangeInBounds(this.fromIndex, this.toIndex, slice.size)
    }

    val size: Int
        get() = this.toIndex - this.fromIndex

    operator fun get(index: Int): TElement {
        ArraySegment.checkIndexSizeExcluded(index, this.size)

        return this.base[index + this.fromIndex]
    }

    operator fun set(index: Int, element: TElement) {
        ArraySegment.checkIndexSizeExcluded(index, this.size)

        this.base[index + this.fromIndex] = element
    }

    fun copy(): Array<TElement> = this.base.copyOfRange(this.fromIndex, this.toIndex)

    fun fill(element: TElement) = this.base.fill(element, this.fromIndex, this.toIndex)

    fun reverse() = this.base.reverse(this.fromIndex, this.toIndex)

    fun shuffle(rand: Random = Random.Default) = this.base.shuffle(rand)

    fun sort() {
        val comparator = DefaultComparator<TElement>()

        this.sort(comparator)
    }

    fun sort(comp: (TElement, TElement) -> Int) {
        val comparator = FuncComparator(comp)

        this.sort(comparator)
    }

    fun sort(comp: Comparator<in TElement>) {
        this.base.sortWith(comp, this.fromIndex, this.toIndex)
    }

    fun rotate(amount: Int) {
        val rotationsNeeded = amount.mod(this.size)

        if (0 == rotationsNeeded) {
            return
        }

        val (left, right) = this.splitArray(rotationsNeeded)

        for ((index, item) in (right + left).withIndex()) {
            this[index] = item
        }
    }

    private fun splitArray(amount: Int): Pair<Sequence<TElement>, Sequence<TElement>> {
        val leftIndices = this.fromIndex until this.fromIndex + amount
        val rightIndices = this.fromIndex + amount until this.toIndex

        val leftArray = this.base.sliceArray(leftIndices).asSequence()
        val rightArray = this.base.sliceArray(rightIndices).asSequence()

        return leftArray to rightArray
    }

    fun partition(predicate: (TElement) -> Boolean) {
        val left = this.asSequence().filter(predicate)
        val right = this.asSequence().filterNot(predicate)

        for ((index, element) in (left + right).withIndex()) {
            this[index] = element
        }
    }

    fun forEach(action: (TElement) -> Unit) {
        for (item in this) {
            action(item)
        }
    }

    fun indexOf(element: TElement): Int {
        for (index in this.fromIndex until this.toIndex) {
            if (element == this.base[index]) {
                return index - this.fromIndex
            }
        }

        return -1
    }

    fun lastIndexOf(element: TElement): Int {
        for (index in (this.toIndex - 1) downTo fromIndex) {
            if (element == this.base[index]) {
                return index - this.fromIndex
            }
        }

        return -1
    }

    operator fun contains(element: TElement): Boolean = -1 != this.indexOf(element)

    override fun iterator(): Iterator<TElement> = object : Iterator<TElement> {
        private var index = 0

        override fun hasNext(): Boolean = this.index < this@ArraySegment.size

        override fun next(): TElement {
            if (!this.hasNext()) {
                throw NoSuchElementException()
            }

            val item = this@ArraySegment[this.index]

            ++(this.index)

            return item
        }
    }
}

fun <TElement> Array<TElement>.segment(fromIndex: Int, toIndex: Int): ArraySegment<TElement> =
    ArraySegment(this, fromIndex, toIndex)

fun <TElement> ArraySegment<TElement>.segment(fromIndex: Int, toIndex: Int): ArraySegment<TElement> =
    ArraySegment(this, fromIndex, toIndex)

fun <TElement> ArraySegment<TElement>.isEmpty(): Boolean = 0 == this.size

fun <TElement> Array<TElement>.contentEquals(other: ArraySegment<TElement>): Boolean =
    ArraySegment(this).contentEquals(other)

fun <TElement> ArraySegment<TElement>.contentEquals(other: Array<TElement>): Boolean =
    this.contentEquals(ArraySegment(other))

fun <TElement> ArraySegment<TElement>.contentEquals(other: ArraySegment<TElement>): Boolean {
    if (this.size != other.size) {
        return false
    }

    val left = this.iterator()
    val right = other.iterator()

    while (left.hasNext()) {
        if (left.next() != right.next()) {
            return false
        }
    }

    return true
}
