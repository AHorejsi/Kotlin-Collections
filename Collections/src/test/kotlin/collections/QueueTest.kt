package collections

import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.test.*

class QueueTest {
    @Test
    fun testPrimaryConstructor() {
        assertDoesNotThrow{ VectorQueue<Int>(0) }
        assertDoesNotThrow{ LinkedQueue<Int>() }

        assertFailsWith<IllegalArgumentException> { VectorQueue<Int>(-1) }
    }

    @Test
    fun testSize() {
        val vector = VectorQueue<Int>()
        val linked = LinkedQueue<Int>()

        this.testSizeHelper(vector)
        this.testSizeHelper(linked)
    }

    private fun testSizeHelper(queue: Queue<Int>) {
        val initialSize = assertDoesNotThrow{ queue.size }
        assertEquals(0, initialSize)

        val total = 50

        repeat(total) {
            val oldSize = assertDoesNotThrow{ queue.size }
            queue.enqueue(0)
            val newSize = assertDoesNotThrow{ queue.size }

            assertEquals(oldSize + 1, newSize)
        }

        val finalSize1 = assertDoesNotThrow{ queue.size }
        assertEquals(total, finalSize1)

        repeat(total) {
            val oldSize = assertDoesNotThrow{ queue.size }
            queue.dequeue()
            val newSize = assertDoesNotThrow{ queue.size }

            assertEquals(oldSize - 1, newSize)
        }

        val finalSize2 = assertDoesNotThrow{ queue.size }
        assertEquals(0, finalSize2)
    }

    @Test
    fun testIsEmpty() {
        val vector = VectorQueue<Int>()
        val linked = LinkedQueue<Int>()

        this.testIsEmptyHelper(vector)
        this.testIsEmptyHelper(linked)
    }

    private fun testIsEmptyHelper(queue: Queue<Int>) {
        val initial = assertDoesNotThrow{ queue.isEmpty()}
        assertTrue(initial)

        queue.enqueue(0)
        val firstAdd = assertDoesNotThrow{ queue.isEmpty() }
        assertTrue(!firstAdd)

        queue.enqueue(0)
        val secondAdd = assertDoesNotThrow{ queue.isEmpty() }
        assertTrue(!secondAdd)

        queue.dequeue()
        val firstRemove = assertDoesNotThrow{ queue.isEmpty() }
        assertTrue(!firstRemove)

        queue.dequeue()
        val secondRemove = assertDoesNotThrow{ queue.isEmpty() }
        assertTrue(secondRemove)
    }

    @Test
    fun testEnqueueAndDequeueAndFront() {
        val vector = VectorQueue<Int>()
        val linked = LinkedQueue<Int>()

        this.testEnqueueAndDequeueAndFrontHelper(vector)
        this.testEnqueueAndDequeueAndFrontHelper(linked)
    }

    private fun testEnqueueAndDequeueAndFrontHelper(queue: Queue<Int>) {
        assertFailsWith<NoSuchElementException>{ queue.front() }
        assertFailsWith<NoSuchElementException>{ queue.dequeue() }

        val total = 50
        val range = 1 .. total

        for (value in range) {
            assertDoesNotThrow{ queue.enqueue(value) }
        }

        for (value in range) {
            val frontItem = assertDoesNotThrow{ queue.front() }
            val dequeuedItem = assertDoesNotThrow{ queue.dequeue() }

            assertEquals(frontItem, dequeuedItem)
            assertEquals(dequeuedItem, value)
        }

        assertFailsWith<NoSuchElementException>{ queue.front() }
        assertFailsWith<NoSuchElementException>{ queue.dequeue() }
    }

    @Test
    fun testEnqueue_And_DequeueOrNull_And_FrontOrNull() {
        val vector = VectorQueue<Int>()
        val linked = LinkedQueue<Int>()

        this.testEnqueue_And_DequeueOrNull_And_FrontOrNull_Helper(vector)
        this.testEnqueue_And_DequeueOrNull_And_FrontOrNull_Helper(linked)
    }

    private fun testEnqueue_And_DequeueOrNull_And_FrontOrNull_Helper(queue: Queue<Int>) {
        this.testOrNull(queue)

        val total = 75

        repeat(total) {
            assertDoesNotThrow{ queue.enqueue(0) }
        }

        repeat(total) {
            val frontItem = assertDoesNotThrow{ queue.front() }
            val dequeuedItem = assertDoesNotThrow{ queue.dequeue() }

            assertNotNull(frontItem)
            assertNotNull(dequeuedItem)
        }

        this.testOrNull(queue)
    }

    private fun testOrNull(queue: Queue<Int>) {
        val frontItem = assertDoesNotThrow{ queue.frontOrNull() }
        val dequeuedItem = assertDoesNotThrow{ queue.dequeueOrNull() }

        assertNull(frontItem)
        assertNull(dequeuedItem)
    }

    @Test
    fun testEnqueue_And_TryDequeue_And_TryFront() {
        val vector = VectorQueue<Int?>()
        val linked = LinkedQueue<Int?>()

        this.testEnqueue_And_TryDequeue_And_TryFront_Helper(vector)
        this.testEnqueue_And_TryDequeue_And_TryFront_Helper(linked)
    }

    private fun testEnqueue_And_TryDequeue_And_TryFront_Helper(queue: Queue<Int?>) {
        this.testTry(queue)

        val total = 100

        repeat(total) {
            assertDoesNotThrow{ queue.enqueue(null) }
        }

        repeat(total) {
            val frontResult = assertDoesNotThrow{ queue.tryFront() }
            val dequeuedResult = assertDoesNotThrow{ queue.tryDequeue() }

            assertTrue(frontResult.isSuccess)
            assertTrue(dequeuedResult.isSuccess)
        }

        this.testTry(queue)
    }

    private fun testTry(queue: Queue<Int?>) {
        val frontResult = assertDoesNotThrow{ queue.tryFront() }
        val dequeuedResult = assertDoesNotThrow{ queue.tryDequeue() }

        assertFailsWith<NoSuchElementException>{ frontResult.getOrThrow() }
        assertFailsWith<NoSuchElementException>{ dequeuedResult.getOrThrow() }
    }

    @Test
    fun testClear() {
        val vector = VectorQueue<Int>()
        val linked = LinkedQueue<Int>()

        this.testClearHelper(vector)
        this.testClearHelper(linked)
    }

    private fun testClearHelper(queue: Queue<Int>) {
        val total = 200

        repeat(total) {
            queue.enqueue(it)
        }

        assertTrue(!queue.isEmpty())
        assertDoesNotThrow{ queue.clear() }
        assertTrue(queue.isEmpty())

        assertDoesNotThrow{ queue.clear() }
    }
}