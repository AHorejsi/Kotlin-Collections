package collectionsTest

import collections.*
import org.junit.jupiter.api.assertDoesNotThrow
import reusable.*
import kotlin.test.*

@Suppress("SameParameterValue")
class VectorListTest {
    @Test
    fun testConstructor() {
        assertDoesNotThrow{ VectorList<Int>(0) }
        assertFailsWith<IllegalArgumentException>{ VectorList<Int>(-1) }
    }

    @Test
    fun testVectorListOf() {
        val vec1 = assertDoesNotThrow{ vectorListOf<Int>() }
        val vec2 = assertDoesNotThrow{ vectorListOf(1, 2, 3) }

        val size1 = 0
        val size2 = 3

        assertEquals(size1, vec1.size)
        assertEquals(size2, vec2.size)
    }

    @Test
    fun testToVectorList() {
        val set = (1 .. 17).toHashSet()
        val seq = sequenceOf(0, 5, 7, -8, -4, 4, 2)
        val iter = (-7 until 11 step 3).asIterable()
        val array = arrayOf(-1, 4, 0, -6, 8, -9, 8, 5)

        val vecFromSet = assertDoesNotThrow{ set.toVectorList() }
        val vecFromSeq = assertDoesNotThrow{ seq.toVectorList() }
        val vecFromIter = assertDoesNotThrow{ iter.toVectorList() }
        val vecFromArray = assertDoesNotThrow{ array.toVectorList() }

        this.testIteratorEquality(vecFromSet.iterator(), set.iterator())
        this.testIteratorEquality(vecFromIter.iterator(), iter.iterator())
        this.testIteratorEquality(vecFromSeq.iterator(), seq.iterator())
        this.testIteratorEquality(vecFromArray.iterator(), array.iterator())

        assertTrue(set.size <= vecFromSet.capacity)
        assertTrue(seq.count() <= vecFromSeq.capacity)
        assertTrue(iter.count() <= vecFromIter.capacity)
        assertTrue(array.size <= vecFromArray.capacity)
    }

    private fun testIteratorEquality(left: Iterator<*>, right: Iterator<*>) {
        while (left.hasNext() && right.hasNext()) {
            val leftItem = left.next()
            val rightItem = right.next()

            assertEquals(leftItem, rightItem)
        }

        assertFalse(left.hasNext())
        assertFalse(right.hasNext())
    }

    @Test
    fun testIsRandomAccess() {
        val vec = vectorListOf<Int>()

        testIsRandomAccess(vec)
    }

    @Test
    fun testWithIndex() {
        val vec = (1 .. 16).toVectorList()

        testWithIndex(vec)
    }

    @Test
    fun testAtLeast() {
        val count = 100

        val lesser = 0.replicate(count - 1).toVectorList()
        val equal = 0.replicate(count).toVectorList()
        val greater = 0.replicate(count + 1).toVectorList()

        testAtLeast(count, lesser, equal, greater)
    }

    @Test
    fun testAtMost() {
        val count = 100

        val lesser = 0.replicate(count - 1).toVectorList()
        val equal = 0.replicate(count).toVectorList()
        val greater = 0.replicate(count + 1).toVectorList()

        testAtMost(count, lesser, equal, greater)
    }

    @Test
    fun testExactly() {
        val count = 100

        val lesser = 0.replicate(count - 1).toVectorList()
        val equal = 0.replicate(count).toVectorList()
        val greater = 0.replicate(count + 1).toVectorList()

        testExactly(count, lesser, equal, greater)
    }

    @Test
    fun testSize() {
        val vec = vectorListOf<Int>()

        testSizeAfterAdd(vec, 0)
        testSizeAfterAddAll(vec, 100)
        testSizeAfterRemoveLast(vec)
        testSizeAfterAddAll(vec, 200)
        testSizeAfterRemoveFromBack(vec, 150)

        val finalSize = assertDoesNotThrow{ vec.size }

        assertEquals(150, finalSize)
    }

    @Test
    fun testIsEmpty() {
        val vec: MutableList<Int> = vectorListOf()

        testIsEmpty(vec)
    }

    @Test
    fun testCapacity() {
        this.testCapacityWithAdding()
        this.testCapacityWithRemoving()
    }

    private fun testCapacityWithAdding() {
        val initialCapacity = 51

        val vec = VectorList<Int>(initialCapacity)
        val capacity = assertDoesNotThrow{ vec.capacity }

        assertEquals(initialCapacity, capacity)

        this.testCapacityAfterAddingUpToCapacity(vec)
        this.testCapacityAfterGoingOneOver(vec)
    }

    private fun testCapacityAfterAddingUpToCapacity(vec: VectorList<Int>) {
        val oldCapacity = assertDoesNotThrow{ vec.capacity }

        for (num in 0 until oldCapacity) {
            val newCapacity = assertDoesNotThrow{ vec.capacity }

            assertEquals(oldCapacity, newCapacity)

            vec.add(num)
        }

        val newCapacity = assertDoesNotThrow{ vec.capacity }
        assertEquals(oldCapacity, newCapacity)
    }

    private fun testCapacityAfterGoingOneOver(vec: VectorList<Int>) {
        val capacityBeforeAdd = assertDoesNotThrow{ vec.capacity }
        vec.add(-1)
        val capacityAfterAdd = assertDoesNotThrow{ vec.capacity }

        assertTrue(capacityBeforeAdd < capacityAfterAdd)
    }

    private fun testCapacityWithRemoving() {
        val cap1 = 120
        val cap2 = 121
        val cap3 = 8
        val cap4 = VectorList.MIN_CAPACITY

        val vec1 = (1 .. cap1).toVectorList()
        val vec2 = (1 .. cap2).toVectorList()
        val vec3 = (1 .. cap3).toVectorList()
        val vec4 = (1 .. cap4).toVectorList()

        this.testCapacityAfterRemoving(vec1)
        this.testCapacityAfterRemoving(vec2)
        this.testCapacityAfterRemoving(vec3)
        this.testCapacityAfterRemoving(vec4)
    }

    private fun testCapacityAfterRemoving(vec: VectorList<Int>) {
        val capacityBeforeRemove = assertDoesNotThrow{ vec.capacity }

        vec.removeFromBack((vec.size / 2) + 1)

        if (capacityBeforeRemove <= VectorList.MIN_CAPACITY) {
            assertEquals(capacityBeforeRemove, VectorList.MIN_CAPACITY)
        }
        else {
            val capacityAfterRemove = assertDoesNotThrow{ vec.capacity }

            assertTrue(capacityBeforeRemove > capacityAfterRemove)
        }
    }

    @Test
    fun testEnsureCapacity() {
        val initialCapacity = 29
        val capacityChange = 3

        val vec = VectorList<Int>(initialCapacity)
        assertEquals(initialCapacity, vec.capacity)

        this.testSmallerCapacity(vec, capacityChange)
        this.testLargerCapacity(vec, capacityChange)

        val capacityBeforeFail = vec.capacity
        assertFailsWith<IllegalArgumentException>{ vec.ensureCapacity(-1) }
        assertEquals(capacityBeforeFail, vec.capacity)
    }

    private fun testSmallerCapacity(vec: VectorList<Int>, capacityChange: Int) {
        val currentCapacity = vec.capacity
        val smallerCapacity = vec.capacity - capacityChange

        assertDoesNotThrow{ vec.ensureCapacity(smallerCapacity) }

        assertEquals(currentCapacity, vec.capacity)
        assertNotEquals(smallerCapacity, vec.capacity)
    }

    private fun testLargerCapacity(vec: VectorList<Int>, capacityChange: Int) {
        val currentCapacity = vec.capacity
        val largerCapacity = vec.capacity + capacityChange

        assertDoesNotThrow{ vec.ensureCapacity(largerCapacity) }

        assertNotEquals(currentCapacity, vec.capacity)
        assertEquals(largerCapacity, vec.capacity)
    }

    @Test
    fun testTrimToSize() {
        val initialCapacity = 22
        val vec = VectorList<Int>(initialCapacity)
        assertEquals(initialCapacity, vec.capacity)

        this.testAfterIncreasingSize(vec)
        this.testWhenSizeEqualsCapacityBeforeAdding(vec)
    }

    private fun testAfterIncreasingSize(vec: VectorList<Int>) {
        val targetSize = 8

        vec.addAll(0 until targetSize)

        assertEquals(targetSize, vec.size)
        assertNotEquals(targetSize, vec.capacity)

        assertDoesNotThrow{ vec.trimToSize() }

        assertEquals(targetSize, vec.size)
        assertTrue(targetSize <= vec.capacity)
    }

    private fun testWhenSizeEqualsCapacityBeforeAdding(vec: VectorList<Int>) {
        val items = 0.replicate(vec.capacity - vec.size)
        vec.addAll(items)

        val oldCapacity = vec.capacity
        vec.add(-100)
        val newCapacity = vec.capacity

        assertNotEquals(oldCapacity, newCapacity)
    }

    @Test
    fun testTryFirst() {
        this.testTryFirstOnEmpty()
        this.testTryFirstOnNonempty()
    }

    private fun testTryFirstOnEmpty() {
        val vec = vectorListOf<Int>()

        val result1 = assertDoesNotThrow{ vec.tryFirst() }
        val result2 = assertDoesNotThrow{ vec.tryFirst{ 0 == it % 2 } }

        assertFailsWith<NoSuchElementException>{ result1.getOrThrow() }
        assertFailsWith<NoSuchElementException>{ result2.getOrThrow() }
    }

    private fun testTryFirstOnNonempty() {
        val range = (10 .. 14).asSequence()
        val vec = (range + range).toVectorList()

        val result1 = assertDoesNotThrow{ vec.tryFirst() }
        val result2 = assertDoesNotThrow{ vec.tryFirst{ 0 == it % 3 } }
        val result3 = assertDoesNotThrow{ vec.tryFirst{ 0 == it % 8 } }

        assertEquals(10, result1.getOrThrow())
        assertEquals(12, result2.getOrThrow())
        assertFailsWith<NoSuchElementException>{ result3.getOrThrow() }
    }

    @Test
    fun testTryLast() {
        this.testTryLastOnEmpty()
        this.testTryLastOnNonempty()
    }

    private fun testTryLastOnEmpty() {
        val vec = vectorListOf<Int>()

        val result1 = assertDoesNotThrow{ vec.tryLast() }
        val result2 = assertDoesNotThrow{ vec.tryLast{ 0 == it % 2 } }

        assertFailsWith<NoSuchElementException>{ result1.getOrThrow() }
        assertFailsWith<NoSuchElementException>{ result2.getOrThrow() }
    }

    private fun testTryLastOnNonempty() {
        val range = (10 .. 14).asSequence()
        val vec = (range + range).toVectorList()

        val result1 = assertDoesNotThrow{ vec.tryLast() }
        val result2 = assertDoesNotThrow{ vec.tryLast{ 0 == it % 3 } }
        val result3 = assertDoesNotThrow{ vec.tryLast{ 0 == it % 8 } }

        assertEquals(14, result1.getOrThrow())
        assertEquals(12, result2.getOrThrow())
        assertFailsWith<NoSuchElementException>{ result3.getOrThrow() }
    }

    @Test
    fun testGet() {
        val vec = (1 .. 50).toVectorList()

        testGetDuringIteration(vec)

        testGetAfterAdding(vec, -1)
        testGetAfterAdding(vec, -2)

        testGetAt(vec, vec.size - 2, -1)

        testGetAfterAddingAt(vec, 0, -3)
        testGetAfterAddingAt(vec, vec.size, -4)
        testGetAfterAddingAt(vec, vec.size / 2, -5)

        testGetAfterRemovingAt(vec, 0)
        testGetAfterRemovingAt(vec, vec.size / 4)
        testGetAfterRemovingAt(vec, 3 * vec.size / 4)
        testGetAfterRemovingAt(vec, vec.lastIndex)

        testGetDuringIteration(vec)

        testGetOutOfBounds(vec)
    }

    @Test
    fun testWrapGet() {
        val vec = (1 .. 50).toVectorList()
        testWrapGetAtEnds(vec)
        testWrapGetRelative(vec)

        val empty = vectorListOf<Int>()
        testWrapGetOnEmpty(empty)
    }

    @Test
    fun testTryGet() {
        val vec = (1 .. 15).toVectorList()

        testTryGetInBounds(vec)
        testTryGetOutOfBounds(vec)
    }

    @Test
    fun testSet() {
        val vec = (1 .. 10).toVectorList()

        testSetAt(vec, 0, -1)
        testSetAt(vec, vec.lastIndex, -2)
        testSetAt(vec, vec.size / 2, -3)

        testSetOutOfBounds(vec, -4)
    }

    @Test
    fun testWrapSet() {
        val vec = (1 .. 10).toVectorList()
        testWrapSetAt(vec, 0, -1)
        testWrapSetAt(vec, vec.size / 2, -2)
        testWrapSetAt(vec, vec.lastIndex, -3)
        testWrapSetAt(vec, -1, -4)
        testWrapSetAt(vec, vec.size, -5)

        val empty = vectorListOf<Int>()
        testWrapSetOnEmpty(empty, -6)
    }

    @Test
    fun testTrySet() {
        val vec = (0 .. 10).toVectorList()

        testTrySetAt(vec, 0, -1)
        testTrySetAt(vec, vec.size / 2, -2)
        testTrySetAt(vec, vec.lastIndex, -3)

        testTrySetOutOfBounds(vec, -4)
    }

    @Test
    fun testAdd() {
        val amount = 10
        val subAmount = 5
        val vec = vectorListOf<Int>()

        testAddConsecutively(vec, amount, subAmount)

        testIndexedAdd(vec, 0, -1)
        testIndexedAdd(vec, vec.size, -2)
        testIndexedAdd(vec, vec.size / 2, -3)
        testIndexedAdd(vec, vec.size / 4, -4)
        testIndexedAdd(vec, 3 * vec.size / 4, -5)

        testIndexedAddOutOfBounds(vec, -1, -6)
        testIndexedAddOutOfBounds(vec, vec.size + 1, -6)
    }

    @Test
    fun testAddAll() {
        val vec = vectorListOf<Int>()

        val other1 = (1 .. 10).toList()
        val other2 = (20 downTo 11).toSet()
        val other3 = (5 until 15).toHashSet()

        val empty1 = emptySet<Int>()
        val empty2 = hashSetOf<Int>()

        testAddAllWithOther(vec, other1)
        testAddAllWithOther(vec, other2)
        testAddAllWithOther(vec, other3)

        testAddAllWithEmpty(vec, empty1)
        testAddAllWithEmpty(vec, empty2)

        testAddAllWithSelf(vec)

        testIndexedAddAllWithOther(vec, 0, other1)
        testIndexedAddAllWithOther(vec, vec.size, other2)
        testIndexedAddAllWithOther(vec, vec.size / 2, other3)

        testIndexedAddAllWithEmpty(vec, 0, empty1)
        testIndexedAddAllWithEmpty(vec, vec.size, empty2)

        testIndexedAddAllWithSelf(vec, 0)
        testIndexedAddAllWithSelf(vec, vec.size)
        testIndexedAddAllWithSelf(vec, vec.size / 2)

        testIndexedAddAllOutOfBounds(vec, -1, empty1)
        testIndexedAddAllOutOfBounds(vec, vec.size + 1, other1)
    }

    @Test
    fun testInsert() {
        val vec1 = (1 .. 10).toVectorList()
        val vec2 = (-10 .. -1).toVectorList()

        testInsert(vec1, vec2)
        testInsert(vec2, vec1)
        testInsert(vec1, vec1)
        testInsert(vec2, vec2)
    }

    @Test
    fun testResize() {
        val vec = vectorListOf<Int>()

        testResizeByIncrease(vec, 100, 0)
        testResizeByIncrease(vec, 150, 1)
        testResizeByDecrease(vec, 50, 2)
        testInvalidResize(vec, -1, -1)
    }

    @Test
    fun testRemove() {
        val vec = vectorListOf(0, 1, 2, 4, 5, 0, 7, 8, 9, 12)

        testRemoveByElement(vec, 0, true)
        testRemoveByElement(vec, 12, true)
        testRemoveByElement(vec, 7, true)

        testRemoveByElement(vec, -1, false)
        testRemoveByElement(vec, 13, false)
        testRemoveByElement(vec, 7, false)

        assertTrue(0 in vec)
        assertTrue(7 !in vec)
    }

    @Test
    fun testRemoveAll() {
        val vec = (0 until 10).toVectorList()

        val fullyInRange = (2 until 7).toHashSet()
        val partiallyInRange = (-2 until 1).toList()
        val notInRange = (-10 until -1).toSet()

        testRemoveAllByElements(vec, fullyInRange, true)
        testRemoveAllByElements(vec, partiallyInRange, true)
        testRemoveAllByElements(vec, notInRange, false)
    }

    @Test
    fun testDelete() {
        val range = 0 .. 50

        val vec = range.toVectorList()
        val other = (range step 5).toSet()

        testDeleteWithOther(vec, other, 11)
    }

    @Test
    fun testRemoveAt() {
        val vec = (0 .. 50).toVectorList()

        testRemoveByIndex(vec, vec.lastIndex)
        testRemoveByIndex(vec, vec.size / 2)
        testRemoveByIndex(vec, 0)
        testRemoveByIndex(vec, vec.size / 3)
        testRemoveByIndex(vec, 2 * vec.size / 3)

        testRemoveByIndexOutOfBounds(vec, -1)
        testRemoveByIndexOutOfBounds(vec, vec.size)
    }

    @Test
    fun testRemoveFromBack() {
        val vec1 = (1 .. 101).toVectorList()
        testRemoveFromBack(vec1, vec1.size / 2 + 1)

        val vec2 = (1 .. 101).toVectorList()
        testRemoveFromBack(vec2, vec2.size / 2)

        val vec3 = (1 .. 100).toVectorList()
        testRemoveFromBack(vec3, vec3.size / 2 + 1)

        val vec4 = (1 .. 100).toVectorList()
        testRemoveFromBack(vec4, vec4.size / 2)

        val vec5 = vectorListOf<Int>()
        testRemoveFromBackWithNegativeAmount(vec5, -1)
    }

    @Test
    fun testRemoveRange() {
        val vec = (0 until 100).toVectorList()

        testRemoveRange(vec, vec.size / 4, 3 * vec.size / 4)
        testRemoveRange(vec, 0, vec.size / 2)
        testRemoveRange(vec, vec.size / 2, vec.size)

        testRemoveInvalidRange(vec, -1, vec.size)
        testRemoveInvalidRange(vec, 0, vec.size + 1)
    }

    @Test
    fun testRemoveAllOf() {
        val vec1 = vectorListOf(6, 1, 5, 1, 9, 5, 1, 9, 5, 2, 7, 3, 9, 5, 1, 4, 1, 1)
        val oldSize1 = vec1.size

        testRemoveAllByElement(vec1, 0, 0)
        testRemoveAllByElement(vec1, 1, 6)
        testRemoveAllByElement(vec1, 3, 1)
        testRemoveAllByElement(vec1, 5, 4)
        testRemoveAllByElement(vec1, 9, 3)

        assertEquals(vec1.size, oldSize1 - 14)

        val vec2 = vectorListOf(1, 4, 9, 3, 2, 4, 1, 2, 4, 9, 2, 3, 1, 4, 1, 3, 2, 9, 3, 1, 3, 9, 2, 4, 1)
        val oldSize2 = vec2.size

        testRemoveAllByPredicate(vec2, { 0 == it % 2 }, 10)
        testRemoveAllByPredicate(vec2, { 0 == it % 3 }, 9)
        testRemoveAllByPredicate(vec2, { it < 0 }, 0)

        assertEquals(vec2.size, oldSize2 - 19)
        assertTrue(vec2.all{ 1 == it })
    }

    @Test
    fun testRemoveAmount() {
        val vec1 = vectorListOf(6, 1, 5, 1, 9, 5, 1, 9, 5, 2, 7, 3, 9, 5, 1, 4, 1, 1)
        val oldSize1 = vec1.size

        testRemoveAmountByElement(vec1, 0, 1, 0)
        testRemoveAmountByElement(vec1, 1, 4, 4)
        testRemoveAmountByElement(vec1, 5, 7, 4)

        testRemoveNegativeAmountByElement(vec1, 9, -1)

        assertEquals(vec1.size, oldSize1 - 8)

        val vec2 = vectorListOf(1, 4, 9, 3, 2, 4, 1, 2, 4, 9, 2, 3, 1, 4, 1, 3, 2, 9, 3, 1, 3, 9, 2, 4, 1)
        val oldSize2 = vec2.size

        testRemoveAmountByPredicate(vec2, { 0 == it % 2 }, 7, 7)
        testRemoveAmountByPredicate(vec2, { 0 == it % 3 }, 13, 9)
        testRemoveAmountByPredicate(vec2, { it < 0 }, 1, 0)

        testRemoveNegativeAmount(vec2, { it < 0 }, -1)

        assertEquals(vec2.size, oldSize2 - 16)
    }

    @Test
    fun testRetainAll() {
        val range = 1 .. 100

        val vec = range.toVectorList()
        val other = (range step 3).toSet()
        val empty = emptyList<Int>()

        testRetainAllWithOther(vec, other)
        testRetainAllWithSelf(vec)
        testRetainAllWithEmpty(vec, empty)
    }

    @Test
    fun testKeep() {
        val range = 1 .. 100

        val vec = range.toVectorList()
        val other = (range step 3).toSet()
        val empty = emptyList<Int>()

        testKeepWithOther(vec, other)
        testKeepWithSelf(vec)
        testKeepWithEmpty(vec, empty)
    }

    @Test
    fun testClear() {
        val vec = vectorListOf<Int>()

        testClear(vec)
    }

    @Test
    fun testContains() {
        val vec = vectorListOf<Int>()
        val range = 0 until 10

        this.testSearchAfterAdding(vec, range)
        this.testSearchAfterRemovingByElement(vec)
        this.testSearchAfterRemovingByIndex(vec)
        this.testDuplicates(vec)
    }

    private fun testSearchAfterAdding(vec: VectorList<Int>, range: IntRange) {
        vec.addAll(range)

        for (num in range) {
            val found = assertDoesNotThrow{ num in vec }

            assertTrue(found)
        }
    }

    private fun testSearchAfterRemovingByElement(vec: VectorList<Int>) {
        this.testSearchAfterRemovingByElementWith(vec, 0)
        this.testSearchAfterRemovingByElementWith(vec, 5)
        this.testSearchAfterRemovingByElementWith(vec, 9)
        this.testSearchAfterRemovingByElementWith(vec, -1)
    }

    private fun testSearchAfterRemovingByElementWith(vec: VectorList<Int>, value: Int) {
        vec.remove(value)

        val found = assertDoesNotThrow{ value in vec }

        assertFalse(found)
    }

    private fun testSearchAfterRemovingByIndex(vec: VectorList<Int>) {
        this.testSearchAfterRemovingByIndexWith(vec, 0)
        this.testSearchAfterRemovingByIndexWith(vec, vec.lastIndex)
        this.testSearchAfterRemovingByIndexWith(vec, vec.size / 2)
    }

    private fun testSearchAfterRemovingByIndexWith(vec: VectorList<Int>, index: Int) {
        val item = vec.removeAt(index)

        val found = assertDoesNotThrow{ item in vec }

        assertFalse(found)
    }

    private fun testDuplicates(vec: VectorList<Int>) {
        val value = 100

        vec.add(0, value)
        vec.add(vec.size, value)
        vec.add(vec.size / 2, value)

        vec.remove(value)
        val foundAfter1 = assertDoesNotThrow{ value in vec }
        assertTrue(foundAfter1)

        vec.remove(value)
        val foundAfter2 = assertDoesNotThrow{ value in vec }
        assertTrue(foundAfter2)

        vec.remove(value)
        val foundAfter3 = assertDoesNotThrow{ value in vec }
        assertFalse(foundAfter3)
    }

    @Test
    fun testContainsAll() {
        this.testIfContainsNonemptySublists()
        this.testIfContainsOtherCollections()
    }

    private fun testIfContainsNonemptySublists() {
        val vec = (1 .. 20).toVectorList()

        for (leftIndex in vec.indices) {
            val max = vec.size - leftIndex

            for (length in 0 until max) {
                val sub = vec.subList(leftIndex, leftIndex + length)
                val found = assertDoesNotThrow{ vec.containsAll(sub) }

                assertTrue(found)
            }
        }
    }

    private fun testIfContainsOtherCollections() {
        val range = 1 .. 20
        val vec = range.toVectorList()

        val collection1 = (range step 2).toList()
        val collection2 = (range step 3).toHashSet()
        val collection3 = (range step 5).toSet()
        val collection4 = setOf(1, 20, 5, 9)
        val collection5 = setOf(-1, 1, 2, 3, 0)

        this.testContainsAllWith(vec, collection1, true)
        this.testContainsAllWith(vec, collection2, true)
        this.testContainsAllWith(vec, collection3, true)
        this.testContainsAllWith(vec, collection4, true)
        this.testContainsAllWith(vec, collection5, false)
    }

    private fun testContainsAllWith(vec: VectorList<Int>, collection: Collection<Int>, expected: Boolean) {
        val found = assertDoesNotThrow{ vec.containsAll(collection) }

        assertEquals(expected, found)
    }

    @Test
    fun testIndexOf() {
        val range = 0 .. 20
        val vec = (range + range).toVectorList()

        this.testIndexSuccessStates(vec, range)
        this.testIndexFailStates(vec)
    }

    private fun testIndexSuccessStates(vec: VectorList<Int>, range: IntRange) {
        val size = range.count()

        for (index in 0 until size) {
            @Suppress("UnnecessaryVariable", "RedundantSuppression")
            val value = index

            val index1 = assertDoesNotThrow{ vec.indexOf(value) }
            val index2 = assertDoesNotThrow{ vec.index(index + size, value) }

            assertEquals(vec[index1], vec[index2])
            assertNotEquals(index1, index2)

            assertNotEquals(-1, index1)
            assertNotEquals(-1, index2)
        }
    }

    private fun testIndexFailStates(vec: VectorList<Int>) {
        val search1 = assertDoesNotThrow{ vec.indexOf(Int.MIN_VALUE) }
        val search2 = assertDoesNotThrow{ vec.indexOf(Int.MAX_VALUE) }

        assertEquals(-1, search1)
        assertEquals(-1, search2)

        assertFailsWith<IndexOutOfBoundsException>{ vec.index(-1, Int.MIN_VALUE) }
        assertFailsWith<IndexOutOfBoundsException>{ vec.index(vec.size + 1, Int.MIN_VALUE) }
    }

    @Test
    fun testIndexWithPredicate() {
        val range = 0 until 40
        val vec = range.toVectorList()

        this.testIndexWithPredicateSuccessStates(vec)
        this.testIndexWithPredicateFailStates(vec)
    }

    private fun testIndexWithPredicateSuccessStates(vec: VectorList<Int>) {
        val divisor = 4

        for (index in 0 .. vec.size) {
            val foundIndex = assertDoesNotThrow{ vec.index(index) { 0 == it % divisor } }

            assertTrue(foundIndex - index <= divisor)
        }
    }

    private fun testIndexWithPredicateFailStates(vec: VectorList<Int>) {
        val search = assertDoesNotThrow{ vec.index(0) { it < 0 } }

        assertEquals(-1, search)

        assertFailsWith<IndexOutOfBoundsException>{ vec.index(-1) { false } }
        assertFailsWith<IndexOutOfBoundsException>{ vec.index(vec.size + 1) { false } }
    }

    @Test
    fun testLastIndex() {
        val range = 0 .. 20
        val vec = (range + range).toVectorList()

        this.testLastIndexSuccessStates(vec, range)
        this.testLastIndexFailStates(vec)
    }

    private fun testLastIndexSuccessStates(vec: VectorList<Int>, range: IntRange) {
        val size = range.count()

        for (index in 0 until size) {
            @Suppress("UnnecessaryVariable", "RedundantSuppression")
            val value = index

            val index1 = assertDoesNotThrow{ vec.lastIndexOf(value) }
            val index2 = assertDoesNotThrow{ vec.lastIndex(index + size, value) }

            assertEquals(vec[index1], vec[index2])
            assertNotEquals(index1, index2)

            assertNotEquals(-1, index1)
            assertNotEquals(-1, index2)
        }
    }

    private fun testLastIndexFailStates(vec: VectorList<Int>) {
        val search1 = assertDoesNotThrow{ vec.lastIndexOf(Int.MIN_VALUE) }
        val search2 = assertDoesNotThrow{ vec.lastIndexOf(Int.MAX_VALUE) }

        assertEquals(-1, search1)
        assertEquals(-1, search2)

        assertFailsWith<IndexOutOfBoundsException>{ vec.lastIndex(-1, Int.MAX_VALUE) }
        assertFailsWith<IndexOutOfBoundsException>{ vec.lastIndex(vec.size + 1, Int.MAX_VALUE) }
    }

    @Test
    fun testLastIndexWithPredicate() {
        val range = 0 .. 40
        val vec = range.toVectorList()

        this.testLastIndexWithPredicateSuccessStates(vec)
        this.testLastIndexWithPredicateFailStates(vec)
    }

    private fun testLastIndexWithPredicateSuccessStates(vec: VectorList<Int>) {
        val divisor = 4

        for (index in 0 .. vec.size) {
            val foundIndex = assertDoesNotThrow { vec.lastIndex(index) { 0 == it % divisor } }

            assertTrue(index - foundIndex <= divisor)
        }
    }

    private fun testLastIndexWithPredicateFailStates(vec: VectorList<Int>) {
        val search = assertDoesNotThrow{ vec.lastIndex(0) { it < 0 } }

        assertEquals(-1, search)

        assertFailsWith<IndexOutOfBoundsException>{ vec.lastIndex(-1) { false } }
        assertFailsWith<IndexOutOfBoundsException>{ vec.lastIndex(vec.size + 1) { false } }
    }

    @Test
    fun testSwap() {
        val vec = vectorListOf("1", "2", "3", "4", "5")

        this.testSwappingSameElement(vec)
        this.testSwappingDifferentElement(vec)
    }

    private fun testSwappingSameElement(vec: VectorList<String>) {
        val index = 3

        val old1 = vec[index]
        val old2 = vec[index]

        assertSame(vec[index], vec[index])
        assertSame(vec[index], old1)
        assertSame(vec[index], old2)
        assertSame(old1, old2)

        assertDoesNotThrow{ vec.swap(index, index) }

        assertSame(vec[index], vec[index])
        assertSame(old1, vec[index])
        assertSame(old2, vec[index])
        assertSame(old1, old2)
    }

    private fun testSwappingDifferentElement(vec: VectorList<String>) {
        val index1 = 1
        val index2 = 4

        val old1 = vec[index1]
        val old2 = vec[index2]

        assertNotSame(vec[index1], vec[index2])
        assertSame(vec[index1], old1)
        assertSame(vec[index2], old2)
        assertNotSame(old1, old2)

        assertDoesNotThrow{ vec.swap(index1, index2) }

        assertNotSame(vec[index1], vec[index2])
        assertSame(vec[index1], old2)
        assertSame(vec[index2], old1)
        assertNotSame(old1, old2)
    }

    @Test
    fun testIsSorted() {
        val reverseOrdering = reverseOrder<Int>()

        val vec1 = vectorListOf(7, -7, 8, 33, -100, 11, 5, -1011, 35382, 87, -19)
        val vec2 = vec1.asReversed().toVectorList()
        val vec3 = vec1.sorted().toVectorList()
        val vec4 = vec3.asReversed().toVectorList()

        val isSorted1 = assertDoesNotThrow{ vec1.isSorted() }
        val isSorted2 = assertDoesNotThrow{ vec2.isSorted(reverseOrdering) }
        val isSorted3 = assertDoesNotThrow{ vec3.isSorted() }
        val isSorted4 = assertDoesNotThrow{ vec4.isSorted(reverseOrdering) }

        assertFalse(isSorted1)
        assertFalse(isSorted2)
        assertTrue(isSorted3)
        assertTrue(isSorted4)
    }

    @Test
    fun testIsSortedUntil() {
        val reverseOrdering = reverseOrder<Int>()

        this.testIsSortedUntilOnFullySorted(reverseOrdering)
        this.testIsSortedUntilOnPartiallySorted(reverseOrdering)
    }

    private fun testIsSortedUntilOnFullySorted(reverse: Comparator<Int>) {
        val size = 10

        val vec1 = (1 .. size).toVectorList()
        val vec2 = vec1.asReversed().toVectorList()

        val vec1Sorted = assertDoesNotThrow{ vec1.isSortedUntil() }
        val vec2Sorted = assertDoesNotThrow{ vec2.isSortedUntil() }
        val vec1SortedInReverse = assertDoesNotThrow{ vec1.isSortedUntil(reverse) }
        val vec2SortedInReverse = assertDoesNotThrow{ vec2.isSortedUntil(reverse) }

        assertEquals(size, vec1Sorted)
        assertEquals(1, vec2Sorted)
        assertEquals(1, vec1SortedInReverse)
        assertEquals(size, vec2SortedInReverse)
    }

    private fun testIsSortedUntilOnPartiallySorted(reverse: Comparator<Int>) {
        val max = 10
        val range = 1 .. max

        val forward = range.asSequence()
        val backward = range.reversed().asSequence()

        val forward2Backward = (forward + backward.drop(1)).toVectorList()
        val backward2Forward = (backward + forward.drop(1)).toVectorList()

        val f2bSorted = assertDoesNotThrow{ forward2Backward.isSortedUntil() }
        val b2fSorted = assertDoesNotThrow{ forward2Backward.isSortedUntil(reverse) }
        val f2bSortedInReverse = assertDoesNotThrow{ backward2Forward.isSortedUntil() }
        val b2fSortedInReverse = assertDoesNotThrow{ backward2Forward.isSortedUntil(reverse) }

        assertEquals(max, f2bSorted)
        assertEquals(1, b2fSorted)
        assertEquals(1, f2bSortedInReverse)
        assertEquals(max, b2fSortedInReverse)
    }

    @Test
    fun testSortWith() {
        val vec = vectorListOf(1011, -10, 56642, 43432, -48397, 432, 86, -9974)
        val comp = { left: Int, right: Int -> left - right }

        val newList = assertDoesNotThrow{ vec.sortedWith(comp) }
        assertDoesNotThrow{ vec.sortWith(comp) }

        assertTrue(vec.isSorted(comp))
        assertTrue(newList.isSorted(comp))

        assertEquals(vec, newList)
    }

    @Test
    fun testEquals() {
        val range = 1 .. 10

        val vec = range.toVectorList()
        val other1 = range.toList()
        val other2 = (range step 2).toList()
        val other3 = (range step 5).toList()

        testEquals(vec, other1, true)
        testEquals(other1, vec, true)

        testEquals(vec, other2, false)
        testEquals(vec, other3, false)
        testEquals(other2, vec, false)
        testEquals(other3, vec, false)
    }

    @Test
    fun testHashCode() {
        val range = 1 .. 100

        val vec1 = (range step 5).toVectorList()
        val vec2 = (range step 4).toVectorList()
        val vec3 = (range step 5).toVectorList()

        testHashCode(vec1, vec2)
        testHashCode(vec2, vec3)
        testHashCode(vec3, vec1)
    }

    @Test
    fun testIsPermutationOf() {
        this.testReversedPermutation()
        this.testDifferentSizedPermutations()
        this.testArbitraryPermutations()
        this.testDifferentListsForPermutation()
    }

    private fun testReversedPermutation() {
        val range = 1 .. 1000000

        val vec1 = range.toVectorList()
        val vec2 = range.reversed().toVectorList()

        val result1 = assertDoesNotThrow{ vec1.isPermutationOf(vec2) }
        val result2 = assertDoesNotThrow{ vec2.isPermutationOf(vec1) }

        assertEquals(result1, result2)
    }

    private fun testDifferentSizedPermutations() {
        val vec1 = vectorListOf(1, 2, 3, 4, 5)
        val vec2 = vectorListOf(1, 2, 4, 5)

        val result = assertDoesNotThrow{ vec1.isPermutationOf(vec2) }

        assertFalse(result)
    }

    private fun testArbitraryPermutations() {
        val vec1 = vectorListOf(1, 5, 3, 2, 4)
        val vec2 = vectorListOf(5, 4, 1, 3, 2)

        val result1 = assertDoesNotThrow{ vec1.isPermutationOf(vec2) }
        val result2 = assertDoesNotThrow{ vec2.isPermutationOf(vec1) }

        assertEquals(result1, result2)
    }

    private fun testDifferentListsForPermutation() {
        val vec1 = vectorListOf(1, 2)
        val vec2 = vectorListOf(3, 4)
        val vec3 = vectorListOf(2, 3)

        val result12 = assertDoesNotThrow{ vec1.isPermutationOf(vec2) }
        val result13 = assertDoesNotThrow{ vec1.isPermutationOf(vec3) }
        val result21 = assertDoesNotThrow{ vec2.isPermutationOf(vec1) }
        val result23 = assertDoesNotThrow{ vec2.isPermutationOf(vec3) }
        val result31 = assertDoesNotThrow{ vec3.isPermutationOf(vec1) }
        val result32 = assertDoesNotThrow{ vec3.isPermutationOf(vec2) }

        assertFalse(result12)
        assertFalse(result13)
        assertFalse(result21)
        assertFalse(result23)
        assertFalse(result31)
        assertFalse(result32)
    }

    @Test
    fun testNext() {
        this.testNextOnTriviallySized()

        val vec = (1 .. 8).toVectorList()

        var count = 0
        val factorial = vec.fold(1) { curr, acc -> curr * acc }

        do {
            ++count
        } while (assertDoesNotThrow{ vec.next() })

        assertEquals(factorial, count)
    }

    private fun testNextOnTriviallySized() {
        val zero = vectorListOf<Int>()
        val one = vectorListOf(0)

        val isGreater0 = assertDoesNotThrow{ zero.next() }
        val isGreater1 = assertDoesNotThrow{ one.next() }

        assertFalse(isGreater0)
        assertFalse(isGreater1)
    }

    @Test
    fun testPrev() {
        this.testPrevOnTriviallySized()

        val vec = (1 .. 8).reversed().toVectorList()

        var count = 0
        val factorial = vec.fold(1) { curr, acc -> curr * acc }

        do {
            ++count
        } while (vec.prev())

        assertEquals(factorial, count)
    }

    private fun testPrevOnTriviallySized() {
        val zero = vectorListOf<Int>()
        val one = vectorListOf(0)

        val isLess0 = assertDoesNotThrow{ zero.prev() }
        val isLess1 = assertDoesNotThrow{ one.prev() }

        assertFalse(isLess0)
        assertFalse(isLess1)
    }

    @Test
    fun testSeparationPoint() {
        val max = 100
        val range = 1 .. max

        for (denominator in 2 .. max) {
            val copy1 = range.toVectorList()
            val copy2 = range.toVectorList()

            val condition = { num: Int -> 0 == num % denominator }

            val separationPoint1 = copy1.separate(condition)
            val separationPoint2 = copy2.stableSeparate(condition)

            val separationPoint3 = assertDoesNotThrow{ copy1.separationPoint(condition) }
            val separationPoint4 = assertDoesNotThrow{ copy2.separationPoint(condition) }

            assertNotNull(separationPoint3)
            assertNotNull(separationPoint4)

            assertEquals(separationPoint1, separationPoint2)
            assertEquals(separationPoint2, separationPoint3)
            assertEquals(separationPoint3, separationPoint4)
        }
    }

    @Test
    fun testSeparate() {
        val max = 100
        val vec = (1 .. max).toVectorList()

        for (denominator in 2 .. max) {
            val condition = { num: Int -> 0 == num % denominator }
            val separationPoint = assertDoesNotThrow{ vec.separate(condition) }

            for (index in 0 until separationPoint) {
                val result = condition(vec[index])

                assertTrue(result)
            }

            for (index in separationPoint until vec.size) {
                val result = condition(vec[index])

                assertFalse(result)
            }
        }
    }

    @Test
    fun testStableSeparate() {
        val max = 100
        val vec = (1 .. max).toVectorList()

        for (denominator in 2 .. max) {
            val copy = vec.toVectorList()
            val condition = { num: Int -> 0 == num % denominator }

            val separationPoint = assertDoesNotThrow{ copy.stableSeparate(condition) }

            this.checkIfPartitioned(copy, condition, separationPoint)
            this.checkIfStablyPartitioned(vec, copy, condition)
        }
    }

    private fun checkIfPartitioned(vec: VectorList<Int>, condition: (Int) -> Boolean, separationPoint: Int) {
        for (index in 0 until separationPoint) {
            val result = condition(vec[index])

            assertTrue(result)
        }

        for (index in separationPoint until vec.size) {
            val result = condition(vec[index])

            assertFalse(result)
        }
    }

    private fun checkIfStablyPartitioned(vec: VectorList<Int>, copy: VectorList<Int>, condition: (Int) -> Boolean) {
        for ((index1, item1) in vec.withIndex()) {
            for ((index2, item2) in vec.withIndex()) {
                if (index1 == index2) {
                    continue
                }

                this.checkForStablePositions(copy, condition, index1, item1, index2, item2)
            }
        }
    }

    private fun checkForStablePositions(
        copy: VectorList<Int>,
        condition: (Int) -> Boolean,
        index1: Int,
        item1: Int,
        index2: Int,
        item2: Int
    ) {
        val foundIndex1 = copy.indexOf(item1)
        val foundIndex2 = copy.indexOf(item2)

        val success1 = condition(item1)
        val success2 = condition(item2)

        if (index1 < index2) {
            when {
                success1 -> assertTrue(foundIndex1 < foundIndex2)
                !success1 && success2 -> assertTrue(foundIndex1 > foundIndex2)
            }
        }
        else {
            when {
                success2 -> assertTrue(foundIndex1 > foundIndex2)
                success1 && !success2 -> assertTrue(foundIndex1 < foundIndex2)
            }
        }
    }

    @Test
    fun testIntersperse() {
        val vec1 = (1 .. 10).toVectorList()
        val vec2 = (1 .. 11).toVectorList()
        val vec3 = vectorListOf<Int>()
        val vec4 = vectorListOf(0)
        val vec5 = vectorListOf(0, 0)

        this.testIntersperseOn(vec1)
        this.testIntersperseOn(vec2)
        this.testIntersperseOn(vec3)
        this.testIntersperseOn(vec4)
        this.testIntersperseOn(vec5)
    }

    private fun testIntersperseOn(vec: VectorList<Int>) {
        val copy = vec.toList().iterator()
        val separator = -1

        assertDoesNotThrow{ vec.intersperse(separator) }

        var atSeparator = false

        for (item in vec) {
            if (atSeparator) {
                assertEquals(separator, item)
            }
            else {
                assertEquals(copy.next(), item)
            }

            atSeparator = !atSeparator
        }

        assertNotEquals(separator, vec.firstOrNull())
        assertNotEquals(separator, vec.lastOrNull())
    }

    @Test
    fun testCompare() {
        val vec1 = vectorListOf(0, 55, -6, 42, 1000, -999, 23, 689, -1)

        val vec2 = vec1.toVectorList()
        vec2.next()

        val vec3 = vec1.toVectorList()
        vec3.prev()

        val vec4 = vec3.toVectorList()
        vec2.next()

        testLessThanComparison(vec1, vec2)
        testLessThanComparison(vec3, vec1)
        testLessThanComparison(vec4, vec2)

        testEqualComparison(vec1, vec1)
        testEqualComparison(vec2, vec2)
        testEqualComparison(vec3, vec3)
        testEqualComparison(vec4, vec4)

        testGreaterThanComparison(vec2, vec1)
        testGreaterThanComparison(vec1, vec3)
        testGreaterThanComparison(vec2, vec4)
    }

    @Test
    fun testToString() {
        val vec1 = vectorListOf<Int>()
        testToString(vec1, "[]")

        val vec2 = vectorListOf(1000)
        testToString(vec2, "[1000]")

        val vec3 = vectorListOf(-1, 4, 0, -7, 16, -11)
        testToString(vec3, "[-1, 4, 0, -7, 16, -11]")
    }
}

class VectorIteratorTest {
    @Test
    fun testConstructor() {
        val empty = vectorListOf<Int>()
        val nonempty = vectorListOf(1)

        assertDoesNotThrow{ empty.iterator() }
        assertDoesNotThrow{ nonempty.iterator() }
    }

    @Test
    fun testHasNext() {
        this.testHasNextIfEmpty()
        this.testHasNextIfNonempty()
        this.testConcurrentModificationOnHasNext()
    }

    private fun testHasNextIfEmpty() {
        val iter = vectorListOf<Int>().iterator()

        val result = assertDoesNotThrow{ iter.hasNext() }

        assertFalse(result)
    }

    private fun testHasNextIfNonempty() {
        val max = 74
        val vec = (0 until max).toVectorList()
        val iter = vec.iterator()

        repeat(max) {
            val midResult = assertDoesNotThrow{ iter.hasNext() }
            assertTrue(midResult)

            assertDoesNotThrow{ iter.next() }
        }

        val endResult = assertDoesNotThrow{ iter.hasNext() }
        assertFalse(endResult)
    }

    private fun testConcurrentModificationOnHasNext() {
        val list = (1 .. 17).toVectorList()
        val iter = list.iterator()

        assertDoesNotThrow{ iter.hasNext() }

        list.clear()

        assertFailsWith<ConcurrentModificationException>{ iter.hasNext() }
    }

    @Test
    fun testNext() {
        this.testNextIfEmpty()
        this.testNextIfNonempty()
        this.testConcurrentModification()
    }

    private fun testNextIfEmpty() {
        val vec = vectorListOf<Int>()
        val iter = vec.iterator()

        assertFailsWith<NoSuchElementException>{ iter.next() }
    }

    private fun testNextIfNonempty() {
        val vec = (1 .. 50 step 10).toVectorList()
        val iter = vec.iterator()

        for (index in vec.indices) {
            val item = assertDoesNotThrow{ iter.next() }

            assertEquals(vec[index], item)
        }

        assertFailsWith<NoSuchElementException>{ iter.next() }
    }

    private fun testConcurrentModification() {
        val vec = (0 .. 30 step 3).toVectorList()
        val iter = vec.iterator()

        assertDoesNotThrow{ iter.next() }

        vec.add(0, -1)

        assertFailsWith<ConcurrentModificationException>{ iter.next() }
    }

    @Test
    fun testRemove() {
        val vec = (0 .. 100 step 5).toVectorList()
        val iter = vec.iterator()

        assertFailsWith<IllegalStateException>{ iter.remove() }

        val initialSize = vec.size
        var amountRemoved = 0

        var state = false

        while (iter.hasNext()) {
            iter.next()

            if (state) {
                assertDoesNotThrow{ iter.remove() }
                assertFailsWith<IllegalStateException>{ iter.remove() }

                ++amountRemoved
            }

            state = !state
        }

        assertTrue(vec.all{ 0 == it % 10 })
        assertEquals(initialSize - amountRemoved, vec.size)

        assertDoesNotThrow{ iter.remove() }
        assertFailsWith<IllegalStateException>{ iter.remove() }

        assertTrue(100 !in vec)

        vec.add(12)
        assertFailsWith<ConcurrentModificationException>{ iter.remove() }
    }
}

class VectorListIteratorTest {
    @Test
    fun testConstructor() {
        this.testConstructorWithEmptyVector()
        this.testConstructorWithFilledVector()
    }

    private fun testConstructorWithEmptyVector() {
        val vec = vectorListOf<Int>()

        assertDoesNotThrow{ vec.listIterator() }
        assertDoesNotThrow{ vec.listIterator(0) }

        assertFailsWith<IndexOutOfBoundsException>{ vec.listIterator(1) }
    }

    private fun testConstructorWithFilledVector() {
        val vec = vectorListOf(1, 2, 3, 4, 5, 6, 7, 8)

        for (index in 0 .. vec.size) {
            assertDoesNotThrow{ vec.listIterator(index) }
        }

        assertFailsWith<IndexOutOfBoundsException>{ vec.listIterator(-1) }
        assertFailsWith<IndexOutOfBoundsException>{ vec.listIterator(vec.size + 1) }
    }

    @Test
    fun testPreviousIndex() {
        val vec = (1 .. 13).toVectorList()

        for (index in 1 .. vec.lastIndex) {
            val iter = vec.listIterator(index)
            val index1 = assertDoesNotThrow{ iter.previousIndex() }
            assertEquals(index - 1, index1)

            iter.previous()
            val index2 = assertDoesNotThrow{ iter.previousIndex() }
            assertEquals(index - 2, index2)

            iter.next()
            val index3 = assertDoesNotThrow{ iter.previousIndex() }
            assertEquals(index - 1, index3)

            iter.next()
            val index4 = assertDoesNotThrow{ iter.previousIndex() }
            assertEquals(index, index4)
        }
    }

    @Test
    fun testNextIndex() {
        val vec = (1 .. 13).toVectorList()

        for (index in 1 .. vec.lastIndex) {
            val iter = vec.listIterator(index)
            val index1 = assertDoesNotThrow{ iter.nextIndex() }
            assertEquals(index, index1)

            iter.next()
            val index2 = assertDoesNotThrow{ iter.nextIndex() }
            assertEquals(index + 1, index2)

            iter.previous()
            val index3 = assertDoesNotThrow{ iter.nextIndex() }
            assertEquals(index, index3)

            iter.previous()
            val index4 = assertDoesNotThrow{ iter.nextIndex() }
            assertEquals(index - 1, index4)
        }
    }

    @Test
    fun testHasPrevious() {
        val vec = vectorListOf(-19, 100, 75, 14, -32, -27, 11, Int.MAX_VALUE, Int.MIN_VALUE)

        this.testAtBeginningForHasPrevious(vec)
        this.testAfterForHasPrevious(vec)
    }

    private fun testAtBeginningForHasPrevious(vec: VectorList<Int>) {
        val iter = vec.listIterator(0)

        val result = assertDoesNotThrow{ iter.hasPrevious() }

        assertFalse(result)
    }

    private fun testAfterForHasPrevious(vec: VectorList<Int>) {
        for (index in 1 .. vec.size) {
            val iter = vec.listIterator(index)

            val result = assertDoesNotThrow{ iter.hasPrevious() }

            assertTrue(result)
        }
    }

    @Test
    fun testHasNext() {
        val vec = vectorListOf(Int.MIN_VALUE, Int.MAX_VALUE, 0, 14, 1010, -65, -33, 2)

        this.testAtEndForHasNext(vec)
        this.testBeforeForHasNext(vec)
    }

    private fun testAtEndForHasNext(vec: VectorList<Int>) {
        val iter = vec.listIterator(vec.size)

        val result = assertDoesNotThrow{ iter.hasNext() }

        assertFalse(result)
    }

    private fun testBeforeForHasNext(vec: VectorList<Int>) {
        for (index in 0 .. vec.lastIndex) {
            val iter = vec.listIterator(index)

            val result = assertDoesNotThrow{ iter.hasNext() }

            assertTrue(result)
        }
    }

    @Test
    fun testPrevious() {
        val vec = (1 .. 15).toVectorList()

        for (index in 0 .. vec.size) {
            val iter = vec.listIterator(index)

            this.testPreviousWithIndex(iter, index, vec)
        }
    }

    private fun testPreviousWithIndex(iter: MutableListIterator<Int>, startIndex: Int, vec: VectorList<Int>) {
        var index = startIndex - 1

        while (iter.hasPrevious()) {
            val item = assertDoesNotThrow{ iter.previous() }

            assertEquals(vec[index], item)

            --index
        }

        assertFailsWith<NoSuchElementException>{ iter.previous() }
    }

    @Test
    fun testNext() {
        val vec = (1 .. 15).toVectorList()

        for (index in 0 .. vec.size) {
            val iter = vec.listIterator(index)

            this.testNextWithIndex(iter, index, vec)
        }
    }

    private fun testNextWithIndex(iter: MutableListIterator<Int>, startIndex: Int, vec: VectorList<Int>) {
        var index = startIndex

        while (iter.hasNext()) {
            val item = assertDoesNotThrow{ iter.next() }

            assertEquals(vec[index], item)

            ++index
        }

        assertFailsWith<NoSuchElementException>{ iter.next() }
    }

    @Test
    fun testSet() {
        this.testSetWithNext()
        this.testSetWithPrevious()
        this.testSetAtEnds()
        this.testSetAfterRemove()
    }

    private fun testSetWithNext() {
        val oddMax = 101
        val vec = (1 .. oddMax).toVectorList()
        val iter = vec.listIterator()
        val isEven = { num: Int -> 0 == num % 2 }
        val oldSize = vec.size

        assertFailsWith<IllegalStateException>{ iter.set(0) }

        while (iter.hasNext()) {
            val item = iter.next()

            if (isEven(item)) {
                assertDoesNotThrow{ iter.set(-1) }
            }
        }

        assertEquals(oldSize, vec.size)
        assertTrue(vec.none(isEven))

        vec.add(1)
        assertFailsWith<ConcurrentModificationException>{ iter.set(-1) }
    }

    private fun testSetWithPrevious() {
        val evenMax = 100
        val vec = (1 .. evenMax).toVectorList()
        val iter = vec.listIterator(vec.size)
        val isOdd = { num: Int -> 1 == num % 2 }
        val oldSize = vec.size

        assertFailsWith<IllegalStateException>{ iter.set(0) }

        while (iter.hasPrevious()) {
            val item = iter.previous()

            if (isOdd(item)) {
                assertDoesNotThrow{ iter.set(-1) }
            }
        }

        assertEquals(oldSize, vec.size)
        assertTrue(vec.none(isOdd))

        vec.add(1)
        assertFailsWith<ConcurrentModificationException>{ iter.set(-1) }
    }

    private fun testSetAtEnds() {
        val vec = 0.replicate(10).toVectorList()

        val iter1 = vec.listIterator(0)
        iter1.next()

        val iter2 = vec.listIterator(vec.size)
        iter2.previous()

        val value1 = 1
        val value2 = 2

        assertNotEquals(value1, vec.first())
        assertNotEquals(value2, vec.last())

        assertDoesNotThrow{ iter1.set(value1) }
        assertDoesNotThrow{ iter2.set(value2) }

        assertEquals(value1, vec.first())
        assertEquals(value2, vec.last())

        vec.removeLast()
        assertFailsWith<ConcurrentModificationException>{ iter1.set(-1) }
        assertFailsWith<ConcurrentModificationException>{ iter2.set(-1) }
    }

    private fun testSetAfterRemove() {
        val vec = (1 .. 150).toVectorList()
        val iter = vec.listIterator()

        repeat(vec.size / 2) {
            iter.next()
        }
        iter.remove()
        assertFailsWith<IllegalStateException>{ iter.set(-1) }

        repeat(vec.size / 4) {
            iter.previous()
        }
        iter.remove()
        assertFailsWith<IllegalStateException>{ iter.set(-1) }
    }

    @Test
    fun testRemove() {
        this.testRemoveWithNext()
        this.testRemoveWithPrevious()
        this.testRemoveAfterAdding()
    }

    private fun testRemoveWithNext() {
        val vec = (0 .. 100).toVectorList()
        val iter = vec.listIterator()
        val isOdd = { num: Int -> 1 == num % 2 }
        val oldSize = vec.size

        assertFailsWith<IllegalStateException>{ iter.remove() }

        while (iter.hasNext()) {
            val item = iter.next()

            if (isOdd(item)) {
                assertDoesNotThrow{ iter.remove() }
                assertFailsWith<IllegalStateException>{ iter.remove() }
            }
        }

        assertTrue(oldSize > vec.size)
        assertTrue(vec.none(isOdd))

        assertDoesNotThrow{ iter.remove() }
        assertTrue(100 !in vec)
        assertFailsWith<IllegalStateException>{ iter.remove() }

        vec.add(vec.size / 2, Int.MAX_VALUE)
        assertFailsWith<ConcurrentModificationException>{ iter.remove() }
    }

    private fun testRemoveWithPrevious() {
        val vec = (1 .. 101).toVectorList()
        val iter = vec.listIterator(vec.size)
        val isEven = { num: Int -> 0 == num % 2 }
        val oldSize = vec.size

        assertFailsWith<IllegalStateException>{ iter.remove() }

        while (iter.hasPrevious()) {
            val item = iter.previous()

            if (isEven(item)) {
                assertDoesNotThrow{ iter.remove() }
                assertFailsWith<IllegalStateException>{ iter.remove() }
            }
        }

        assertTrue(oldSize > vec.size)
        assertTrue(vec.none(isEven))

        assertDoesNotThrow{ iter.remove() }
        assertTrue(1 !in vec)
        assertFailsWith<IllegalStateException>{ iter.remove() }

        vec.retainAll(1 .. 10)
        assertFailsWith<ConcurrentModificationException>{ iter.remove() }
    }

    private fun testRemoveAfterAdding() {
        val vec = (1 .. 50).toVectorList()
        val iter = vec.listIterator()
        val amountToAdd = 4

        repeat(vec.size / 2) {
            iter.next()
        }

        val itemAfterNext = iter.next()

        repeat(amountToAdd) {
            iter.add(-1 - it)
        }

        assertDoesNotThrow{ iter.remove() }
        assertFailsWith<IllegalStateException>{ iter.remove() }

        repeat(vec.size / 4) {
            iter.previous()
        }

        val itemAfterPrev = iter.previous()

        repeat(amountToAdd) {
            iter.add(-1 - it)
        }

        assertDoesNotThrow{ iter.remove() }
        assertFailsWith<IllegalStateException>{ iter.remove() }

        assertFalse(itemAfterNext in vec)
        assertFalse(itemAfterPrev in vec)
    }

    @Test
    fun testAdd() {
        val vec = (0 until 100).toVectorList()

        this.testAddInMiddleOfListIterator(vec)
        this.testAddAtEndsOfListIterator(vec)
    }

    private fun testAddInMiddleOfListIterator(vec: VectorList<Int>) {
        val iter = vec.listIterator()

        repeat(3 * vec.size / 4) {
            iter.next()
        }
        this.testAddWithIterator(vec, iter, -7)

        repeat(vec.size / 2) {
            iter.previous()
        }
        this.testAddWithIterator(vec, iter, -11)

        iter.remove()
        this.testAddWithIterator(vec, iter, -3)

        vec.removeFromBack(5)
        assertFailsWith<ConcurrentModificationException>{ iter.add(0) }
    }

    private fun testAddWithIterator(vec: VectorList<Int>, iter: MutableListIterator<Int>, item: Int) {
        assertDoesNotThrow{ iter.add(item) }
        assertEquals(vec[iter.nextIndex()], item)
        assertEquals(iter.next(), item)
    }

    private fun testAddAtEndsOfListIterator(vec: VectorList<Int>) {
        val edgeItem = -27

        val begin = vec.listIterator(0)
        assertDoesNotThrow{ begin.add(edgeItem) }

        val end = vec.listIterator(vec.size)
        assertDoesNotThrow{ end.add(edgeItem) }

        assertEquals(edgeItem, vec.first())
        assertEquals(edgeItem, vec.last())
    }
}

@Suppress("SameParameterValue")
class VectorSublistTest {
    @Test
    fun testConstructor() {
        this.testConstructionOnEmptyList()
        this.testConstructionOnNonemptyList()
    }

    private fun testConstructionOnEmptyList() {
        val vec = vectorListOf<Int>()

        assertDoesNotThrow{ vec.subList(0, 0) }
        assertFailsWith<IndexOutOfBoundsException>{ vec.subList(-1, vec.size) }
        assertFailsWith<IndexOutOfBoundsException>{ vec.subList(0, vec.size + 1) }
    }

    private fun testConstructionOnNonemptyList() {
        val vec = (1 .. 10).toVectorList()

        for (startIndex in 0 until vec.size) {
            for (endIndex in 0 .. vec.size) {
                this.testSublistConstruction(vec, startIndex, endIndex)
            }
        }

        assertFailsWith<IndexOutOfBoundsException>{ vec.subList(-1, vec.size) }
        assertFailsWith<IndexOutOfBoundsException>{ vec.subList(0, vec.size + 1) }
    }

    private fun testSublistConstruction(vec: VectorList<Int>, startIndex: Int, endIndex: Int) {
        if (startIndex > endIndex) {
            assertFailsWith<IllegalArgumentException>{ vec.subList(startIndex, endIndex) }
        }
        else {
            assertDoesNotThrow{ vec.subList(startIndex, endIndex) }
        }
    }

    @Test
    fun testIsRandomAccess() {
        val vec = (1 .. 10).toVectorList()
        val sub = vec.subList(3, 7)

        val vecResult = assertDoesNotThrow{ vec.isRandomAccess }
        val subResult = assertDoesNotThrow{ sub.isRandomAccess }

        assertEquals(vecResult, subResult)
    }

    @Test
    fun testWithIndex() {
        val vec = (1 .. 10).toVectorList()

        for (startIndex in 0 until vec.size) {
            for (endIndex in startIndex .. vec.size) {
                val sub = vec.subList(startIndex, endIndex)

                this.testIterationWithIndex(sub)
            }
        }
    }

    private fun testIterationWithIndex(sub: MutableList<Int>) {
        for (initialIndex in 0 until sub.size) {
            val indexed = assertDoesNotThrow{ sub.withIndex(initialIndex) }

            this.testIterationWithIndices(indexed, sub, initialIndex)
        }
    }

    private fun testIterationWithIndices(indexed: Iterable<IndexedValue<Int>>, sub: MutableList<Int>, initialIndex: Int) {
        for ((currentIndex, item) in indexed) {
            assertEquals(item, sub[currentIndex - initialIndex])

            if (currentIndex >= sub.size) {
                assertFailsWith<IndexOutOfBoundsException>{ sub[currentIndex] }
            }
            else {
                assertDoesNotThrow{ sub[currentIndex] }
            }
        }
    }

    @Test
    fun testSize() {
        val vec = (1 .. 500).toVectorList()
        val startIndex = 43
        val endIndex = 98

        val sub = vec.subList(startIndex, endIndex)
        val oldSize = sub.size
        assertEquals(oldSize, endIndex - startIndex)

        val added = this.testSizeAfterAdding(sub)
        val removed = this.testSizeAfterRemoving(sub)

        assertEquals(oldSize + added - removed, sub.size)
    }

    private fun testSizeAfterAdding(sub: MutableList<Int>): Int {
        val newCollection = (1 .. 500).toSet()
        val oldSize = sub.size

        sub.addAll(newCollection)

        assertEquals(oldSize + newCollection.size, sub.size)

        return newCollection.size
    }

    private fun testSizeAfterRemoving(sub: MutableList<Int>): Int {
        val amountToRemove = 300
        val oldSize = sub.size

        sub.removeFromBack(amountToRemove)

        assertEquals(oldSize - amountToRemove, sub.size)

        return amountToRemove
    }

    @Test
    fun testIsEmpty() {
        val vec = (1 .. 50).toVectorList()

        this.testIsEmptyOnConstruction(vec)
        this.testIsEmptyOnModification(vec)
    }

    private fun testIsEmptyOnConstruction(vec: VectorList<Int>) {
        for (startIndex in 0 until vec.size) {
            for (endIndex in startIndex .. vec.size) {
                val sub = vec.subList(startIndex, endIndex)
                val empty = assertDoesNotThrow{ sub.isEmpty() }

                if (startIndex == endIndex) {
                    assertTrue(empty)
                }
                else {
                    assertFalse(empty)
                }
            }
        }
    }

    private fun testIsEmptyOnModification(vec: VectorList<Int>) {
        val startIndex = 16

        val sub = vec.subList(startIndex, startIndex)
        val onConstruct = assertDoesNotThrow{ sub.isEmpty() }
        assertTrue(onConstruct)

        sub.add(-1)
        val onAdd = assertDoesNotThrow{ sub.isEmpty() }
        assertFalse(onAdd)

        sub.removeLast()
        val onRemove = assertDoesNotThrow{ sub.isEmpty() }
        assertTrue(onRemove)
    }

    @Test
    fun testAtLeast() {
        val vec = (1 .. 20).toVectorList()

        val startIndex = 5
        val endIndex = 15
        val size = endIndex - startIndex

        val sub1 = vec.subList(startIndex, endIndex - 1)
        val sub2 = vec.subList(startIndex, endIndex)
        val sub3 = vec.subList(startIndex, endIndex + 1)

        val result1 = assertDoesNotThrow{ sub1.atLeast(size) }
        val result2 = assertDoesNotThrow{ sub2.atLeast(size) }
        val result3 = assertDoesNotThrow{ sub3.atLeast(size) }

        assertFalse(result1)
        assertTrue(result2)
        assertTrue(result3)
    }

    @Test
    fun testAtMost() {
        val vec = (1 .. 20).toVectorList()

        val startIndex = 5
        val endIndex = 15
        val size = endIndex - startIndex

        val sub1 = vec.subList(startIndex, endIndex - 1)
        val sub2 = vec.subList(startIndex, endIndex)
        val sub3 = vec.subList(startIndex, endIndex + 1)

        val result1 = assertDoesNotThrow{ sub1.atMost(size) }
        val result2 = assertDoesNotThrow{ sub2.atMost(size) }
        val result3 = assertDoesNotThrow{ sub3.atMost(size) }

        assertTrue(result1)
        assertTrue(result2)
        assertFalse(result3)
    }

    @Test
    fun testExactly() {
        val vec = (1 .. 20).toVectorList()

        val startIndex = 5
        val endIndex = 15
        val size = endIndex - startIndex

        val sub1 = vec.subList(startIndex, endIndex - 1)
        val sub2 = vec.subList(startIndex, endIndex)
        val sub3 = vec.subList(startIndex, endIndex + 1)

        val result1 = assertDoesNotThrow{ sub1.exactly(size) }
        val result2 = assertDoesNotThrow{ sub2.exactly(size) }
        val result3 = assertDoesNotThrow{ sub3.exactly(size) }

        assertFalse(result1)
        assertTrue(result2)
        assertFalse(result3)
    }

    @Test
    fun testTryFirst() {
        val vec = (1 .. 10).toVectorList()

        this.testTryFirstOnEmpty(vec)
        this.testTryFirstOnNonempty(vec)
    }

    private fun testTryFirstOnEmpty(vec: VectorList<Int>) {
        val index = 7
        val sub = vec.subList(index, index)

        val result = assertDoesNotThrow{ sub.tryFirst() }

        assertFailsWith<NoSuchElementException>{ result.getOrThrow() }
    }

    @Suppress("ConvertTwoComparisonsToRangeCheck")
    private fun testTryFirstOnNonempty(vec: VectorList<Int>) {
        val startIndex = 2
        val endIndex = 8

        val sub = vec.subList(startIndex, endIndex)

        val notPredicated = assertDoesNotThrow{ sub.tryFirst() }
        val itemWithNoPredicate = assertDoesNotThrow{ notPredicated.getOrThrow() }
        assertEquals(itemWithNoPredicate, vec[startIndex])

        val success = assertDoesNotThrow{ sub.tryFirst{ 0 == it % 4 } }
        val successItem = assertDoesNotThrow{ success.getOrThrow() }
        val vecIndex = vec.indexOf(successItem)
        assertTrue(startIndex < vecIndex && vecIndex < endIndex)

        val max = vec.max()
        val failed = assertDoesNotThrow{ sub.tryFirst{ it > max } }
        assertFailsWith<NoSuchElementException>{ failed.getOrThrow() }
    }

    @Test
    fun testTryLast() {
        val vec = (1 .. 10).toVectorList()

        this.testTryLastOnEmpty(vec)
        this.testTryLastOnNonempty(vec)
    }

    private fun testTryLastOnEmpty(vec: VectorList<Int>) {
        val index = 1
        val sub = vec.subList(index, index)

        val result = assertDoesNotThrow{ sub.tryLast() }

        assertFailsWith<NoSuchElementException>{ result.getOrThrow() }
    }

    @Suppress("ConvertTwoComparisonsToRangeCheck")
    private fun testTryLastOnNonempty(vec: VectorList<Int>) {
        val startIndex = 1
        val endIndex = 6

        val sub = vec.subList(startIndex, endIndex)

        val notPredicated = assertDoesNotThrow{ sub.tryLast() }
        val itemWithNoPredicate = assertDoesNotThrow{ notPredicated.getOrThrow() }
        assertEquals(itemWithNoPredicate, vec[endIndex - 1])

        val success = assertDoesNotThrow{ sub.tryLast{ 0 == it % 4 } }
        val successItem = assertDoesNotThrow{ success.getOrThrow() }
        val vecIndex = vec.indexOf(successItem)
        assertTrue(startIndex < vecIndex && vecIndex < endIndex)

        val max = vec.max()
        val failed = assertDoesNotThrow{ sub.tryLast{ it > max } }
        assertFailsWith<NoSuchElementException>{ failed.getOrThrow() }
    }

    @Test
    fun testGet() {
        val vec = (1 .. 100).toVectorList()

        for (startIndex in 0 until vec.size) {
            for (endIndex in startIndex .. vec.size) {
                val sub = vec.subList(startIndex, endIndex)

                this.testItemGet(vec, sub, startIndex)

                @Suppress("KotlinConstantConditions")
                run {
                    assertFailsWith<IndexOutOfBoundsException>{ sub[-1] }
                    assertFailsWith<IndexOutOfBoundsException>{ sub[sub.size] }
                }
            }
        }
    }

    private fun testItemGet(vec: VectorList<Int>, sub: MutableList<Int>, startIndex: Int) {
        for (subIndex in sub.indices) {
            val vecIndex = subIndex + startIndex

            val vecItem = assertDoesNotThrow{ vec[vecIndex] }
            val subItem = assertDoesNotThrow{ sub[subIndex] }

            assertEquals(subItem, vecItem)
        }
    }

    @Test
    fun testTryGet() {
        val vec = (1 .. 25).toVectorList()

        val startIndex = 9
        val endIndex = 19
        val sub = vec.subList(startIndex, endIndex)

        for (subIndex in sub.indices) {
            val vecIndex = subIndex + startIndex

            val resultSub = assertDoesNotThrow{ sub.tryGet(subIndex) }
            val resultVec = assertDoesNotThrow{ vec.tryGet(vecIndex) }

            val subItem = assertDoesNotThrow{ resultSub.getOrThrow() }
            val vecItem = assertDoesNotThrow{ resultVec.getOrThrow() }

            assertEquals(subItem, vecItem)
        }

        val out1 = assertDoesNotThrow{ sub.tryGet(-1) }
        val out2 = assertDoesNotThrow{ sub.tryGet(sub.size) }

        assertFailsWith<IndexOutOfBoundsException>{ out1.getOrThrow() }
        assertFailsWith<IndexOutOfBoundsException>{ out2.getOrThrow() }
    }

    @Test
    fun testWrapGet() {
        val vec = (1 .. 10).toVectorList()

        for (startIndex in 0 until vec.size) {
            for (endIndex in startIndex .. vec.size) {
                val sub = vec.subList(startIndex, endIndex)

                if (sub.isEmpty()) {
                    assertFailsWith<IllegalStateException>{ sub.wrapGet(0) }
                }
                else {
                    this.testWrapGetWith(sub)
                }
            }
        }
    }

    private fun testWrapGetWith(sub: MutableList<Int>) {
        val size = sub.size

        for (index1 in sub.indices) {
            val index2 = index1 - size
            val index3 = index1 + size

            val item1 = assertDoesNotThrow{ sub.wrapGet(index1) }
            val item2 = assertDoesNotThrow{ sub.wrapGet(index2) }
            val item3 = assertDoesNotThrow{ sub.wrapGet(index3) }

            assertEquals(item1, item2)
            assertEquals(item2, item3)
        }
    }

    @Test
    fun testSet() {
        val vec = (1 .. 15).toVectorList()

        var item = -1000

        for (startIndex in 0 until vec.size) {
            for (endIndex in startIndex + 1 .. vec.size) {
                val sub = vec.subList(startIndex, endIndex)

                this.testSetWith(vec, sub, startIndex, item)

                ++item
            }
        }
    }

    private fun testSetWith(vec: VectorList<Int>, sub: MutableList<Int>, startIndex: Int, item: Int) {
        for (index in sub.indices) {
            val subItem = sub[index]
            val vecItem = vec[index + startIndex]

            val setItem = assertDoesNotThrow{ sub.set(index, item) }

            assertEquals(subItem, vecItem)
            assertEquals(vecItem, setItem)

            assertEquals(item, sub[index])
            assertEquals(item, vec[index + startIndex])
        }
    }

    @Test
    fun testTrySet() {
        val vec = (1 .. 50).toVectorList()

        val startIndex = 31
        val endIndex = 48
        val sub = vec.subList(startIndex, endIndex)

        this.testTrySetAt(sub, 0, -1)
        this.testTrySetAt(sub, sub.lastIndex, -2)
        this.testTrySetAt(sub, sub.size / 2, -3)
    }

    private fun testTrySetAt(sub: MutableList<Int>, index: Int, value: Int) {
        val elem = sub[index]

        val setResult = assertDoesNotThrow{ sub.trySet(index, value) }
        val getResult = assertDoesNotThrow{ sub.tryGet(index) }

        val oldItem = assertDoesNotThrow{ setResult.getOrThrow() }
        val newItem = assertDoesNotThrow{ getResult.getOrThrow() }

        assertEquals(elem, oldItem)
        assertEquals(value, newItem)
    }

    @Test
    fun testWrapSet() {
        val vec = (1 .. 10).toVectorList()

        var value = -1000

        for (startIndex in 0 until vec.size) {
            for (endIndex in startIndex .. vec.size) {
                val sub = vec.subList(startIndex, endIndex)

                if (sub.isEmpty()) {
                    assertFailsWith<IllegalStateException>{ sub.wrapSet(0, 1000) }
                }
                else {
                    value = this.testWrapSetWith(sub, value)
                }
            }
        }
    }

    private fun testWrapSetWith(sub: MutableList<Int>, value: Int): Int {
        val size = sub.size
        var newValue = value

        for (index1 in sub.indices) {
            val initialItem = assertDoesNotThrow{ sub.wrapGet(index1) }

            val index2 = index1 - size
            val index3 = index1 + size

            val prevItem1 = assertDoesNotThrow{ sub.wrapSet(index1, newValue) }
            val currentItem1 = assertDoesNotThrow{ sub.wrapGet(index1) }
            assertEquals(initialItem, prevItem1)
            assertEquals(newValue, currentItem1)
            ++newValue

            val prevItem2 = assertDoesNotThrow{ sub.wrapSet(index2, newValue) }
            val currentItem2 = assertDoesNotThrow{ sub.wrapGet(index2) }
            assertEquals(currentItem1, prevItem2)
            assertEquals(newValue, currentItem2)
            ++newValue

            val prevItem3 = assertDoesNotThrow{ sub.wrapSet(index3, newValue) }
            val currentItem3 = assertDoesNotThrow{ sub.wrapGet(index3) }
            assertEquals(currentItem2, prevItem3)
            assertEquals(newValue, currentItem3)
            ++newValue
        }

        return newValue
    }

    @Test
    fun testSwap() {
        val vec = (1 .. 5).toVectorList()

        val startIndex = 1
        val endIndex = 4
        val sub = vec.subList(startIndex, endIndex)

        this.testSwapAtSameIndex(sub)
        this.testSwapAtDifferentIndex(sub)
    }

    private fun testSwapAtSameIndex(sub: MutableList<Int>) {
        val index = 2
        val item = sub[index]

        assertDoesNotThrow{ sub.swap(index, index) }

        assertEquals(item, sub[index])
    }

    private fun testSwapAtDifferentIndex(sub: MutableList<Int>) {
        val index1 = 0
        val index2 = 1

        val item1 = sub[index1]
        val item2 = sub[index2]

        assertDoesNotThrow{ sub.swap(index1, index2) }

        assertEquals(sub[index1], item2)
        assertEquals(sub[index2], item1)
    }

    @Test
    fun testAdd() {
        val vec = (1 .. 10).toVectorList()

        this.testAddWith(vec, 5, 9, -1)
        this.testAddWith(vec, 0, 1, -2)
        this.testAddWith(vec, 8, 8, -3)
    }

    private fun testAddWith(vec: VectorList<Int>, startIndex: Int, endIndex: Int, value: Int) {
        val sub = vec.subList(startIndex, endIndex)

        val success = assertDoesNotThrow{ sub.add(value) }

        assertTrue(success)

        assertEquals(value, sub.last())
        assertEquals(value, vec[endIndex])
    }

    @Test
    fun testAddWithIndexing() {
        val vec = (1 .. 10).toVectorList()

        this.testIndexAddWith(vec, 1, 4, -1)
        this.testIndexAddWith(vec, 4, vec.size - 2, -2)
        this.testIndexAddWith(vec, 2, vec.size - 6, -3)
        this.testIndexAddWith(vec, 6, vec.size, -4)
    }

    private fun testIndexAddWith(vec: VectorList<Int>, startIndex: Int, endIndex: Int, value: Int) {
        val sub = vec.subList(startIndex, endIndex)

        assertFailsWith<IndexOutOfBoundsException>{ sub.add(-1, value) }
        assertFailsWith<IndexOutOfBoundsException>{ sub.add(sub.size + 1, value) }

        assertDoesNotThrow{ sub.add(sub.size, value) }
        assertEquals(value, sub.last())
        assertEquals(value, vec[endIndex])

        assertDoesNotThrow{ sub.add(0, value) }
        assertEquals(value, sub.first())
        assertEquals(value, vec[startIndex])

        val half = sub.size / 2
        assertDoesNotThrow{ sub.add(half, value) }
        assertEquals(value, sub[half])
        assertEquals(value, vec[startIndex + half])
    }

    @Test
    fun testAddAll() {
        val vec = (1 .. 30).toVectorList()

        this.testAddingEmptyCollection(vec)
        this.testAddingNonemptyCollections(vec)
    }

    private fun testAddingEmptyCollection(vec: VectorList<Int>) {
        val initialSize = vec.size

        val startIndex = 2
        val endIndex = 15
        val sub = vec.subList(startIndex, endIndex)

        val empty = emptyList<Int>()
        val change = assertDoesNotThrow{ sub.addAll(empty) }

        assertFalse(change)
        assertEquals(initialSize, vec.size)
    }

    private fun testAddingNonemptyCollections(vec: VectorList<Int>) {
        val list = (-10 .. -1).toList()
        this.testAddAllWith(vec, list, 10, vec.size - 10)

        val set = (-15 .. -5).toSet()
        this.testAddAllWith(vec, set, 0, vec.size - 12)

        this.testAddAllWith(vec, vec, 11, 11)
    }

    private fun testAddAllWith(vec: VectorList<Int>, collection: Collection<Int>, startIndex: Int, endIndex: Int) {
        val sub = vec.subList(startIndex, endIndex)

        val vecSize = vec.size
        val subSize = sub.size
        val collectionSize = collection.size

        val change = assertDoesNotThrow{ sub.addAll(collection) }

        val iter = collection.iterator()

        for (index in subSize up collectionSize) {
            val item = iter.next()

            assertEquals(item, sub[index])
            assertEquals(item, vec[index + startIndex])
        }

        assertTrue(change)
        assertEquals(vec.size, vecSize + collectionSize)
        assertEquals(sub.size, subSize + collectionSize)
    }

    @Test
    fun testAddAllWithIndexing() {
        val vec = (1 .. 100).toVectorList()

        this.testAddingWithIndexEmptyCollections(vec)
        this.testAddingWithIndexNonemptyCollections(vec)
    }

    private fun testAddingWithIndexEmptyCollections(vec: VectorList<Int>) {
        val startIndex = 44
        val endIndex = 83
        val sub = vec.subList(startIndex, endIndex)
        val initialSize = sub.size

        val empty = emptySet<Int>()
        val index = initialSize / 2

        val change = assertDoesNotThrow{ sub.addAll(index, empty) }

        assertFalse(change)
        assertEquals(sub.size, initialSize)
    }

    private fun testAddingWithIndexNonemptyCollections(vec: VectorList<Int>) {
        val list = listOf(7, 10, -65, 0, -43, 11)
        this.helperTestAddingWithIndexOn(vec, list, 12, 90)

        val set = setOf(-1, 55, 31, 800, 6032, -2)
        this.helperTestAddingWithIndexOn(vec, set, 2, 33)

        this.testAddingSelfWithIndex(vec)
    }

    private fun helperTestAddingWithIndexOn(vec: VectorList<Int>, collection: Collection<Int>, startIndex: Int, endIndex: Int) {
        val sub = vec.subList(startIndex, endIndex)

        val vecSize = vec.size
        val subSize = sub.size
        val amountAdded = 3 * collection.size

        this.helperTestAddingWithIndexOn(vec, sub, startIndex, collection, 0)
        this.helperTestAddingWithIndexOn(vec, sub, startIndex, collection, sub.size)
        this.helperTestAddingWithIndexOn(vec, sub, startIndex, collection, sub.size / 2)

        assertEquals(vec.size, vecSize + amountAdded)
        assertEquals(sub.size, subSize + amountAdded)
    }

    private fun helperTestAddingWithIndexOn(
        vec: VectorList<Int>,
        sub: MutableList<Int>,
        startIndex: Int,
        collection: Collection<Int>,
        insertIndex: Int
    ) {
        val collectionSize = collection.size

        val change = assertDoesNotThrow{ sub.addAll(insertIndex, collection) }

        val iter = collection.iterator()

        for (index in insertIndex up collectionSize) {
            val item = iter.next()

            assertEquals(item, sub[index])
            assertEquals(item, vec[index + startIndex])
        }

        assertTrue(change)
    }

    private fun testAddingSelfWithIndex(vec: VectorList<Int>) {
        val startIndex = vec.size / 4
        val endIndex = 3 * vec.size / 4
        val sub = vec.subList(startIndex, endIndex)

        val vecSize1 = vec.size
        val subSize1 = sub.size
        val changeWithBase = assertDoesNotThrow{ sub.addAll(sub.size / 3, vec) }

        assertTrue(changeWithBase)
        assertEquals(sub.size, subSize1 + vecSize1)
        assertEquals(vec.size, vecSize1 + vecSize1)

        val vecSize2 = vec.size
        val subSize2 = sub.size
        val changeWithSelf = assertDoesNotThrow{ sub.addAll(2 * sub.size / 3, sub) }

        assertTrue(changeWithSelf)
        assertEquals(sub.size, subSize2 + subSize2)
        assertEquals(vec.size, subSize2 + vecSize2)
    }

    @Test
    fun testInsert() {
        val vec = (1 .. 10).toVectorList()

        val startIndex = 3
        val endIndex = 9
        val sub = vec.subList(startIndex, endIndex)

        val list = (-100 .. -90).toList()

        this.testInsertWith(sub, list)
        this.testInsertWith(sub, sub)
        //this.testInsertWith(sub, vec)
    }

    private fun testInsertWith(sub: MutableList<Int>, collection: Collection<Int>) {
        val subSize = sub.size

        val amountAdded = assertDoesNotThrow{ sub.insert(collection) }

        assertEquals(sub.size, subSize + amountAdded)
    }

    @Test
    fun testResize() {
        val vec = (1 .. 100).toVectorList()

        val startIndex = 15
        val endIndex = 89
        val sub = vec.subList(startIndex, endIndex)

        this.testDecreasingResize(vec, sub)
        this.testIncreasingResize(vec, sub)
        this.testInvalidResize(vec, sub)
    }

    private fun testDecreasingResize(vec: VectorList<Int>, sub: MutableList<Int>) {
        val vecSize = vec.size
        val subSize = sub.size

        val value = Int.MIN_VALUE
        val newSize = sub.size / 2
        val sizeChange = subSize - newSize

        assertDoesNotThrow{ sub.resize(newSize) { value } }

        assertEquals(newSize, sub.size)
        assertEquals(-1, sub.indexOf(value))

        assertEquals(vec.size, vecSize - sizeChange)
    }

    private fun testIncreasingResize(vec: VectorList<Int>, sub: MutableList<Int>) {
        val vecSize = vec.size
        val subSize = sub.size

        val value = Int.MAX_VALUE
        val newSize = sub.size * 4
        val sizeChange = newSize - subSize

        assertDoesNotThrow{ sub.resize(newSize) { value } }

        assertEquals(newSize, sub.size)
        assertEquals(subSize, sub.indexOf(value))

        assertEquals(vec.size, vecSize + sizeChange)
    }

    private fun testInvalidResize(vec: VectorList<Int>, sub: MutableList<Int>) {
        val vecSize = vec.size
        val subSize = sub.size

        assertFailsWith<IllegalArgumentException>{ sub.resize(-1) { throw InternalError() } }

        assertEquals(vec.size, vecSize)
        assertEquals(sub.size, subSize)
    }

    @Test
    fun testRemove() {

    }

    @Test
    fun testRemoveAll() {

    }

    @Test
    fun testRemoveAt() {

    }

    @Test
    fun testDelete() {

    }

    @Test
    fun testRetainAll() {

    }

    @Test
    fun testKeep() {

    }

    @Test
    fun testRemoveFirstOf() {

    }

    @Test
    fun testRemoveAllOf() {

    }

    @Test
    fun testRemoveAllOfWithPredicate() {

    }

    @Test
    fun testRemoveAmount() {

    }

    @Test
    fun testRemoveFromBack() {

    }

    @Test
    fun testRemoveRange() {

    }

    @Test
    fun testClear() {
        val vec = (0 until 25).toVectorList()
        val size = vec.size

        val startIndex = 13
        val endIndex = 22
        val sub = vec.subList(startIndex, endIndex)
        val subSize = sub.size

        assertDoesNotThrow{ sub.clear() }
        assertEquals(size - subSize, vec.size)

        assertFalse(startIndex in vec)
        assertFalse(endIndex - 1 in vec)
        assertTrue(endIndex in vec)
    }

    @Test
    fun testContains() {
        val vec = (1 .. 50).toVectorList()

        for (startIndex in 0 until vec.size) {
            for (endIndex in startIndex .. vec.size) {
                val sub = vec.subList(startIndex, endIndex)

                this.testContainsOn(vec, sub, startIndex, endIndex)
            }
        }
    }

    private fun testContainsOn(vec: VectorList<Int>, sub: MutableList<Int>, startIndex: Int, endIndex: Int) {
        for (vecIndex in vec.indices) {
            val result = assertDoesNotThrow{ vec[vecIndex] in sub }

            @Suppress("ConvertTwoComparisonsToRangeCheck")
            if (startIndex <= vecIndex && vecIndex < endIndex) {
                assertTrue(result)
            }
            else {
                assertFalse(result)
            }
        }
    }

    @Test
    fun testContainsAll() {
        val vec = (0 .. 100).toVectorList()

        val startIndex = 22
        val endIndex = 78
        val sub = vec.subList(startIndex, endIndex)

        val fullyInRange = listOf(56, 32, 73, 76, 47, 32, 59)
        this.testSuccessfulContainsAll(sub, fullyInRange)

        val partiallyInRange = hashSetOf(10, 100, 11, 54, -110)
        this.testFailedContainsAll(sub, partiallyInRange)

        val notInRange = setOf(80, 90, -65)
        this.testFailedContainsAll(sub, notInRange)
    }

    private fun testSuccessfulContainsAll(sub: MutableList<Int>, values: Collection<Int>) {
        val result = assertDoesNotThrow{ sub.containsAll(values) }

        assertTrue(result)
    }

    private fun testFailedContainsAll(sub: MutableList<Int>, values: Collection<Int>) {
        val result = assertDoesNotThrow{ sub.containsAll(values) }

        assertFalse(result)
    }

    @Test
    fun testIndexOf() {
        val vec = (0 .. 110 step 5).toVectorList()
        val size = vec.size

        for (startIndex in 0 until size) {
            for (endIndex in startIndex + 1 .. size) {
                val sub = vec.subList(startIndex, endIndex)

                this.testIndexWith(vec, sub)
            }
        }
    }

    private fun testIndexWith(vec: VectorList<Int>, sub: MutableList<Int>) {
        for (value in sub) {
            val foundSubIndex = assertDoesNotThrow{ sub.indexOf(value) }
            val foundVecIndex = assertDoesNotThrow{ vec.indexOf(value) }

            assertEquals(vec[foundVecIndex], sub[foundSubIndex])

            val aheadSubIndex = assertDoesNotThrow{ sub.index(foundSubIndex + 1, value) }
            val aheadVecIndex = assertDoesNotThrow{ vec.index(foundVecIndex + 1, value) }

            assertEquals(-1, aheadVecIndex)
            assertEquals(-1, aheadSubIndex)
        }
    }

    @Test
    fun testIndexWithPredicate() {
        val vec = (0 .. 40).toVectorList()
        val size = vec.size

        for (startIndex in 0 until size) {
            for (endIndex in startIndex + 1 .. size) {
                val sub = vec.subList(startIndex, endIndex)

                this.testIndexWithPredicateSuccessStates(vec, sub, startIndex)
                this.testIndexWithPredicateFailedStates(sub)
            }
        }
    }

    private fun testIndexWithPredicateSuccessStates(vec: VectorList<Int>, sub: MutableList<Int>, startIndex: Int) {
        val divisor = 4
        val condition = { value: Int -> 0 == value % divisor }

        for (index in sub.indices) {
            val foundSubIndex = assertDoesNotThrow{ sub.index(index, condition) }
            val foundVecIndex = assertDoesNotThrow{ vec.index(index + startIndex, condition) }

            if (foundSubIndex >= 0) {
                assertTrue(foundSubIndex - index <= divisor)
                assertEquals(vec[foundVecIndex], sub[foundSubIndex])
            }
        }
    }

    private fun testIndexWithPredicateFailedStates(sub: MutableList<Int>) {
        val foundIndex = assertDoesNotThrow{ sub.index(0) { it < 0 } }

        assertEquals(-1, foundIndex)

        assertFailsWith<IndexOutOfBoundsException>{ sub.index(-1) { false } }
        assertFailsWith<IndexOutOfBoundsException>{ sub.index(sub.size + 1) { false } }
    }

    @Test
    fun testLastIndexOf() {

    }

    @Test
    fun testLastIndexWithPredicate() {

    }

    @Test
    fun testSeparationPoint() {
        val max = 100
        val vec1 = (1 .. max).toVectorList()
        val vec2 = vec1.toVectorList()

        val startIndex = 26
        val endIndex = 90
        val sub1 = vec1.subList(startIndex, endIndex)
        val sub2 = vec2.subList(startIndex, endIndex)

        val condition = { num: Int -> 0 == num % 2 }

        val separationPoint1A = sub1.separate(condition)
        val separationPoint2A = sub2.stableSeparate(condition)

        val separationPoint1B = assertDoesNotThrow{ sub1.separationPoint(condition) }
        val separationPoint2B = assertDoesNotThrow{ sub2.separationPoint(condition) }

        assertNotNull(separationPoint1B)
        assertNotNull(separationPoint2B)

        assertEquals(separationPoint1A, separationPoint2A)
        assertEquals(separationPoint2A, separationPoint1B)
        assertEquals(separationPoint1B, separationPoint2B)
    }

    @Test
    fun testSeparate() {
        val vec = (1 .. 100).toVectorList()
        val size = vec.size

        for (startIndex in 0 until size) {
            for (endIndex in startIndex + 1 .. size) {
                val sub = vec.subList(startIndex, endIndex)

                this.testSeparateOn(sub)
            }
        }
    }

    private fun testSeparateOn(sub: MutableList<Int>) {
        for (divisor in sub) {
            val condition = { value: Int -> 0 == value % divisor }
            val point = assertDoesNotThrow{ sub.separate(condition) }

            for (index in 0 until point) {
                val result = condition(sub[index])

                assertTrue(result)
            }

            for (index in point until sub.size) {
                val result = condition(sub[index])

                assertFalse(result)
            }
        }
    }

    @Test
    fun testStableSeparate() {
        
    }

    @Test
    fun testIntersperse() {

    }

    @Test
    fun testSubList() {

    }

    @Test
    fun testEquals() {

    }

    @Test
    fun testIsPermutationOf() {

    }

    @Test
    fun testHashCode() {

    }

    @Test
    fun testCompare() {

    }

    @Test
    fun testNext() {

    }

    @Test
    fun testPrev() {

    }

    @Test
    fun testIsSorted() {
        val vec = vectorListOf(10, 3, 4, 8, 11, 15, -1, 23, 4)

        val startIndex = 1
        val endIndex = 6
        val sub = vec.subList(startIndex, endIndex)

        val result = assertDoesNotThrow{ sub.isSorted() }

        assertTrue(result)
    }

    @Test
    fun testIsSortedUntil() {

    }

    @Test
    fun testSortWith() {
        val vec = vectorListOf(-7, 1, 8, -9, 9, 4, -3, 6, 3, -3, 0, 4, 5, 0, 5)
        val size = vec.size
        val comp = { left: Int, right: Int -> left - right }

        val startIndex = size / 5
        val endIndex = 3 * size / 5
        val sub = vec.subList(startIndex, endIndex)

        val newList = assertDoesNotThrow{ sub.sortedWith(comp) }
        assertDoesNotThrow{ sub.sortWith(comp) }

        assertTrue(newList.isSorted(comp))
        assertTrue(sub.isSorted(comp))

        assertEquals(sub, newList)

        assertFalse(vec.isSorted(comp))
    }

    @Test
    fun testToString() {
        val vec = (1 .. 10).toVectorList()
        val size = vec.size

        for (startIndex in 0 until size) {
            for (endIndex in startIndex .. size) {
                val sub = vec.subList(startIndex, endIndex)
                val copy = sub.toVectorList()

                val subStr = assertDoesNotThrow{ sub.toString() }
                val copyStr = assertDoesNotThrow{ copy.toString() }

                assertEquals(subStr, copyStr)
            }
        }
    }
}
