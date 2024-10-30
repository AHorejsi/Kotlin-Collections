package collections

fun <TElement> Set<TElement>.count(element: @UnsafeVariance TElement): Int =
    if (element in this) 1 else 0

fun <TElement> MutableSet<TElement>.removeAmount(amount: Int, element: @UnsafeVariance TElement): Int {
    checkIfNegativeAmount(amount)

    return if (amount > 0 && this.remove(element)) 1 else 0
}
