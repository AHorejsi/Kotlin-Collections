package me.alexh.collects

internal class ReverseSortedSet<TElement>(
    private val under: MutableSortedSet<TElement>
) : SealedMutableCollection<TElement>(), MutableSortedSet<TElement> {
    override val size: Int
        get() = this.under.size

    @Suppress("UNCHECKED_CAST")
    override val comparator: Comparator<in TElement>
        get() =
            this.under.comparator?.reversed() ?:
            Comparator{ left, right -> (right as Comparable<TElement>).compareTo(left) }

    override fun tryMin(): Result<TElement> = this.under.tryMax()

    override fun tryMax(): Result<TElement> = this.under.tryMin()

    override fun add(element: TElement): Boolean = this.under.add(element)

    override operator fun contains(element: TElement): Boolean = element in this.under

    override fun remove(element: TElement): Boolean = this.under.remove(element)

    override fun clear() = this.under.clear()

    override fun tryLesser(max: TElement, inclusive: Boolean): Result<TElement> = this.tryGreater(max, inclusive)

    override fun tryGreater(min: TElement, inclusive: Boolean): Result<TElement> = this.tryLesser(min, inclusive)

    override fun subSet(
        min: TElement,
        minInclusive: Boolean,
        max: TElement,
        maxInclusive: Boolean
    ): MutableSortedSet<TElement> {
        if (this.comparator.compare(min, max) > 0) {
            throw IllegalArgumentException()
        }

        var actualMin = this.tryGreater(min, minInclusive)
        var actualMax = this.tryLesser(max, maxInclusive)

        if (this.comparator.compare(actualMin.getOrThrow(), actualMax.getOrThrow()) > 0) {
            actualMin = Result.failure(NoSuchElementException())
            actualMax = Result.failure(NoSuchElementException())
        }

        return SortedSubset(this, actualMin, actualMax)
    }

    override fun reverseSet(): MutableSortedSet<TElement> = this.under

    override fun iterator(): MutableIterator<TElement> = this.under.reversed().toMutableList().iterator()
}
