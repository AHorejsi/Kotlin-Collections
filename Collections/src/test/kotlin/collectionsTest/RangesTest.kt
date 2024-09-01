package collectionsTest

import collections.count
import collections.down
import collections.move
import collections.up
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

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

                val range1 = start1 up amount
                val range2 = start2 up amount
                val range3 = start3 up amount

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

                val range1 = start1 up amount
                val range2 = start2 up amount
                val range3 = start3 up amount

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

                val range1 = start1 down amount
                val range2 = start2 down amount
                val range3 = start3 down amount

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

                val range1 = start1 down amount
                val range2 = start2 down amount
                val range3 = start3 down amount

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
        val small = 1000000
        val large = 1000000000L

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

        @Suppress("RedundantAsSequence")
        for (range1 in intRanges) {
            val range2 = range1 step 2
            val range3 = range1 step 3

            assertEquals(range1.count(), range1.asSequence().count())
            assertEquals(range2.count(), range2.asSequence().count())
            assertEquals(range3.count(), range3.asSequence().count())
        }
    }

    private fun testCountWithLongRanges(small: Int, large: Long) {
        val longRanges = arrayOf(
            -small .. large,
            large downTo -small,
            0 .. large,
            0 downTo -large
        )

        @Suppress("RedundantAsSequence")
        for (range1 in longRanges) {
            val range2 = range1 step 2
            val range3 = range1 step 3

            assertEquals(range1.count(), range1.asSequence().count().toLong())
            assertEquals(range2.count(), range2.asSequence().count().toLong())
            assertEquals(range3.count(), range3.asSequence().count().toLong())
        }
    }

    @Test
    fun testMove() {
        this.testMoveWithInt()
        this.testMoveWithLong()
    }

    private fun testMoveWithInt() {
        val amount1 = 10000

        val upward1 = 0 move amount1
        val downward1 = 0 move -amount1

        assertEquals(upward1.count(), amount1)
        assertEquals(downward1.count(), amount1)
    }

    private fun testMoveWithLong() {
        val amount2 = 1000000000L

        val upward2 = 0L move amount2
        val downward2 = 0L move -amount2

        assertEquals(upward2.count(), amount2)
        assertEquals(downward2.count(), amount2)
    }
}