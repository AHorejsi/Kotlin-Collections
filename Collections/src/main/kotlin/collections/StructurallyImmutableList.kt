package collections

import kotlin.reflect.KCallable

internal class StructurallyImmutableList<TElement>(
    private val base: ArraySegment<TElement>
) : AbstractList<TElement>(), RandomAccess {
    override val size: Int
        get() = this.base.size

    override fun isEmpty(): Boolean =
        this.base.isEmpty()

    override operator fun get(index: Int): TElement =
        this.base[index]

    override operator fun set(index: Int, element: TElement): TElement {
        val old = this.base[index]

        this.base[index] = element

        return old
    }

    override fun add(element: TElement): Boolean {
        val func: (TElement) -> Boolean = this::add

        unsupported(StructurallyImmutableList::class, func as KCallable<*>)
    }

    override fun add(index: Int, element: TElement) {
        val func: (Int, TElement) -> Unit = this::add

        unsupported(StructurallyImmutableList::class, func as KCallable<*>)
    }

    override fun addAll(elements: Collection<TElement>): Boolean {
        val func: (Collection<TElement>) -> Boolean = this::addAll

        unsupported(StructurallyImmutableList::class, func as KCallable<*>)
    }

    override fun addAll(index: Int, elements: Collection<TElement>): Boolean {
        val func: (Int, Collection<TElement>) -> Boolean = this::addAll

        unsupported(StructurallyImmutableList::class, func as KCallable<*>)
    }

    override fun removeAt(index: Int): TElement =
        unsupported(StructurallyImmutableList::class, this::removeAt)

    override fun remove(element: TElement): Boolean =
        unsupported(StructurallyImmutableList::class, this::remove)

    override fun removeAll(elements: Collection<TElement>): Boolean =
        unsupported(StructurallyImmutableList::class, this::removeAll)

    override fun retainAll(elements: Collection<TElement>): Boolean =
        unsupported(StructurallyImmutableList::class, this::retainAll)

    override fun clear(): Unit =
        unsupported(StructurallyImmutableList::class, this::clear)

    override operator fun contains(element: TElement): Boolean =
        element in this.base

    override fun indexOf(element: TElement): Int =
        this.base.indexOf(element)

    override fun lastIndexOf(element: TElement): Int =
        this.base.lastIndexOf(element)

    override fun listIterator(index: Int): MutableListIterator<TElement> = object : MutableListIterator<TElement> {
        private var currentIndex: Int = index
        private var lastUsedIndex: Int? = null

        override fun previousIndex(): Int =
            this.currentIndex - 1

        override fun nextIndex(): Int =
            this.currentIndex

        override fun hasPrevious(): Boolean =
            this.previousIndex() >= 0

        override fun hasNext(): Boolean =
            this.nextIndex() < this@StructurallyImmutableList.size

        override fun previous(): TElement {
            checkIfPrev(this)

            val item = this@StructurallyImmutableList[this.previousIndex()]

            this.lastUsedIndex = this.currentIndex
            --(this.currentIndex)

            return item
        }

        override fun next(): TElement {
            checkIfNext(this)

            val item = this@StructurallyImmutableList[this.nextIndex()]

            this.lastUsedIndex = this.currentIndex
            ++(this.currentIndex)

            return item
        }

        override fun set(element: TElement) {
            this.lastUsedIndex?.let {
                this@StructurallyImmutableList[it] = element
            }
        }

        override fun add(element: TElement): Unit =
            unsupported(this::class, this::add)

        override fun remove(): Unit =
            unsupported(this::class, this::remove)
    }
}