package me.alexh.collects

internal class SortedSubset<TElement>(
    private val under: MutableSortedSet<TElement>,
    private var min: Result<TElement>,
    private var max: Result<TElement>
) : SealedMutableCollection<TElement>(), MutableSortedSet<TElement> {
    @Suppress("UNCHECKED_CAST")
    private val actualComparator: Comparator<in TElement> = this.comparator ?: Comparator{ left, right -> (left as Comparable<TElement>).compareTo(right) }
    override var size: Int = 0
        private set

    init {
        if (this.min.isSuccess) {
            var min = this.min.getOrThrow()
            val max = this.max.getOrThrow()

            while (0 != this.actualComparator.compare(min, max)) {
                ++(this.size)
                min = this.under.greater(min, false)
            }
        }
    }

    override val comparator: Comparator<in TElement>?
        get() = this.under.comparator

    override fun tryMin(): Result<TElement> = this.min

    override fun tryMax(): Result<TElement> = this.max

    override fun add(element: TElement): Boolean {
        val added = this.under.add(element)

        if (added) {
            if (super.isEmpty()) {
                this.min = Result.success(element)
                this.max = Result.success(element)
            }
            else {
                if (this.actualComparator.compare(element, this.min.getOrThrow()) < 0) {
                    this.min = Result.success(element)
                }
                else if (this.actualComparator.compare(element, this.max.getOrThrow()) > 0) {
                    this.max = Result.success(element)
                }
            }

            ++(this.size)
        }

        return added
    }

    override operator fun contains(element: TElement): Boolean {
        val singletonList = listOf(element)

        return this.containsAll(singletonList)
    }

    override fun containsAll(elements: Collection<TElement>): Boolean {
        if (super.isEmpty()) {
            return false
        }
        else {
            val min = this.min.getOrThrow()
            val max = this.max.getOrThrow()

            for (item in elements) {
                if (!this.searchNonEmpty(item, min, max)) {
                    return false
                }
            }

            return true
        }
    }

    private fun searchNonEmpty(element: TElement, min: TElement, max: TElement): Boolean =
        if (this.actualComparator.compare(element, min) < 0 || this.actualComparator.compare(element, max) > 0)
            false
        else
            element in this.under

    override fun remove(element: TElement): Boolean {
        if (super.isEmpty()) {
            return false
        }

        val min = this.min.getOrThrow()
        val max = this.max.getOrThrow()

        val minComparison = this.actualComparator.compare(element, min)
        val maxComparison = this.actualComparator.compare(element, max)

        if (minComparison < 0 || maxComparison > 0) {
            return false
        }

        return this.doRemove(element, min, minComparison, max, maxComparison)
    }

    private fun doRemove(
        element: TElement,
        min: TElement,
        minComparison: Int,
        max: TElement,
        maxComparison: Int
    ): Boolean {
        val removed = this.under.remove(element)

        if (removed) {
            if (0 == minComparison) {
                this.min = this.under.tryGreater(min, false)
            }
            if (0 == maxComparison) {
                this.max = this.under.tryLesser(max, false)
            }

            --(this.size)
        }

        return removed
    }

    override fun tryLesser(max: TElement, inclusive: Boolean): Result<TElement> =
        if (super.isEmpty() || this.actualComparator.compare(max, this.min.getOrThrow()) < 0)
            Result.failure(NoSuchElementException())
        else
            this.under.tryLesser(max, inclusive)

    override fun tryGreater(min: TElement, inclusive: Boolean): Result<TElement> =
        if (super.isEmpty() || this.actualComparator.compare(min, this.max.getOrThrow()) > 0)
            Result.failure(NoSuchElementException())
        else
            this.under.tryGreater(min, inclusive)

    override fun clear() {
        if (!super.isEmpty()) {
            var minVal = this.min.getOrThrow()
            val maxVal = this.max.getOrThrow()

            while (this.actualComparator.compare(minVal, maxVal) < 0) {
                this.under.remove(minVal)

                val min = this.under.tryGreater(minVal, false)

                if (min.isSuccess) {
                    minVal = min.getOrThrow()
                }
            }

            this.under.remove(maxVal)

            this.min = Result.failure(NoSuchElementException())
            this.max = Result.failure(NoSuchElementException())
            this.size = 0
        }
    }

    override fun subSet(
        min: TElement,
        minInclusive: Boolean,
        max: TElement,
        maxInclusive: Boolean
    ): MutableSortedSet<TElement> {
        if (this.actualComparator.compare(min, max) > 0) {
            throw IllegalArgumentException()
        }

        var actualMin = this.tryGreater(min, minInclusive)
        var actualMax = this.tryLesser(max, maxInclusive)

        if (this.actualComparator.compare(actualMin.getOrThrow(), actualMax.getOrThrow()) > 0) {
            actualMin = Result.failure(NoSuchElementException())
            actualMax = Result.failure(NoSuchElementException())
        }

        return SortedSubset(this.under, actualMin, actualMax)
    }

    override fun reverseSet(): MutableSortedSet<TElement> = ReverseSortedSet(this)

    override fun iterator(): MutableIterator<TElement> = object : MutableIterator<TElement> {
        var last: Result<TElement> = Result.failure(IllegalStateException())
        var current: Result<TElement> = this@SortedSubset.min

        override fun hasNext(): Boolean {
            if (this.current.isFailure) {
                return false
            }
            else {
                val current = this.current.getOrThrow()
                val max = this@SortedSubset.max.getOrThrow()

                return this@SortedSubset.actualComparator.compare(max, current) <= 0
            }
        }

        override fun next(): TElement {
            if (!this.hasNext()) {
                throw NoSuchElementException()
            }

            val elem = this.current.getOrThrow()

            this.last = this.current
            this.current = this@SortedSubset.tryGreater(elem, false)

            return elem
        }

        override fun remove() {
            this@SortedSubset.remove(this.last.getOrThrow())
            this.last = Result.failure(IllegalStateException())
        }
    }
}
