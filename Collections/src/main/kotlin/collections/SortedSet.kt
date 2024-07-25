package collections

interface SortedSet<TElement> : Collection<TElement> {
    fun first(): TElement

    fun last(): TElement

    fun lesser(element: TElement, inclusive: Boolean): TElement

    fun greater(element: TElement, inclusive: Boolean): TElement
}

interface MutableSortedSet<TElement> : SortedSet<TElement>, MutableCollection<TElement> {
    fun removeFirst(): TElement

    fun removeLast(): TElement
}

fun <TElement> SortedSet<TElement>.firstOrNull(): TElement? =
    if (this.isEmpty())
        null
    else
        this.first()

fun <TElement> SortedSet<TElement>.tryFirst(): Result<TElement> =
    runCatching { this.first() }

fun <TElement> SortedSet<TElement>.lastOrNull(): TElement? =
    if (this.isEmpty())
        null
    else
        this.last()

fun <TElement> SortedSet<TElement>.tryLast(): Result<TElement> =
    runCatching { this.last() }

fun <TElement> SortedSet<TElement>.lesserOrNull(element: TElement, inclusive: Boolean): TElement? =
    this.tryLesser(element, inclusive).getOrNull()

fun <TElement> SortedSet<TElement>.tryLesser(element: TElement, inclusive: Boolean): Result<TElement> =
    runCatching { this.lesser(element, inclusive) }

fun <TElement> SortedSet<TElement>.greaterOrNull(element: TElement, inclusive: Boolean): TElement? =
    this.tryGreater(element, inclusive).getOrNull()

fun <TElement> SortedSet<TElement>.tryGreater(element: TElement, inclusive: Boolean): Result<TElement> =
    runCatching { this.greater(element, inclusive) }

fun <TElement> MutableSortedSet<TElement>.removeFirstOrNull(): TElement? =
    if (this.isEmpty())
        null
    else
        this.removeFirst()

fun <TElement> MutableSortedSet<TElement>.tryRemoveFirst(): Result<TElement> =
    runCatching { this.removeFirst() }

fun <TElement> MutableSortedSet<TElement>.removeLastOrNull(): TElement? =
    if (this.isEmpty())
        null
    else
        this.removeLast()

fun <TElement> MutableSortedSet<TElement>.tryRemoveLast(): Result<TElement> =
    runCatching { this.removeLast() }
