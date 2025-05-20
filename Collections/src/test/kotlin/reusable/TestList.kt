package reusable

import asserts.*
import collections.*
import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.test.*

fun testIsRandomAccess(list: List<Int>, expected: Boolean) {
    val success = assertDoesNotThrow{ list.isRandomAccess }

    assertEquals(expected, success)
}

fun testWithIndex(list: List<Int>) {
    for (index in list.indices) {
        testIterationWithIndex(list, index)
    }
}

private fun testIterationWithIndex(list: List<Int>, startIndex: Int) {
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

fun testListIteratorConstruction(list: List<Int>) {
    assertDoesNotThrow{ list.listIterator() }
    assertDoesNotThrow{ list.listIterator(list.size) }

    assertFailsWith<IndexOutOfBoundsException>{ list.listIterator(-1) }
    assertFailsWith<IndexOutOfBoundsException>{ list.listIterator(list.size + 1) }
}

fun testPreviousIndexOnListIterator(list: List<Int>) {
    for (index in 1 .. list.lastIndex) {
        val iter = list.listIterator(index)
        val index1 = assertDoesNotThrow{ iter.previousIndex() }
        assertEquals(index - 1, index1)

        iter.previous()
        val index2 = assertDoesNotThrow{ iter.previousIndex() }
        assertEquals(index - 2, index2)

        iter.next()
        val index3 = assertDoesNotThrow{ iter.previousIndex() }
        assertEquals(index - 1, index3)

        iter.next()
        val index4 = assertDoesNotThrow{ iter.previousIndex() }
        assertEquals(index, index4)
    }
}

fun testNextIndexOnListIterator(list: List<Int>) {
    for (index in 1 .. list.lastIndex) {
        val iter = list.listIterator(index)
        val index1 = assertDoesNotThrow{ iter.nextIndex() }
        assertEquals(index, index1)

        iter.next()
        val index2 = assertDoesNotThrow{ iter.nextIndex() }
        assertEquals(index + 1, index2)

        iter.previous()
        val index3 = assertDoesNotThrow{ iter.nextIndex() }
        assertEquals(index, index3)

        iter.previous()
        val index4 = assertDoesNotThrow{ iter.nextIndex() }
        assertEquals(index - 1, index4)
    }
}

fun testHasPreviousOnListIterator(list: List<Int>) {
    val iter = list.listIterator(0)
    val beginResult = assertDoesNotThrow{ iter.hasPrevious() }

    assertTrue(!beginResult)

    while (iter.hasNext()) {
        iter.next()

        val result = assertDoesNotThrow{ iter.hasPrevious() }

        assertTrue(result)
    }
}

fun testHasNextOnListIterator(list: List<Int>) {
    val iter = list.listIterator(list.size)
    val endResult = assertDoesNotThrow{ iter.hasNext() }

    assertTrue(!endResult)

    while (iter.hasPrevious()) {
        iter.previous()

        val result = assertDoesNotThrow{ iter.hasNext() }

        assertTrue(result)
    }
}

fun testPreviousOnListIterator(list: List<Int>) {
    for (index in 0 .. list.size) {
        val iter = list.listIterator(index)

        testPreviousWithIndex(iter, index, list)
    }
}

private fun testPreviousWithIndex(iter: ListIterator<Int>, startIndex: Int, list: List<Int>) {
    var index = startIndex - 1

    while (iter.hasPrevious()) {
        val elem = assertDoesNotThrow{ iter.previous() }

        assertEquals(list[index], elem)

        --index
    }

    assertFailsWith<NoSuchElementException>{ iter.previous() }
}

fun testNextOnListIterator(list: List<Int>) {
    for (index in 0 .. list.size) {
        val iter = list.listIterator(index)

        testNextWithIndex(iter, index, list)
    }
}

private fun testNextWithIndex(iter: ListIterator<Int>, startIndex: Int, list: List<Int>) {
    var index = startIndex

    while (iter.hasNext()) {
        val item = assertDoesNotThrow{ iter.next() }

        assertEquals(list[index], item)

        ++index
    }

    assertFailsWith<NoSuchElementException>{ iter.next() }
}

fun testSetOnListIterator(list: MutableList<Int>) {
    testSetWithNext(list)
    testSetWithPrevious(list)
    testSetAfterRemove(list)
    testSetAfterAdd(list)
}

private fun testSetWithNext(list: MutableList<Int>) {
    val index = list.size / 2
    val value = -1

    val iter = list.listIterator(index)

    assertFailsWith<IllegalStateException>{ iter.set(value) }

    iter.next()
    assertDoesNotThrow{ iter.set(value) }

    assertEquals(value, iter.previous())
    assertEquals(value, list[index])
}

private fun testSetWithPrevious(list: MutableList<Int>) {
    val index = list.size / 3
    val value = -1

    val iter = list.listIterator(index)

    assertFailsWith<IllegalStateException>{ iter.set(value) }

    iter.previous()
    assertDoesNotThrow{ iter.set(value) }

    assertEquals(value, iter.next())
    assertEquals(value, list[index - 1])
}

private fun testSetAfterRemove(list: MutableList<Int>) {
    val index = 3 * list.size / 4
    val value = -1

    val iter = list.listIterator(index)

    iter.next()
    iter.remove()

    assertFailsWith<IllegalStateException>{ iter.set(value) }
}

private fun testSetAfterAdd(list: MutableList<Int>) {
    val index = 4 * list.size / 5

    val value1 = -1
    val value2 = -2

    val iter = list.listIterator(index)

    iter.next()

    iter.add(value2)
    iter.add(value2)

    assertFailsWith<IllegalStateException>{ iter.set(value1) }
}

fun testRemoveOnListIterator(list: MutableList<Int>) {
    testRemoveWithNext(list)
    testRemoveWithPrevious(list)
    testIllegalStatesWithRemove(list)
}

private fun testRemoveWithNext(list: MutableList<Int>) {
    val index = list.size / 3

    val iter = list.listIterator(index)

    val elem = iter.next()

    val oldSize = list.size
    assertDoesNotThrow{ iter.remove() }
    val newSize = list.size

    assertEquals(oldSize - 1, newSize)
    assertNotContains(list, elem)
}

private fun testRemoveWithPrevious(list: MutableList<Int>) {
    val index = 2 * list.size / 3

    val iter = list.listIterator(index)

    val elem = iter.previous()

    val oldSize = list.size
    assertDoesNotThrow{ iter.remove() }
    val newSize = list.size

    assertEquals(oldSize - 1, newSize)
    assertNotContains(list, elem)
}

private fun testIllegalStatesWithRemove(list: MutableList<Int>) {
    val index = list.size / 2

    val iter = list.listIterator(index)

    assertFailsWith<IllegalStateException>{ iter.remove() }

    iter.next()

    assertDoesNotThrow{ iter.remove() }
    assertFailsWith<IllegalStateException>{ iter.remove() }
}

fun testAddOnListIterator(list: MutableList<Int>) {
    testAddHelper(list, -1, 0)
    testAddHelper(list, -2, list.size / 2)
    testAddHelper(list, -3, list.size)
}

private fun testAddHelper(list: MutableList<Int>, value: Int, index: Int) {
    val iter = list.listIterator(index)

    val oldSize = list.size
    assertDoesNotThrow{ iter.add(value) }
    val newSize = list.size

    println(index)
    println(value)
    println(list)

    assertEquals(value, list[index])
    assertEquals(oldSize + 1, newSize)

    if (iter.hasPrevious()) {
        assertEquals(value, iter.previous())
    }
}

fun testConcurrentModification(list: MutableList<Int>) {
    val iter = list.listIterator()

    list.add(0)
    list.removeAt(0)

    assertFailsWith<ConcurrentModificationException>{ iter.previousIndex() }
    assertFailsWith<ConcurrentModificationException>{ iter.nextIndex() }
    assertFailsWith<ConcurrentModificationException>{ iter.hasPrevious() }
    assertFailsWith<ConcurrentModificationException>{ iter.hasNext() }
    assertFailsWith<ConcurrentModificationException>{ iter.previous() }
    assertFailsWith<ConcurrentModificationException>{ iter.next() }
    assertFailsWith<ConcurrentModificationException>{ iter.set(0) }
    assertFailsWith<ConcurrentModificationException>{ iter.remove() }
    assertFailsWith<ConcurrentModificationException>{ iter.add(0) }
}
