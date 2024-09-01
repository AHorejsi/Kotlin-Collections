package collections.functional

/*import arrow.core.memoize
import collections.*
import collections.empty

sealed class PureLine<TElement> : PureList<TElement> {
    companion object {
        fun <TItem> empty(): PureLine<TItem> =
            Empty()

        fun <TItem> single(only: TItem): PureLine<TItem> =
            Cons(only)
    }

    private class Empty<TItem> : PureLine<TItem>() {
        override val head: TItem
            get() = empty("Empty Line" )

        override val tail: PureLine<TItem>
            get() = empty("Empty Line")

        override val size: Int
            get() = 0
    }

    private class Cons<TItem>(
        override val head: TItem,
        override val tail: PureLine<TItem> = PureLine.empty()
    ) : PureLine<TItem>() {
        override val size: Int by lazy(LazyThreadSafetyMode.PUBLICATION) { 1 + this.tail.size }
    }

    private class Sieve<TItem>(
        private val base: PureLine<TItem>,
        private val predicate: (TItem) -> Boolean
    ) : PureLine<TItem>() {
        private val leading: PureLine<TItem> by lazy(LazyThreadSafetyMode.PUBLICATION) { this.getLeadingNode(this.base) }

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

    private val getter: (Int) -> TElement by lazy(LazyThreadSafetyMode.PUBLICATION) { this::getHelper.memoize() }

    abstract override val head: TElement

    abstract override val tail: PureLine<TElement>

    override fun isEmpty(): Boolean =
        this is PureLine.Empty<*>

    override operator fun get(index: Int): TElement =
        runCatching{ this.getter(index) }.getOrElse{ outOfBounds(index) }

    private fun getHelper(index: Int): TElement =
        if (0 == index)
            this.head
        else
            this.tail[index - 1]

    override fun sieve(predicate: (TElement) -> Boolean): PureList<TElement> =
        PureLine.Sieve(this, predicate)

    override fun <TOther> transform(operation: (TElement) -> TOther): PureList<TOther> =
        PureLine.Transform(this, operation)

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
        private var past: Stack<PureLine<TElement>> = LinkedStack()

        init {
            this.getLine(index, 0, this@PureLine)
        }

        private tailrec fun getLine(targetIndex: Int, currentIndex: Int, line: PureLine<TElement>) {
            if (currentIndex != targetIndex) {
                this.past.push(line)

                this.getLine(targetIndex, currentIndex + 1, line.tail)
            }
        }

        override fun previousIndex(): Int =
            this.currentIndex - 1

        override fun nextIndex(): Int =
            this.currentIndex

        override fun hasPrevious(): Boolean =
            !this.past.isEmpty()

        override fun hasNext(): Boolean =
            this.past.tryPeek().map{ !it.tail.isEmpty() }.getOrDefault(true)

        override fun previous(): TElement {
            checkIfPrev(this)

            return this.past.pop().head
        }

        override fun next(): TElement {
            checkIfNext(this)

            val top = this.past.tryPeek()
            val next = top.fold(
                { it.tail },
                { this@PureLine }
            )

            this.past.push(next)

            return next.head
        }
    }
}*/
