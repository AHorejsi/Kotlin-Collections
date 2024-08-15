package collections

import arrow.core.Option

fun <TElement> Array<out TElement>.safeGet(index: Int): Option<TElement> =
    AsList(this).safeGet(index)

fun <TElement> Array<in TElement>.safeSet(index: Int, element: TElement): Option<Unit> =
    AsList(this).safeSet(index, element).map{ Unit }

fun <TElement> Array<out TElement>.tryGet(index: Int): Result<TElement> =
    AsList(this).tryGet(index)

fun <TElement> Array<in TElement>.trySet(index: Int, element: TElement): Result<Unit> =
    AsList(this).trySet(index, element).map{ Unit }

fun <TElement> Array<out TElement>.wrapGet(index: Int): TElement =
    AsList(this).wrapGet(index)

fun <TElement> Array<in TElement>.wrapSet(index: Int, element: TElement) {
    AsList(this).wrapSet(index, element)
}

fun <TElement> Array<out TElement>.index(fromIndex: Int, element: @UnsafeVariance TElement): Int =
    this.index(fromIndex) { it == element }

fun <TElement> Array<out TElement>.index(fromIndex: Int, predicate: (TElement) -> Boolean): Int =
    AsList(this).index(fromIndex, predicate)

fun <TElement> Array<out TElement>.lastIndex(fromIndex: Int, element: @UnsafeVariance TElement): Int =
    this.lastIndex(fromIndex) { it == element }

fun <TElement> Array<out TElement>.lastIndex(fromIndex: Int, predicate: (TElement) -> Boolean): Int =
    AsList(this).lastIndex(fromIndex, predicate)

fun <TElement> Array<out TElement>.indices(element: @UnsafeVariance TElement): Sequence<Int> =
    this.indices(0, element)

fun <TElement> Array<out TElement>.indices(predicate: (TElement) -> Boolean): Sequence<Int> =
    this.indices(0, predicate)

fun <TElement> Array<out TElement>.indices(fromIndex: Int, element: @UnsafeVariance TElement): Sequence<Int> =
    this.indices(fromIndex) { it == element }

fun <TElement> Array<out TElement>.indices(fromIndex: Int, predicate: (TElement) -> Boolean): Sequence<Int> =
    AsList(this).indices(fromIndex, predicate)

fun <TElement> Array<out TElement>.isPermutationOf(other: List<TElement>): Boolean =
    AsList(this).isPermutationOf(other)

/*fun <TElement> Array<TElement>.nextPermutation(): Boolean {

}

fun <TElement> Array<TElement>.prevPermutation(): Boolean {

}*/

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
    val leftList = AsList(leftArray)
    val rightList = AsList(rightArray)

    return compare(leftList, rightList, comp)
}
