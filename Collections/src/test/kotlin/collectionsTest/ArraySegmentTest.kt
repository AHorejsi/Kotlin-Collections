package collectionsTest

import collections.*
import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.random.Random
import kotlin.test.*

@Suppress("SameParameterValue")
class ArraySegmentTest {
    @Test
    fun testConstructor() {
        val max = 25
        val array = arrayOfNulls<Int>(max)

        for (startIndex in 0 until max) {
            for (endIndex in 0 .. max) {
                this.testConstructionOfSegment(array, startIndex, endIndex)
            }
        }

        assertDoesNotThrow{ emptyArray<Int>().segment().segment() }
        assertFailsWith<IndexOutOfBoundsException>{ array.segment(fromIndex=-1) }
        assertFailsWith<IndexOutOfBoundsException>{ array.segment(toIndex=array.size + 1) }
    }

    private fun testConstructionOfSegment(array: Array<Int?>, startIndex: Int, endIndex: Int) {
        if (startIndex > endIndex) {
            assertFailsWith<IllegalArgumentException>{ array.segment(startIndex, endIndex) }
        }
        else {
            val slice = assertDoesNotThrow{ array.segment(startIndex, endIndex) }

            this.testSegmentConstructor(slice)
        }
    }

    private fun testSegmentConstructor(slice: ArraySegment<Int?>) {
        val size = slice.size

        for (startIndex in 0 until size) {
            for (endIndex in 0 .. size) {
                this.testConstructorOfSubSegment(slice, startIndex, endIndex)
            }
        }

        assertFailsWith<IndexOutOfBoundsException>{ slice.segment(fromIndex=-1) }
        assertFailsWith<IndexOutOfBoundsException>{ slice.segment(toIndex=slice.size + 1) }
    }

    private fun testConstructorOfSubSegment(slice: ArraySegment<Int?>, startIndex: Int, endIndex: Int) {
        if (startIndex > endIndex) {
            assertFailsWith<IllegalArgumentException>{ slice.segment(startIndex, endIndex) }
        }
        else {
            assertDoesNotThrow{ slice.segment(startIndex, endIndex) }
        }
    }

    @Test
    fun testRangeConstructor() {
        val size = 30
        val array = arrayOfNulls<Int>(size)

        for (startIndex in 0 until size) {
            for (endIndex in 0 until size) {
                val range1 = startIndex until endIndex
                val range2 = startIndex .. endIndex

                this.testRangeOnArray(array, range1)
                this.testRangeOnArray(array, range2)
            }
        }

        assertFailsWith<IndexOutOfBoundsException>{ array.segment(-1 until size) }
        assertFailsWith<IndexOutOfBoundsException>{ array.segment(0 .. size) }
    }

    private fun testRangeOnArray(array: Array<Int?>, range: IntRange) {
        val slice = assertDoesNotThrow { array.segment(range) }

        this.testSubrangeConstructor(slice)
    }

    private fun testSubrangeConstructor(slice: ArraySegment<Int?>) {
        val size = slice.size

        for (startIndex in 0 until size) {
            for (endIndex in 0 until size) {
                val range1 = startIndex until endIndex
                val range2 = startIndex .. endIndex

                assertDoesNotThrow{ slice.segment(range1) }
                assertDoesNotThrow{ slice.segment(range2) }
            }
        }

        assertFailsWith<IndexOutOfBoundsException>{ slice.segment(-1 until size ) }
        assertFailsWith<IndexOutOfBoundsException>{ slice.segment(0 .. size) }
    }

    @Test
    fun testSize() {
        val size = 29
        val array = arrayOfNulls<Int>(size)

        for (startIndex in 0 until size) {
            for (endIndex in startIndex .. size) {
                val segment = array.segment(startIndex, endIndex)

                assertEquals(endIndex - startIndex, segment.size)
            }
        }
    }

    @Test
    fun testLastIndex() {
        val size = 11
        val array = arrayOfNulls<Int>(size)

        for (startIndex in 0 until size) {
            for (endIndex in startIndex .. size) {
                val slice = array.segment(startIndex, endIndex)
                val subSize = endIndex - startIndex

                assertEquals(subSize - 1, slice.lastIndex)
            }
        }
    }

    @Test
    fun testIsEmpty() {
        val size = 11
        val array = arrayOfNulls<Int>(size)

        for (startIndex in 0 until size) {
            for (endIndex in startIndex .. size) {
                val slice = array.segment(startIndex, endIndex)
                val empty = slice.isEmpty()

                if (startIndex == endIndex) {
                    assertTrue(empty)
                }
                else {
                    assertFalse(empty)
                }
            }
        }
    }

    @Test
    fun testGet() {
        val size = 57
        val array = Array(size) { it + 1 }

        for (startIndex in 0 until size) {
            for (endIndex in startIndex .. size) {
                val slice = array.segment(startIndex, endIndex)

                if (!slice.isEmpty()) {
                    this.testGetWithRange(array, slice, startIndex)
                }
                else {
                    assertFailsWith<IndexOutOfBoundsException>{ slice[0] }
                    assertFailsWith<IndexOutOfBoundsException>{ slice[slice.lastIndex] }
                }

                assertFailsWith<IndexOutOfBoundsException>{ slice[-1] }
                assertFailsWith<IndexOutOfBoundsException>{ slice[slice.size] }
            }
        }
    }

    private fun testGetWithRange(array: Array<Int>, slice: ArraySegment<Int>, startIndex: Int) {
        for (index in 0 until slice.size) {
            assertEquals(slice[index], array[index + startIndex])
        }
    }

    @Test
    fun testSafeGet() {
        val size = 42
        val startIndex = 22
        val endIndex = 39

        val array = Array(size) { it + 1 }

        this.testSafeGetAtIndices(array, startIndex, endIndex)
    }

    private fun testSafeGetAtIndices(array: Array<Int>, startIndex: Int, endIndex: Int) {
        val slice = array.segment(startIndex, endIndex)

        this.testSuccessfulSafeGet(array, slice, startIndex, 0)
        this.testSuccessfulSafeGet(array, slice, startIndex, slice.size / 2)
        this.testSuccessfulSafeGet(array, slice, startIndex, slice.lastIndex)

        this.testFailedSafeGet(slice, -1)
        this.testFailedSafeGet(slice, slice.size)
    }

    private fun testSuccessfulSafeGet(array: Array<Int>, slice: ArraySegment<Int>, startIndex: Int, itemIndex: Int) {
        val option = slice.safeGet(itemIndex)

        assertTrue(option.isSome())
        assertEquals(slice[itemIndex], option.getOrThrow())
        assertEquals(array[itemIndex + startIndex], option.getOrThrow())
    }

    private fun testFailedSafeGet(slice: ArraySegment<Int>, itemIndex: Int) {
        val option = slice.safeGet(itemIndex)

        assertTrue(option.isNone())
        assertEquals(null, option.getOrNull())
    }

    @Test
    fun testTryGet() {
        val size = 39
        val startIndex = 8
        val endIndex = 20

        val array = Array(size) { it + 1 }

        this.testTryGetAtIndices(array, startIndex, endIndex)
    }

    private fun testTryGetAtIndices(array: Array<Int>, startIndex: Int, endIndex: Int) {
        val slice = array.segment(startIndex, endIndex)

        this.testSuccessfulTryGet(array, slice, startIndex, 0)
        this.testSuccessfulTryGet(array, slice, startIndex, slice.size / 2)
        this.testSuccessfulTryGet(array, slice, startIndex, slice.lastIndex)

        this.testFailedTryGet(slice, -1)
        this.testFailedTryGet(slice, slice.size)
    }

    private fun testSuccessfulTryGet(array: Array<Int>, slice: ArraySegment<Int>, startIndex: Int, itemIndex: Int) {
        val result = slice.tryGet(itemIndex)

        assertTrue(result.isSuccess)
        assertEquals(slice[itemIndex], result.getOrThrow())
        assertEquals(array[itemIndex + startIndex], result.getOrThrow())
    }

    private fun testFailedTryGet(slice: ArraySegment<Int>, itemIndex: Int) {
        val result = slice.tryGet(itemIndex)

        assertTrue(result.isFailure)
        assertFailsWith<IndexOutOfBoundsException>{ result.getOrThrow() }
    }

    @Test
    fun testWrapGet() {
        val size = 19
        val startIndex = 3
        val endIndex = 12

        val array = Array(size) { it }
        val slice = array.segment(startIndex, endIndex)

        for (index in 0 until slice.size) {
            val back = assertDoesNotThrow{ slice.wrapGet(index - slice.size) }
            val current = assertDoesNotThrow{ slice.wrapGet(index) }
            val ahead = assertDoesNotThrow{ slice.wrapGet(index + slice.size) }

            this.testWrapEquality(slice[index], back, current, ahead)
            this.testWrapEquality(array[index + startIndex], back, current, ahead)
        }
    }

    private fun testWrapEquality(item: Int, back: Int, current: Int, ahead: Int) {
        assertEquals(item, back)
        assertEquals(item, current)
        assertEquals(item, ahead)
    }

    @Test
    fun testSet() {
        val size = 62
        val array = Array(size) { it }

        for (startIndex in 0 until size) {
            for (endIndex in startIndex .. size) {
                val slice = array.segment(startIndex, endIndex)

                this.testSetAtEnds(array, slice, startIndex, endIndex)
                this.testSetAtMiddle(array, slice, startIndex)
            }
        }
    }

    private fun testSetAtEnds(array: Array<Int>, slice: ArraySegment<Int>, startIndex: Int, endIndex: Int) {
        val value = Int.MAX_VALUE - (startIndex + endIndex) * 13
        val negValue = -value

        val sliceSize = slice.size
        val lastIndex = slice.lastIndex

        when (sliceSize) {
            0 -> {
                assertFailsWith<IndexOutOfBoundsException>{ slice[0] = value }
                assertFailsWith<IndexOutOfBoundsException>{ slice[lastIndex] = negValue }
            }
            1 -> {
                this.testSetIndex(array, slice, 0, startIndex, value)
                assertEquals(slice[0], slice[lastIndex])

                this.testSetIndex(array, slice, lastIndex, endIndex - 1, negValue)
                assertEquals(slice[0], slice[lastIndex])
            }
            else -> {
                this.testSetIndex(array, slice, 0, startIndex, value)
                this.testSetIndex(array, slice, lastIndex, endIndex - 1, negValue)

                assertNotEquals(slice[0], slice[lastIndex])
            }
        }
    }

    private fun testSetAtMiddle(array: Array<Int>, slice: ArraySegment<Int>, startIndex: Int) {
        val midIndex = slice.size / 2
        val value = 0

        if (slice.isEmpty()) {
            assertFailsWith<IndexOutOfBoundsException>{ slice[midIndex] = value }
        }
        else {
            this.testSetIndex(array, slice, midIndex, midIndex + startIndex, value)
        }
    }

    private fun testSetIndex(array: Array<Int>, slice: ArraySegment<Int>, sliceIndex: Int, arrayIndex: Int, value: Int) {
        assertDoesNotThrow{ slice[sliceIndex] = value }

        assertEquals(value, slice[sliceIndex])
        assertEquals(slice[sliceIndex], array[arrayIndex])
    }

    @Test
    fun testSafeSet() {
        val size = 31
        val startIndex = 11
        val endIndex = 25

        val array = arrayOfNulls<Int>(size)

        this.testSafeSetAtIndices(array, startIndex, endIndex)
    }

    private fun testSafeSetAtIndices(array: Array<Int?>, startIndex: Int, endIndex: Int) {
        val slice = array.segment(startIndex, endIndex)

        this.testSuccessfulSafeSet(array, slice, startIndex, 0)
        this.testSuccessfulSafeSet(array, slice, startIndex, slice.size / 4)
        this.testSuccessfulSafeSet(array, slice, startIndex, 3 * slice.size / 4)
        this.testSuccessfulSafeSet(array, slice, startIndex, slice.lastIndex)

        this.testFailedSafeSet(slice, -1)
        this.testFailedSafeSet(slice, slice.size)
    }

    private fun testSuccessfulSafeSet(array: Array<Int?>, slice: ArraySegment<Int?>, startIndex: Int, itemIndex: Int) {
        val option = slice.safeSet(itemIndex, Int.MIN_VALUE)

        assertTrue(option.isSome())
        assertEquals(Int.MIN_VALUE, slice[itemIndex])
        assertEquals(Int.MIN_VALUE, array[itemIndex + startIndex])
    }

    private fun testFailedSafeSet(slice: ArraySegment<Int?>, itemIndex: Int) {
        val option = slice.safeSet(itemIndex, Int.MIN_VALUE)

        assertTrue(option.isNone())
    }

    @Test
    fun testTrySet() {
        val size = 55
        val startIndex = 17
        val endIndex = 48

        this.testTrySetAtIndices(size, startIndex, endIndex)
    }

    private fun testTrySetAtIndices(size: Int, startIndex: Int, endIndex: Int) {
        val array = arrayOfNulls<Int>(size)
        val slice = array.segment(startIndex until endIndex)

        this.testSuccessfulTrySet(array, slice, startIndex, 0)
        this.testSuccessfulTrySet(array, slice, startIndex, slice.size / 2)
        this.testSuccessfulTrySet(array, slice, startIndex, slice.lastIndex)

        this.testFailedTrySet(slice, -1)
        this.testFailedTrySet(slice, slice.size)
    }

    private fun testSuccessfulTrySet(array: Array<Int?>, slice: ArraySegment<Int?>, startIndex: Int, itemIndex: Int) {
        val result = slice.trySet(itemIndex, Int.MIN_VALUE)

        assertDoesNotThrow{ result.getOrThrow() }
        assertEquals(Int.MIN_VALUE, slice[itemIndex])
        assertEquals(Int.MIN_VALUE, array[itemIndex + startIndex])
    }

    private fun testFailedTrySet(slice: ArraySegment<Int?>, itemIndex: Int) {
        val result = slice.trySet(itemIndex, Int.MIN_VALUE)

        assertFailsWith<IndexOutOfBoundsException>{ result.getOrThrow() }
    }

    @Test
    fun testWrapSet() {
        val size = 19
        val startIndex = 3
        val endIndex = 12

        val array = Array(size) { it }
        val slice = array.segment(startIndex, endIndex)

        for (index in 0 until slice.size) {
            this.testWrapSetEquality(slice, index, Int.MIN_VALUE)
            this.testWrapSetEquality(slice, index - slice.size, Int.MAX_VALUE)
            this.testWrapSetEquality(slice, index + slice.size, 0)
        }
    }

    private fun testWrapSetEquality(
        slice: ArraySegment<Int>,
        wrapIndex: Int,
        value: Int
    ) {
        assertDoesNotThrow{ slice.wrapSet(wrapIndex, value) }
        assertEquals(value, slice.wrapGet(wrapIndex))
    }

    @Test
    fun testCopy() {
        val size = 50
        val array = Array(size) { it * 2 }

        for (startIndex in 0 until size) {
            for (endIndex in startIndex .. size) {
                val slice = array.segment(startIndex, endIndex)

                val newArray = assertDoesNotThrow{ slice.copy() }
                val newSlice = newArray.segment()

                assertEquals(slice.size, newArray.size)
                assertNotSame(array, newArray)

                assertTrue(slice.contentEquals(newSlice))
                assertTrue(newSlice.contentEquals(slice))
            }
        }
    }

    @Test
    fun testFill() {
        val size = 101
        val array = Array(size) { Int.MAX_VALUE }

        var value = Int.MIN_VALUE

        for (startIndex in 0 until size) {
            for (endIndex in startIndex .. size) {
                val slice = array.segment(startIndex, endIndex)
                slice.fill(value)

                val result = slice.all(value::equals)
                assertTrue(result)

                if (startIndex == endIndex) {
                    assertEquals(-1, array.indexOf(value))
                    assertEquals(-1, array.lastIndexOf(value))
                }
                else {
                    assertEquals(startIndex, array.indexOf(value))
                    assertEquals(endIndex - 1, array.lastIndexOf(value))
                }

                ++value
            }
        }
    }

    @Test
    fun testReverse() {
        val size = 68
        val array = Array(size) { (it + 1) * 2 }

        for (startIndex in 0 until size) {
            for (endIndex in startIndex .. size) {
                val slice = array.segment(startIndex, endIndex)
                val copy = slice.copy()

                slice.reverse()

                this.testReverseEquality(slice, copy, array, startIndex)
            }
        }
    }

    private fun testReverseEquality(
        slice: ArraySegment<Int>,
        copy: Array<Int>,
        orig: Array<Int>,
        startIndex: Int
    ) {
        var index1 = 0
        var index2 = slice.lastIndex

        while (index1 < slice.size) {
            assertEquals(slice[index1], copy[index2])
            assertEquals(slice[index1], orig[startIndex + index1])

            ++index1
            --index2
        }
    }

    @Test
    fun testShuffle() {
        val size = 10
        val array1 = Array(size) { it }
        val array2 = Array(size) { it }

        this.testWithCustomRandom(size, array1, array2, 0)
    }

    private fun testWithCustomRandom(size: Int, array1: Array<Int>, array2: Array<Int>, seed: Int) {
        val rng1 = Random(seed)
        val rng2 = Random(seed)

        for (startIndex in 0 until size) {
            for (endIndex in startIndex .. size) {
                val slice1 = array1.segment(startIndex, endIndex)
                val slice2 = array2.segment(startIndex, endIndex)

                slice1.shuffle(rng1)
                slice2.shuffle(rng2)

                assertTrue(slice1.contentEquals(slice2))
            }
        }
    }

    @Test
    fun testSwap() {
        val size = 10
        val fromIndex = 1
        val toIndex = 5

        val array = Array(size) { it }

        this.testSwapAtEnds(array, fromIndex, toIndex)
    }

    private fun testSwapAtEnds(array: Array<Int>, fromIndex: Int, toIndex: Int) {
        val slice = array.segment(fromIndex, toIndex)

        val first = slice.first()
        val last = slice.last()

        slice.swap(0, slice.lastIndex)

        assertEquals(first, slice.last())
        assertEquals(last, slice.first())

        assertEquals(array[toIndex - 1], slice.last())
        assertEquals(array[fromIndex], slice.first())
    }

    @Test
    fun testSort() {
        val size = 35

        this.testSortWithComparator(size, 7, 19, inOrder())
        this.testSortWithComparator(size, 22, 33, reverseOrder())
    }

    private fun testSortWithComparator(size: Int, startIndex: Int, endIndex: Int, comp: Comparator<Int>) {
        val array = Array(size) { if (0 == it % 2) it else -it }
        val slice = array.segment(startIndex, endIndex)

        slice.sort(comp)

        for (index in 0 until slice.lastIndex) {
            val lessThan = comp.compare(slice[index], slice[index + 1]) <= 0

            assertTrue(lessThan)
        }
    }

    @Test
    fun testRotate() {

    }

    @Test
    fun testPartition() {

    }

    @Test
    fun testIndexOf() {

    }

    @Test
    fun testIndexWithPredicate() {

    }

    @Test
    fun testLastIndexOf() {

    }

    @Test
    fun testLastIndexWithPredicate() {

    }

    @Test
    fun testIsPermutationOf() {

    }

    @Test
    fun testContains() {
        val size = 10
        val startIndex = 1
        val endIndex = 9

        val array = Array(size) { it }
        val slice = array.segment(startIndex, endIndex)

        for (index in 0 until slice.size) {
            assertTrue(index in array)

            val result = index in slice

            if (index < startIndex || index >= endIndex) {
                assertFalse(result)
            }
            else {
                assertTrue(result)
            }
        }
    }

    @Test
    fun testContentEquals() {
        val large = arrayOf(0, 1, 2, 3, -1, 1, 2, 3, 0)

        this.testEqualSegments(large)
        this.testNotEqualSegments(large)
        this.testNullSegments(large)
    }

    private fun testEqualSegments(large: Array<Int>) {
        val before = large.segment(1, 4)
        val after = large.segment(5, 8)

        assertTrue(before.contentEquals(after))
        assertTrue(after.contentEquals(before))

        assertTrue(before.contentEquals(before))
        assertTrue(after.contentEquals(after))
    }

    private fun testNotEqualSegments(large: Array<Int>) {
        val before = large.segment(0, 4)
        val after = large.segment(5, 9)

        assertFalse(before.contentEquals(after))
        assertFalse(after.contentEquals(before))

        assertTrue(before.contentEquals(before))
        assertTrue(after.contentEquals(after))
    }

    private fun testNullSegments(large: Array<Int>) {
        val nullArray: Array<Int>? = null
        val nullSegment: ArraySegment<Int>? = null

        assertTrue(nullSegment.contentEquals(nullSegment))

        assertFalse(large.contentEquals(nullArray))
        assertFalse(nullArray.contentEquals(large))
    }

    @Test
    fun testToString() {
        val array = Array(10) { it }

        @Suppress("EmptyRange")
        val slice1 = array.segment(1 .. 0)
        val slice2 = array.segment(7 .. 7)
        val slice3 = array.segment(2 .. 5)

        assertEquals("[]", slice1.toString())
        assertEquals("[7]", slice2.toString())
        assertEquals("[2, 3, 4, 5]", slice3.toString())
    }
}

class ArraySegmentIteratorTest {
    @Test
    fun testConstructor() {
        val size = 61
        val array = arrayOfNulls<Int>(size)

        for (startIndex in 0 until size) {
            for (endIndex in startIndex .. size) {
                val slice = array.segment(startIndex, endIndex)

                assertDoesNotThrow{ slice.iterator() }
            }
        }
    }

    @Test
    fun testHasNext() {
        val size = 79
        val array = arrayOfNulls<Int>(size)

        for (startIndex in 0 until size) {
            for (endIndex in startIndex .. size) {
                val slice = array.segment(startIndex, endIndex)

                val iter = slice.iterator()

                repeat(slice.size) {
                    assertTrue(iter.hasNext())
                    assertDoesNotThrow{ iter.next() }
                }

                assertFalse(iter.hasNext())
            }
        }
    }

    @Test
    fun testNext() {
        val size = 52
        val array = Array(size) { it * 66 - 31 }

        for (startIndex in 0 until size) {
            for (endIndex in startIndex .. size) {
                val slice = array.segment(startIndex, endIndex)

                this.testNextEquality(startIndex, array, slice)
            }
        }
    }

    private fun testNextEquality(startIndex: Int, array: Array<Int>, slice: ArraySegment<Int>) {
        val iter = slice.iterator()
        var index = 0

        while (iter.hasNext()) {
            val item1 = assertDoesNotThrow{ iter.next() }
            val item2 = assertDoesNotThrow{ slice[index] }
            val item3 = assertDoesNotThrow{ array[index + startIndex] }

            assertEquals(item1, item2)
            assertEquals(item2, item3)

            ++index
        }
    }
}
