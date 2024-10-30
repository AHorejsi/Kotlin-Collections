package collections

import kotlin.reflect.KCallable
import kotlin.reflect.KClass

internal class ResultUtils private constructor() {
    init {
        noInstances(ResultUtils::class)
    }

    companion object {
        val FAILED_SEARCH = NoSuchElementException("No item found matching the given predicate")
    }
}

internal fun checkIfNegativeCapacity(capacity: Int) {
    if (capacity < 0) {
        throw IllegalArgumentException("Capacity cannot be negative. Specified capacity is $capacity")
    }
}

internal fun checkIfNegativeAmount(amount: Int) {
    if (amount < 0) {
        throw IllegalArgumentException("Amount cannot be negative. Specified amount is $amount")
    }
}

internal fun checkIfNegativeAmount(amount: Long) {
    if (amount < 0L) {
        throw IllegalArgumentException("Amount cannot be negative. Specified amount is $amount")
    }
}

internal fun checkIfIndexIsAccessible(index: Int, size: Int) {
    if (index < 0 || index >= size) {
        outOfBoundsWithSizeInclusive(index, size)
    }
}

internal fun checkIfIndexCanBeInsertedAt(index: Int, size: Int) {
    if (index < 0 || index > size) {
        outOfBoundsWithSizeExclusive(index, size)
    }
}

internal fun checkIfValidRange(fromIndex: Int, toIndex: Int) {
    if (fromIndex > toIndex) {
        throw IllegalArgumentException("fromIndex <= toIndex. FromIndex = $fromIndex, ToIndex = $toIndex")
    }
}

internal fun checkIfRangeInBounds(fromIndex: Int, toIndex: Int, size: Int) {
    if (0 == fromIndex && 0 == toIndex && 0 == size) {
        return
    }

    if (fromIndex < 0 || fromIndex >= size) {
        throw IndexOutOfBoundsException("0 <= fromIndex < size. FromIndex = $fromIndex, Size = $size")
    }

    if (toIndex < 0 || toIndex > size) {
        throw IndexOutOfBoundsException("0 <= toIndex <= size. ToIndex = $toIndex, Size = $size")
    }
}

internal fun checkIfSameSize(size1: Int, size2: Int) {
    if (size1 != size2) {
        throw IllegalArgumentException("Sizes must be equal. Size1 = $size1, Size2 = $size2")
    }
}

internal fun checkIfEmptyCollection(collect: Collection<*>) {
    if (collect.isEmpty()) {
        throw IllegalArgumentException("Collection must be non-empty")
    }
}

internal fun checkIfEmptyListForWrappedIndexing(list: List<*>) {
    if (list.isEmpty()) {
        throw IllegalStateException("List must be non-empty for wrapped indexing")
    }
}

internal fun checkIfPrev(iter: ListIterator<*>) {
    if (!iter.hasPrevious()) {
        throw NoSuchElementException("No prior elements. Iterator at beginning")
    }
}

internal fun checkIfNext(iter: Iterator<*>) {
    if (!iter.hasNext()) {
        throw NoSuchElementException("No later elements. Iterator at end")
    }
}

internal fun checkIfUnderlyingCollectionHasBeenModified(modCount1: Int, modCount2: Int) {
    if (modCount1 != modCount2) {
        throw ConcurrentModificationException("Underlying collection has been modified")
    }
}

internal fun checkForOverflowOnAddition(left: Int, right: Int): Int {
    val output = left + right
    val hasOverflow = left > 0 && right > 0 && output < 0

    return if (hasOverflow)
        throw ArithmeticException("Overflow from addition. Left Operand = $left, Right Operand = $right, Output = $output")
    else
        output
}

internal fun checkForOverflowOnAddition(left: Long, right: Long): Long {
    val output = left + right
    val hasOverflow = left > 0 && right > 0 && output < 0

    return if (hasOverflow)
        throw ArithmeticException("Overflow from addition. Left Operand = $left, Right Operand = $right, Output = $output")
    else
        output
}

internal fun checkForOverflowOnSubtraction(left: Int, right: Int): Int {
    val output = left - right
    val hasOverflow = ((left >= 0).xor(right >= 0)) && ((left >= 0).xor(output >= 0))

    return if (hasOverflow)
        throw ArithmeticException("Overflow from subtraction. Left Operand = $left, Right Operand = $right, Output = $output")
    else
        output
}

internal fun checkForOverflowOnSubtraction(left: Long, right: Long): Long {
    val output = left - right
    val hasOverflow = ((left >= 0) != (right >= 0)) && ((left >= 0) != (output >= 0))

    return if (hasOverflow)
        throw ArithmeticException("Overflow from subtraction. Left Operand = $left, Right Operand = $right, Output = $output")
    else
        output
}

internal fun noInstances(cls: KClass<*>): Nothing =
    throw InternalError("No instances of ${cls.simpleName} are allowed")

internal fun outOfBoundsWithoutSize(index: Int): Nothing =
    throw IndexOutOfBoundsException(index)

internal fun outOfBoundsWithSizeInclusive(index: Int, size: Int): Nothing =
    throw IndexOutOfBoundsException("0 <= index < size. Index = $index, Size = $size")

internal fun outOfBoundsWithSizeExclusive(index: Int, size: Int): Nothing =
    throw IndexOutOfBoundsException("0 <= index <= size. Index = $index, Size = $size")

internal fun noneToUse(): Nothing =
    throw IllegalStateException("No item at last cursor")

internal fun empty(cls: KClass<*>): Nothing =
    throw NoSuchElementException("Empty ${cls.simpleName}")

internal fun unsupported(cls: KClass<*>, func: KCallable<*>): Nothing =
    throw UnsupportedOperationException("No ${func.name} for ${cls.simpleName}")
