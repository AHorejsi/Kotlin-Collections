package collections

import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.test.*

class StackTest {
    @Test
    fun testPrimaryConstructor() {
        assertDoesNotThrow{ VectorStack<Int>(0) }
        assertDoesNotThrow{ LinkedStack<Int>() }

        assertFailsWith<IllegalArgumentException>{ VectorStack<Int>(-1) }
    }

    @Test
    fun testSize() {
        val vector = VectorStack<Int>()
        val linked = LinkedStack<Int>()

        this.testSizeHelper(vector)
        this.testSizeHelper(linked)
    }

    private fun testSizeHelper(stack: Stack<Int>) {
        val initialSize = assertDoesNotThrow{ stack.size }
        assertEquals(0, initialSize)

        val total = 50

        repeat(total) {
            val oldSize = assertDoesNotThrow{ stack.size }
            stack.push(0)
            val newSize = assertDoesNotThrow{ stack.size }

            assertEquals(oldSize + 1, newSize)
        }

        val finalSize1 = assertDoesNotThrow{ stack.size }
        assertEquals(total, finalSize1)

        repeat(total) {
            val oldSize = assertDoesNotThrow{ stack.size }
            stack.pop()
            val newSize = assertDoesNotThrow{ stack.size }

            assertEquals(oldSize - 1, newSize)
        }

        val finalSize2 = assertDoesNotThrow{ stack.size }
        assertEquals(0, finalSize2)
    }

    @Test
    fun testIsEmpty() {
        val vector = VectorStack<Int>()
        val linked = LinkedStack<Int>()

        this.testIsEmptyHelper(vector)
        this.testIsEmptyHelper(linked)
    }

    private fun testIsEmptyHelper(stack: Stack<Int>) {
        val initial = assertDoesNotThrow{ stack.isEmpty()}
        assertTrue(initial)

        stack.push(0)
        val firstAdd = assertDoesNotThrow{ stack.isEmpty() }
        assertTrue(!firstAdd)

        stack.push(0)
        val secondAdd = assertDoesNotThrow{ stack.isEmpty() }
        assertTrue(!secondAdd)

        stack.pop()
        val firstRemove = assertDoesNotThrow{ stack.isEmpty() }
        assertTrue(!firstRemove)

        stack.pop()
        val secondRemove = assertDoesNotThrow{ stack.isEmpty() }
        assertTrue(secondRemove)
    }

    @Test
    fun testPushAndPopAndPeek() {
        val vector = VectorStack<Int>()
        val linked = LinkedStack<Int>()

        this.testPushAndPopAndPeekHelper(vector)
        this.testPushAndPopAndPeekHelper(linked)
    }

    private fun testPushAndPopAndPeekHelper(stack: Stack<Int>) {
        assertFailsWith<NoSuchElementException>{ stack.peek() }
        assertFailsWith<NoSuchElementException>{ stack.pop() }

        val total = 50

        repeat(total) {
            assertDoesNotThrow{ stack.push(it) }

            val item = assertDoesNotThrow{ stack.peek() }

            assertEquals(it, item)
        }

        while (!stack.isEmpty()) {
            val peekedItem = assertDoesNotThrow{ stack.peek() }
            val poppedItem = assertDoesNotThrow{ stack.pop() }

            assertEquals(peekedItem, poppedItem)
        }

        assertFailsWith<NoSuchElementException>{ stack.peek() }
        assertFailsWith<NoSuchElementException>{ stack.pop() }
    }

    @Test
    fun testPush_And_PopOrNull_And_PeekOrNull() {
        val vector = VectorStack<Int>()
        val linked = LinkedStack<Int>()

        this.testPush_And_PopOrNull_And_PeekOrNull_Helper(vector)
        this.testPush_And_PopOrNull_And_PeekOrNull_Helper(linked)
    }

    private fun testPush_And_PopOrNull_And_PeekOrNull_Helper(stack: Stack<Int>) {
        this.testOrNull(stack)

        val total = 75

        repeat(total) {
            stack.push(0)
        }

        repeat(total) {
            val peekedItem = assertDoesNotThrow{ stack.peekOrNull() }
            val poppedItem = assertDoesNotThrow{ stack.popOrNull() }

            assertNotNull(peekedItem)
            assertNotNull(poppedItem)
        }

        this.testOrNull(stack)
    }

    private fun testOrNull(stack: Stack<Int>) {
        val peekedItem = assertDoesNotThrow{ stack.peekOrNull() }
        val poppedItem = assertDoesNotThrow{ stack.popOrNull() }

        assertNull(peekedItem)
        assertNull(poppedItem)
    }

    @Test
    fun testPush_And_TryPop_And_TryPeek() {
        val vector = VectorStack<Int?>()
        val linked = LinkedStack<Int?>()

        this.testPush_And_TryPop_And_TryPeek_Helper(vector)
        this.testPush_And_TryPop_And_TryPeek_Helper(linked)
    }

    private fun testPush_And_TryPop_And_TryPeek_Helper(stack: Stack<Int?>) {
        this.testTry(stack)

        val total = 75

        repeat(total) {
            stack.push(0)
        }

        repeat(total) {
            val peekedItem = assertDoesNotThrow{ stack.tryPeek() }
            val poppedItem = assertDoesNotThrow{ stack.tryPop() }

            assertTrue(peekedItem.isSuccess)
            assertTrue(poppedItem.isSuccess)
        }

        this.testTry(stack)
    }

    private fun testTry(stack: Stack<Int?>) {
        val peekedResult = assertDoesNotThrow{ stack.tryPeek() }
        val poppedResult = assertDoesNotThrow{ stack.tryPop() }

        assertFailsWith<NoSuchElementException>{ peekedResult.getOrThrow() }
        assertFailsWith<NoSuchElementException>{ poppedResult.getOrThrow() }
    }

    @Test
    fun testClear() {
        val vector = VectorStack<Int>()
        val linked = LinkedStack<Int>()

        this.testClearHelper(vector)
        this.testClearHelper(linked)
    }

    private fun testClearHelper(stack: Stack<Int>) {
        val total = 100

        repeat(total) {
            stack.push(it)
        }

        assertTrue(!stack.isEmpty())
        assertDoesNotThrow{ stack.clear() }
        assertTrue(stack.isEmpty())

        assertDoesNotThrow{ stack.clear() }
    }
}
