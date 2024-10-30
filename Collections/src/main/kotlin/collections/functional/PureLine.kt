package collections.functional

import collections.*
import collections.empty
import java.io.Serializable
import kotlin.math.abs

@Suppress("RemoveRedundantQualifierName")
sealed class PureLine<TElement> : PureList<TElement>, Serializable {
    companion object {
        @Suppress("ConstPropertyName")
        private const val serialVersionUID: Long = 1L

        fun <TItem> empty(): PureLine<TItem> =
            PureLine.Empty()

        fun <TItem> single(only: TItem): PureLine<TItem> =
            PureLine.Cons(only)
    }

    private class Empty<TItem> : PureLine<TItem>() {
        override val head: TItem
            get() = empty(PureLine::class)

        override val tail: PureLine<TItem>
            get() = empty(PureLine::class)

        override val size: Int
            get() = 0
    }

    private class Cons<TItem>(
        override val head: TItem,
        override val tail: PureLine<TItem> = PureLine.empty()
    ) : PureLine<TItem>()

    private class Sieve<TItem>(
        private val base: PureLine<TItem>,
        private val predicate: (TItem) -> Boolean
    ) : PureLine<TItem>() {
        private val leading: PureLine<TItem> by lazy(LazyThreadSafetyMode.PUBLICATION)
            { this.getLeadingNode(this.base) }

        private tailrec fun getLeadingNode(line: PureLine<TItem>): PureLine<TItem> =
            if (line.isEmpty() || this.predicate(line.head))
                line
            else
                this.getLeadingNode(line.tail)

        override val head: TItem
            get() = this.leading.head

        override val tail: PureLine<TItem> by lazy(LazyThreadSafetyMode.PUBLICATION)
            { PureLine.Sieve(this.leading.tail, this.predicate) }
    }

    private class Transform<TItem, TOther>(
        private val base: PureLine<TItem>,
        private val operation: (TItem) -> TOther
    ) : PureLine<TOther>() {
        override val head: TOther by lazy(LazyThreadSafetyMode.PUBLICATION)
            { this.operation(this.base.head) }

        override val tail: PureLine<TOther> by lazy(LazyThreadSafetyMode.PUBLICATION)
            { PureLine.Transform(this.base.tail, this.operation) }
    }

    private class Draw<TItem>(
        private val base: PureLine<TItem>,
        private val amount: Int
    ) : PureLine<TItem>() {
        override val head: TItem by lazy(LazyThreadSafetyMode.PUBLICATION)
            { this.base.head }

        override val tail: PureLine<TItem> by lazy(LazyThreadSafetyMode.PUBLICATION)
            { if (1 == this.amount) PureLine.empty() else PureLine.Draw(this.base, this.amount - 1) }
    }

    private class DrawWhile<TItem>(
        private val base: PureLine<TItem>,
        private val predicate: (TItem) -> Boolean
    ) : PureLine<TItem>() {
        override val head: TItem by lazy(LazyThreadSafetyMode.PUBLICATION) { this.base.head }

        override val tail: PureLine<TItem> by lazy(LazyThreadSafetyMode.PUBLICATION)
            { if (this.predicate(this.base.head)) this.base.tail else PureLine.empty() }
    }

    private class Skip<TItem>(
        private val base: PureLine<TItem>,
        private val amount: Int
    ) : PureLine<TItem>() {
        private val leading: PureLine<TItem> by lazy(LazyThreadSafetyMode.PUBLICATION)
            { this.getLeadingNode(this.base, this.amount) }

        private tailrec fun getLeadingNode(line: PureLine<TItem>, amount: Int): PureLine<TItem> =
            if (line.isEmpty() || 0 == amount)
                line
            else
                this.getLeadingNode(line.tail, amount - 1)

        override val head: TItem
            get() = this.leading.head

        override val tail: PureLine<TItem>
            get() = this.leading.tail
    }

    private class SkipWhile<TItem>(
        private val base: PureLine<TItem>,
        private val predicate: (TItem) -> Boolean
    ) : PureLine<TItem>() {
        private val leading: PureLine<TItem> by lazy(LazyThreadSafetyMode.PUBLICATION)
            { this.getLeadingNode(this.base) }

        private tailrec fun getLeadingNode(line: PureLine<TItem>): PureLine<TItem> =
            if (line.isEmpty() || this.predicate(line.head))
                line
            else
                this.getLeadingNode(line.tail)

        override val head: TItem
            get() = this.leading.head

        override val tail: PureLine<TItem>
            get() = this.leading.tail
    }

    private val getter: MutableMap<Int, TElement> by lazy(LazyThreadSafetyMode.PUBLICATION)
        { hashMapOf() }

    abstract override val head: TElement

    abstract override val tail: PureLine<TElement>

    override val size: Int by lazy(LazyThreadSafetyMode.PUBLICATION)
        { 1 + this.tail.size }

    override fun isEmpty(): Boolean =
        this is PureLine.Empty<*>

    override operator fun get(index: Int): TElement =
        this.getter.getOrElse(index) {
            val newItem = this.getHelper(index, this)

            this.getter[index] = newItem

            return newItem
        }

    private tailrec fun getHelper(index: Int, line: PureLine<TElement>): TElement =
        if (0 == index)
            line.head
        else
            this.getHelper(index - 1, line.tail)

    override fun update(index: Int, element: TElement): PureLine<TElement> {
        val singleton = mapOf(index to element)

        return this.updateAll(singleton)
    }

    override fun updateAll(elements: Map<Int, TElement>): PureLine<TElement> =
        if (elements.isEmpty())
            this
        else
            this.updateAllHelper(0, elements)

    private fun updateAllHelper(index: Int, elements: Map<Int, TElement>): PureLine<TElement> =
        if (this.isEmpty())
            this
        else
            this.tail.updateAllHelper(index + 1, elements).prepend(elements[index] ?: this.head)

    override fun prepend(element: TElement): PureLine<TElement> =
        PureLine.Cons(element, this)

    override fun prependAll(elements: Collection<TElement>): PureLine<TElement> {
        if (elements.isEmpty()) {
            return this
        }

        val iter = elements.iterator()

        return this.prependAllHelper(iter)
    }

    private fun prependAllHelper(elements: Iterator<TElement>): PureLine<TElement> {
        if (!elements.hasNext()) {
            return PureLine.empty()
        }

        val item = elements.next()

        return this.prependAllHelper(elements).prepend(item)
    }

    override fun append(element: TElement): PureLine<TElement> {
        val singleton = listOf(element)

        return this.appendAll(singleton)
    }

    override fun appendAll(elements: Collection<TElement>): PureLine<TElement> {
        if (elements.isEmpty()) {
            return this
        }

        return PureLine.empty<TElement>().prependAll(elements).prependAll(this)
    }

    override fun insert(index: Int, element: TElement): PureLine<TElement> {
        val singleton = listOf(element)

        return this.insertAll(index, singleton)
    }

    override fun insertAll(index: Int, elements: Collection<TElement>): PureLine<TElement> {
        checkIfIndexCanBeInsertedAt(index, this.size)

        if (elements.isEmpty()) {
            return this
        }

        return when (index) {
            0 -> this.prependAll(elements)
            this.size -> this.appendAll(elements)
            else -> this.insertAllHelper(index, elements)
        }
    }

    private fun insertAllHelper(index: Int, elements: Collection<TElement>): PureLine<TElement> {
        val (front, back) = this.split(index)

        var newLine = PureLine.empty<TElement>()

        newLine = newLine.prependAll(back)
        newLine = newLine.prependAll(elements)
        newLine = newLine.prependAll(front)

        return newLine
    }

    override fun delete(element: TElement): PureLine<TElement> =
        this.deleteHelper(this, element, false)

    private fun deleteHelper(line: PureLine<TElement>, element: TElement, found: Boolean): PureLine<TElement> {
        if (line.isEmpty()) {
            return line
        }

        val success = !found && line.head == element
        val newLine = this.deleteHelper(line.tail, element, success)

        return if (success)
            newLine
        else
            newLine.prepend(line.head)
    }

    override fun deleteAll(elements: Collection<TElement>): PureLine<TElement> =
        this.deleteAllHelper(elements.iterator())

    private fun deleteAllHelper(iter: Iterator<TElement>): PureLine<TElement> {
        if (!iter.hasNext()) {
            return this
        }

        val elem = iter.next()

        return this.deleteAllHelper(iter).delete(elem)
    }

    override fun deleteAt(index: Int): PureLine<TElement> {
        val singleton = listOf(index)

        return this.deleteAt(singleton)
    }

    override fun deleteAt(indices: Collection<Int>): PureLine<TElement> {
        if (indices.isEmpty()) {
            return this
        }

        return this.deleteAtHelper(this, 0, indices)
    }

    private fun deleteAtHelper(line: PureLine<TElement>, index: Int, indices: Collection<Int>): PureLine<TElement> {
        if (line.isEmpty()) {
            return line
        }

        val newLine = this.deleteAtHelper(line.tail, index + 1, indices)

        return if (index in indices)
            newLine
        else
            newLine.prepend(line.head)
    }

    override fun deleteRange(fromIndex: Int, toIndex: Int): PureLine<TElement> =
        this.deleteRangeHelper(this, fromIndex until toIndex, 0)

    private fun deleteRangeHelper(line: PureLine<TElement>, range: IntRange, currentIndex: Int): PureLine<TElement> {
        if (line.isEmpty()) {
            return line
        }

        val newLine = this.deleteRangeHelper(line.tail, range, currentIndex + 1)

        return if (currentIndex in range)
            newLine
        else
            newLine.prepend(line.head)
    }

    override fun draw(amount: Int): PureLine<TElement> =
        PureLine.Draw(this, amount)

    override fun drawWhile(predicate: (TElement) -> Boolean): PureLine<TElement> =
        PureLine.DrawWhile(this, predicate)

    override fun skip(amount: Int): PureLine<TElement> =
        PureLine.Skip(this, amount)

    override fun skipWhile(predicate: (TElement) -> Boolean): PureLine<TElement> =
        PureLine.SkipWhile(this, predicate)

    override fun drawLast(amount: Int): PureLine<TElement> =
        if (amount >= this.size)
            this
        else
            this.skip(this.size - amount)

    override fun drawLastWhile(predicate: (TElement) -> Boolean): PureLine<TElement> =
        this.reverse().drawWhile(predicate)

    override fun skipLast(amount: Int): PureLine<TElement> =
        if (amount >= this.size)
            PureLine.empty()
        else
            this.draw(this.size - amount)

    override fun skipLastWhile(predicate: (TElement) -> Boolean): PureLine<TElement> =
        this.reverse().skipWhile(predicate)

    override fun split(index: Int): Pair<PureLine<TElement>, PureLine<TElement>> {
        val back = runCatching{ this.splitHelper(this, index) }.getOrElse{ outOfBoundsWithoutSize(index) }
        val front = this.draw(index)

        return Pair(front, back)
    }

    private tailrec fun splitHelper(line: PureLine<TElement>, index: Int): PureLine<TElement> =
        if (0 == index)
            line
        else
            this.splitHelper(line.tail, index - 1)

    override fun find(predicate: (TElement) -> Boolean): Result<TElement> =
        this.findHelper(this, predicate)

    private tailrec fun findHelper(line: PureLine<TElement>, predicate: (TElement) -> Boolean): Result<TElement> =
        if (line.isEmpty())
            Result.failure(ResultUtils.FAILED_SEARCH)
        else if (predicate(this.head))
            Result.success(this.head)
        else
            this.findHelper(line.tail, predicate)

    override fun findLast(predicate: (TElement) -> Boolean): Result<TElement> =
        this.findLastHelper(this, predicate, Result.failure(ResultUtils.FAILED_SEARCH))

    private tailrec fun findLastHelper(
        line: PureLine<TElement>,
        predicate: (TElement) -> Boolean,
        lastFound: Result<TElement>
    ): Result<TElement> =
        if (line.isEmpty())
            lastFound
        else if (predicate(line.head))
            this.findLastHelper(line.tail, predicate, Result.success(line.head))
        else
            this.findLastHelper(line.tail, predicate, lastFound)

    override fun rotate(amount: Int): PureLine<TElement> {
        if (!this.tail.isEmpty()) {
            return this
        }

        var rotations = abs(amount % this.size)

        if (amount < 0) {
            rotations = this.size - rotations
        }

        val (left, right) = this.split(rotations)

        return left.prependAll(right)
    }

    override fun subList(fromIndex: Int, toIndex: Int): PureLine<TElement> =
        this.skip(fromIndex).draw(toIndex - fromIndex)

    override fun sieve(predicate: (TElement) -> Boolean): PureLine<TElement> =
        PureLine.Sieve(this, predicate)

    override fun <TOther> transform(operation: (TElement) -> TOther): PureLine<TOther> =
        PureLine.Transform(this, operation)

    override fun replace(new: TElement, old: TElement): PureLine<TElement> =
        this.replace(new) { it == old }

    override fun replace(new: TElement, predicate: (TElement) -> Boolean): PureLine<TElement> =
        this.transform{ if (predicate(it)) new else it }

    override fun reverse(): PureLine<TElement> {
        if (this.isEmpty()) {
            return this
        }

        return this.tail.reverse().prepend(this.head)
    }

    override fun separate(predicate: (TElement) -> Boolean): Pair<PureLine<TElement>, PureLine<TElement>> {
        val empty = PureLine.empty<TElement>()

        return this.partitionHelper(this, predicate, empty, empty)
    }

    private tailrec fun partitionHelper(
        line: PureLine<TElement>,
        predicate: (TElement) -> Boolean,
        yes: PureLine<TElement>,
        no: PureLine<TElement>
    ): Pair<PureLine<TElement>, PureLine<TElement>> {
        if (line.isEmpty()) {
            return Pair(yes, no)
        }

        val elem = line.head

        var newYes = yes
        var newNo = no

        if (predicate(elem)) {
            newYes = yes.prepend(elem)
        }
        else {
            newNo = no.prepend(elem)
        }

        return this.partitionHelper(line.tail, predicate, newYes, newNo)
    }

    override fun sort(): PureLine<TElement> {
        val default = inOrder<TElement>()

        return this.sort(default::compare)
    }

    override fun sort(comp: (TElement, TElement) -> Int): PureLine<TElement> {
        if (this.isEmpty() || this.tail.isEmpty()) {
            return this
        }

        val half = this.size / 2

        val (left, right) = this.split(half)

        val sortedLeft = left.sort(comp)
        val sortedRight = right.sort(comp)

        return this.mergeSortHelper(sortedLeft, sortedRight, comp)
    }

    private fun mergeSortHelper(
        left: PureLine<TElement>,
        right: PureLine<TElement>,
        comp: (TElement, TElement) -> Int
    ): PureLine<TElement> {
        if (left.isEmpty()) {
            return right
        }
        if (right.isEmpty()) {
            return left
        }

        return if (comp(left.head, right.head) <= 0)
            this.mergeSortHelper(left.tail, right, comp).prepend(left.head)
        else
            this.mergeSortHelper(left, right.tail, comp).prepend(right.head)
    }

    override fun iterator(): Iterator<TElement> = object : Iterator<TElement> {
        private var line = this@PureLine

        override fun hasNext(): Boolean =
            !this.line.isEmpty()

        override fun next(): TElement {
            checkIfNext(this)

            val item = this.line.head

            this.line = this.line.tail

            return item
        }
    }

    override fun listIterator(index: Int): ListIterator<TElement> = object : ListIterator<TElement> {
        private var currentIndex: Int = index
        private val past: Stack<PureLine<TElement>> = LinkedStack()

        init {
            runCatching {
                this.getLine(index, 0, this@PureLine)
            }.onFailure {
                outOfBoundsWithoutSize(index)
            }
        }

        private tailrec fun getLine(targetIndex: Int, currentIndex: Int, line: PureLine<TElement>) {
            if (currentIndex < targetIndex) {
                this.past.push(line)

                this.getLine(targetIndex, currentIndex + 1, line.tail)
            }
        }

        override fun previousIndex(): Int =
            this.currentIndex - 1

        override fun nextIndex(): Int =
            this.currentIndex

        override fun hasPrevious(): Boolean =
            this.previousIndex() >= 0

        override fun hasNext(): Boolean =
            this.nextIndex() < this@PureLine.size

        override fun previous(): TElement {
            checkIfPrev(this)

            --(this.currentIndex)

            return this.past.pop().head
        }

        override fun next(): TElement {
            checkIfNext(this)

            val next =
                if (this.hasPrevious())
                    this.past.peek().tail
                else
                    this@PureLine

            this.past.push(next)
            ++(this.currentIndex)

            return next.head
        }
    }
}
