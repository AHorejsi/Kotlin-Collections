package collections

import arrow.core.Option

fun <TElement> Array<out TElement>.safeGet(index: Int): Option<TElement> =
    this.segment().safeGet(index)

fun <TElement> Array<in TElement>.safeSet(index: Int, element: TElement): Option<Unit> =
    this.segment().safeSet(index, element)

fun <TElement> Array<out TElement>.tryGet(index: Int): Result<TElement> =
    this.segment().tryGet(index)

fun <TElement> Array<in TElement>.trySet(index: Int, element: TElement): Result<Unit> =
    this.segment().trySet(index, element)

fun <TElement> Array<out TElement>.wrapGet(index: Int): TElement =
    this.segment().wrapGet(index)

fun <TElement> Array<in TElement>.wrapSet(index: Int, element: TElement) {
    this.segment().wrapSet(index, element)
}

fun <TElement> Array<out TElement>.index(fromIndex: Int, element: @UnsafeVariance TElement): Int =
    this.index(fromIndex) { it == element }

fun <TElement> Array<out TElement>.index(fromIndex: Int, predicate: (TElement) -> Boolean): Int =
    this.segment().index(fromIndex, predicate)

fun <TElement> Array<out TElement>.lastIndex(fromIndex: Int, element: @UnsafeVariance TElement): Int =
    this.lastIndex(fromIndex) { it == element }

fun <TElement> Array<out TElement>.lastIndex(fromIndex: Int, predicate: (TElement) -> Boolean): Int =
    this.segment().lastIndex(fromIndex, predicate)

fun <TElement> Array<out TElement>.swap(index1: Int, index2: Int) =
    this.segment().swap(index1, index2)

fun <TElement> Array<out TElement>.isPermutationOf(other: Array<TElement>): Boolean =
    this.segment().isPermutationOf(other.segment())

fun <TElement> Array<TElement>.next(comp: Comparator<TElement>? = null): Boolean =
    this.next(comp.function)

fun <TElement> Array<TElement>.next(comp: (TElement, TElement) -> Int): Boolean =
    AsList(this.segment()).next(comp)

fun <TElement> Array<TElement>.prev(comp: Comparator<TElement>? = null): Boolean =
    this.prev(comp.function)

fun <TElement> Array<TElement>.prev(comp: (TElement, TElement) -> Int): Boolean =
    AsList(this.segment()).prev(comp)

fun <TElement> compare(
    leftArray: Array<out TElement>,
    rightArray: Array<out TElement>,
    comp: Comparator<TElement>? = null
): Int =
    compare(leftArray, rightArray, comp.function)

fun <TElement> compare(
    leftArray: Array<out TElement>,
    rightArray: Array<out TElement>,
    comp: (TElement, TElement) -> Int
): Int {
    val leftList = AsList(leftArray.segment())
    val rightList = AsList(rightArray.segment())

    return compare(leftList, rightList, comp)
}

fun <TElement> toString(arr: Array<TElement>): String =
    AsList(arr.segment()).toString()
