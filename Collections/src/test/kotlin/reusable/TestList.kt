package reusable

import collections.isRandomAccess
import collections.withIndex
import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.test.assertEquals
import kotlin.test.assertTrue

private fun ensureEmpty(list: List<*>) {
    if (list.isEmpty()) {
        throw InternalError("List must not be empty")
    }
}

private fun ensureNoDuplicates(list: List<*>) {
    val set = HashSet<Any?>(list.size)

    for (item in list) {
        if (!set.add(item)) {
            throw InternalError("Duplicate Item: $item")
        }
    }
}

fun testIsRandomAccess(list: List<Int>) {
    val success = assertDoesNotThrow{ list.isRandomAccess }

    assertTrue(success)
}

fun testIsNotRandomAccess(list: List<Int>) {
    val success = assertDoesNotThrow{ list.isRandomAccess }

    assertTrue(!success)
}

fun testWithIndex(list: List<*>) {
    ensureEmpty(list)
    ensureNoDuplicates(list)

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
