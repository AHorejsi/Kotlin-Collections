package collections.functional

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.getOrElse
import collections.*
import collections.checkIfIndexIsAccessible
import java.io.Serializable

@Suppress("RemoveRedundantQualifierName")
sealed class PureVec<TElement> : PureList<TElement>, RandomAccess, Serializable {
    companion object {
        @Suppress("ConstPropertyName")
        private const val serialVersionUID: Long = 1L

        fun <TItem> empty(): PureVec<TItem> =
            PureVec.Empty()

        fun <TItem> single(only: TItem): PureVec<TItem> =
            PureVec.Singleton(only)
    }

    private class Empty<TItem> : PureVec<TItem>() {
        override val head: TItem
            get() = empty(PureVec::class)

        override val tail: PureVec<TItem>
            get() = empty(PureVec::class)

        override val size: Int
            get() = 0

        override operator fun get(index: Int): TItem =
            outOfBoundsWithoutSize(index)

        override fun updateAll(elements: Map<Int, TItem>): PureVec<TItem> {
            @Suppress("ReplaceNegatedIsEmptyWithIsNotEmpty")
            if (!elements.isEmpty()) {
                val index = elements.keys.first()

                outOfBoundsWithoutSize(index)
            }

            return this
        }

        override fun prependAll(elements: Collection<TItem>): PureVec<TItem> =
            when (elements.size) {
                0 -> this
                1 -> PureVec.single(elements.single())
                else -> super.prependAll(elements)
            }

        override fun appendAll(elements: Collection<TItem>): PureVec<TItem> =
            this.prependAll(elements)

        override fun reverse(): PureVec<TItem> =
            this

        override fun replace(new: TItem, predicate: (TItem) -> Boolean): PureVec<TItem> =
            this

        override fun <TOther> transform(operation: (TItem) -> TOther): PureVec<TOther> =
            PureVec.empty()

        override fun separate(predicate: (TItem) -> Boolean): Pair<PureVec<TItem>, PureVec<TItem>> =
            this to this

        override fun sieve(predicate: (TItem) -> Boolean): PureVec<TItem> =
            this

        override fun rotate(amount: Int): PureVec<TItem> =
            this

        override fun sort(comp: (TItem, TItem) -> Int): PureVec<TItem> =
            this

        override fun subList(fromIndex: Int, toIndex: Int): PureVec<TItem> =
            if (0 != fromIndex || 0 != toIndex)
                throw IndexOutOfBoundsException()
            else
                this
    }

    private class Singleton<TItem>(override val head: TItem) : PureVec<TItem>() {
        override val tail: PureVec<TItem>
            get() = PureVec.empty()

        override val size: Int
            get() = 1

        override operator fun get(index: Int): TItem {
            checkIfIndexIsAccessible(index, 1)

            return this.head
        }

        override fun updateAll(elements: Map<Int, TItem>): PureVec<TItem> {
            if (elements.isEmpty()) {
                return this
            }

            for (index in elements.keys) {
                checkIfIndexIsAccessible(index, 1)
            }

            val item = elements.getValue(0)

            return PureVec.single(item)
        }

        override fun prependAll(elements: Collection<TItem>): PureVec<TItem> {
            if (elements.isEmpty()) {
                return this
            }

            val newSize = elements.size + 1
            val newCapacity = super.nextCapacity(newSize)
            val newData = Array<Option<TItem>>(newCapacity) { None }

            for ((index, item) in elements.withIndex()) {
                newData[index] = Some(item)
            }

            newData[newSize - 1] = Some(this.head)

            return PureVec.Multiple(newData, 0, newSize)
        }

        override fun appendAll(elements: Collection<TItem>): PureVec<TItem> {
            if (elements.isEmpty()) {
                return this
            }

            val newSize = elements.size + 1
            val newCapacity = super.nextCapacity(newSize)
            val newData = Array<Option<TItem>>(newCapacity) { None }

            var index = 1

            for (item in elements) {
                newData[index] = Some(item)
                ++index
            }

            newData[0] = Some(this.head)

            return PureVec.Multiple(newData, 0, newSize)
        }

        override fun reverse(): PureVec<TItem> = this

        override fun replace(new: TItem, predicate: (TItem) -> Boolean): PureVec<TItem> =
            if (predicate(this.head))
                PureVec.single(new)
            else
                this

        override fun <TOther> transform(operation: (TItem) -> TOther): PureVec<TOther> {
            val result = operation(this.head)

            return PureVec.single(result)
        }

        override fun separate(predicate: (TItem) -> Boolean): Pair<PureVec<TItem>, PureVec<TItem>> =
            if (predicate(this.head))
                this to PureVec.empty()
            else
                PureVec.empty<TItem>() to this

        override fun sieve(predicate: (TItem) -> Boolean): PureVec<TItem> =
            if (predicate(this.head))
                this
            else
                PureVec.empty()

        override fun rotate(amount: Int): PureVec<TItem> =
            this

        override fun sort(comp: (TItem, TItem) -> Int): PureVec<TItem> =
            this

        override fun subList(fromIndex: Int, toIndex: Int): PureVec<TItem> {
            checkIfRangeInBounds(fromIndex, toIndex, 1)
            checkIfValidRange(fromIndex, toIndex)

            return if ((0 == fromIndex || 1 == fromIndex) && fromIndex == toIndex)
                PureVec.empty()
            else
                this
        }
    }

    private class Multiple<TItem>(
        val data: Array<Option<TItem>>,
        val startIndex: Int,
        override val size: Int
    ) : PureVec<TItem>() {
        override fun get(index: Int): TItem {
            val actualIndex = this.actualIndex(index)

            return this.data[actualIndex].getOrElse{ outOfBoundsWithSizeInclusive(index, this.size) }
        }

        override fun prependAll(elements: Collection<TItem>): PureVec<TItem> {
            if (elements.isEmpty()) {
                return this
            }

            return if (this.slotsAvailableInFront(elements.size))
                this.prependToVec(elements)
            else
                super.prependAll(elements)
        }

        private fun slotsAvailableInFront(amountToAdd: Int): Boolean {
            if (this.size + amountToAdd > this.data.size) {
                return false
            }

            var index = this.startIndex

            repeat(amountToAdd) {
                index = this.actualIndex(index - 1)

                if (this.data[index].isSome()) {
                    return false
                }
            }

            return true
        }

        private fun prependToVec(elements: Collection<TItem>): PureVec<TItem> {
            val iter = elements.iterator()

            val startIndex = this.actualIndex(this.startIndex - elements.size - 1)
            var index = startIndex

            while (iter.hasNext()) {
                val elem = iter.next()

                this.data[index] = Some(elem)

                index = this.actualIndex(index + 1)
            }

            return PureVec.Multiple(this.data, startIndex, this.size + elements.size)
        }

        override fun appendAll(elements: Collection<TItem>): PureVec<TItem> {
            if (elements.isEmpty()) {
                return this
            }

            return if (this.slotsAvailableInBack(elements.size))
                this.appendToVec(elements)
            else
                super.appendAll(elements)
        }

        private fun slotsAvailableInBack(amountToAdd: Int): Boolean {
            if (this.size + amountToAdd > this.data.size) {
                return false
            }

            var index = this.actualIndex(this.startIndex + this.size)

            repeat(amountToAdd) {
                if (this.data[index].isSome()) {
                    return false
                }

                index = this.actualIndex(index + 1)
            }

            return true
        }

        private fun appendToVec(elements: Collection<TItem>): PureVec<TItem> {
            val iter = elements.iterator()

            var index = this.actualIndex(this.startIndex + this.size)

           while (iter.hasNext()) {
                val item = iter.next()

                this.data[index] = Some(item)

                index = this.actualIndex(index + 1)
            }

            return PureVec.Multiple(this.data, this.startIndex, this.size + elements.size)
        }

        private fun actualIndex(index: Int): Int = (index + this.startIndex).mod(this.data.size)
    }

    private class Slice<TItem>(
        val base: PureVec<TItem>,
        val fromIndex: Int,
        val toIndex: Int
    ) : PureVec<TItem>() {
        override val head: TItem
            get() = this.base[this.fromIndex]

        override val size: Int
            get() = this.toIndex - this.fromIndex

        override operator fun get(index: Int): TItem {
            checkIfIndexIsAccessible(index, this.size)

            return this.base[index + this.fromIndex]
        }

        override fun subList(fromIndex: Int, toIndex: Int): PureVec<TItem> {
            checkIfValidRange(fromIndex, toIndex)
            checkIfRangeInBounds(fromIndex, toIndex, this.size)

            return when (toIndex - fromIndex) {
                0 -> PureVec.empty()
                1 -> PureVec.single(this[fromIndex])
                this.size -> this
                else -> PureVec.Slice(this.base, fromIndex + this.fromIndex, fromIndex + this.toIndex)
            }
        }
    }

    private class Reverse<TItem>(val base: PureVec<TItem>) : PureVec<TItem>() {
        override val size: Int
            get() = this.base.size

        override operator fun get(index: Int): TItem =
            this.base[this.lastIndex - index]

        override fun reverse(): PureVec<TItem> =
            this.base
    }

    private class Transform<TItem, TOther>(
        val base: PureVec<TItem>,
        val operation: (TItem) -> TOther
    ) : PureVec<TOther>() {
        private val transformed: Array<Any?> by lazy(LazyThreadSafetyMode.PUBLICATION)
            { arrayOfNulls(this.size) }

        override val size: Int
            get() = this.base.size

        override operator fun get(index: Int): TOther {
            val transformedItem = this.transformed[index]

            transformedItem?.let {
                @Suppress("UNCHECKED_CAST")
                return it as TOther
            } ?: run {
                val newItem = this.operation(this.base[index])

                this.transformed[index] = newItem

                return newItem
            }
        }
    }

    private class Rotate<TItem>(
        val base: PureVec<TItem>,
        val amount: Int
    ) : PureVec<TItem>() {
        override val size: Int
            get() = this.base.size

        override operator fun get(index: Int): TItem {
            checkIfIndexIsAccessible(index, this.size)

            return this.base[this.actualIndex(index)]
        }

        override fun rotate(amount: Int): PureVec<TItem> {
            if (0 == amount.mod(this.size)) {
                return this
            }

            val newAmount = this.actualIndex(amount)

            return if (0 == newAmount)
                this.base
            else
                PureVec.Rotate(this.base, newAmount)
        }

        private fun actualIndex(index: Int): Int = (index + this.amount).mod(this.base.size)
    }

    override val head: TElement
        get() =
            if (this.isEmpty())
                empty(PureVec::class)
            else
                this[0]

    override val tail: PureVec<TElement>
        get() =
            if (this.isEmpty())
                empty(PureVec::class)
            else
                this.subList(1, this.size)

    protected fun nextCapacity(size: Int): Int =
        size * 3 / 2

    override fun isEmpty(): Boolean =
        0 == this.size

    override fun force(): PureVec<TElement> {
        if (this.size <= 1 || this is PureVec.Multiple<TElement>) {
            return this
        }

        val base = Array<Option<TElement>>(this.size) { None }

        for ((index, item) in this.withIndex()) {
            base[index] = Some(item)
        }

        return PureVec.Multiple(base, 0, this.size)
    }

    override fun update(index: Int, element: TElement): PureVec<TElement> {
        val singleton = mapOf(index to element)

        return this.updateAll(singleton)
    }

    override fun updateAll(elements: Map<Int, TElement>): PureVec<TElement> {
        val newData = Array<Option<TElement>>(this.size) { None }

        for ((index, item) in elements) {
            newData[index] = Some(item)
        }

        for ((index, item) in this.withIndex()) {
            if (None === newData[index]) {
                newData[index] = Some(item)
            }
        }

        return PureVec.Multiple(newData, 0, this.size)
    }

    override fun prepend(element: TElement): PureVec<TElement> {
        val singleton = PureVec.single(element)

        return this.prependAll(singleton)
    }

    override fun prependAll(elements: Collection<TElement>): PureVec<TElement> {
        if (elements.isEmpty()) {
            return this
        }

        val left = elements.asSequence()
        val right = this.asSequence()
        val newSize = this.size + elements.size

        return this.newVec(left, right, newSize)
    }

    override fun append(element: TElement): PureVec<TElement> {
        val singleton = PureVec.single(element)

        return this.appendAll(singleton)
    }

    override fun appendAll(elements: Collection<TElement>): PureVec<TElement> {
        if (elements.isEmpty()) {
            return this
        }

        val left = this.asSequence()
        val right = elements.asSequence()
        val newSize = this.size + elements.size

        return this.newVec(left, right, newSize)
    }

    private fun newVec(left: Sequence<TElement>, right: Sequence<TElement>, newSize: Int): PureVec<TElement> {
        val newCapacity = this.nextCapacity(newSize)
        val newData = Array<Option<TElement>>(newCapacity) { None }

        for ((index, item) in (left + right).withIndex()) {
            newData[index] = Some(item)
        }

        return PureVec.Multiple(newData, 0, newSize)
    }

    override fun insert(index: Int, element: TElement): PureVec<TElement> {
        val singleton = PureVec.single(element)

        return this.insertAll(index, singleton)
    }

    override fun insertAll(index: Int, elements: Collection<TElement>): PureVec<TElement> {
        checkIfIndexCanBeInsertedAt(index, this.size)

        return when (index) {
            0 -> this.prependAll(elements)
            this.size -> this.appendAll(elements)
            else -> this.insertToVec(index, elements)
        }
    }

    private fun insertToVec(index: Int, elements: Collection<TElement>): PureVec<TElement> {
        val seq = this.asSequence()

        val left = seq.take(index)
        val mid = elements.asSequence()
        val right = seq.drop(index)

        val newSize = this.size + elements.size
        val newCapacity = this.nextCapacity(newSize)
        val newData = Array<Option<TElement>>(newCapacity) { None }

        for ((insertIndex, item) in (left + mid + right).withIndex()) {
            newData[insertIndex] = Some(item)
        }

        return PureVec.Multiple(newData, 0, newSize)
    }

    override fun delete(element: TElement): PureVec<TElement> {
        val index = this.indexOf(element)

        return if (-1 == index)
            this
        else
            this.deleteAt(index)
    }

    override fun deleteAt(index: Int): PureVec<TElement> =
        this.deleteRange(index, index + 1)

    override fun deleteRange(fromIndex: Int, toIndex: Int): PureVec<TElement> {
        checkIfValidRange(fromIndex, toIndex)
        checkIfRangeInBounds(fromIndex, toIndex, this.size)

        return if (0 == fromIndex)
            this.skip(toIndex)
        else if (this.size == toIndex)
            this.skipLast(toIndex - fromIndex)
        else
            this.removeRangeFromVec(fromIndex, toIndex)
    }

    private fun removeRangeFromVec(fromIndex: Int, toIndex: Int): PureVec<TElement> {
        val left = this.subList(0, fromIndex)
        val right = this.subList(toIndex, this.size)

        return left.appendAll(right)
    }

    override fun deleteAll(elements: Collection<TElement>): PureVec<TElement> =
        this.sieve(elements::contains)

    override fun deleteAt(indices: Collection<Int>): PureVec<TElement> {
        if (indices.isEmpty()) {
            return this
        }

        val newData = Array<Option<TElement>>(this.size) { None }
        var newIndex = 0

        for ((index, item) in this.withIndex()) {
            if (index !in indices) {
                newData[newIndex] = Some(item)
                ++newIndex
            }
        }

        @Suppress("UNCHECKED_CAST")
        return when (newIndex) {
            0 -> PureVec.empty()
            1 -> PureVec.single(newData[0] as TElement)
            else -> PureVec.Multiple(newData, 0, newIndex)
        }
    }

    override fun draw(amount: Int): PureVec<TElement> =
        if (amount >= this.size)
            this
        else
            this.subList(0, amount)

    override fun drawLast(amount: Int): PureVec<TElement> =
        if (amount >= this.size)
            this
        else
            this.skip(this.size - amount)

    override fun drawWhile(predicate: (TElement) -> Boolean): PureVec<TElement> {
        val index = this.index(0, predicate)

        return this.draw(index)
    }

    override fun drawLastWhile(predicate: (TElement) -> Boolean): PureVec<TElement> {
        val index = this.lastIndex(this.size, predicate)

        return this.drawLast(this.size - index + 1)
    }

    override fun skip(amount: Int): PureVec<TElement> =
        if (amount >= this.size)
            PureVec.empty()
        else
            this.subList(amount, this.size)

    override fun skipLast(amount: Int): PureVec<TElement> =
        if (amount >= this.size)
            PureVec.empty()
        else
            this.draw(this.size - amount)

    override fun skipWhile(predicate: (TElement) -> Boolean): PureVec<TElement> {
        val index = this.index(0, predicate)

        return this.skip(index)
    }

    override fun skipLastWhile(predicate: (TElement) -> Boolean): PureVec<TElement> {
        val index = this.lastIndex(this.size, predicate)

        return this.skipLast(index)
    }

    override fun subList(fromIndex: Int, toIndex: Int): PureVec<TElement> {
        checkIfValidRange(fromIndex, toIndex)
        checkIfRangeInBounds(fromIndex, toIndex, this.size)

        return when (toIndex - fromIndex) {
            0 -> PureVec.empty()
            1 -> PureVec.single(this[fromIndex])
            this.size -> this
            else -> PureVec.Slice(this, fromIndex, toIndex)
        }
    }

    override fun split(index: Int): Pair<PureVec<TElement>, PureVec<TElement>> {
        val nextIndex = index + 1

        val left = this.draw(nextIndex)
        val right = this.skip(nextIndex)

        return left to right
    }

    override fun reverse(): PureVec<TElement> =
        PureVec.Reverse(this)

    override fun replace(new: TElement, old: TElement): PureVec<TElement> =
        this.replace(new) { it == old }

    override fun replace(new: TElement, predicate: (TElement) -> Boolean): PureVec<TElement> =
        this.transform{ if (predicate(it)) new else it }

    override fun <TOther> transform(operation: (TElement) -> TOther): PureVec<TOther> =
        PureVec.Transform(this, operation)

    override fun rotate(amount: Int): PureVec<TElement> =
        if (0 == amount.mod(this.size))
            this
        else
            PureVec.Rotate(this, amount)

    override fun sieve(predicate: (TElement) -> Boolean): PureVec<TElement> {
        val newData = Array<Option<TElement>>(this.size) { None }
        val index = 0

        return this.sieveHelper(this, newData, index, predicate)
    }

    private tailrec fun sieveHelper(
        current: PureVec<TElement>,
        newData: Array<Option<TElement>>,
        index: Int,
        predicate: (TElement) -> Boolean
    ): PureVec<TElement> {
        if (current.isEmpty()) {
            return this.makeVec(newData, index)
        }

        val item = current.head
        var newIndex = index

        if (predicate(item)) {
            newData[newIndex] = Some(item)
            ++newIndex
        }

        return this.sieveHelper(current.tail, newData, newIndex, predicate)
    }

    override fun find(predicate: (TElement) -> Boolean): Result<TElement> =
        this.findHelper(this, 0, predicate)

    private tailrec fun findHelper(
        vec: PureVec<TElement>,
        index: Int,
        predicate: (TElement) -> Boolean
    ): Result<TElement> {
        if (index == vec.size) {
            return Result.failure(ResultUtils.FAILED_SEARCH)
        }

        val item = vec[index]

        if (predicate(item)) {
            return Result.success(item)
        }

        return this.findHelper(vec, index + 1, predicate)
    }

    override fun findLast(predicate: (TElement) -> Boolean): Result<TElement> =
        this.findLastHelper(this, this.lastIndex, predicate)

    private tailrec fun findLastHelper(
        vec: PureVec<TElement>,
        index: Int,
        predicate: (TElement) -> Boolean
    ): Result<TElement> {
        if (-1 == index) {
            return Result.failure(ResultUtils.FAILED_SEARCH)
        }

        val item = vec[index]

        if (predicate(item)) {
            return Result.success(item)
        }

        return this.findLastHelper(vec, index - 1, predicate)
    }

    private fun makeVec(newData: Array<Option<TElement>>, index: Int): PureVec<TElement> =
        @Suppress("UNCHECKED_CAST")
        when (index) {
            0 -> PureVec.empty()
            1 -> PureVec.single(newData[0] as TElement)
            else -> PureVec.Multiple(newData, 0, index)
        }

    override fun separate(predicate: (TElement) -> Boolean): Pair<PureVec<TElement>, PureVec<TElement>> {
        val left = PureVec.empty<TElement>()
        val right = PureVec.empty<TElement>()

        return this.partitionHelper(this, left, right, predicate)
    }

    private tailrec fun partitionHelper(
        current: PureVec<TElement>,
        left: PureVec<TElement>,
        right: PureVec<TElement>,
        predicate: (TElement) -> Boolean
    ) : Pair<PureVec<TElement>, PureVec<TElement>> {
        if (current.isEmpty()) {
            return left to right
        }

        val item = current.head
        var newLeft = left
        var newRight = right

        if (predicate(item)) {
            newLeft = newLeft.append(item)
        }
        else {
            newRight = newRight.append(item)
        }

        return this.partitionHelper(current.tail, newLeft, newRight, predicate)
    }

    override fun sort(): PureVec<TElement> {
        val default = inOrder<TElement>()

        return this.sort(default::compare)
    }

    override fun sort(comp: (TElement, TElement) -> Int): PureVec<TElement> {
        val newData = this.copyToArray()

        this.quickSortHelper(newData, comp, 0, this.lastIndex)

        return PureVec.Multiple(newData, 0, this.size)
    }

    private fun copyToArray(): Array<Option<TElement>> {
        val newData = Array<Option<TElement>>(this.size) { None }

        for ((index, item) in this.withIndex()) {
            newData[index] = Some(item)
        }

        return newData
    }

    private tailrec fun quickSortHelper(newData: Array<Option<TElement>>, comp: (TElement, TElement) -> Int, startIndex: Int, endIndex: Int) {
        if (startIndex >= endIndex) {
            return
        }

        val pivot = this.quickSortPartition(newData, comp, startIndex, endIndex)

        @Suppress("NON_TAIL_RECURSIVE_CALL")
        this.quickSortHelper(newData, comp, startIndex, pivot - 1)
        this.quickSortHelper(newData, comp, pivot + 1, endIndex)
    }

    private fun quickSortPartition(newData: Array<Option<TElement>>, comp: (TElement, TElement) -> Int, startIndex: Int, endIndex: Int): Int {
        val pivotItem = this.medianOfThree(newData, comp, startIndex, endIndex)
        var pivotIndex = (startIndex + endIndex + 1) / 2

        newData.swap(pivotIndex, endIndex)

        for (index in startIndex until endIndex) {
            @Suppress("UNCHECKED_CAST")
            val current = newData[index] as TElement

            if (comp(current, pivotItem) < 0) {
                newData.swap(index, pivotIndex)
                ++pivotIndex
            }
        }

        newData.swap(pivotIndex, endIndex)

        return pivotIndex
    }

    @Suppress("UNCHECKED_CAST")
    private fun medianOfThree(newData: Array<Option<TElement>>, comp: (TElement, TElement) -> Int, startIndex: Int, endIndex: Int): TElement {
        val midIndex = (startIndex + endIndex + 1) / 2

        val startItem = newData[startIndex] as TElement
        val midItem = newData[midIndex] as TElement
        val endItem = newData[endIndex] as TElement

        if (comp(endItem, startItem) < 0) {
            newData.swap(startIndex, endIndex)
        }

        if (comp(midItem, startItem) < 0) {
            newData.swap(midIndex, startIndex)
        }

        if (comp(endItem, midItem) < 0) {
            newData.swap(endIndex, midIndex)
        }

        return midItem
    }

    override fun listIterator(index: Int): ListIterator<TElement> = object : ListIterator<TElement> {
        init {
            checkIfIndexCanBeInsertedAt(index, this@PureVec.size)
        }

        private var current: Int = index

        override fun previousIndex(): Int =
            this.current - 1

        override fun nextIndex(): Int =
            this.current

        override fun hasPrevious(): Boolean =
            this.previousIndex() >= 0

        override fun hasNext(): Boolean =
            this.nextIndex() < this@PureVec.size

        override fun previous(): TElement {
            checkIfPrev(this)

            val item = this@PureVec[this.previousIndex()]

            --(this.current)

            return item
        }

        override fun next(): TElement {
            checkIfNext(this)

            val item = this@PureVec[this.nextIndex()]

            ++(this.current)

            return item
        }
    }
}
