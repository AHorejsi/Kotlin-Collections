package me.alexh.collects

interface SortedSet<TElement> : Set<TElement> {
    val comparator: Comparator<in TElement>?

    fun min(): TElement = this.tryMin().getOrThrow()

    fun minOrNull(): TElement? = this.tryMin().getOrNull()

    fun tryMin(): Result<TElement>

    fun max(): TElement = this.tryMax().getOrThrow()

    fun maxOrNull(): TElement? = this.tryMax().getOrNull()

    fun tryMax(): Result<TElement>

    fun lesser(max: TElement, inclusive: Boolean): TElement = this.tryLesser(max, inclusive).getOrThrow()

    fun lesserOrNull(max: TElement, inclusive: Boolean): TElement? = this.tryLesser(max, inclusive).getOrNull()

    fun tryLesser(max: TElement, inclusive: Boolean): Result<TElement>

    fun greater(min: TElement, inclusive: Boolean): TElement = this.tryGreater(min, inclusive).getOrThrow()

    fun greaterOrNull(min: TElement, inclusive: Boolean): TElement? = this.tryGreater(min, inclusive).getOrNull()

    fun tryGreater(min: TElement, inclusive: Boolean): Result<TElement>

    fun headSet(min: TElement, inclusive: Boolean): SortedSet<TElement>

    fun tailSet(max: TElement, inclusive: Boolean): SortedSet<TElement>

    fun subSet(min: TElement, minInclusive: Boolean, max: TElement, maxInclusive: Boolean): SortedSet<TElement>

    fun reverseSet(): SortedSet<TElement>
}

interface MutableSortedSet<TElement> : MutableSet<TElement>, SortedSet<TElement> {
    override fun headSet(min: TElement, inclusive: Boolean): MutableSortedSet<TElement> {
        val minToUse = if (inclusive) (Result.success(min)) else this.tryGreater(min, false)
        val maxToUse = this.tryMax()

        return SortedSubset(this, minToUse, maxToUse)
    }

    override fun tailSet(max: TElement, inclusive: Boolean): MutableSortedSet<TElement> {
        val minToUse = this.tryMin()
        val maxToUse = if (inclusive) (Result.success(max)) else this.tryLesser(max, false)

        return SortedSubset(this, minToUse, maxToUse)
    }

    override fun subSet(min: TElement, minInclusive: Boolean, max: TElement, maxInclusive: Boolean): MutableSortedSet<TElement>

    override fun reverseSet(): MutableSortedSet<TElement>
}
