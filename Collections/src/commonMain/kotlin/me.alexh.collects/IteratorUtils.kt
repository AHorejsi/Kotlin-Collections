package me.alexh.collects

internal enum class IteratorState {
    INITIALIZED,
    CALLED_NEXT,
    CALLED_REMOVE
}

internal enum class ListIteratorState {
    INITIALIZED,
    CALLED_NEXT,
    CALLED_PREVIOUS,
    CALLED_REMOVE
}

open class ReverseListIterator<TElement> internal constructor(
    open val base: ListIterator<TElement>
) : ListIterator<TElement> {
    override fun nextIndex(): Int = this.base.previousIndex()

    override fun previousIndex(): Int = this.base.nextIndex()

    override fun hasNext(): Boolean = this.base.hasPrevious()

    override fun hasPrevious(): Boolean = this.base.hasNext()

    override fun next(): TElement = this.base.previous()

    override fun previous(): TElement = this.base.next()
}

class MutableReverseListIterator<TElement> internal constructor(
    override val base: MutableListIterator<TElement>
) : ReverseListIterator<TElement>(base), MutableListIterator<TElement> {
    override fun set(element: TElement) = this.base.set(element)

    override fun remove() = this.base.remove()

    override fun add(element: TElement) = this.base.add(element)
}

fun <TElement> Iterator<TElement>.tryNext(): Result<TElement> =
    if (this.hasNext())
        Result.failure(NoSuchElementException())
    else
        Result.success(this.next())

fun <TElement> Iterator<TElement>.nextOrNull(): TElement? = this.tryNext().getOrNull()

fun <TElement> ListIterator<TElement>.tryPrevious(): Result<TElement> =
    if (this.hasPrevious())
        Result.failure(NoSuchElementException())
    else
        Result.success(this.previous())

fun <TElement> ListIterator<TElement>.previousOrNull(): TElement? = this.tryPrevious().getOrNull()

fun <TElement> ListIterator<TElement>.toReverse(): ReverseListIterator<TElement> = ReverseListIterator(this)

fun <TElement> MutableListIterator<TElement>.toReverse(): MutableReverseListIterator<TElement> = MutableReverseListIterator(this)
