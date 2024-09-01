package collectionsTest

import collections.quad
import collections.quint
import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.test.Test
import kotlin.test.assertEquals

class TuplesTest {
    @Test
    fun testQuad() {
        val first = Int.MIN_VALUE
        val second = Long.MIN_VALUE
        val third = Int.MAX_VALUE
        val fourth = Long.MAX_VALUE

        val quad = assertDoesNotThrow{ quad(first, second, third, fourth) }
        val (t1, t2, t3, t4) = quad

        assertEquals(first, t1)
        assertEquals(second, t2)
        assertEquals(third, t3)
        assertEquals(fourth, t4)

        assertEquals(t1, quad.first)
        assertEquals(t2, quad.second)
        assertEquals(t3, quad.third)
        assertEquals(t4, quad.fourth)
    }

    @Test
    fun testQuint() {
        val first = Int.MIN_VALUE
        val second = Long.MIN_VALUE
        val third = Int.MAX_VALUE
        val fourth = Long.MAX_VALUE
        val fifth = 0.toByte()

        val quint = assertDoesNotThrow{ quint(first, second, third, fourth, fifth) }
        val (t1, t2, t3, t4, t5) = quint

        assertEquals(first, t1)
        assertEquals(second, t2)
        assertEquals(third, t3)
        assertEquals(fourth, t4)
        assertEquals(fifth, t5)

        assertEquals(t1, quint.first)
        assertEquals(t2, quint.second)
        assertEquals(t3, quint.third)
        assertEquals(t4, quint.fourth)
        assertEquals(t5, quint.fifth)
    }
}