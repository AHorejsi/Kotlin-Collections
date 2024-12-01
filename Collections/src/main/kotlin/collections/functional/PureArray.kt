package collections.functional

import collections.*
import java.io.Serializable
import kotlin.math.abs
import kotlin.math.min

@Suppress("RemoveRedundantQualifierName")
class PureArray<TElement> private constructor(
    private val base: Array<Any?>
) : PureList<TElement>, RandomAccess, Serializable {
    companion object {
        @Suppress("ConstPropertyName")
        private const val serialVersionUID: Long = 1L

        private val EMPTY: Array<Any?> = emptyArray()

        fun <TItem> empty(): PureArray<TItem> =
            PureArray(PureArray.EMPTY)

        fun <TItem> single(only: TItem): PureArray<TItem> {
            val array = arrayOf<Any?>(only)

            return PureArray(array)
        }
    }

    override val head: TElement
        get() =
            if (this.isEmpty())
                empty(PureArray::class)
            else
                this[0]

    override val tail: PureArray<TElement>
        get() =
            if (this.isEmpty())
                empty(PureArray::class)
            else
                this.skip(1)

    override val size: Int
        get() = this.base.size

    override fun isEmpty(): Boolean =
        0 == this.size

    override fun force(): PureArray<TElement> =
        this

    override operator fun get(index: Int): TElement {
        checkIfIndexIsAccessible(index, this.size)

        @Suppress("UNCHECKED_CAST")
        return this.base[index] as TElement
    }

    override fun update(index: Int, element: TElement): PureArray<TElement> {
        val singleton = mapOf(index to element)

        return this.updateAll(singleton)
    }

    override fun updateAll(elements: Map<Int, TElement>): PureArray<TElement> {
        if (elements.isEmpty()) {
            return this
        }

        val copy = arrayOfNulls<Any>(this.size)

        for (index in this.indices) {
            copy[index] = elements.getOrDefault(index, this[index])
        }

        return PureArray(copy)
    }

    override fun prepend(element: TElement): PureArray<TElement> {
        val singleton = listOf(element)

        return this.prependAll(singleton)
    }

    override fun prependAll(elements: Collection<TElement>): PureArray<TElement> {
        if (elements.isEmpty()) {
            return this
        }

        val newBase = arrayOfNulls<Any>(elements.size + this.size)

        this.concat(elements, this, newBase)

        return PureArray(newBase)
    }

    override fun append(element: TElement): PureArray<TElement> {
        val singleton = listOf(element)

        return this.appendAll(singleton)
    }

    override fun appendAll(elements: Collection<TElement>): PureArray<TElement> {
        if (elements.isEmpty()) {
            return this
        }

        val newBase = arrayOfNulls<Any>(this.size + elements.size)

        this.concat(this, elements, newBase)

        return PureArray(newBase)
    }

    private fun concat(left: Collection<TElement>, right: Collection<TElement>, newBase: Array<Any?>) {
        var index = 0

        for (element in left) {
            newBase[index] = element
            ++index
        }

        for (element in right) {
            newBase[index] = element
            ++index
        }
    }

    override fun insert(index: Int, element: TElement): PureArray<TElement> {
        val singleton = listOf(element)

        return this.insertAll(index, singleton)
    }

    override fun insertAll(index: Int, elements: Collection<TElement>): PureArray<TElement> {
        checkIfIndexCanBeInsertedAt(index, this.size)

        when (index) {
            0 -> { return this.prependAll(elements) }
            this.size -> { return this.appendAll(elements) }
        }

        if (elements.isEmpty()) {
            return this
        }

        val newBase = arrayOfNulls<Any>(elements.size + this.size)
        val seq = this.asSequence()
        var currentIndex = 0

        currentIndex = this.assign(newBase, currentIndex, seq.take(index))
        currentIndex = this.assign(newBase, currentIndex, elements.asSequence())
        this.assign(newBase, currentIndex, seq.drop(index))

        return PureArray(newBase)
    }

    private fun assign(newBase: Array<Any?>, startIndex: Int, seq: Sequence<TElement>): Int {
        var currentIndex = startIndex

        for (item in seq) {
            newBase[currentIndex] = item
            ++currentIndex
        }

        return currentIndex
    }

    override fun draw(amount: Int): PureArray<TElement> =
        this.subList(0, min(amount, this.size))

    override fun drawLast(amount: Int): PureArray<TElement> =
        if (amount >= this.size)
            this.draw(amount)
        else
            this.skip(this.size - amount)

    override fun drawWhile(predicate: (TElement) -> Boolean): PureArray<TElement> {
        val index = this.index(0, predicate)

        return if (-1 == index)
            this
        else
            this.draw(index + 1)
    }

    override fun drawLastWhile(predicate: (TElement) -> Boolean): PureArray<TElement> {
        val index = this.lastIndex(this.size, predicate)

        return if (-1 == index)
            this
        else
            this.drawLast(index + 1)
    }

    override fun skip(amount: Int): PureArray<TElement> = this.subList(min(amount, this.size), this.size)

    override fun skipLast(amount: Int): PureArray<TElement> =
        if (amount >= this.size)
            this.skip(amount)
        else
            this.draw(this.size - amount)

    override fun skipWhile(predicate: (TElement) -> Boolean): PureArray<TElement> {
        val index = this.index(0, predicate)

        return if (-1 == index)
            PureArray.empty()
        else
            this.skip(index)
    }

    override fun skipLastWhile(predicate: (TElement) -> Boolean): PureArray<TElement> {
        val index = this.lastIndex(this.size, predicate)

        return if (-1 == index)
            PureArray.empty()
        else
            this.skipLast(index)
    }

    override fun delete(element: TElement): PureArray<TElement> {
        val singleton = listOf(element)

        return this.deleteAll(singleton)
    }

    override fun deleteAll(elements: Collection<TElement>): PureArray<TElement> {
        val indices = elements.asSequence()
            .map(this::indexOf)
            .filterNot((-1)::equals)
            .toHashSet()

        if (indices.isEmpty()) {
            return this
        }

        var insertIndex = 0
        val newBase = arrayOfNulls<Any>(this.size - indices.size)

        for ((index, item) in this.withIndex()) {
            if (index !in indices) {
                newBase[insertIndex] = item
                ++insertIndex
            }
        }

        return PureArray(newBase)
    }

    override fun deleteAt(index: Int): PureArray<TElement> = this.deleteRange(index, index + 1)

    override fun deleteAt(indices: Collection<Int>): PureArray<TElement> {
        if (indices.isEmpty()) {
            return this
        }

        val newBase = arrayOfNulls<Any>(this.size - indices.size)
        var actualIndex = 0

        for ((index, item) in this.withIndex()) {
            if (index !in indices) {
                newBase[actualIndex] = item
                ++actualIndex
            }
        }

        return PureArray(newBase)
    }

    override fun deleteRange(fromIndex: Int, toIndex: Int): PureArray<TElement> {
        val amountToRemove = toIndex - fromIndex

        if (0 == amountToRemove) {
            return this
        }

        var insertIndex = 0
        val newBase = arrayOfNulls<Any>(this.size - amountToRemove)

        for (index in 0 until fromIndex) {
            newBase[insertIndex] = this[index]
            ++insertIndex
        }
        for (index in toIndex until this.size) {
            newBase[insertIndex] = this[index]
            ++insertIndex
        }

        return PureArray(newBase)
    }

    override fun sieve(predicate: (TElement) -> Boolean): PureList<TElement> {
        val items = this.asSequence().filter(predicate)

        if (!items.any()) {
            return this
        }

        val size = items.count()
        val newBase = arrayOfNulls<Any>(size)

        for ((index, item) in items.withIndex()) {
            newBase[index] = item
        }

        return PureArray(newBase)
    }

    override fun split(index: Int): Pair<PureArray<TElement>, PureArray<TElement>> {
        checkIfIndexCanBeInsertedAt(index, this.size)

        val left = this.draw(index)
        val right = this.skip(index)

        return left to right
    }

    override fun find(predicate: (TElement) -> Boolean): Result<TElement> {
        val index = this.index(0, predicate)

        return if (-1 == index)
            Result.failure(ResultUtils.FAILED_SEARCH)
        else
            Result.success(this[index])
    }

    override fun findLast(predicate: (TElement) -> Boolean): Result<TElement> {
        val index = this.lastIndex(this.size, predicate)

        return if (-1 == index)
            Result.failure(ResultUtils.FAILED_SEARCH)
        else
            Result.success(this[index])
    }

    override fun subList(fromIndex: Int, toIndex: Int): PureArray<TElement> {
        if (0 == fromIndex && this.size == toIndex) {
            return this
        }

        val copy = this.base.copyOfRange(fromIndex, toIndex)

        return PureArray(copy)
    }

    override fun sort(): PureArray<TElement> {
        val default = inOrder<TElement>()

        return this.sort(default::compare)
    }

    override fun sort(comp: (TElement, TElement) -> Int): PureArray<TElement> {
        val newBase = this.base.copyOf()
        val comparator = Comparator(comp)

        @Suppress("UNCHECKED_CAST")
        (newBase as Array<TElement>).sortWith(comparator)

        return PureArray(newBase)
    }

    override fun replace(new: TElement, old: TElement): PureArray<TElement> = this.replace(new) { it == old }

    override fun replace(new: TElement, predicate: (TElement) -> Boolean): PureArray<TElement> {
        val newBase = this.base.copyOf()

        for ((index, item) in this.withIndex()) {
            if (predicate(item)) {
                newBase[index] = new
            }
        }

        return PureArray(newBase)
    }

    override fun <TOther> transform(operation: (TElement) -> TOther): PureArray<TOther> {
        val items = this.asSequence().map(operation).iterator()
        val newBase = Array<Any?>(this.size) { items.next() }

        return PureArray(newBase)
    }

    override fun reverse(): PureArray<TElement> {
        val newBase = this.base.reversedArray()

        return PureArray(newBase)
    }

    override fun rotate(amount: Int): PureArray<TElement> {
        if (0 == amount.mod(this.size)) {
            return this
        }

        return if (amount < 0)
            this.rotateLeft(amount)
        else
            this.rotateRight(amount)
    }

    private fun rotateLeft(amount: Int): PureArray<TElement> {
        val actualAmount = abs(amount)
        val seq = this.asSequence()

        val left = seq.take(this.size - actualAmount)
        val right = seq.drop(actualAmount)

        val newBase = arrayOfNulls<Any>(this.size)

        for ((index, item) in (left + right).withIndex()) {
            newBase[index] = item
        }

        return PureArray(newBase)
    }

    private fun rotateRight(amount: Int): PureArray<TElement> {
        val seq = this.asSequence()

        val left = seq.drop(this.size - amount)
        val right = seq.take(amount)

        val newBase = arrayOfNulls<Any>(this.size)

        for ((index, item) in (left + right).withIndex()) {
            newBase[index] = item
        }

        return PureArray(newBase)
    }

    override fun separate(predicate: (TElement) -> Boolean): Pair<PureArray<TElement>, PureArray<TElement>> {
        val left = arrayOfNulls<Any>(this.size)
        val right = arrayOfNulls<Any>(this.size)

        var leftIndex = 0
        var rightIndex = 0

        for (item in this) {
            if (predicate(item)) {
                left[leftIndex] = item
                ++leftIndex
            }
            else {
                right[rightIndex] = item
                ++rightIndex
            }
        }

        return when (this.size) {
            leftIndex -> this to PureArray.empty()
            rightIndex -> PureArray.empty<TElement>() to this
            else -> PureArray<TElement>(left) to PureArray(right)
        }
    }

    override fun listIterator(index: Int): ListIterator<TElement> = object : ListIterator<TElement> {
        init {
            checkIfIndexCanBeInsertedAt(index, this@PureArray.size)
        }

        private var current: Int = index

        override fun previousIndex(): Int =
            this.current - 1

        override fun nextIndex(): Int =
            this.current

        override fun hasPrevious(): Boolean =
            this.previousIndex() >= 0

        override fun hasNext(): Boolean =
            this.nextIndex() < this@PureArray.size

        override fun previous(): TElement {
            checkIfPrev(this)

            val item = this@PureArray[this.previousIndex()]

            --(this.current)

            return item
        }

        override fun next(): TElement {
            checkIfNext(this)

            val item = this@PureArray[this.nextIndex()]

            ++(this.current)

            return item
        }
    }
}
