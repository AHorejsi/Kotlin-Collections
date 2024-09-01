package collectionsTest

import collections.*
import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.test.*

class VectorStackTest {
    @Test
    fun testConstructor() {
        assertDoesNotThrow{ VectorStack<Int>() }
        assertDoesNotThrow{ VectorStack<Int>(0) }
        assertFailsWith<IllegalArgumentException>{ VectorStack<Int>(-1) }
    }
}

class LinkedStackTest {
    @Test
    fun testConstructor() {
        assertDoesNotThrow{ LinkedStack<Int>() }
    }
}

@Suppress("SameParameterValue")
class StackTest {
    @Test
    fun testSize() {
        val vec = VectorStack<Int>()
        val link = LinkedStack<Int>()

        this.testSizeOn(vec)
        this.testSizeOn(link)
    }

    private fun testSizeOn(stack: Stack<Int>) {
        val sizeBeforePush = stack.size
        stack.push(0)
        assertEquals(sizeBeforePush, stack.size - 1)

        val sizeBeforePeek = stack.size
        stack.peek()
        assertEquals(sizeBeforePeek, stack.size)

        val sizeBeforePop = stack.size
        stack.pop()
        assertEquals(sizeBeforePop, stack.size + 1)
    }

    @Test
    fun testIsEmpty() {
        val vec = VectorStack<Int>()
        val link = LinkedStack<Int>()

        this.testIsEmptyOn(vec)
        this.testIsEmptyOn(link)
    }

    private fun testIsEmptyOn(stack: Stack<Int>) {
        assertTrue(stack.isEmpty())

        stack.push(0)
        assertFalse(stack.isEmpty())

        stack.peek()
        assertFalse(stack.isEmpty())

        stack.pop()
        assertTrue(stack.isEmpty())

        repeat(20) {
            stack.push(it)
            assertFalse(stack.isEmpty())
        }

        stack.clear()
        assertTrue(stack.isEmpty())
    }

    @Test
    fun testAccess() {
        val vec = VectorStack<Int>()
        val link = LinkedStack<Int>()

        this.testAccessOn(vec)
        this.testAccessOn(link)
    }

    private fun testAccessOn(stack: Stack<Int>) {
        assertFailsWith<NoSuchElementException>{ stack.peek() }
        assertFailsWith<NoSuchElementException>{ stack.pop() }

        val size = 100

        repeat(size) {
            assertDoesNotThrow{ stack.push(it) }

            assertEquals(it, stack.peek())
        }

        while (!stack.isEmpty()) {
            val peeked = assertDoesNotThrow{ stack.peek() }
            val popped = assertDoesNotThrow{ stack.pop() }

            assertEquals(peeked, popped)
        }

        assertFailsWith<NoSuchElementException>{ stack.peek() }
        assertFailsWith<NoSuchElementException>{ stack.pop() }
    }

    @Test
    fun testAccessOrNull() {
        val vec = VectorStack<Int>()
        val link = LinkedStack<Int>()

        this.testAccessOrNullOn(vec)
        this.testAccessOrNullOn(link)
    }

    private fun testAccessOrNullOn(stack: Stack<Int>) {
        this.testAccessOrNullOnEmpty(stack)

        repeat(80) {
            stack.push(it)
        }

        while (!stack.isEmpty()) {
            val peeked = assertDoesNotThrow{ stack.peekOrNull() }
            val popped = assertDoesNotThrow{ stack.popOrNull() }

            assertNotNull(peeked)
            assertNotNull(popped)

            assertEquals(peeked, popped)
        }

        this.testAccessOrNullOnEmpty(stack)
    }

    private fun testAccessOrNullOnEmpty(stack: Stack<Int>) {
        val peeked = assertDoesNotThrow{ stack.peekOrNull() }
        val popped = assertDoesNotThrow{ stack.popOrNull() }

        assertNull(peeked)
        assertNull(popped)
    }

    @Test
    fun testSafeAccess() {
        val vec = VectorStack<Int>()
        val link = LinkedStack<Int>()

        this.testSafeAccessOn(vec)
        this.testSafeAccessOn(link)
    }

    private fun testSafeAccessOn(stack: Stack<Int>) {
        this.testSafeAccessOnEmpty(stack)

        repeat(80) {
            stack.push(it)
        }

        while (!stack.isEmpty()) {
            val peeked = assertDoesNotThrow{ stack.safePeek() }
            val popped = assertDoesNotThrow{ stack.safePop() }

            assertTrue(peeked.isSome())
            assertTrue(popped.isSome())

            assertEquals(peeked, popped)
        }

        this.testSafeAccessOnEmpty(stack)
    }

    private fun testSafeAccessOnEmpty(stack: Stack<Int>) {
        val peeked = assertDoesNotThrow{ stack.safePeek() }
        val popped = assertDoesNotThrow{ stack.safePop() }

        assertTrue(peeked.isNone())
        assertTrue(popped.isNone())
    }

    @Test
    fun testTryAccess() {
        val vec = VectorStack<Int?>()
        val link = LinkedStack<Int?>()

        this.testTryAccessOn(vec)
        this.testTryAccessOn(link)
    }

    private fun testTryAccessOn(stack: Stack<Int?>) {
        this.testTryAccessOnEmpty(stack)

        repeat(10) {
            stack.push(null)
        }

        while (!stack.isEmpty()) {
            val peeked = assertDoesNotThrow{ stack.tryPeek() }
            val popped = assertDoesNotThrow{ stack.tryPop() }

            assertTrue(peeked.isSuccess)
            assertTrue(popped.isSuccess)
        }

        this.testTryAccessOnEmpty(stack)
    }

    private fun testTryAccessOnEmpty(stack: Stack<Int?>) {
        val peeked = assertDoesNotThrow{ stack.tryPeek() }
        val popped = assertDoesNotThrow{ stack.tryPop() }

        assertFailsWith<NoSuchElementException>{ peeked.getOrThrow() }
        assertFailsWith<NoSuchElementException>{ popped.getOrThrow() }
    }
}
