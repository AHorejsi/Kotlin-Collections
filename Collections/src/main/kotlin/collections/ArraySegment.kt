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

    fun copy(): Array<TElement> =
        this.base.copyOfRange(this.fromIndex, this.toIndex)

    fun fill(element: TElement) =
        this.base.fill(element, this.fromIndex, this.toIndex)

    fun reverse() =
        this.base.reverse(this.fromIndex, this.toIndex)

    fun shuffle(rand: Random = Random.Default) {
        if (this.size <= 1) {
            return
        }

        if (2 == this.size) {
            this.shuffleSize2(rand)
        }
        else {
            this.shuffleGreaterThan2(rand)
        }
    }

    private fun shuffleSize2(rand: Random) {
        if (rand.nextBoolean()) {
            this.swap(0, 1)
        }
    }

    private fun shuffleGreaterThan2(rand: Random) {
        val size = this.size

        for (index in this.lastIndex downTo 0) {
            val randomIndex = rand.nextInt(index, size)

            this.swap(index, randomIndex)
        }
    }

    fun sort(comp: Comparator<in TElement>? = null) =
        this.base.sortWith(comp.nonnull, this.fromIndex, this.toIndex)

    fun rotate(amount: Int) {
        val rotationsNeeded = amount.mod(this.size)

        if (0 == rotationsNeeded) {
            return
        }

        val (left, right) = this.splitArray(rotationsNeeded)

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

    fun partition(predicate: (TElement) -> Boolean): Int {
        val (front, end) = this.asIterable().partition(predicate)

        for ((index, item) in front.withIndex()) {
            this[index] = item
        }

        for ((index, item) in end.withIndex(front.size)) {
            this[index] = item
        }

        return front.size
    }

    fun indexOf(element: TElement): Int {
        for (index in 0 until this.size) {
            if (element == this[index]) {
                return index
            }
        }

        return -1
    }

    fun lastIndexOf(element: TElement): Int {
        for (index in this.lastIndex downTo 0) {
            if (element == this[index]) {
                return index
            }
        }

        return -1
    }

    operator fun contains(element: TElement): Boolean =
        -1 != this.indexOf(element)

    override fun toString(): String =
        this.asString()

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

fun <TElement> ArraySegment<out TElement>.safeGet(index: Int): Option<TElement> =
    AsList(this).safeGet(index)

fun <TElement> ArraySegment<in TElement>.safeSet(index: Int, element: TElement): Option<Unit> =
    AsList(this).safeSet(index, element).map{ _ -> return@map }

fun <TElement> ArraySegment<out TElement>.tryGet(index: Int): Result<TElement> =
    AsList(this).tryGet(index)

fun <TElement> ArraySegment<in TElement>.trySet(index: Int, element: TElement): Result<Unit> =
    AsList(this).trySet(index, element).map{ _ -> return@map }

fun <TElement> ArraySegment<out TElement>.wrapGet(index: Int): TElement =
    AsList(this).wrapGet(index)

fun <TElement> ArraySegment<in TElement>.wrapSet(index: Int, element: TElement) {
    AsList(this).wrapSet(index, element)
}

fun <TElement> ArraySegment<TElement>.swap(index1: Int, index2: Int) =
    AsList(this).swap(index1, index2)

fun <TElement> ArraySegment<out TElement>.index(fromIndex: Int, element: @UnsafeVariance TElement): Int =
    this.index(fromIndex) { it == element }

fun <TElement> ArraySegment<out TElement>.index(fromIndex: Int, predicate: (TElement) -> Boolean): Int =
    AsList(this).index(fromIndex, predicate)

fun <TElement> ArraySegment<out TElement>.lastIndex(fromIndex: Int, element: @UnsafeVariance TElement): Int =
    this.lastIndex(fromIndex) { it == element }

fun <TElement> ArraySegment<out TElement>.lastIndex(fromIndex: Int, predicate: (TElement) -> Boolean): Int =
    AsList(this).lastIndex(fromIndex, predicate)

fun <TElement> ArraySegment<out TElement>.isPermutationOf(other: ArraySegment<out TElement>): Boolean =
    AsList(this).isPermutationOf(AsList(other))

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
