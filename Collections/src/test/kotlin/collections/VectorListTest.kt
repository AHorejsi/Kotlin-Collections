package collections

import asserts.assertGreater
import asserts.assertLess
import asserts.assertLessEqual
import asserts.assertNotContains
import objects.TestObject
import org.junit.jupiter.api.assertDoesNotThrow
import reusable.*
import kotlin.test.*

@Suppress("SameParameterValue")
class VectorListTest {
    @Test
    fun testPrimaryConstructor() {
        assertDoesNotThrow{ VectorList<Int>(0) }
        assertFailsWith<IllegalArgumentException>{ VectorList<Int>(-1) }
    }

    @Test
    fun testFillConstructor() {
        val size = 30
        val vec = assertDoesNotThrow{ VectorList(size) { Int.MIN_VALUE } }

        assertEquals(size, vec.size)
        assertLessEqual(size, vec.capacity)
        assertTrue(vec.all(Int.MIN_VALUE::equals))
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

        this.testForEquality(vecFromSet.iterator(), set.iterator())
        this.testForEquality(vecFromIter.iterator(), iter.iterator())
        this.testForEquality(vecFromSeq.iterator(), seq.iterator())
        this.testForEquality(vecFromArray.iterator(), array.iterator())

        assertLessEqual(set.size, vecFromSet.capacity)
        assertLessEqual(seq.count(), vecFromSeq.capacity)
        assertLessEqual(iter.count(), vecFromIter.capacity)
        assertLessEqual(array.size, vecFromArray.capacity)
    }

    private fun testForEquality(left: Iterator<*>, right: Iterator<*>) {
        while (left.hasNext() && right.hasNext()) {
            val leftItem = left.next()
            val rightItem = right.next()

            assertEquals(leftItem, rightItem)
        }

        assertTrue(!left.hasNext())
        assertTrue(!right.hasNext())
    }

    @Test
    fun testIsRandomAccess() {
        val vec = vectorListOf<Int>()

        testIsRandomAccess(vec, true)
    }

    @Test
    fun testWithIndex() {
        val vec = (1 .. 10).toVectorList()

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

        val initialSize = assertDoesNotThrow{ vec.size }
        assertEquals(0, initialSize)

        testSizeAfterAdd(vec, 1)
        testSizeAfterAddAll(vec, 100)
        testSizeAfterRemoveLast(vec)
        testSizeAfterAddAll(vec, 200)
        testSizeAfterRemoveFromBack(vec, 150)

        val finalSize = assertDoesNotThrow{ vec.size }
        assertEquals(150, finalSize)
    }

    @Test
    fun testIsEmpty() {
        val vec = vectorListOf<Int>()

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

        assertLess(capacityBeforeAdd, capacityAfterAdd)
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

            assertGreater(capacityBeforeRemove, capacityAfterRemove)
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
        assertLessEqual(targetSize, vec.capacity)
    }

    private fun testWhenSizeEqualsCapacityBeforeAdding(vec: VectorList<Int>) {
        val items = 0.replicate(vec.capacity - vec.size)
        vec.addAll(items)

        val oldCapacity = vec.capacity
        vec.add(-100)
        val newCapacity = vec.capacity

        assertLess(oldCapacity, newCapacity)
    }

    @Test
    fun testTryFirst() {
        val vec1 = (10 .. 15).toVectorList()
        testTryFirst(vec1, { 0 == it % 3 }, 10, 12)

        val vec2 = vectorListOf<Int>()
        testTryFirstOnEmpty(vec2) { 0 == it }
    }

    @Test
    fun testTryLast() {
        val vec1 = (10 .. 15).toVectorList()
        testTryLast(vec1, { 0 == it % 2 }, 15, 14)

        val vec2 = vectorListOf<Int>()
        testTryLastOnEmpty(vec2) { 0 == it }
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

        assertContains(vec, 0)
        assertNotContains(vec, 7)
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
        val vec1 = vectorListOf<Int>()
        val range1 = (0 until 10).toList()
        testContainsAfterAdding(vec1, range1)

        val vec2 = vectorListOf(1, 2, 3, 1, 2, 3, 2, 1, 2, 3, 2, 1, 2, 3, 1, 2, 1 ,3 ,2)
        val element2 = 3
        testContainsAfterRemovingByElement(vec2, element2)

        val vec3 = (1 .. 20).toVectorList()
        testContainsAfterRemovingByIndex(vec3, 0)
        testContainsAfterRemovingByIndex(vec3, vec3.lastIndex)
        testContainsAfterRemovingByIndex(vec3, vec3.size / 2)

        val vec4 = (1 .. 50).toVectorList()
        testContains(vec4, 1, true)
        testContains(vec4, 25, true)
        testContains(vec4, 50, true)
        testContains(vec4, -1, false)
        testContains(vec4, 51, false)
    }

    @Test
    fun testContainsAll() {
        val range = 1 .. 20
        val vec = range.toVectorList()

        val collection1 = (range step 2).toList()
        val collection2 = (range step 3).toHashSet()
        val collection3 = (range step 5).toSet()
        val collection4 = setOf(1, 20, 5, 9)
        val collection5 = setOf(-1, 1, 2, 3, 0)
        val collection6 = listOf(5, 1, 25, -1, 5)

        testContainsAll(vec, collection1, true)
        testContainsAll(vec, collection2, true)
        testContainsAll(vec, collection3, true)
        testContainsAll(vec, collection4, true)
        testContainsAll(vec, collection5, false)
        testContainsAll(vec, collection6, false)
    }

    @Test
    fun testIndexOf() {
        val vec1 =  vectorListOf(1, -9, 4, 7, 10, -1002, 76, -5, 444, 162, -10, 0)
        val vec2 = (vec1 + vec1).toVectorList()
        testIndexOf(vec1, vec2)

        val vec3 = vectorListOf(1, 3, 7, 9, 5)
        testIndexWithPredicate(vec3, 0, 1) { 0 == it % 3 }
        testIndexWithPredicate(vec3, 2, 3) { 0 == it % 3 }
        testIndexWithPredicate(vec3, 0, 4) { 0 == it % 5 }
        testIndexWithPredicate(vec3, 0, -1) { it < 0 }
    }

    @Test
    fun testLastIndexOf() {
        val vec1 =  vectorListOf(1, -9, 4, 7, 10, -1002, 76, -5, 444, 162, -10, 0)
        val vec2 = (vec1 + vec1).toVectorList()
        testLastIndexOf(vec1, vec2)

        val vec3 = vectorListOf(10, 1, 3, 7, 9, 5)
        testLastIndexWithPredicate(vec3, 5, 0) { 0 == it % 5 }
        testLastIndexWithPredicate(vec3, 6, 4) { 0 == it % 3 }
        testLastIndexWithPredicate(vec3, 6, -1) { it < 0 }
    }

    @Test
    fun testSwap() {
        val vec = (1 .. 5).map{ it.toString() }.toVectorList()

        testSwap(vec, 1, 4)
    }

    @Test
    fun testIsSorted() {
        val ordering =  inOrder<Int>()
        val reverseOrdering = reverseOrder<Int>()

        val vec1 = vectorListOf(7, -7, 8, 33, -100, 11, 5, -1011, 35382, 87, -19)
        val vec2 = vec1.asReversed().toVectorList()
        val vec3 = vec1.sorted().toVectorList()
        val vec4 = vec3.asReversed().toVectorList()

        testIsSorted(vec1, ordering, false)
        testIsSorted(vec2, reverseOrdering, false)
        testIsSorted(vec3, ordering, true)
        testIsSorted(vec4, reverseOrdering, true)
    }

    @Test
    fun testIsSortedUntil() {
        val ordering = inOrder<Int>()
        val reverseOrdering = reverseOrder<Int>()

        val vec1 = vectorListOf(1, 2, 6, 8, 0, -1)
        val vec2 = vectorListOf(10, 7, 5, 8, 7, 2)
        val vec3 = (1 .. 10).toVectorList()
        val vec4 = vec3.asReversed().toVectorList()

        testIsSortedUntil(vec1, ordering, 4)
        testIsSortedUntil(vec2, reverseOrdering, 3)
        testIsSortedUntil(vec3, ordering, vec3.size)
        testIsSortedUntil(vec4, reverseOrdering, vec4.size)
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
        val vec1 = vectorListOf(1, 9, 2, 7, 6, 4)
        val vec2 = vectorListOf(6, 2, 7, 1, 4, 9)
        val vec3 = vectorListOf(9, 6, 2, 4, 7)
        val vec4 = vectorListOf(2, 3, 9, 1, 4, 6)

        testIsPermutationOf(vec1, vec2, true)
        testIsPermutationOf(vec1, vec3, false)
        testIsPermutationOf(vec1, vec4, false)
    }

    @Test
    fun testNext() {
        val vec = (1 .. 8).toVectorList()

        testNext(vec)
    }

    @Test
    fun testPrev() {
        val vec = (8 downTo 1).toVectorList()

        testPrev(vec)
    }

    @Test
    fun testSeparationPoint() {
        val vec1 = vectorListOf(0, 2, 4, 6, 8, 1, 3, 5, 7, 9)
        val separationPoint = 5

        testSuccessfulSeparationPoint(vec1, { 0 == it % 2 }, separationPoint)

        val vec2 = vectorListOf(1, 2, 1)

        testFailedSeparationPoint(vec2) { 0 == it % 2 }

        val max3 = 100
        val vec3 = (1 .. max3).toVectorList()

        for (denominator in 2 .. max3) {
            val copy2A = vec3.toVectorList()
            val copy2B = vec3.toVectorList()

            testSeparationPoint(copy2A, copy2B) { 0 == it % denominator }
        }
    }

    @Test
    fun testSeparate() {
        val max = 100
        val vec = (1 .. max).toVectorList()

        for (denominator in 2 .. max) {
            testSeparate(vec) { 0 == it % denominator }
        }
    }

    @Test
    fun testStableSeparate() {
        val max = 100
        val values = (1 .. max).asSequence()
        val vec = (values + values).map{ TestObject(it) }.toVectorList()

        for (denominator in 2 .. max) {
            val copy = vec.toVectorList()

            testStableSeparate(vec, copy) { 0 == it.value % denominator }
        }
    }

    @Test
    fun testRotate() {
        val vec1 = vectorListOf(1, 2, 3, 4, 5, 6)
        val vec2 = vec1.toVectorList()

        val result1 = vectorListOf(3, 4, 5, 6, 1, 2)
        val result2 = vectorListOf(5, 6, 1, 2, 3, 4)

        testRotate(vec1, result1, -2)
        testRotate(vec2, result2, 2)
    }

    @Test
    fun testIntersperse() {
        val even = (1 .. 10).toVectorList()
        val odd = (1 .. 11).toVectorList()
        val empty = vectorListOf<Int>()
        val single = vectorListOf(0)

        testIntersperse(even, -1)
        testIntersperse(odd, -1)
        testIntersperse(empty, -1)
        testIntersperse(single, -1)
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

        testIteratorConstruction(empty)
        testIteratorConstruction(nonempty)
    }

    @Test
    fun testHasNext() {
        val empty = vectorListOf<Int>()
        testHasNextOnIterator(empty, 0)

        val vec = (1 .. 10).toVectorList()
        testHasNextOnIterator(vec, vec.size)
    }

    @Test
    fun testNext() {
        val vec = (1 .. 50).toVectorList()

        testNextOnIterator(vec)
    }

    @Test
    fun testRemove() {
        val vec = (0 .. 100 step 5).toVectorList()

        testRemoveOnIterator(vec)
    }
}

class VectorListIteratorTest {
    @Test
    fun testConstructor() {
        val vec1 = vectorListOf<Int>()
        testListIteratorConstruction(vec1)

        val vec2 = (1 .. 5).toVectorList()
        testListIteratorConstruction(vec2)
    }

    @Test
    fun testPreviousIndex() {
        val vec = (1 .. 13).toVectorList()

        testPreviousIndexOnListIterator(vec)
    }

    @Test
    fun testNextIndex() {
        val vec = (1 .. 13).toVectorList()

        testNextIndexOnListIterator(vec)
    }

    @Test
    fun testHasPrevious() {
        val vec = vectorListOf(-19, 100, 75, 14, -32, -27, 11, Int.MAX_VALUE, Int.MIN_VALUE)

        testHasPreviousOnListIterator(vec)
    }

    @Test
    fun testHasNext() {
        val vec = vectorListOf(Int.MIN_VALUE, Int.MAX_VALUE, 0, 14, 1010, -65, -33, 2)

        testHasNextOnListIterator(vec)
    }

    @Test
    fun testPrevious() {
        val vec = (1 .. 15).toVectorList()

        testPreviousOnListIterator(vec)
    }

    @Test
    fun testNext() {
        val vec = (1 .. 15).toVectorList()

        testNextOnListIterator(vec)
    }

    @Test
    fun testSet() {
        TODO()
    }

    @Test
    fun testRemove() {
        TODO()
    }

    @Test
    fun testAdd() {
        TODO()
    }

    @Test
    fun testConcurrentModification() {
        val vec = (1 .. 10).toVectorList()

        testConcurrentModification(vec)
    }
}

@Suppress("SameParameterValue")
class VectorSublistTest {
    @Test
    fun testConstructor() {
        val vec = (1 .. 25).toVectorList()

        testConstructionOnSublist(vec)
    }

    @Test
    fun testIsRandomAccess() {
        val vec = vectorListOf<Int>()

        testIsRandomAccessOnSublist(vec)
    }

    @Test
    fun testWithIndex() {
        val vec = (1 .. 10).toVectorList()

        testWithIndexOnSublists(vec)
    }

    @Test
    fun testSize() {
        val vec = (1 .. 500).toVectorList()
        testSizeOnSublistConstruction(vec, 43, 98)

        val other = listOf(15, 87, 90, 21, 65, 87, 82, 55, 59, 69)
        testSizeAfterAddingOnSublist(vec, 15, 91, other)

        val amountToRemove = 38
        testSizeAfterRemovingOnSublist(vec, 3, 61, amountToRemove)

        val sub = vec.subList(16, 80)
        testSizeAfterAdd(sub, -1)
        testSizeAfterAddAll(sub, 100)
        testSizeAfterRemoveLast(sub)
        testSizeAfterAddAll(sub, 200)
        testSizeAfterRemoveFromBack(sub, 150)
    }

    @Test
    fun testIsEmpty() {
        val vec = (1 .. 50).toVectorList()

        testIsEmptyAfterConstructionOnSublist(vec)
        testIsEmptyAfterModificationOnSublist(vec, vec.size / 2)
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

        testAtLeast(size, sub1, sub2, sub3)
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

        testAtMost(size, sub1, sub2, sub3)
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

        testExactly(size, sub1, sub2, sub3)
    }

    @Test
    fun testTryFirst() {
        val vec = (15 .. 55).toVectorList()

        val sub = vec.subList(6, 38)
        testTryFirst(sub, { 0 == it % 4 }, 21, 24)

        val empty = vec.subList(15, 15)
        testTryFirstOnEmpty(empty) { 0 == it }
    }

    @Test
    fun testTryLast() {
        val vec = (15 .. 55).toVectorList()

        val sub = vec.subList(6, 38)
        testTryLast(sub, { 0 == it % 10 }, 52, 50)

        val empty = vec.subList(15, 15)
        testTryLastOnEmpty(empty) { 0 == it }
    }

    @Test
    fun testGet() {
        val vec = (1 .. 100).toVectorList()

        for (startIndex in 0 until vec.size) {
            for (endIndex in startIndex .. vec.size) {
                testGetOnSublist(vec, startIndex, endIndex)
            }
        }
    }

    @Test
    fun testTryGet() {
        val vec = (1 .. 10).toVectorList()

        for (startIndex in vec.indices) {
            for (endIndex in startIndex .. vec.size) {
                val sub = vec.subList(startIndex, endIndex)

                testTryGetInBounds(sub)
                testTryGetOutOfBounds(sub)
            }
        }
    }

    @Test
    fun testWrapGet() {
        val vec = (1 .. 10).toVectorList()

        for (startIndex in 0 until vec.size) {
            for (endIndex in startIndex .. vec.size) {
                val sub = vec.subList(startIndex, endIndex)

                if (startIndex == endIndex) {
                    testWrapGetOnEmpty(sub)
                }
                else {
                    testWrapGetRelative(sub)
                    testWrapGetAtEnds(sub)
                }
            }
        }
    }

    @Test
    fun testSet() {
        val vec = (1 .. 50).toVectorList()

        val startIndex = 25
        val endIndex = 39
        val sub = vec.subList(startIndex, endIndex)

        testSetOnSublist(vec, sub, startIndex, 0, -1)
        testSetOnSublist(vec, sub, startIndex, sub.lastIndex, -2)
        testSetOnSublist(vec, sub, startIndex, sub.size / 2, -3)

        testSetOutOfBounds(sub, -4)
    }

    @Test
    fun testTrySet() {
        val vec = (1 .. 10).toVectorList()

        val startIndex = 2
        val endIndex = 9
        val sub = vec.subList(startIndex, endIndex)

        testTrySetAt(sub, 0, -1)
        testTrySetAt(sub, sub.lastIndex, -2)
        testTrySetAt(sub, sub.size / 2, -3)

        testTrySetOutOfBounds(sub, -1)
    }

    @Test
    fun testWrapSet() {
        val vec = (1 .. 20).toVectorList()

        val startIndex = 10
        val endIndex = 16
        val sub1 = vec.subList(startIndex, endIndex)

        testWrapSetAt(sub1, 0, -1)
        testWrapSetAt(sub1, sub1.size / 2, -2)
        testWrapSetAt(sub1, sub1.lastIndex, -3)
        testWrapSetAt(sub1, -1, -4)
        testWrapSetAt(sub1, sub1.size, -5)

        val index2 = 4
        val sub2 = vec.subList(index2, index2)
        testWrapSetOnEmpty(sub2, -6)
    }

    @Test
    fun testSwap() {
        val vec = (1 .. 10).map{ it.toString() }.toVectorList()

        val startIndex = 3
        val endIndex = 10
        val sub = vec.subList(startIndex, endIndex)

        testSwapOnSublist(vec, sub, startIndex, 0, sub.lastIndex)
        testSwap(sub, 1, sub.lastIndex - 1)
    }

    @Test
    fun testAdd() {
        val vec = (1 .. 50).toVectorList()

        val startIndex = 14
        val endIndex = 45
        val sub = vec.subList(startIndex, endIndex)

        testAddOnSublist(vec, sub, endIndex, -1)

        testIndexedAddOnSublist(vec, sub, startIndex, 0, -2)
        testIndexedAddOnSublist(vec, sub, startIndex, sub.size, -3)
        testIndexedAddOnSublist(vec, sub, startIndex, sub.size / 2, -4)

        testIndexedAddOutOfBounds(sub, -1, -5)
        testIndexedAddOutOfBounds(sub, sub.size + 1, -5)
    }

    @Test
    fun testAddAll() {
        this.addAllAtEnd()
        this.addAllAtIndices()
    }

    private fun addAllAtEnd() {
        val vec = (1 .. 10).toVectorList()

        val other1 = (-10 .. -1).toList()
        val other2 = (20 downTo 11).toSet()
        val other3 = (5 .. 15).toHashSet()

        testAddAllWithOtherOnSublist(vec, other1, 2, 8)
        testAddAllWithOtherOnSublist(vec, other2, vec.size / 4, 3 * vec.size / 4)
        testAddAllWithOtherOnSublist(vec, other3, vec.size / 5, 4 * vec.size / 5)

        val empty = emptyList<Int>()

        testAddAllWithEmptyOnSublist(vec, empty, 1, 10)

        testAddAllWithSelfOnSublist(vec, vec.size / 2, 3 * vec.size / 4)
        testAddAllWithSelfOnSublist(vec, vec.size / 4, vec.size / 2)
        testAddAllWithSelfOnSublist(vec, vec.size / 4, 3 * vec.size / 4)

        testAddAllWithBaseOnSublist(vec, vec.size / 2, 3 * vec.size / 4)
        testAddAllWithBaseOnSublist(vec, vec.size / 4, vec.size / 2)
        testAddAllWithBaseOnSublist(vec, vec.size / 4, 3 * vec.size / 4)

        testAddAllWithSublistOnBaseList(vec, vec.size / 4, 3 * vec.size / 4)
    }

    private fun addAllAtIndices() {
        val vec = (1 .. 10).toVectorList()

        val other1 = (-10 .. -1).toList()
        val other2 = (20 downTo 11).toSet()
        val other3 = (5 .. 14).toHashSet()

        testIndexedAddAllWithOtherOnSublist(vec, other1, 1, 8, 0)
        testIndexedAddAllWithOtherOnSublist(vec, other2, 8, 19, 5)
        testIndexedAddAllWithOtherOnSublist(vec, other3, 3, 27, 24)

        testIndexedAddAllWithSelfOnSublist(vec, vec.size / 2, 3 * vec.size / 4) { 0 }
        testIndexedAddAllWithSelfOnSublist(vec, vec.size / 4, vec.size / 2) { it.size / 2 }
        testIndexedAddAllWithSelfOnSublist(vec, vec.size / 4, 3 * vec.size / 4) { it.size }

        testIndexedAddAllWithBaseOnSublist(vec, vec.size / 2, 3 * vec.size / 4) { 0 }
        testIndexedAddAllWithBaseOnSublist(vec, vec.size / 4, vec.size / 2) { it.size / 2 }
        testIndexedAddAllWithBaseOnSublist(vec, vec.size / 4, 3 * vec.size / 4) { it.size }

        testIndexedAddAllWithSublistOnBaseList(vec, vec.size / 2, 3 * vec.size / 4) { 0 }
        testIndexedAddAllWithSublistOnBaseList(vec, vec.size / 4, vec.size / 2) { it.size / 2 }
        testIndexedAddAllWithSublistOnBaseList(vec, vec.size / 4, 3 * vec.size / 4) { it.size }
    }

    @Test
    fun testInsert() {
        val vec = (1 .. 10).toVectorList()

        val other1 = (-10 .. -1).toList()
        val other2 = (20 downTo 11).toSet()
        val other3 = (5 .. 15).toHashSet()

        testInsertWithOtherOnSublist(vec, 2, 8, other1)
        testInsertWithOtherOnSublist(vec, vec.size / 4, 3 * vec.size / 4, other2)
        testInsertWithOtherOnSublist(vec, vec.size / 5, 4 * vec.size / 5, other3)

        val empty = emptyList<Int>()

        testInsertWithEmptyOnSublist(vec, 1, 10, empty)

        testInsertWithSelfOnSublist(vec, vec.size / 2, 3 * vec.size / 4)
        testInsertWithSelfOnSublist(vec, vec.size / 4, vec.size / 2)
        testInsertWithSelfOnSublist(vec, vec.size / 4, 3 * vec.size / 4)

        testInsertWithBaseOnSublist(vec, vec.size / 2, 3 * vec.size / 4)
        testInsertWithBaseOnSublist(vec, vec.size / 4, vec.size / 2)
        testInsertWithBaseOnSublist(vec, vec.size / 4, 3 * vec.size / 4)

        testInsertWithSublistOnBaseList(vec, vec.size / 4, 3 * vec.size / 4)
    }

    @Test
    fun testResize() {
        val vec = (1 .. 50).toVectorList()
        testResizeByIncreaseOnSublist(vec, vec.size / 4, vec.size / 2, 25, -1)
        testResizeByDecreaseOnSublist(vec, vec.size / 2, 3 * vec.size / 4, 10, -2)

        val sub = vec.subList(vec.size / 5, 4 * vec.size / 5)
        testInvalidResize(sub, -10, Int.MAX_VALUE)
    }

    @Test
    fun testRemove() {
        val vec = (0 until 100).toVectorList()

        val startIndex = 8
        val endIndex = 77
        val sub = vec.subList(startIndex, endIndex)

        val value1 = 10
        val value2 = 53
        val value3 = 71

        val value4 = -1
        val value5 = 5
        val value6 = 77
        val value7 = 100

        testRemoveByElementOnSublist(vec, sub, value1, true)
        testRemoveByElementOnSublist(vec, sub, value2, true)
        testRemoveByElementOnSublist(vec, sub, value3, true)

        testRemoveByElementOnSublist(vec, sub, value4, false)
        testRemoveByElementOnSublist(vec, sub, value5, false)
        testRemoveByElementOnSublist(vec, sub, value6, false)
        testRemoveByElementOnSublist(vec, sub, value7, false)

        assertNotContains(vec, value1)
        assertNotContains(vec, value2)
        assertNotContains(vec, value3)

        assertNotContains(vec, value4)
        assertContains(vec, value5)
        assertContains(vec, value6)
        assertNotContains(vec, value7)
    }

    @Test
    fun testRemoveAt() {
        val vec = (50 .. 100).toVectorList()

        val oldSize = vec.size

        testRemoveAtOnSublist(vec, 14, 37) { 0 }
        testRemoveAtOnSublist(vec, 2, 18) { it.size / 4 }
        testRemoveAtOnSublist(vec, 16, 45) { it.size / 2 }
        testRemoveAtOnSublist(vec, 26, 39) { 3 * it.size / 4 }
        testRemoveAtOnSublist(vec, 33, 44) { it.lastIndex }

        assertEquals(vec.size, oldSize - 5)
    }

    @Test
    fun testRemoveAll() {
        val vec1 = (1 .. 50).toVectorList()
        val fullyInRange = (15 .. 25).toSet()
        testRemoveAllOnSublist(vec1, fullyInRange, 11, 40, 11, true)

        val vec2 = (1 .. 50).toVectorList()
        val partiallyInRange = (5 .. 15).toList()
        testRemoveAllOnSublist(vec2, partiallyInRange, 1, 7, 3, true)

        val vec3 = (1 .. 50).toVectorList()
        val notInRange = (25 .. 35).toHashSet()
        testRemoveAllOnSublist(vec3, notInRange, 15, 23, 0, false)

        val vec4 = vectorListOf(1, 2, 3, 3, 3, 2, 1, 1)
        testRemoveAllWithSublistOnBaseList(vec4, 4, 8, listOf(3, 3, 2, 1))
    }

    @Test
    fun testDelete() {
        val vec1 = (1 .. 50).toVectorList()
        val fullyInRange = (15 .. 25).toSet()
        testDeleteOnSublist(vec1, fullyInRange, 11, 40, 11)

        val vec2 = (1 .. 50).toVectorList()
        val partiallyInRange = (5 .. 15).toList()
        testDeleteOnSublist(vec2, partiallyInRange, 1, 7, 3)

        val vec3 = (1 .. 50).toVectorList()
        val notInRange = (25 .. 35).toHashSet()
        testDeleteOnSublist(vec3, notInRange, 15, 23, 0)

        val vec4 = vectorListOf(1, 2, 3, 3, 3, 2, 1, 1)
        testDeleteWithSublistOnBaseList(vec4, 4, 8, listOf(3, 3, 2, 1))
    }

    @Test
    fun testRemoveAllOf() {
        TODO()
    }

    @Test
    fun testRemoveAmount() {
        TODO()
    }

    @Test
    fun testRetainAll() {
        TODO()
    }

    @Test
    fun testKeep() {
        TODO()
    }

    @Test
    fun testRemoveFromBack() {
        val vec1 = (1 .. 30).toVectorList()
        testRemoveFromBackOnSublist(vec1, 13, 24, 6)

        val vec2 = (1 .. 30).toVectorList()
        testRemoveFromBackOnSublist(vec2, 2, 8, 10)

        val vec3 = (1 .. 30).toVectorList()
        val sub3 = vec3.subList(10, 20)
        testRemoveFromBackWithNegativeAmount(sub3, -1)
    }

    @Test
    fun testRemoveRange() {
        val vec1 = (1 .. 50).toVectorList()
        testRemoveRangeOnSublist(vec1, 5, 29, { it.size / 4 }, { 3 * it.size / 4 })

        val vec2 = (1 .. 50).toVectorList()
        testRemoveRangeOnSublist(vec2, 31, 49, { 0 }, { it.size / 2 })

        val vec3 = (1 .. 50).toVectorList()
        testRemoveRangeOnSublist(vec3, 29, 38, { it.size / 2 }, { it.size })
    }

    @Test
    fun testClear() {
        val vec = (1 .. 25).toVectorList()

        val startIndex = 4
        val endIndex = 21

        testClearOnSublist(vec, startIndex, endIndex)
    }

    @Test
    fun testContains() {
        TODO()
    }

    @Test
    fun testContainsAll() {
        TODO()
    }

    @Test
    fun testIndexOf() {
        TODO()
    }

    @Test
    fun testLastIndexOf() {
        TODO()
    }

    @Test
    fun testSeparationPoint() {
        TODO()
    }

    @Test
    fun testSeparate() {
        TODO()
    }

    @Test
    fun testStableSeparate() {
        TODO()
    }

    @Test
    fun testRotate() {
        TODO()
    }

    @Test
    fun testIntersperse() {
        TODO()
    }

    @Test
    fun testEquals() {
        val vec = vectorListOf(1, 2, 3, 0, 1, 2, 3)

        val sub1 = vec.subList(0, 3)
        val sub2 = vec.subList(4, 7)

        testEquals(sub1, sub2, true)

        val sub3 = vec.subList(0, 4)
        val sub4 = vec.subList(4, 7)

        testEquals(sub3, sub4, false)

        val sub5 = vec.subList(1, 4)
        val sub6 = vec.subList(2, 6)

        testEquals(sub5, sub6, false)
    }

    @Test
    fun testIsPermutationOf() {
        val vec = vectorListOf(1, 2, 3, 0, 1, 2, 3, 0, 1, 2, 3, 0)

        val sub1 = vec.subList(0, 4)
        val sub2 = vec.subList(3, 7)

        testIsPermutationOf(sub1, sub2, true)

        val sub3 = vec.subList(1, 5)
        val sub4 = vec.subList(8, 12)

        testIsPermutationOf(sub3, sub4, true)

        val sub5 = vec.subList(0, 3)
        val sub6 = vec.subList(1, 4)

        testIsPermutationOf(sub5, sub6, false)
    }

    @Test
    fun testHashCode() {
        val vec = vectorListOf(1, 2, 3, 0, 1, 2, 3)

        val sub1 = vec.subList(0, 3)
        val sub2 = vec.subList(4, 7)

        testHashCode(sub1, sub2)
    }

    @Test
    fun testCompare() {
        TODO()
    }

    @Test
    fun testNext() {
        val vec = (1 .. 8).toVectorList()

        val startIndex = 2
        val endIndex = 7
        val sub = vec.subList(startIndex, endIndex)

        testNext(sub)
    }

    @Test
    fun testPrev() {
        val vec = (8 downTo 1).toVectorList()

        val startIndex = 2
        val endIndex = 7
        val sub = vec.subList(startIndex, endIndex)

        testPrev(sub)
    }

    @Test
    fun testIsSorted() {
        TODO()
    }

    @Test
    fun testIsSortedUntil() {
        TODO()
    }

    @Test
    fun testToString() {
        val vec = (1 .. 10).toVectorList()

        val empty = vec.subList(0, 0)
        testToString(empty, "[]")

        val single = vec.subList(5, 6)
        testToString(single, "[6]")

        val multiple = vec.subList(3, 7)
        testToString(multiple, "[4, 5, 6, 7]")
    }
}
