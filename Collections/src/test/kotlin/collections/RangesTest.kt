package collections

import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.test.*

@Suppress("SameParameterValue")
class RangesTest {
    @Test
    fun testUp() {
        this.testUpWithInt()
        this.testUpWithLong()
    }

    private fun testUpWithInt() {
        val values = arrayOf(20, 10, 5, 4, 2, 1, 0)

        for (start1 in values) {
            for (amount in values) {
                val start2 = 0
                val start3 = -start1

                val range1 = assertDoesNotThrow{ start1 up amount }
                val range2 = assertDoesNotThrow{ start2 up amount }
                val range3 = assertDoesNotThrow{ start3 up amount }

                this.testIntUpwardRange(range1, start1, amount)
                this.testIntUpwardRange(range2, start2, amount)
                this.testIntUpwardRange(range3, start3, amount)
            }
        }

        assertFailsWith<IllegalArgumentException>{ 0 up -1 }
        assertFailsWith<ArithmeticException>{ Int.MAX_VALUE up 1 }
    }

    private fun testIntUpwardRange(range: IntRange, start: Int, amount: Int) {
        assertEquals(amount, range.count())

        if (0 != amount) {
            assertEquals(start + amount - 1, range.last)
        }
    }

    private fun testUpWithLong() {
        val max = Int.MAX_VALUE.toLong()
        val values = arrayOf(max, max / 10, max / 5, max / 2, 0)

        for (start1 in values) {
            for (amount in values) {
                val start2 = 0L
                val start3 = -start1

                val range1 = assertDoesNotThrow{ start1 up amount }
                val range2 = assertDoesNotThrow{ start2 up amount }
                val range3 = assertDoesNotThrow{ start3 up amount }

                this.testLongUpwardRange(range1, start1, amount)
                this.testLongUpwardRange(range2, start2, amount)
                this.testLongUpwardRange(range3, start3, amount)
            }
        }

        assertFailsWith<IllegalArgumentException>{ 0L up -1L }
        assertFailsWith<ArithmeticException>{ Long.MAX_VALUE up 1L }
    }

    private fun testLongUpwardRange(range: LongRange, start: Long, amount: Long) {
        assertEquals(amount, range.count())

        if (0L != amount) {
            assertEquals(start + amount - 1, range.last)
        }
    }

    @Test
    fun testDown() {
        this.testDownWithInt()
        this.testDownWithLong()
    }

    private fun testDownWithInt() {
        val values = arrayOf(20, 10, 5, 4, 2, 1)

        for (start1 in values) {
            for (amount in values) {
                val start2 = 0
                val start3 = -start1

                val range1 = assertDoesNotThrow{ start1 down amount }
                val range2 = assertDoesNotThrow{ start2 down amount }
                val range3 = assertDoesNotThrow{ start3 down amount }

                this.testIntDownwardRange(range1, start1, amount)
                this.testIntDownwardRange(range2, start2, amount)
                this.testIntDownwardRange(range3, start3, amount)
            }
        }

        assertFailsWith<IllegalArgumentException>{ 0 down -1 }
        assertFailsWith<ArithmeticException>{ Int.MIN_VALUE down 1 }
    }

    private fun testIntDownwardRange(range: IntProgression, start: Int, amount: Int) {
        assertEquals(amount, range.count())

        if (0 != amount) {
            assertEquals(start - amount + 1, range.last)
        }
    }

    private fun testDownWithLong() {
        val max = Int.MAX_VALUE.toLong()
        val values = arrayOf(max, max / 10, max / 5, max / 4, max / 2, 0)

        for (start1 in values) {
            for (amount in values) {
                val start2 = 0L
                val start3 = -start1

                val range1 = assertDoesNotThrow{ start1 down amount }
                val range2 = assertDoesNotThrow{ start2 down amount }
                val range3 = assertDoesNotThrow{ start3 down amount }

                this.testLongDownwardRange(range1, start1, amount)
                this.testLongDownwardRange(range2, start2, amount)
                this.testLongDownwardRange(range3, start3, amount)
            }
        }

        assertFailsWith<IllegalArgumentException>{ 0L down -1L }
        assertFailsWith<ArithmeticException>{ Long.MIN_VALUE down 1L }
    }

    private fun testLongDownwardRange(range: LongProgression, start: Long, amount: Long) {
        assertEquals(amount, range.count())

        if (0L != amount) {
            assertEquals(start - amount + 1, range.last)
        }
    }

    @Test
    fun testCount() {
        val small = 1000
        val large = 1000000L

        this.testCountWithIntRanges(small)
        this.testCountWithLongRanges(small, large)
    }

    private fun testCountWithIntRanges(small: Int) {
        val intRanges = arrayOf(
            -small .. small,
            small downTo -small,
            0 .. small,
            0 downTo -small
        )

        for (range1 in intRanges) {
            val range2 = range1 step 2
            val range3 = range1 step 3

            val count1 = assertDoesNotThrow{ range1.count() }
            val count2 = assertDoesNotThrow{ range2.count() }
            val count3 = assertDoesNotThrow{ range3.count() }

            assertEquals(count1, range1.asIterable().count())
            assertEquals(count2, range2.asIterable().count())
            assertEquals(count3, range3.asIterable().count())
        }
    }

    private fun testCountWithLongRanges(small: Int, large: Long) {
        val longRanges = arrayOf(
            -small .. large,
            large downTo -small,
            0 .. large,
            0 downTo -large
        )

        for (range1 in longRanges) {
            val range2 = range1 step 2L
            val range3 = range1 step 3L

            val count1 = assertDoesNotThrow{ range1.count() }
            val count2 = assertDoesNotThrow{ range2.count() }
            val count3 = assertDoesNotThrow{ range3.count() }

            assertEquals(count1, range1.asIterable().count().toLong())
            assertEquals(count2, range2.asIterable().count().toLong())
            assertEquals(count3, range3.asIterable().count().toLong())
        }
    }

    @Test
    fun testMove() {
        this.testMoveWithInt()
        this.testMoveWithLong()
    }

    private fun testMoveWithInt() {
        val amount = 10000

        val upward = assertDoesNotThrow{ 0 move amount }
        assertEquals(upward.count(), amount)
        assertEquals(0, upward.first)
        assertEquals(amount - 1, upward.last)

        val downward = assertDoesNotThrow{ 0 move -amount }
        assertEquals(downward.count(), amount)
        assertEquals(0, downward.first)
        assertEquals(-amount + 1, downward.last)
    }

    private fun testMoveWithLong() {
        val amount = 10000L

        val upward = assertDoesNotThrow{ 0L move amount }
        assertEquals(upward.count(), amount)
        assertEquals(0L, upward.first)
        assertEquals(amount - 1L, upward.last)

        val downward = assertDoesNotThrow{ 0L move -amount }
        assertEquals(downward.count(), amount)
        assertEquals(0L, downward.first)
        assertEquals(-amount + 1L, downward.last)
    }

    @Test
    fun testGet() {
        this.testGetWithInt()
        this.testGetWithLong()
    }

    private fun testGetWithInt() {
        val rangeList = listOf(
            1 .. 1000,
            1000 downTo 1,
            -1000 .. -1,
            -1 downTo -1000,
            -1000 .. 1000,
            1000 downTo -1000
        )

        for (range1 in rangeList) {
            val range2 = range1 step 2
            val range3 = range1 step 3

            this.testGetWithIntOn(range1)
            this.testGetWithIntOn(range2)
            this.testGetWithIntOn(range3)
        }
    }

    private fun testGetWithIntOn(range: IntProgression) {
        val iter = range.iterator()
        val size = range.count()

        for (index in 0 until size) {
            val iteratedItem = iter.next()
            val indexedItem = assertDoesNotThrow{ range[index] }

            assertEquals(indexedItem, iteratedItem)
        }

        assertFailsWith<IndexOutOfBoundsException>{ range[-1] }
        assertFailsWith<IndexOutOfBoundsException>{ range[size] }
    }

    private fun testGetWithLong() {
        val rangeList = listOf(
            1L .. 1000L,
            1000L downTo 1L,
            -1000L .. -1L,
            -1L downTo -1000L,
            -1000L .. 1000L,
            1000L downTo -1000L
        )

        for (range1 in rangeList) {
            val range2 = range1 step 2L
            val range3 = range1 step 3L

            this.testGetWithLongOn(range1)
            this.testGetWithLongOn(range2)
            this.testGetWithLongOn(range3)
        }
    }

    private fun testGetWithLongOn(range: LongProgression) {
        val iter = range.iterator()
        val size = range.count()

        for (index in 0L until size) {
            val iteratedItem = iter.next()
            val indexedItem = assertDoesNotThrow{ range[index] }

            assertEquals(indexedItem, iteratedItem)
        }

        assertFailsWith<IndexOutOfBoundsException>{ range[-1] }
        assertFailsWith<IndexOutOfBoundsException>{ range[size] }
    }

    @Test
    fun testIndexOf() {
        this.testIndexOfWithInt()
        this.testIndexOfWithLong()
    }

    private fun testIndexOfWithInt() {
        val range1 = 0 until 1000 step 2

        this.testIndexOfWith(range1, 0, 0)
        this.testIndexOfWith(range1, 500, 250)
        this.testIndexOfWith(range1, 998, 499)
        this.testIndexOfWith(range1, -1, -1)
        this.testIndexOfWith(range1, 1000, -1)

        val range2 = 1000 downTo 0 step 2

        this.testIndexOfWith(range2, 0, 500)
        this.testIndexOfWith(range2, 500, 250)
        this.testIndexOfWith(range2, 1000, 0)
        this.testIndexOfWith(range2, -1, -1)
        this.testIndexOfWith(range2, 1001, -1)

        val range3 = 0 .. 1000 step 3

        this.testIndexOfWith(range3, 0, 0)
        this.testIndexOfWith(range3, 501, 167)
        this.testIndexOfWith(range3, 999, 333)
        this.testIndexOfWith(range3, -1, -1)
        this.testIndexOfWith(range3, 1000, -1)

        val range4 = 1000 downTo 0 step 3

        this.testIndexOfWith(range4, 1, 333)
        this.testIndexOfWith(range4, 499, 167)
        this.testIndexOfWith(range4, 1000, 0)
        this.testIndexOfWith(range4, 0, -1)
        this.testIndexOfWith(range4, 1001, -1)
    }

    private fun testIndexOfWith(range: IntProgression, value: Int, expectedIndex: Int) {
        val resultIndex = assertDoesNotThrow{ range.indexOf(value) }

        assertEquals(expectedIndex, resultIndex)
    }

    private fun testIndexOfWithLong() {
        val range1 = 0L until 1000L step 2L

        this.testIndexOfWith(range1, 0L, 0L)
        this.testIndexOfWith(range1, 500L, 250L)
        this.testIndexOfWith(range1, 998L, 499L)
        this.testIndexOfWith(range1, -1L, -1L)
        this.testIndexOfWith(range1, 1000L, -1L)

        val range2 = 1000L downTo 0L step 2L

        this.testIndexOfWith(range2, 0L, 500L)
        this.testIndexOfWith(range2, 500L, 250L)
        this.testIndexOfWith(range2, 1000L, 0L)
        this.testIndexOfWith(range2, -1L, -1L)
        this.testIndexOfWith(range2, 1001L, -1L)

        val range3 = 0L .. 1000L step 3L

        this.testIndexOfWith(range3, 0L, 0L)
        this.testIndexOfWith(range3, 501L, 167L)
        this.testIndexOfWith(range3, 999L, 333L)
        this.testIndexOfWith(range3, -1L, -1L)
        this.testIndexOfWith(range3, 1000L, -1L)

        val range4 = 1000L downTo 0L step 3L

        this.testIndexOfWith(range4, 1L, 333L)
        this.testIndexOfWith(range4, 499L, 167L)
        this.testIndexOfWith(range4, 1000L, 0L)
        this.testIndexOfWith(range4, 0L, -1L)
        this.testIndexOfWith(range4, 1001L, -1L)
    }

    private fun testIndexOfWith(range: LongProgression, value: Long, expectedIndex: Long) {
        val resultIndex = assertDoesNotThrow{ range.indexOf(value) }

        assertEquals(expectedIndex, resultIndex)
    }

    @Test
    fun testContains() {
        this.testContainsWithInt()
        this.testContainsWithLong()
    }

    private fun testContainsWithInt() {
        val range1 = 0 .. 1000 step 2

        this.testContainsOn(range1, 0, true)
        this.testContainsOn(range1, 500, true)
        this.testContainsOn(range1, 1000, true)
        this.testContainsOn(range1, -1, false)
        this.testContainsOn(range1, 1001, false)

        val range2 = 1000 downTo 0 step 2

        this.testContainsOn(range2, 0, true)
        this.testContainsOn(range2, 500, true)
        this.testContainsOn(range2, 1000, true)
        this.testContainsOn(range2, -1, false)
        this.testContainsOn(range2, 1001, false)

        val range3 = 0 .. 1000 step 3

        this.testContainsOn(range3, 0, true)
        this.testContainsOn(range3, 501, true)
        this.testContainsOn(range3, 999, true)
        this.testContainsOn(range3, -1, false)
        this.testContainsOn(range3, 1000, false)

        val range4 = 1000 downTo 0 step 3

        this.testContainsOn(range4, 1, true)
        this.testContainsOn(range4, 499, true)
        this.testContainsOn(range4, 1000, true)
        this.testContainsOn(range4, 0, false)
        this.testContainsOn(range4, 1001, false)
    }

    private fun testContainsOn(range: IntProgression, value: Int, expected: Boolean) {
        val result = assertDoesNotThrow{ value in range }

        assertEquals(expected, result)
    }

    private fun testContainsWithLong() {
        val range1 = 0L .. 1000L step 2L

        this.testContainsOn(range1, 0L, true)
        this.testContainsOn(range1, 500L, true)
        this.testContainsOn(range1, 1000L, true)
        this.testContainsOn(range1, -1L, false)
        this.testContainsOn(range1, 1001L, false)

        val range2 = 1000L downTo 0L step 2L

        this.testContainsOn(range2, 0L, true)
        this.testContainsOn(range2, 500L, true)
        this.testContainsOn(range2, 1000L, true)
        this.testContainsOn(range2, -1L, false)
        this.testContainsOn(range2, 1001L, false)

        val range3 = 0L .. 1000L step 3L

        this.testContainsOn(range3, 0L, true)
        this.testContainsOn(range3, 501L, true)
        this.testContainsOn(range3, 999L, true)
        this.testContainsOn(range3, -1L, false)
        this.testContainsOn(range3, 1000L, false)

        val range4 = 1000L downTo 0L step 3L

        this.testContainsOn(range4, 1L, true)
        this.testContainsOn(range4, 499L, true)
        this.testContainsOn(range4, 1000L, true)
        this.testContainsOn(range4, 0L, false)
        this.testContainsOn(range4, 1001L, false)
    }

    private fun testContainsOn(range: LongProgression, value: Long, expected: Boolean) {
        val result = assertDoesNotThrow{ value in range }

        assertEquals(expected, result)
    }
}
