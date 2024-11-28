package reusable

import asserts.assertNotContains
import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

fun testContainsAfterAdding(collection: MutableCollection<Int>, other: Collection<Int>) {
    collection.addAll(other)

    for (item in other) {
        val added = assertDoesNotThrow{ item in collection }

        assertTrue(added)
    }
}

fun testContainsAfterRemovingByElement(collection: MutableCollection<Int>, element: Int) {
    val amount = collection.count{ it == element }

    repeat(amount - 1) {
        testContainsAfterRemoveWhileStillContained(collection, element)
    }

    testRemoveWithOneLeftContained(collection, element)
}

private fun testContainsAfterRemoveWhileStillContained(collection: MutableCollection<Int>, element: Int) {
    val removed = collection.remove(element)

    assertTrue(removed)

    val contained = assertDoesNotThrow{ element in collection }

    assertTrue(contained)
}

private fun testRemoveWithOneLeftContained(collection: MutableCollection<Int>, element: Int) {
    val removed = collection.remove(element)

    assertTrue(removed)

    val contained = assertDoesNotThrow{ element in collection }

    assertTrue(!contained)
}

fun testHasNextOnIterator(collection: MutableCollection<Int>, count: Int) {
    val iter = collection.iterator()

    repeat(count) {
        val more = assertDoesNotThrow{ iter.hasNext() }

        assertTrue(more)

        assertDoesNotThrow{ iter.next() }
    }

    val more = assertDoesNotThrow{ iter.hasNext() }

    assertTrue(!more)

    collection.add(0)
    assertFailsWith<ConcurrentModificationException>{ iter.hasNext() }
}

fun testRemoveOnIterator(collection: MutableCollection<Int>) {
    val iter = collection.iterator()
    val initialSize = collection.size

    assertFailsWith<IllegalStateException>{ iter.remove() }

    var state = false
    var amountRemoved = 0

    while (iter.hasNext()) {
        val elem = iter.next()

        if (state) {
            assertDoesNotThrow{ iter.remove() }
            assertNotContains(collection, elem)

            ++amountRemoved
        }

        state = !state
    }

    assertEquals(initialSize - amountRemoved, collection.size)

    assertDoesNotThrow{ iter.remove() }
    assertFailsWith<IllegalStateException>{ iter.remove() }

    collection.clear()
    assertFailsWith<ConcurrentModificationException>{ iter.remove() }
}
