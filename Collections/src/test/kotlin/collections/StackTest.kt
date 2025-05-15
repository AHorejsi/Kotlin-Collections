package collections

import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

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
        assertEquals(0, stack.size)

        val total = 50

        for (count in 1 .. total) {
            val oldSize = stack.size
            stack.push(0)
            val newSize = stack.size

            assertEquals(oldSize + 1, newSize)
        }

        assertEquals(total, stack.size)
    }

    @Test
    fun testIsEmpty() {
        val vector = VectorStack<Int>()
        val linked = LinkedStack<Int>()

        this.testIsEmptyHelper(vector)
        this.testIsEmptyHelper(linked)
    }

    private fun testIsEmptyHelper(stack: Stack<Int>) {
        assertTrue(stack.isEmpty())

        stack.push(0)
        assertTrue(!stack.isEmpty())

        stack.push(0)
        assertTrue(!stack.isEmpty())

        stack.pop()
        assertTrue(!stack.isEmpty())

        stack.pop()
        assertTrue(stack.isEmpty())
    }

    @Test
    fun testPush() {

    }

    @Test
    fun testPop() {

    }

    @Test
    fun testPopOrNull() {

    }

    @Test
    fun testTryPop() {

    }

    @Test
    fun testPeek() {

    }

    @Test
    fun testPeekOrNull() {

    }

    @Test
    fun testTryPeek() {

    }

    @Test
    fun testClear() {

    }
}
