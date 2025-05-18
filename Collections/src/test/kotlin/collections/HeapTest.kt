package collections

import asserts.assertLessEqual
import objects.TestObject
import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.test.*

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
        val elements = arrayOf(-1, 11, 5, 39, 9, 4, 1, 20, 100, -90, 3, 444, 3256, 9975, -8745)

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
        val binary = BinaryHeap<Int>()

        this.testIsEmptyHelper(binary)
    }

    private fun testIsEmptyHelper(heap: Heap<Int>) {
        val initial = assertDoesNotThrow{ heap.isEmpty() }
        assertTrue(initial)

        heap.push(1)
        val firstAdd = assertDoesNotThrow{ heap.isEmpty() }
        assertTrue(!firstAdd)

        heap.push(0)
        val secondAdd = assertDoesNotThrow{ heap.isEmpty() }
        assertTrue(!secondAdd)

        heap.pop()
        val firstRemove = assertDoesNotThrow{ heap.isEmpty() }
        assertTrue(!firstRemove)

        heap.pop()
        val secondRemove = assertDoesNotThrow{ heap.isEmpty() }
        assertTrue(secondRemove)
    }

    @Test
    fun testPushAndPopAndPeek() {
        val binary = BinaryHeap<Int>()

        this.testPushAndPopAndPeekHelper(binary)
    }

    private fun testPushAndPopAndPeekHelper(heap: Heap<Int>) {
        assertFailsWith<NoSuchElementException>{ heap.peek() }
        assertFailsWith<NoSuchElementException>{ heap.pop() }

        val elements = arrayOf(8, 6, 3, 7, 2, 5, 3, 7, 0, 7, 5, 4, 2, 1, 3, 8, 3, 9, 0, 0, 6, 4, 3, 4, 2, 1)

        for (item in elements) {
            assertDoesNotThrow{ heap.push(item) }
        }

        var item = assertDoesNotThrow{ heap.pop() }

        while (!heap.isEmpty()) {
            val peekedItem = assertDoesNotThrow{ heap.peek() }
            val nextItem = assertDoesNotThrow{ heap.pop() }

            assertEquals(peekedItem, nextItem)
            assertLessEqual(item, nextItem)

            item = nextItem
        }

        assertFailsWith<NoSuchElementException>{ heap.peek() }
        assertFailsWith<NoSuchElementException>{ heap.pop() }
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
        val binary = BinaryHeap<TestObject>()

        this.testClassCastExceptionHelper(binary)
    }

    private fun testClassCastExceptionHelper(heap: Heap<TestObject>) {
        val obj = TestObject(0)

        assertFailsWith<ClassCastException>{ heap.push(obj) }
    }
}
