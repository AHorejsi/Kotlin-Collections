package collections.functional

import collections.*
import kotlin.math.min

interface PureList<TElement> : List<TElement> {
    val head: TElement

    val tail: PureList<TElement>

    fun force(): PureList<TElement>

    fun update(index: Int, element: TElement): PureList<TElement>

    fun updateAll(elements: Map<Int, TElement>): PureList<TElement>

    fun prepend(element: TElement): PureList<TElement>

    fun prependAll(elements: Collection<TElement>): PureList<TElement>

    fun append(element: TElement): PureList<TElement>

    fun appendAll(elements: Collection<TElement>): PureList<TElement>

    fun insert(index: Int, element: TElement): PureList<TElement>

    fun insertAll(index: Int, elements: Collection<TElement>): PureList<TElement>

    fun delete(element: @UnsafeVariance TElement): PureList<TElement>

    fun deleteAll(elements: Collection<@UnsafeVariance TElement>): PureList<TElement>

    fun deleteAt(index: Int): PureList<TElement>

    fun deleteAt(indices: Collection<Int>): PureList<TElement>

    fun deleteRange(fromIndex: Int, toIndex: Int): PureList<TElement>

    fun sieve(predicate: (TElement) -> Boolean): PureList<TElement>

    fun replace(new: TElement, old: @UnsafeVariance TElement): PureList<TElement>

    fun replace(new: TElement, predicate: (TElement) -> Boolean): PureList<TElement>

    fun draw(amount: Int): PureList<TElement>

    fun drawLast(amount: Int): PureList<TElement>

    fun skip(amount: Int): PureList<TElement>

    fun skipLast(amount: Int): PureList<TElement>

    fun drawWhile(predicate: (TElement) -> Boolean): PureList<TElement>

    fun drawLastWhile(predicate: (TElement) -> Boolean): PureList<TElement>

    fun skipWhile(predicate: (TElement) -> Boolean): PureList<TElement>

    fun skipLastWhile(predicate: (TElement) -> Boolean): PureList<TElement>

    fun split(index: Int): Pair<PureList<TElement>, PureList<TElement>>

    override fun indexOf(element: @UnsafeVariance TElement): Int =
        this.index(0, element)

    override fun lastIndexOf(element: @UnsafeVariance TElement): Int =
        this.lastIndex(0, element)

    override operator fun contains(element: @UnsafeVariance TElement): Boolean =
        -1 == this.indexOf(element)

    override fun containsAll(elements: Collection<@UnsafeVariance TElement>): Boolean =
        elements.all(this::contains)

    fun find(predicate: (TElement) -> Boolean): Result<TElement>

    fun findLast(predicate: (TElement) -> Boolean): Result<TElement>

    override fun subList(fromIndex: Int, toIndex: Int): PureList<TElement>

    fun reverse(): PureList<TElement>

    fun rotate(amount: Int): PureList<TElement>

    fun <TOther> transform(operation: (TElement) -> TOther): PureList<TOther>

    fun sort(): PureList<TElement>

    fun sort(comp: (TElement, TElement) -> Int): PureList<TElement>

    fun separate(predicate: (TElement) -> Boolean): Pair<PureList<TElement>, PureList<TElement>>

    override fun iterator(): Iterator<TElement> =
        this.listIterator()

    override fun listIterator(): ListIterator<TElement> =
        this.listIterator(0)
}

/*fun <TElement1, TElement2> pairUp(left: PureList<TElement1>, right: PureList<TElement2>): PureList<Pair<TElement1, TElement2>> {
    val size = min(left.size, right.size)

    val newLeft = left.draw(size)
    val newRight = right.draw(size)

    return ZipList(newLeft, newRight)
}

private class ZipList<TElement1, TElement2>(
    private val left: PureList<TElement1>,
    private val right: PureList<TElement2>
) : PureList<Pair<TElement1, TElement2>> {
    override val head: Pair<TElement1, TElement2> by lazy(LazyThreadSafetyMode.PUBLICATION)
        { Pair(this.left.head, this.right.head) }

    override val tail: PureList<Pair<TElement1, TElement2>> by lazy(LazyThreadSafetyMode.PUBLICATION)
        { ZipList(this.left.tail, this.right.tail) }

    override val size: Int
        get() = this.left.size

    override fun isEmpty(): Boolean =
        this.left.isEmpty()

    override operator fun get(index: Int): Pair<TElement1, TElement2> {
        val leftItem = this.left[index]
        val rightItem = this.right[index]

        return Pair(leftItem, rightItem)
    }

    override fun update(index: Int, element: Pair<TElement1, TElement2>): PureList<Pair<TElement1, TElement2>> {
        val newLeft = this.left.update(index, element.first)
        val newRight = this.right.update(index, element.second)

        return ZipList(newLeft, newRight)
    }

    override fun updateAll(elements: Map<Int, Pair<TElement1, TElement2>>): PureList<Pair<TElement1, TElement2>> {
        val leftMap = elements.asSequence().map{ Pair(it.key, it.value.first) }.toMap()
        val rightMap = elements.asSequence().map{ Pair(it.key, it.value.second) }.toMap()

        val newLeft = this.left.updateAll(leftMap)
        val newRight = this.right.updateAll(rightMap)

        return ZipList(newLeft, newRight)
    }

    override fun prepend(element: Pair<TElement1, TElement2>): PureList<Pair<TElement1, TElement2>> {
        val newLeft = this.left.prepend(element.first)
        val newRight = this.right.prepend(element.second)

        return ZipList(newLeft, newRight)
    }

    override fun prependAll(elements: Collection<Pair<TElement1, TElement2>>): PureList<Pair<TElement1, TElement2>> {
        val leftList = elements.asSequence().map(Pair<TElement1, TElement2>::first).toList()
        val rightList = elements.asSequence().map(Pair<TElement1, TElement2>::second).toList()

        val newLeft = this.left.prependAll(leftList)
        val newRight = this.right.prependAll(rightList)

        return ZipList(newLeft, newRight)
    }

    override fun append(element: Pair<TElement1, TElement2>): PureList<Pair<TElement1, TElement2>> {
        val newLeft = this.left.append(element.first)
        val newRight = this.right.append(element.second)

        return ZipList(newLeft, newRight)
    }

    override fun appendAll(elements: Collection<Pair<TElement1, TElement2>>): PureList<Pair<TElement1, TElement2>> {
        val leftList = elements.asSequence().map(Pair<TElement1, TElement2>::first).toList()
        val rightList = elements.asSequence().map(Pair<TElement1, TElement2>::second).toList()

        val newLeft = this.left.appendAll(leftList)
        val newRight = this.right.appendAll(rightList)

        return ZipList(newLeft, newRight)
    }

    override fun insert(index: Int, element: Pair<TElement1, TElement2>): PureList<Pair<TElement1, TElement2>> {
        val newLeft = this.left.insert(index, element.first)
        val newRight = this.right.insert(index, element.second)

        return ZipList(newLeft, newRight)
    }

    override fun insertAll(index: Int, elements: Collection<Pair<TElement1, TElement2>>): PureList<Pair<TElement1, TElement2>> {
        val leftList = elements.asSequence().map(Pair<TElement1, TElement2>::first).toList()
        val rightList = elements.asSequence().map(Pair<TElement1, TElement2>::second).toList()

        val newLeft = this.left.insertAll(index, leftList)
        val newRight = this.right.insertAll(index, rightList)

        return ZipList(newLeft, newRight)
    }

    override fun delete(element: Pair<TElement1, TElement2>): PureList<Pair<TElement1, TElement2>> {
        val iter = this.withIndex().iterator()

        return this.deleteHelper(iter, element)
    }

    private tailrec fun deleteHelper(
        iter: Iterator<IndexedValue<Pair<TElement1, TElement2>>>,
        element: Pair<TElement1, TElement2>
    ): PureList<Pair<TElement1, TElement2>> {
        if (!iter.hasNext()) {
            return this
        }

        val (index, pair) = iter.next()

        return if (pair == element)
            this.deleteAt(index)
        else
            this.deleteHelper(iter, element)
    }

    override fun deleteAll(elements: Collection<Pair<TElement1, TElement2>>): PureList<Pair<TElement1, TElement2>> {
        val iter = elements.iterator()

        return this.deleteAllHelper(this, iter)
    }

    private tailrec fun deleteAllHelper(
        list: PureList<Pair<TElement1, TElement2>>,
        iter: Iterator<Pair<TElement1, TElement2>>
    ): PureList<Pair<TElement1, TElement2>> {
        if (!iter.hasNext()) {
            return this
        }

        val elem = iter.next()
        val newList = list.delete(elem)

        return this.deleteAllHelper(newList, iter)
    }

    override fun deleteAt(index: Int): PureList<Pair<TElement1, TElement2>> {
        val newLeft = this.left.deleteAt(index)
        val newRight = this.right.deleteAt(index)

        return ZipList(newLeft, newRight)
    }

    override fun deleteAt(indices: Collection<Int>): PureList<Pair<TElement1, TElement2>> {
        val newLeft = this.left.deleteAt(indices)
        val newRight = this.right.deleteAt(indices)

        return ZipList(newLeft, newRight)
    }

    override fun deleteRange(fromIndex: Int, toIndex: Int): PureList<Pair<TElement1, TElement2>> {
        val newLeft = this.left.deleteRange(fromIndex, toIndex)
        val newRight = this.right.deleteRange(fromIndex, toIndex)

        return ZipList(newLeft, newRight)
    }

    override fun draw(amount: Int): PureList<Pair<TElement1, TElement2>> {
        val newLeft = this.left.draw(amount)
        val newRight = this.right.draw(amount)

        return ZipList(newLeft, newRight)
    }

    override fun skip(amount: Int): PureList<Pair<TElement1, TElement2>> {
        val newLeft = this.left.skip(amount)
        val newRight = this.right.skip(amount)

        return ZipList(newLeft, newRight)
    }

    override fun drawLast(amount: Int): PureList<Pair<TElement1, TElement2>> {
        val newLeft = this.left.drawLast(amount)
        val newRight = this.right.drawLast(amount)

        return ZipList(newLeft, newRight)
    }

    override fun skipLast(amount: Int): PureList<Pair<TElement1, TElement2>> {
        val newLeft = this.left.skipLast(amount)
        val newRight = this.right.skipLast(amount)

        return ZipList(newLeft, newRight)
    }

    override fun drawWhile(predicate: (Pair<TElement1, TElement2>) -> Boolean): PureList<Pair<TElement1, TElement2>> {
        val iter = this.iterator()
        val amount = this.whileHelper(iter, predicate, 0)

        return this.draw(amount)
    }

    override fun skipWhile(predicate: (Pair<TElement1, TElement2>) -> Boolean): PureList<Pair<TElement1, TElement2>> {
        val iter = this.iterator()
        val amount = this.whileHelper(iter, predicate, 0)

        return this.skip(amount)
    }

    private tailrec fun whileHelper(
        iter: Iterator<Pair<TElement1, TElement2>>,
        predicate: (Pair<TElement1, TElement2>) -> Boolean,
        index: Int
    ): Int {
        if (!iter.hasNext()) {
            return index
        }

        val elem = iter.next()

        if (predicate(elem)) {
            return index
        }

        return this.whileHelper(iter, predicate, index + 1)
    }

    override fun listIterator(index: Int): ListIterator<Pair<TElement1, TElement2>> = object : ListIterator<Pair<TElement1, TElement2>> {
        private val left: ListIterator<TElement1> = this@ZipList.left.listIterator(index)
        private val right: ListIterator<TElement2> = this@ZipList.right.listIterator(index)

        override fun previousIndex(): Int =
            this.left.previousIndex()

        override fun nextIndex(): Int =
            this.left.nextIndex()

        override fun hasPrevious(): Boolean =
            this.left.hasPrevious()

        override fun hasNext(): Boolean =
            this.left.hasNext()

        override fun previous(): Pair<TElement1, TElement2> {
            checkIfPrev(this)

            val leftItem = this.left.previous()
            val rightItem = this.right.previous()

            return Pair(leftItem, rightItem)
        }

        override fun next(): Pair<TElement1, TElement2> {
            checkIfNext(this)

            val leftItem = this.left.next()
            val rightItem = this.right.next()

            return Pair(leftItem, rightItem)
        }
    }
}*/
