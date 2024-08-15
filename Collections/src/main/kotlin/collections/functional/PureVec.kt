package collections.functional

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import collections.DefaultComparator
import collections.FuncComparator
import collections.index
import collections.lastIndex

@Suppress("RemoveRedundantQualifierName")
sealed class PureVec<TElement> : PureList<TElement>, RandomAccess {
    companion object {
        private fun checkIndexRange(fromIndex: Int, toIndex: Int, size: Int) {
            if (fromIndex < 0 || fromIndex >= size || toIndex < 0 || toIndex > size) {
                throw IndexOutOfBoundsException()
            }
            if (fromIndex > toIndex) {
                throw IllegalArgumentException()
            }
        }

        private fun checkRangeWithSizeExcluded(index: Int, size: Int) {
            if (index < 0 || index >= size) {
                throw IndexOutOfBoundsException("Index = $index, size = $size")
            }
        }

        private fun checkRangeWithSizeIncluded(index: Int, size: Int) {
            if (index < 0 || index > size) {
                throw IndexOutOfBoundsException("Index = $index, size = $size")
            }
        }

        fun <TItem> empty(): PureVec<TItem> = PureVec.Empty()

        fun <TItem> single(only: TItem): PureVec<TItem> = PureVec.Singleton(only)
    }

    private class Empty<TItem> : PureVec<TItem>() {
        override val head: TItem
            get() = throw NoSuchElementException()

        override val tail: PureVec<TItem>
            get() = throw NoSuchElementException()

        override val size: Int
            get() = 0

        override operator fun get(index: Int): TItem {
            throw IndexOutOfBoundsException("Index = $index, size = 0")
        }

        override fun setAll(elements: Collection<IndexedValue<TItem>>): PureVec<TItem> {
            if (!elements.isEmpty()) {
                throw IndexOutOfBoundsException()
            }

            return this
        }

        override fun prependAll(elements: Collection<TItem>): PureVec<TItem> =
            when (elements.size) {
                0 -> this
                1 -> PureVec.Singleton(elements.single())
                else -> super.prependAll(elements)
            }

        override fun appendAll(elements: Collection<TItem>): PureVec<TItem> =
            this.prependAll(elements)

        override fun reverse(): PureVec<TItem> = this

        override fun replace(new: TItem, predicate: (TItem) -> Boolean): PureVec<TItem> = this

        @Suppress("UNCHECKED_CAST")
        override fun <TOther> transform(operation: (TItem) -> TOther): PureVec<TOther> = this as PureVec<TOther>

        override fun partition(predicate: (TItem) -> Boolean): Pair<PureVec<TItem>, PureVec<TItem>> =
            this to this

        override fun sieve(predicate: (TItem) -> Boolean): PureVec<TItem> = this

        override fun rotate(amount: Int): PureVec<TItem> = this

        override fun sort(comp: (TItem, TItem) -> Int): PureVec<TItem> = this

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
            if (0 != index) {
                throw IndexOutOfBoundsException("Index = $index, size = 1")
            }

            return this.head
        }

        override fun setAll(elements: Collection<IndexedValue<TItem>>): PureVec<TItem> {
            if (elements.isEmpty()) {
                return this
            }

            if (null === elements.firstOrNull{ 0 != it.index }) {
                throw IndexOutOfBoundsException()
            }
            else {
                val last = elements.last()

                return PureVec.single(last.value)
            }
        }

        override fun prependAll(elements: Collection<TItem>): PureVec<TItem> {
            if (elements.isEmpty()) {
                return this
            }

            val newSize = elements.size + 1
            val newCapacity = super.nextCapacity(newSize)
            val newData = arrayOfNulls<Any>(newCapacity)

            for ((index, item) in elements.withIndex()) {
                newData[index] = item
            }

            newData[newSize - 1] = this.head

            return PureVec.Multiple(newData, 0, newSize)
        }

        override fun appendAll(elements: Collection<TItem>): PureVec<TItem> {
            if (elements.isEmpty()) {
                return this
            }

            val newSize = elements.size + 1
            val newCapacity = super.nextCapacity(newSize)
            val newData = arrayOfNulls<Any>(newCapacity)

            var index = 1

            for (item in elements) {
                newData[index] = item
                ++index
            }

            newData[0] = this.head

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

        override fun partition(predicate: (TItem) -> Boolean): Pair<PureVec<TItem>, PureVec<TItem>> =
            if (predicate(this.head))
                this to PureVec.empty()
            else
                PureVec.empty<TItem>() to this

        override fun sieve(predicate: (TItem) -> Boolean): PureVec<TItem> =
            if (predicate(this.head))
                this
            else
                PureVec.empty()

        override fun rotate(amount: Int): PureVec<TItem> = this

        override fun sort(comp: (TItem, TItem) -> Int): PureVec<TItem> = this

        override fun subList(fromIndex: Int, toIndex: Int): PureVec<TItem> =
            if ((0 == fromIndex || 1 == fromIndex) && fromIndex == toIndex)
                PureVec.empty()
            else if (0 == fromIndex && 1 == toIndex)
                this
            else
                throw IndexOutOfBoundsException()
    }

    private class Multiple<TItem>(
        val data: Array<Any?>,
        val startIndex: Int,
        override val size: Int
    ) : PureVec<TItem>() {
        override fun get(index: Int): TItem {
            PureVec.checkRangeWithSizeExcluded(index, this.size)

            @Suppress("UNCHECKED_CAST")
            return this.data[this.actualIndex(index)] as TItem
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

                if (null !== this.data[index]) {
                    return false
                }
            }

            return true
        }

        private fun prependToVec(elements: Collection<TItem>): PureVec<TItem> {
            val iter = elements.iterator()

            val startIndex = this.actualIndex(this.startIndex - elements.size - 1)
            var index = startIndex

            repeat(elements.size) {
                this.data[index] = iter.next()

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
                if (null !== this.data[index]) {
                    return false
                }

                index = this.actualIndex(index + 1)
            }

            return true
        }

        private fun appendToVec(elements: Collection<TItem>): PureVec<TItem> {
            val iter = elements.iterator()

            var index = this.actualIndex(this.startIndex + this.size)

            repeat(elements.size) {
                this.data[index] = iter.next()

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
            PureVec.checkRangeWithSizeExcluded(index, this.size)

            return this.base[index + this.fromIndex]
        }

        override fun subList(fromIndex: Int, toIndex: Int): PureVec<TItem> {
            PureVec.checkIndexRange(fromIndex, toIndex, this.size)

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

        override operator fun get(index: Int): TItem {
            PureVec.checkRangeWithSizeExcluded(index, this.size)

            return this.base[this.lastIndex - index]
        }

        override fun reverse(): PureVec<TItem> = this.base
    }

    private class Replace<TItem>(
        val base: PureVec<TItem>,
        val new: TItem,
        val predicate: (TItem) -> Boolean
    ) : PureVec<TItem>() {
        override val size: Int
            get() = this.base.size

        override operator fun get(index: Int): TItem {
            PureVec.checkRangeWithSizeIncluded(index, this.size)

            val item = this.base[index]

            return if (this.predicate(item)) this.new else item
        }
    }

    private class Transform<TItem, TOther>(
        val base: PureVec<TItem>,
        val operation: (TItem) -> TOther
    ) : PureVec<TOther>() {
        override val size: Int
            get() = this.base.size

        override operator fun get(index: Int): TOther = this.operation(this.base[index])
    }

    private class Rotate<TItem>(
        val base: PureVec<TItem>,
        val amount: Int
    ) : PureVec<TItem>() {
        override val size: Int
            get() = this.base.size

        override operator fun get(index: Int): TItem {
            PureVec.checkRangeWithSizeIncluded(index, this.size)

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
        get() = this[0]

    override val tail: PureVec<TElement>
        get() =
            if (this.isEmpty())
                throw NoSuchElementException()
            else
                this.subList(1, this.size)

    protected fun nextCapacity(size: Int): Int = size * 3 / 2

    override fun isEmpty(): Boolean = 0 == this.size

    override fun set(index: Int, element: TElement): PureVec<TElement> {
        val indexed = IndexedValue(index, element)
        val singleton = PureVec.single(indexed)

        return this.setAll(singleton)
    }

    override fun setAll(elements: Collection<IndexedValue<TElement>>): PureVec<TElement> {
        val newData = arrayOfNulls<Any>(this.size)

        for ((index, item) in elements) {
            newData[index] = item
        }
        for ((index, item) in this.withIndex()) {
            if (null === newData[index]) {
                newData[index] = item
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
        val newData = arrayOfNulls<Any>(newCapacity)

        for ((index, item) in (left + right).withIndex()) {
            newData[index] = item
        }

        return PureVec.Multiple(newData, 0, newSize)
    }

    override fun insert(index: Int, element: TElement): PureVec<TElement> {
        val singleton = PureVec.single(element)

        return this.insertAll(index, singleton)
    }

    override fun insertAll(index: Int, elements: Collection<TElement>): PureVec<TElement> {
        PureVec.checkRangeWithSizeExcluded(index, this.size)

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
        val newData = arrayOfNulls<Any>(newCapacity)

        for ((insertIndex, item) in (left + mid + right).withIndex()) {
            newData[insertIndex] = item
        }

        return PureVec.Multiple(newData, 0, newSize)
    }

    override fun remove(element: TElement): PureVec<TElement> {
        val index = this.indexOf(element)

        return if (-1 == index)
            this
        else
            this.removeAt(index)
    }

    override fun removeAt(index: Int): PureVec<TElement> = this.removeRange(index, index + 1)

    override fun removeRange(fromIndex: Int, toIndex: Int): PureVec<TElement> {
        PureVec.checkIndexRange(fromIndex, toIndex, this.size)

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

    override fun removeAll(elements: Collection<TElement>): PureVec<TElement> = this.sieve(elements::contains)

    override fun removeAt(indices: Collection<Int>): PureVec<TElement> {
        if (indices.isEmpty()) {
            return this
        }

        val newData = arrayOfNulls<Any>(this.size)
        var newIndex = 0

        for ((index, item) in this.withIndex()) {
            if (index !in indices) {
                newData[newIndex] = item
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
        PureVec.checkIndexRange(fromIndex, toIndex, this.size)

        return when (toIndex - fromIndex) {
            0 -> PureVec.empty()
            1 -> PureVec.single(this[fromIndex])
            this.size -> this
            else -> PureVec.Slice(this, fromIndex, toIndex)
        }
    }

    override fun split(index: Int): Pair<PureVec<TElement>, PureVec<TElement>> {
        val left = this.draw(index)
        val right = this.skip(index)

        return left to right
    }

    override fun reverse(): PureVec<TElement> = PureVec.Reverse(this)

    override fun replace(new: TElement, old: TElement): PureVec<TElement> = this.replace(new) { it == old }

    override fun replace(new: TElement, predicate: (TElement) -> Boolean): PureVec<TElement> =
        PureVec.Replace(this, new, predicate)

    override fun <TOther> transform(operation: (TElement) -> TOther): PureVec<TOther> =
        PureVec.Transform(this, operation)

    override fun rotate(amount: Int): PureVec<TElement> =
        if (0 == amount.mod(this.size))
            this
        else
            PureVec.Rotate(this, amount)

    override fun sieve(predicate: (TElement) -> Boolean): PureVec<TElement> {
        val newData = arrayOfNulls<Any>(this.size)
        val index = 0

        return this.sieveHelper(this, newData, index, predicate)
    }

    private tailrec fun sieveHelper(
        current: PureVec<TElement>,
        newData: Array<Any?>,
        index: Int,
        predicate: (TElement) -> Boolean
    ): PureVec<TElement> {
        if (current.isEmpty()) {
            return this.makeVec(newData, index)
        }

        val item = current.head
        var newIndex = index

        if (predicate(item)) {
            newData[newIndex] = item
            ++newIndex
        }

        return this.sieveHelper(current.tail, newData, newIndex, predicate)
    }

    private fun makeVec(newData: Array<Any?>, index: Int): PureVec<TElement> =
        @Suppress("UNCHECKED_CAST")
        when (index) {
            0 -> PureVec.empty()
            1 -> PureVec.single(newData[0] as TElement)
            else -> PureVec.Multiple(newData, 0, index)
        }

    override fun partition(predicate: (TElement) -> Boolean): Pair<PureVec<TElement>, PureVec<TElement>> {
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
        val default = DefaultComparator<TElement>()

        return this.sort(default::compare)
    }

    override fun sort(comp: (TElement, TElement) -> Int): PureVec<TElement> {
        val newData = this.copyToArray()
        val comparator = FuncComparator(comp)

        @Suppress("UNCHECKED_CAST")
        (newData as Array<TElement>).sortWith(comparator)

        return PureVec.Multiple(newData, 0, this.size)
    }

    private fun copyToArray(): Array<Any?> {
        val newData = arrayOfNulls<Any>(this.size)

        for ((index, item) in this.withIndex()) {
            newData[index] = item
        }

        return newData
    }

    override fun find(predicate: (TElement) -> Boolean): Option<TElement> {
        for (item in this) {
            if (predicate(item)) {
                return Some(item)
            }
        }

        return None
    }

    override fun findLast(predicate: (TElement) -> Boolean): Option<TElement> {
        for (index in this.indices) {
            val item = this[index]

            if (predicate(item)) {
                return Some(item)
            }
        }

        return None
    }

    override fun listIterator(index: Int): ListIterator<TElement> = object : ListIterator<TElement> {
        init {
            PureVec.checkRangeWithSizeIncluded(index, this@PureVec.size)
        }

        private var current: Int = index

        override fun previousIndex(): Int = this.current - 1

        override fun nextIndex(): Int = this.current

        override fun hasPrevious(): Boolean = this.previousIndex() >= 0

        override fun hasNext(): Boolean = this.nextIndex() < this@PureVec.size

        override fun previous(): TElement {
            if (!this.hasPrevious()) {
                throw NoSuchElementException()
            }

            val item = this@PureVec[this.previousIndex()]

            --(this.current)

            return item
        }

        override fun next(): TElement {
            if (!this.hasNext()) {
                throw NoSuchElementException()
            }

            val item = this@PureVec[this.nextIndex()]

            ++(this.current)

            return item
        }
    }
}
