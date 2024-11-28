package reusable

import asserts.*
import collections.*
import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.test.*

fun testIsRandomAccess(list: List<Int>, expected: Boolean) {
    val success = assertDoesNotThrow{ list.isRandomAccess }

    assertEquals(expected, success)
}

fun testWithIndex(list: List<*>) {
    for (index in list.indices) {
        testIterationWithIndex(list, index)
    }
}

private fun testIterationWithIndex(list: List<*>, startIndex: Int) {
    val indexed = assertDoesNotThrow{ list.withIndex(startIndex) }

    for ((index, item) in indexed) {
        val elem = assertDoesNotThrow{ list[index - startIndex] }

        assertEquals(item, elem)
    }
}

fun testGetDuringIteration(list: List<Int>) {
    val iter = list.iterator()

    for (index in list.indices) {
        val indexedItem = assertDoesNotThrow{ list[index] }
        val iteratedItem = iter.next()

        assertEquals(iteratedItem, indexedItem)
    }
}

fun testGetAt(list: List<Int>, index: Int, value: Int) {
    val item = assertDoesNotThrow{ list[index] }

    assertEquals(value, item)
}

fun testWrapGetAtEnds(list: List<Int>) {
    val first = list.first()
    val last = list.last()

    val wrappedFirst = assertDoesNotThrow{ list.wrapGet(list.size) }
    val wrappedLast = assertDoesNotThrow{ list.wrapGet(-1) }

    assertEquals(wrappedFirst, first)
    assertEquals(wrappedLast, last)
}

fun testWrapGetRelative(list: List<Int>) {
    val size = list.size

    for (index in 0 until size) {
        val before = assertDoesNotThrow{ list.wrapGet(index - size) }
        val inside = assertDoesNotThrow{ list.wrapGet(index) }
        val after = assertDoesNotThrow{ list.wrapGet(index + size) }

        assertEquals(before, inside)
        assertEquals(inside, after)
    }
}

fun testWrapGetOnEmpty(list: List<Int>) {
    assertFailsWith<IllegalStateException>{ list.wrapGet(0) }
}

fun testTryGetInBounds(list: List<Int>) {
    for (index in list.indices) {
        val result = assertDoesNotThrow{ list.tryGet(index) }

        val item = list[index]
        val elem = assertDoesNotThrow{ result.getOrThrow() }

        assertEquals(item, elem)
    }
}

fun testTryGetOutOfBounds(list: List<Int>) {
    val early = assertDoesNotThrow{ list.tryGet(-1) }
    val late = assertDoesNotThrow{ list.tryGet(list.size) }

    assertFailsWith<IndexOutOfBoundsException>{ early.getOrThrow() }
    assertFailsWith<IndexOutOfBoundsException>{ late.getOrThrow() }
}

fun testIndexOf(list1: List<Int>, list2: List<Int>) {
    for ((index, element) in list1.withIndex()) {
        val adjusted = IndexedValue(index + 1, element)

        testIndexByElementWhenSucceed(list2, element, index)
        testIndexByElementWhenSucceed(list2, adjusted, index + list1.size)
    }

    testIndexByElementWhenFail(list1, 11)
    testIndexByElementWhenFail(list1, 49)

    testIndexByElementWhenFail(list1, IndexedValue(2, -9))
    testIndexByElementWhenFail(list1, IndexedValue(11, -10))
}

fun testIndexByElementWhenSucceed(list: List<Int>, element: Int, expectedIndex: Int) {
    val foundIndex = assertDoesNotThrow{ list.indexOf(element) }

    assertEquals(expectedIndex, foundIndex)
    assertEquals(element, list[foundIndex])
}

fun testIndexByElementWhenSucceed(list: List<Int>, element: IndexedValue<Int>, expectedIndex: Int) {
    val foundIndex = assertDoesNotThrow{ list.index(element.index, element.value) }

    assertEquals(expectedIndex, foundIndex)
    assertEquals(element.value, list[foundIndex])
}

fun testIndexByElementWhenFail(list: List<Int>, element: Int) {
    val foundIndex = assertDoesNotThrow{ list.indexOf(element) }

    assertEquals(-1, foundIndex)
}

fun testIndexByElementWhenFail(list: List<Int>, element: IndexedValue<Int>) {
    val foundIndex = assertDoesNotThrow{ list.index(element.index, element.value) }

    assertEquals(-1, foundIndex)
}

fun testIndexWithPredicate(list: List<Int>, startIndex: Int, expectedIndex: Int, predicate: (Int) -> Boolean) {
    val foundIndex = assertDoesNotThrow{ list.index(startIndex, predicate) }

    assertEquals(expectedIndex, foundIndex)
}

fun testLastIndexOf(list1: List<Int>, list2: List<Int>) {
    for (index in list1.indices.reversed()) {
        val element = list1[index]

        val foundIndex1 = assertDoesNotThrow{ list1.lastIndexOf(element) }
        val foundIndex2 = assertDoesNotThrow{ list2.lastIndexOf(element) }

        assertEquals(list1[foundIndex1], list2[foundIndex2])
        assertEquals(element, list1[foundIndex1])

        assertNotEquals(foundIndex1, foundIndex2)
    }
}

fun testLastIndexWithPredicate(list: MutableList<Int>, startIndex: Int, expectedIndex: Int, predicate: (Int) -> Boolean) {
    val foundIndex = assertDoesNotThrow{ list.lastIndex(startIndex, predicate) }

    assertEquals(expectedIndex, foundIndex)
}

fun testSuccessfulSeparationPoint(list: List<Int>, predicate: (Int) -> Boolean, expectedPoint: Int) {
    val separationPoint = assertDoesNotThrow{ list.separationPoint(predicate) }

    assertNotNull(separationPoint)
    assertEquals(expectedPoint, separationPoint)
}

fun testFailedSeparationPoint(list: List<Int>, predicate: (Int) -> Boolean) {
    val separationPoint = assertDoesNotThrow{ list.separationPoint(predicate) }

    assertNull(separationPoint)
}

fun testLessThanComparison(list: List<Int>, other: List<Int>) {
    assertLess(list, other, ::compare)
    assertNotEquals(list, other)
}

fun testEqualComparison(list: List<Int>, other: List<Int>) {
    val comparison = assertDoesNotThrow{ compare(list, other) }

    assertEquals(0, comparison)
    assertEquals(list, other)
}

fun testGreaterThanComparison(list: List<Int>, other: List<Int>) {
    assertGreater(list, other, ::compare)
    assertNotEquals(list, other)
}

fun testIsPermutationOf(list: List<Int>, other: List<Int>, expected: Boolean) {
    val result1 = assertDoesNotThrow{ list.isPermutationOf(other) }
    val result2 = assertDoesNotThrow{ other.isPermutationOf(list) }

    assertEquals(expected, result1)
    assertEquals(expected, result2)
}

fun testIsSorted(list: List<Int>, comp: Comparator<Int>, expected: Boolean) {
    val result = assertDoesNotThrow{ list.isSorted(comp) }

    assertEquals(expected, result)
}

fun testIsSortedUntil(list: List<Int>, comp: Comparator<Int>, expectedIndex: Int) {
    val resultIndex = assertDoesNotThrow{ list.isSortedUntil(comp) }

    assertEquals(expectedIndex, resultIndex)
}
