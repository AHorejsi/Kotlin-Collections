package reusable

import collections.*
import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

fun testIsRandomAccess(list: List<Int>) {
    val success = assertDoesNotThrow{ list.isRandomAccess }

    assertTrue(success)
}

fun testIsNotRandomAccess(list: List<Int>) {
    val success = assertDoesNotThrow{ list.isRandomAccess }

    assertTrue(!success)
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

fun testLessThanComparison(list: List<Int>, other: List<Int>) {
    val comparison = assertDoesNotThrow{ compare(list, other) }

    assertTrue(comparison < 0)
    assertNotEquals(list, other)
}

fun testEqualComparison(list: List<Int>, other: List<Int>) {
    val comparison = assertDoesNotThrow{ compare(list, other) }

    assertEquals(0, comparison)
    assertEquals(list, other)
}

fun testGreaterThanComparison(list: List<Int>, other: List<Int>) {
    val comparison = assertDoesNotThrow{ compare(list, other) }

    assertTrue(comparison > 0)
    assertNotEquals(list, other)
}
