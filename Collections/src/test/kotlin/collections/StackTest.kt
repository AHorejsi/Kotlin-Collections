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
        val initialSize = assertDoesNotThrow{ stack.size }
        assertEquals(0, initialSize)

        val total = 50

        for (count in 1 .. total) {
            val oldSize = assertDoesNotThrow{ stack.size }
            stack.push(0)
            val newSize = assertDoesNotThrow{ stack.size }

            assertEquals(oldSize + 1, newSize)
        }

        val finalSize = assertDoesNotThrow{ stack.size }
        assertEquals(total, finalSize)
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
