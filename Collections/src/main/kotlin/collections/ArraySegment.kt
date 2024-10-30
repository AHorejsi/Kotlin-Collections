package collections

import arrow.core.Option
import java.io.Serializable
import kotlin.random.Random

class ArraySegment<TElement> internal constructor(
    private val base: Array<TElement>,
    private val fromIndex: Int,
    private val toIndex: Int
) : Iterable<TElement>, Serializable {
    private companion object {
        @Suppress("ConstPropertyName")
        private const val serialVersionUID = 1L
    }

    init {
        checkIfValidRange(this.fromIndex, this.toIndex)
        checkIfRangeInBounds(this.fromIndex, this.toIndex, this.base.size)
    }

    internal constructor(slice: ArraySegment<TElement>, fromIndex: Int, toIndex: Int)
        : this(slice.base, fromIndex + slice.fromIndex, toIndex + slice.fromIndex)
    {
        checkIfRangeInBounds(fromIndex, toIndex, slice.size)
    }

    val size: Int
        get() = this.toIndex - this.fromIndex

    val lastIndex: Int
        get() = this.size - 1

    fun isEmpty(): Boolean =
        this.fromIndex == this.toIndex

    operator fun get(index: Int): TElement {
        checkIfIndexIsAccessible(index, this.size)

        return this.base[index + this.fromIndex]
    }

    operator fun set(index: Int, element: TElement) {
        checkIfIndexIsAccessible(index, this.size)

        this.base[index + this.fromIndex] = element
    }

    fun copyOf(): Array<TElement> =
        this.base.copyOfRange(this.fromIndex, this.toIndex)

    fun fill(element: TElement) =
        this.base.fill(element, this.fromIndex, this.toIndex)

    fun reverse() =
        this.base.reverse(this.fromIndex, this.toIndex)

    fun sort(comp: Comparator<in TElement>? = null) =
        this.base.sortWith(comp.nonnull, this.fromIndex, this.toIndex)

    fun rotate(amount: Int) {
        if (this.size <= 1) {
            return
        }

        val rotationsNeeded = amount % this.size

        if (0 == rotationsNeeded) {
            return
        }

        val (left, right) =
            if (rotationsNeeded < 0) {
                this.splitArray(this.size - amount)
            }
            else {
                this.splitArray(amount)
            }

        this.doCopy(0, right)
        this.doCopy(right.size, left)
    }

    private fun splitArray(amount: Int): Pair<Array<TElement>, Array<TElement>> {
        val leftIndices = this.fromIndex until this.fromIndex + amount
        val rightIndices = this.fromIndex + amount until this.toIndex

        val leftArray = this.base.sliceArray(leftIndices)
        val rightArray = this.base.sliceArray(rightIndices)

        return leftArray to rightArray
    }

    private fun doCopy(startIndex: Int, sub: Array<TElement>) {
        var wholeIndex = startIndex
        var currentIndex = 0

        while (currentIndex < sub.size) {
            this[wholeIndex] = sub[currentIndex]

            ++wholeIndex
            ++currentIndex
        }
    }

    fun indexOf(element: TElement): Int =
        this.index(0, element)

    fun lastIndexOf(element: TElement): Int =
        this.lastIndex(this.size, element)

    operator fun contains(element: TElement): Boolean =
        -1 != this.indexOf(element)

    override fun toString(): String =
        StructurallyImmutableList(this).toString()

    override fun iterator(): Iterator<TElement> = object : Iterator<TElement> {
        private var index = 0

        override fun hasNext(): Boolean = this.index < this@ArraySegment.size

        override fun next(): TElement {
            checkIfNext(this)

            val item = this@ArraySegment[this.index]

            ++(this.index)

            return item
        }
    }
}

fun <TElement> Array<TElement>.segment(fromIndex: Int = 0, toIndex: Int = this.size): ArraySegment<TElement> =
    ArraySegment(this, fromIndex, toIndex)

fun <TElement> Array<TElement>.segment(indices: IntRange): ArraySegment<TElement> =
    if (indices.isEmpty())
        this.segment(indices.first, indices.first)
    else
        this.segment(indices.first, indices.last + 1)

fun <TElement> ArraySegment<TElement>.segment(fromIndex: Int = 0, toIndex: Int = this.size): ArraySegment<TElement> =
    ArraySegment(this, fromIndex, toIndex)

fun <TElement> ArraySegment<TElement>.segment(indices: IntRange): ArraySegment<TElement> =
    if (indices.isEmpty())
        this.segment(indices.first, indices.first)
    else
        this.segment(indices.first, indices.last + 1)

fun <TElement> ArraySegment<out TElement>.tryGet(index: Int): Result<TElement> =
    StructurallyImmutableList(this).tryGet(index)

fun <TElement> ArraySegment<in TElement>.trySet(index: Int, element: TElement): Result<Unit> =
    StructurallyImmutableList(this).trySet(index, element).map{ _ -> return@map }

fun <TElement> ArraySegment<out TElement>.wrapGet(index: Int): TElement =
    StructurallyImmutableList(this).wrapGet(index)

fun <TElement> ArraySegment<in TElement>.wrapSet(index: Int, element: TElement) {
    StructurallyImmutableList(this).wrapSet(index, element)
}

fun <TElement> ArraySegment<TElement>.swap(index1: Int, index2: Int) =
    StructurallyImmutableList(this).swap(index1, index2)

fun <TElement> ArraySegment<out TElement>.index(fromIndex: Int, element: @UnsafeVariance TElement): Int =
    this.index(fromIndex) { it == element }

fun <TElement> ArraySegment<out TElement>.index(fromIndex: Int, predicate: (TElement) -> Boolean): Int =
    StructurallyImmutableList(this).index(fromIndex, predicate)

fun <TElement> ArraySegment<out TElement>.lastIndex(fromIndex: Int, element: @UnsafeVariance TElement): Int =
    this.lastIndex(fromIndex) { it == element }

fun <TElement> ArraySegment<out TElement>.lastIndex(fromIndex: Int, predicate: (TElement) -> Boolean): Int =
    StructurallyImmutableList(this).lastIndex(fromIndex, predicate)

fun <TElement> ArraySegment<out TElement>.isPermutationOf(other: ArraySegment<out TElement>): Boolean =
    StructurallyImmutableList(this).isPermutationOf(StructurallyImmutableList(other))

fun <TElement> ArraySegment<TElement>.next(comp: Comparator<TElement>? = null): Boolean =
    this.next(comp.function)

fun <TElement> ArraySegment<TElement>.next(comp: (TElement, TElement) -> Int): Boolean =
    StructurallyImmutableList(this).next(comp)

fun <TElement> ArraySegment<TElement>.prev(comp: Comparator<TElement>? = null): Boolean =
    this.prev(comp.function)

fun <TElement> ArraySegment<TElement>.prev(comp: (TElement, TElement) -> Int): Boolean =
    StructurallyImmutableList(this).prev(comp)

fun <TElement> ArraySegment<TElement>.separate(predicate: (TElement) -> Boolean): Int =
    StructurallyImmutableList(this).separate(predicate)

fun <TElement> ArraySegment<TElement>.shuffle(rand: Random = Random.Default) =
    StructurallyImmutableList(this).shuffle(rand)

fun <TElement> ArraySegment<out TElement>.isSorted(comp: Comparator<TElement>? = null): Boolean =
    this.isSorted(comp.function)

fun <TElement> ArraySegment<out TElement>.isSorted(comp: (TElement, TElement) -> Int): Boolean =
    this.size == this.isSortedUntil(comp)

fun <TElement> ArraySegment<out TElement>.isSortedUntil(comp: Comparator<TElement>? = null): Int =
    this.isSortedUntil(comp.function)

fun <TElement> ArraySegment<out TElement>.isSortedUntil(comp: (TElement, TElement) -> Int): Int =
    StructurallyImmutableList(this).isSortedUntil(comp)

fun <TElement> ArraySegment<out TElement>?.contentEquals(other: ArraySegment<out TElement>?): Boolean =
    when ((null === this) to (null === other)) {
        true to true -> true
        true to false -> false
        false to true -> false
        else -> contentEqualsHelper(this!!, other!!)
    }

private fun <TElement> contentEqualsHelper(left: ArraySegment<out TElement>, right: ArraySegment<out TElement>): Boolean {
    if (left === right) {
        return true
    }

    if (left.size != right.size) {
        return false
    }

    for (index in 0 until left.size) {
        if (left[index] != right[index]) {
            return false
        }
    }

    return true
}

fun <TElement> compare(
    left: ArraySegment<out TElement>,
    right: ArraySegment<out TElement>,
    comp: Comparator<TElement>? = null
): Int =
    compare(left, right, comp.function)

fun <TElement> compare(
    left: ArraySegment<out TElement>,
    right: ArraySegment<out TElement>,
    comp: (TElement, TElement) -> Int
): Int =
    compare(StructurallyImmutableList(left), StructurallyImmutableList(right), comp)
