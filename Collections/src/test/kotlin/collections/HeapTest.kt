package collections

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.test.Test
import kotlin.test.assertFailsWith

class HeapTest {
    @Test
    fun testConstructor() {
        assertDoesNotThrow{ BinaryHeap<Int>(0) }
        assertFailsWith<IllegalArgumentException>{ BinaryHeap<Int>(-1) }
    }

    @Test
    fun testSize() {
        val binary = BinaryHeap<Int>()

        this.testSizeHelper(binary)
    }

    private fun testSizeHelper(heap: Heap<Int>) {
        val elements = arrayOf(-1, 11, 5, 3, 9, 4, 1, 20, 100, -90, 3, 444, 3256, 3975, -8745)

        for (item in elements) {
            val oldSize = assertDoesNotThrow{ heap.size }
            heap.push(item)
            val newSize = assertDoesNotThrow{ heap.size }

            assertEquals(oldSize + 1, newSize)
        }

        val finalSize1 = assertDoesNotThrow{ heap.size }
        assertEquals(elements.size, finalSize1)

        while (!heap.isEmpty()) {
            val oldSize = assertDoesNotThrow{ heap.size }
            heap.pop()
            val newSize = assertDoesNotThrow{ heap.size }

            assertEquals(oldSize - 1, newSize)
        }

        val finalSize2 = assertDoesNotThrow{ heap.size }
        assertEquals(0, finalSize2)
    }

    @Test
    fun testIsEmpty() {
        TODO()
    }

    @Test
    fun testPushAndPopAndPeek() {
        TODO()
    }

    @Test
    fun testPush_And_TryPop_And_TryPeek() {
        TODO()
    }

    @Test
    fun testPush_And_PopOrNull_And_PeekOrNull() {
        TODO()
    }

    @Test
    fun testClear() {
        TODO()
    }

    @Test
    fun testClassCastException() {
        TODO()
    }
}
