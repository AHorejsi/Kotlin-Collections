package me.alexh.collects.pure

import me.alexh.collects.LeftAccumulator
import me.alexh.collects.Predicate
import me.alexh.collects.RightAccumulator
import me.alexh.collects.Transformer

interface PureList<TElement> : List<TElement> {
    val head: TElement
        get() = this[0]

    val tail: PureList<TElement>
        get() = this.drop(1)

    fun setAt(index: Int, element: TElement): PureList<TElement>

    fun add(element: TElement): PureList<TElement>

    override operator fun contains(element: TElement): Boolean = -1 != this.indexOf(element)

    override fun containsAll(elements: Collection<TElement>): Boolean {
        for (item in elements) {
            if (item !in this) {
                return false
            }
        }

        return true
    }

    fun remove(element: TElement): PureList<TElement>

    fun <TOther> map(transformer: Transformer<in TElement, out TOther>): PureList<TOther>

    fun filter(predicate: Predicate<in TElement>): PureList<TElement>

    fun concat(other: PureList<TElement>): PureList<TElement>

    fun reverse(): PureList<TElement>

    fun take(amount: Int): PureList<TElement>

    fun takeLast(amount: Int): PureList<TElement>

    fun takeWhile(predicate: Predicate<in TElement>): PureList<TElement>

    fun takeLastWhile(predicate: Predicate<in TElement>): PureList<TElement>

    fun drop(amount: Int): PureList<TElement>

    fun dropLast(amount: Int): PureList<TElement>

    fun dropWhile(predicate: Predicate<in TElement>): PureList<TElement>

    fun dropLastWhile(predicate: Predicate<in TElement>): PureList<TElement>

    fun <TOther> foldLeft(acc: LeftAccumulator<in TElement, TOther>, initial: TOther): TOther

    fun <TOther> foldRight(acc: RightAccumulator<in TElement, TOther>, initial: TOther): TOther

    override fun listIterator(): ListIterator<TElement> = this.listIterator(0)
}
