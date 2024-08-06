package collectionsTest

import collections.*
import kotlin.test.*
import org.junit.jupiter.api.assertDoesNotThrow

class VectorListTest {
    @Test
    fun testConstructor() {
        assertDoesNotThrow{ VectorList<Int>(0) }
        assertFailsWith<IllegalArgumentException>{ VectorList<Int>(-1) }
    }

    @Test
    fun testVectorListOf() {
        val vec1 = vectorListOf<Int>()
        val vec2 = vectorListOf(1, 2, 3)

        assertTrue(vec1.isEmpty())
        assertFalse(vec2.isEmpty())

        assertEquals(vec2.capacity, 3)
    }

    @Test
    fun testToVectorList() {
        val list = (1 .. 10).toSet()
        val seq = (10 downTo -10).asSequence()
        val range = -7 until 11 step 3
        val array = arrayOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)

        val vecFromList = list.toVectorList()
        val vecFromSeq = seq.toVectorList()
        val vecFromRange = range.toVectorList()
        val vecFromArray = array.toVectorList()

        this.testCollectionEquality(vecFromList.asSequence(), list.asSequence())
        this.testCollectionEquality(vecFromRange.asSequence(), range.asSequence())
        this.testCollectionEquality(vecFromSeq.asSequence(), seq)
        this.testCollectionEquality(vecFromArray.asSequence(), array.asSequence())

        assertEquals(list.size, vecFromList.capacity)
        assertTrue(seq.count() <= vecFromSeq.capacity)
        assertEquals(range.count(), vecFromRange.capacity)
        assertEquals(array.size, vecFromArray.size)
    }

    private fun testCollectionEquality(left: Sequence<*>, right: Sequence<*>) {
        for ((leftItem, rightItem) in left.zip(right)) {
            assertEquals(leftItem, rightItem)
        }
    }

    @Test
    fun testIsRandomAccess() {
        val vec = vectorListOf<Int>()

        assertTrue(vec.isRandomAccess)
    }

    @Test
    fun testSize() {
        val vec = vectorListOf<Int>()
        assertEquals(0, vec.size)

        this.testSizeAfterAdd(vec)
        this.testSizeAfterRemove(vec)
    }

    private fun testSizeAfterAdd(vec: VectorList<Int>) {
        val amount = 5

        vec.addAll(0 until amount)

        assertEquals(amount, vec.size)
    }

    private fun testSizeAfterRemove(vec: VectorList<Int>) {
        val amountToRemove = 3
        val leftOver = vec.size - amountToRemove

        repeat(amountToRemove) {
            vec.removeLast()
        }

        assertEquals(leftOver, vec.size)
    }

    @Test
    fun testIsEmpty() {
        val vec = vectorListOf<Int>()
        assertTrue(vec.isEmpty())

        vec.add(1)
        assertFalse(vec.isEmpty())

        vec.removeLast()
        assertTrue(vec.isEmpty())
    }

    @Test
    fun testCapacity() {
        val initialCapacity = 50

        val vec = VectorList<Int>(initialCapacity)
        assertEquals(initialCapacity, vec.capacity)

        this.testCapacityAfterAddingUpToCapacity(vec)
        this.testCapacityAfterGoingOneOver(vec)
        this.testCapacityAfterRemoving(vec)
    }

    private fun testCapacityAfterAddingUpToCapacity(vec: VectorList<Int>) {
        val capacity = vec.capacity

        for (num in 0 until capacity) {
            assertEquals(capacity, vec.capacity)

            vec.add(num)
        }

        assertEquals(capacity, vec.capacity)
    }

    private fun testCapacityAfterGoingOneOver(vec: VectorList<Int>) {
        val capacityBeforeAdd = vec.capacity

        vec.add(-1)

        assertTrue(capacityBeforeAdd < vec.capacity)
    }

    private fun testCapacityAfterRemoving(vec: VectorList<Int>) {
        vec.trimToSize()

        val capacityBeforeRemove = vec.capacity

        repeat((vec.capacity / 2) + 1) {
            assertEquals(capacityBeforeRemove, vec.capacity)

            vec.removeLast()
        }

        assertTrue(capacityBeforeRemove > vec.capacity)
    }

    @Test
    fun testEnsureCapacity() {
        val initialCapacity = 8
        val capacityChange = 3

        val vec = VectorList<Int>(initialCapacity)
        assertEquals(initialCapacity, vec.capacity)

        this.testSmallerCapacity(vec, capacityChange)
        this.testLargerCapacity(vec, capacityChange)

        assertFailsWith<IllegalArgumentException>{ vec.ensureCapacity(-1) }
    }

    private fun testSmallerCapacity(vec: VectorList<Int>, capacityChange: Int) {
        val currentCapacity = vec.capacity
        val smallerCapacity = vec.capacity - capacityChange

        vec.ensureCapacity(smallerCapacity)

        assertEquals(currentCapacity, vec.capacity)
        assertNotEquals(smallerCapacity, vec.capacity)
    }

    private fun testLargerCapacity(vec: VectorList<Int>, capacityChange: Int) {
        val currentCapacity = vec.capacity
        val largerCapacity = vec.capacity + capacityChange

        vec.ensureCapacity(largerCapacity)

        assertNotEquals(currentCapacity, vec.capacity)
        assertEquals(largerCapacity, vec.capacity)
    }

    @Test
    fun testTrimToSize() {
        val initialCapacity = 22
        val vec = VectorList<Int>(initialCapacity)
        assertEquals(initialCapacity, vec.capacity)

        this.testAfterIncreasingSize(vec)
        this.testWhenSizeEqualsCapacity(vec)
        this.testWhenSizeEqualsCapacityBeforeAdding(vec)
    }

    private fun testAfterIncreasingSize(vec: VectorList<Int>) {
        val targetSize = 15

        vec.addAll(0 until targetSize)

        assertEquals(targetSize, vec.size)
        assertNotEquals(targetSize, vec.capacity)

        vec.trimToSize()

        assertEquals(targetSize, vec.size)
        assertEquals(targetSize, vec.capacity)
    }

    private fun testWhenSizeEqualsCapacity(vec: VectorList<Int>) {
        val beforeSize = vec.size
        val beforeCapacity = vec.capacity

        assertEquals(beforeSize, beforeCapacity)

        vec.trimToSize()

        val afterSize = vec.size
        val afterCapacity = vec.capacity

        assertEquals(afterSize, afterCapacity)
        assertEquals(beforeSize, afterSize)
        assertEquals(beforeCapacity, afterCapacity)
    }

    private fun testWhenSizeEqualsCapacityBeforeAdding(vec: VectorList<Int>) {
        assertEquals(vec.size, vec.capacity)

        val oldCapacity = vec.capacity

        vec.add(100)

        assertNotEquals(oldCapacity, vec.capacity)
    }

    @Test
    fun testGet() {
        val vec = VectorList<Int>()

        this.testGetAfterAdding(vec)
        this.testGetAfterAddingAtIndex(vec)

        assertFailsWith<IndexOutOfBoundsException>{ vec[-1] }
        assertFailsWith<IndexOutOfBoundsException>{ vec[vec.size] }
    }

    private fun testGetAfterAdding(vec: VectorList<Int>) {
        val targetSize = 11

        vec.addAll(0 until targetSize)

        for (index in vec.indices) {
            assertEquals(index, vec[index])
        }

        val newItem = -1
        vec.add(newItem)

        assertEquals(newItem, vec[vec.lastIndex])
    }

    private fun testGetAfterAddingAtIndex(vec: VectorList<Int>) {
        val newItem1 = -2
        val index1 = vec.lastIndex
        vec.add(index1, newItem1)
        assertEquals(newItem1, vec[index1])

        val newItem2 = 121
        val index2 = vec.size / 2
        vec.add(index2, newItem2)
        assertEquals(newItem2, vec[index2])

        val newItem3 = -11
        val index3 = 0
        vec.add(index3, newItem3)
        assertEquals(newItem3, vec[index3])
    }

    @Test
    fun testWrapGet() {
        val size = 10
        val vec = (0 until size).toVectorList()

        for (index in vec.indices) {
            val item1 = assertDoesNotThrow{ vec.wrapGet(index) }
            val item2 = assertDoesNotThrow{ vec.wrapGet(index - size) }
            val item3 = assertDoesNotThrow{ vec.wrapGet(index + size) }

            assertEquals(item1, item2)
            assertEquals(item2, item3)
        }

        assertDoesNotThrow{ vec.wrapGet(-1) }
        assertDoesNotThrow{ vec.wrapGet(vec.size) }
    }

    @Test
    fun testTryGet() {
        val vec = (0 until 10).toVectorList()

        for (index in vec.indices) {
            val result = vec.tryGet(index)

            assertTrue(result.isSuccess)
        }

        val before = vec.tryGet(-1)
        val after = vec.tryGet(vec.size)

        assertTrue(before.isFailure)
        assertTrue(after.isFailure)
    }

    @Test
    fun testSafeGet() {
        val vec = (0 until 15).toVectorList()

        for (index in vec.indices) {
            val option = vec.safeGet(index)

            assertTrue(option.isSome())
        }

        val before = vec.safeGet(-1)
        val after = vec.safeGet(vec.size)

        assertTrue(before.isNone())
        assertTrue(after.isNone())
    }

    @Test
    fun testSet() {
        val targetSize = 12
        val vec = (0 .. targetSize).toVectorList()

        val newItem = -2
        val index = 5
        val oldItem = vec.set(index, newItem)

        assertNotEquals(oldItem, newItem)
        assertEquals(newItem, vec[index])
        assertNotEquals(oldItem, vec[index])

        assertFailsWith<IndexOutOfBoundsException>{ vec[-1] = newItem }
        assertFailsWith<IndexOutOfBoundsException>{ vec[vec.size] = newItem }
    }

    @Test
    fun testWrapSet() {
        val size = 10
        val vec = (0 until size).toVectorList()
        val index = 7

        this.testWrapSetAtIndex(vec, index, -1)
        this.testWrapSetAtIndex(vec, index - size, -2)
        this.testWrapSetAtIndex(vec, index + size, -3)
        this.testWrapSetOutOfBounds(vec, 11)
    }

    private fun testWrapSetAtIndex(vec: VectorList<Int>, targetIndex: Int, newItem: Int) {
        val oldItem = assertDoesNotThrow{ vec.wrapSet(targetIndex, newItem) }

        assertNotEquals(oldItem, newItem)
        assertEquals(newItem, vec.wrapGet(targetIndex))
        assertNotEquals(oldItem, vec.wrapGet(targetIndex))
    }

    private fun testWrapSetOutOfBounds(vec: VectorList<Int>, newItem: Int) {
        assertDoesNotThrow{ vec.wrapSet(-1, newItem) }
        assertDoesNotThrow{ vec.wrapSet(vec.size, newItem) }

        assertEquals(newItem, vec.wrapGet(-1))
        assertEquals(newItem, vec.wrapGet(vec.size))
    }

    @Test
    fun testTrySet() {
        val vec = (0 until 10).toVectorList()

        this.testTrySetInBounds(vec)
        this.testTrySetOutOfBounds(vec)
    }

    private fun testTrySetInBounds(vec: VectorList<Int>) {
        val newItem = 100
        val index = vec.size / 2
        val result = vec.trySet(index, newItem)

        assertTrue(result.isSuccess)

        assertEquals(newItem, vec[index])
        assertNotEquals(vec[index], result.getOrThrow())
        assertNotEquals(newItem, result.getOrThrow())
    }

    private fun testTrySetOutOfBounds(vec: VectorList<Int>) {
        val index1 = -1
        val index2 = vec.size

        val newItem = -10

        val result1 = vec.trySet(index1, newItem)
        val result2 = vec.trySet(index2, newItem)

        assertTrue(result1.isFailure)
        assertTrue(result2.isFailure)
    }

    @Test
    fun testSafeSet() {
        val vec = (0 until 15).toVectorList()

        this.testSafeSetInBounds(vec)
        this.testSafeSetOutOfBounds(vec)
    }

    private fun testSafeSetInBounds(vec: VectorList<Int>) {
        val newItem = -101

        val index1 = vec.size / 4
        val index2 = 3 * vec.size / 4

        val option1 = vec.safeSet(index1, newItem)
        val option2 = vec.safeSet(index2, newItem)

        assertTrue(option1.isSome())
        assertTrue(option2.isSome())

        assertEquals(newItem, vec[index1])
        assertEquals(newItem, vec[index2])

        assertNotEquals(vec[index1], option1.getOrNull())
        assertNotEquals(vec[index2], option2.getOrNull())

        assertNotEquals(newItem, option1.getOrNull())
        assertNotEquals(newItem, option2.getOrNull())
    }

    private fun testSafeSetOutOfBounds(vec: VectorList<Int>) {
        val newItem = 38

        val option1 = vec.safeSet(-1, newItem)
        val option2 = vec.safeSet(vec.size, newItem)

        assertTrue(option1.isNone())
        assertTrue(option2.isNone())
    }

    @Test
    fun testAdd() {
        val initialCapacity = 21
        val vec = VectorList<Int>(initialCapacity)

        repeat(initialCapacity) {
            val preSize = vec.size

            assertTrue(vec.add(it))

            assertEquals(it, vec[vec.lastIndex])
            assertEquals(preSize, vec.size - 1)
        }
    }

    @Test
    fun testAddWithIndexing() {
        val initialCapacity = 2
        val vec = VectorList<Int>(initialCapacity)

        this.testAfterInsertingAtEnd(vec)
        this.testAfterInsertingAtBeginning(vec, initialCapacity)
        this.testAfterInsertingAtMiddle(vec)

        assertFailsWith<IndexOutOfBoundsException>{ vec.add(-1, Int.MAX_VALUE) }
        assertFailsWith<IndexOutOfBoundsException>{ vec.add(vec.size + 1, Int.MAX_VALUE) }
    }

    private fun testAfterInsertingAtBeginning(vec: VectorList<Int>, initialCapacity: Int) {
        repeat(initialCapacity) {
            vec.add(0, it)

            assertEquals(it, vec[0])
        }
    }

    private fun testAfterInsertingAtEnd(vec: VectorList<Int>) {
        val endItem = -1

        vec.add(vec.size, endItem)

        assertEquals(endItem, vec[vec.lastIndex])
    }

    private fun testAfterInsertingAtMiddle(vec: VectorList<Int>) {
        val midIndex = vec.size / 2
        val midItem = Int.MAX_VALUE

        vec.add(midIndex, midItem)

        assertEquals(midItem, vec[midIndex])
    }

    @Test
    fun testAddAll() {
        val max = 14
        val vec = VectorList<Int>()

        val items = 0 until max
        val listToBeInserted = items.toList()
        val setToBeInserted = items.toSet()

        this.testCollectionToBeInserted(vec, listToBeInserted)
        assertEquals(max, vec.size)

        this.testCollectionToBeInserted(vec, setToBeInserted)
        assertEquals(max + max, vec.size)
    }

    private fun testCollectionToBeInserted(vec: VectorList<Int>, collection: Collection<Int>) {
        val initialSize = vec.size
        vec.addAll(collection)
        val newSize = vec.size

        val iter = collection.iterator()

        for (index in initialSize until newSize) {
            assertEquals(iter.next(), vec[index])
        }
    }

    @Test
    fun testAddAllWithIndexing() {
        val initialSize = 10
        val vec = VectorList<Int>()
        val items = (0 until initialSize).toList()

        vec.addAll(items)

        this.testAddAllAtEnd(vec)
        this.testAddAllAtBeginning(vec)
        this.testAddAllAtMiddle(vec)

        assertFailsWith<IndexOutOfBoundsException>{ vec.addAll(-1, items) }
        assertFailsWith<IndexOutOfBoundsException>{ vec.addAll(vec.size + 1, items ) }
    }

    private fun testAddAllAtBeginning(vec: VectorList<Int>) {
        val amount = 5
        val items = (0 until amount).toList()

        vec.addAll(0, items)

        val vecIter = vec.listIterator(0)
        val otherIter = items.iterator()

        while (otherIter.hasNext()) {
            assertEquals(vecIter.next(), otherIter.next())
        }
    }

    private fun testAddAllAtEnd(vec: VectorList<Int>) {
        val amount = 8
        val items = (0 until amount).toSet()

        vec.addAll(vec.size, items)

        val vecIter = vec.listIterator(vec.size - amount)
        val otherIter = items.iterator()

        while (otherIter.hasNext()) {
            assertEquals(vecIter.next(), otherIter.next())
        }
    }

    private fun testAddAllAtMiddle(vec: VectorList<Int>) {
        val amount = 4
        val items = (0 until amount).toSet()
        val midIndex = vec.size / 2

        vec.addAll(midIndex, items)

        val vecIter = vec.listIterator(midIndex)
        val otherIter = items.iterator()

        while (otherIter.hasNext()) {
            assertEquals(vecIter.next(), otherIter.next())
        }
    }

    @Test
    fun testRemove() {
        val vec = VectorList<Int>(10)

        vec.addAll(0 until 13)
        vec.add(0)

        assertTrue(vec.remove(0))
        assertTrue(vec.remove(12))
        assertTrue(vec.remove(7))

        assertFalse(vec.remove(-1))
        assertFalse(vec.remove(13))
        assertFalse(vec.remove(7))

        assertTrue(0 in vec)
    }

    @Test
    fun testRemoveAll() {
        val vec = vectorListOf<Int>()

        vec.addAll(0 until 10)

        val fullyInRange = (2 until 7).toHashSet()
        val partiallyInRange = (-2 until 1).toHashSet()
        val notInRange = (-10 until -1).toHashSet()

        assertTrue(vec.removeAll(fullyInRange))
        assertTrue(vec.removeAll(partiallyInRange))
        assertFalse(vec.removeAll(notInRange))
    }

    @Test
    fun testRemoveAt() {
        val vec = vectorListOf<Int>()

        vec.addAll(20 downTo 0)

        this.testRemovingAtEnds(vec)
        this.testRemovingInMiddle(vec)

        assertFailsWith<IndexOutOfBoundsException>{ vec.removeAt(-1) }
        assertFailsWith<IndexOutOfBoundsException>{ vec.removeAt(vec.size) }
    }

    private fun testRemovingAtEnds(vec: VectorList<Int>) {
        val startSize = vec.size

        val first = vec[0]
        val last = vec[vec.lastIndex]

        val removedFirst = vec.removeAt(0)
        val removedLast = vec.removeAt(vec.lastIndex)

        assertEquals(first, removedFirst)
        assertEquals(last, removedLast)

        assertEquals(startSize - 2, vec.size)

        assertFalse(first in vec)
        assertFalse(last in vec)
    }

    private fun testRemovingInMiddle(vec: VectorList<Int>) {
        val startSize = vec.size

        val midIndex1 = vec.size / 2
        val mid1 = vec[midIndex1]
        val removedMid1 = vec.removeAt(midIndex1)

        val midIndex2 = vec.size / 4
        val mid2 = vec[midIndex2]
        val removedMid2 = vec.removeAt(midIndex2)

        val midIndex3 = 3 * vec.size / 4
        val mid3 = vec[midIndex3]
        val removedMid3 = vec.removeAt(midIndex3)

        assertEquals(mid1, removedMid1)
        assertEquals(mid2, removedMid2)
        assertEquals(mid3, removedMid3)

        assertEquals(startSize - 3, vec.size)

        assertFalse(mid1 in vec)
        assertFalse(mid2 in vec)
        assertFalse(mid3 in vec)
    }

    @Test
    fun testRetainAll() {
        val vec = VectorList<Int>()
        val range = 0 .. 100
        val divisor = 2

        vec.addAll(range)
        val changed = vec.retainAll((range step divisor).toSet())

        for (item in vec) {
            assertTrue(0 == item % divisor)
        }
        assertTrue(changed)


        val empty = listOf<Int>()

        assertFalse(vec.retainAll(vec))
        assertTrue(vec.retainAll(empty))
        assertTrue(vec.isEmpty())
    }

    @Test
    fun testClear() {
        val vec = vectorListOf<Int>()
        assertTrue(vec.isEmpty())

        vec.addAll(0 until 10)
        assertFalse(vec.isEmpty())

        vec.clear()
        assertTrue(vec.isEmpty())

        assertFailsWith<IndexOutOfBoundsException>{ vec[0] }
        assertFailsWith<IndexOutOfBoundsException>{ vec[vec.lastIndex] }
    }

    @Test
    fun testDelete() {
        val range = 0 .. 50

        val vec = range.toVectorList()
        val divisibleBy10 = (range step 10).toVectorList()
        val divisibleBy5 = (range step 5).toVectorList()
        val divisibleBy2 = (range step 2).toVectorList()

        assertEquals(6, vec.delete(divisibleBy10))
        assertEquals(5, vec.delete(divisibleBy5))
        assertEquals(20, vec.delete(divisibleBy2))
    }

    @Test
    fun testKeep() {

    }

    @Test
    fun testRemoveFromBack() {
        val vec = (1 .. 10).toVectorList()

        val amountRemoved1 = vec.removeFromBack(6)

        assertEquals(6, amountRemoved1)
        assertEquals(4, vec.size)

        val amountRemoved2 = vec.removeFromBack(5)

        assertEquals(4, amountRemoved2)
        assertTrue(vec.isEmpty())
    }

    @Test
    fun testRemoveRange() {
        val vec = (0 until 20).toVectorList()

        val range1 = (vec.size / 5) .. (vec.size / 3)
        val range2 = (vec.size / 2) .. (3 * vec.size / 4)

        this.testRemovingRange(vec, range1)
        this.testRemovingRange(vec, range2)
    }

    private fun testRemovingRange(vec: VectorList<Int>, range: IntRange) {
        val newSize = vec.size - (range.last - range.first)

        vec.removeRange(range.first, range.last)

        assertEquals(newSize, vec.size)
    }

    @Test
    fun testContains() {
        val vec = VectorList<Int>()
        val range = 0 until 10

        this.testSearchAfterAdding(vec, range)
        this.testSearchAfterRemovingByElement(vec)
        this.testSearchAfterRemovingByIndex(vec)
        this.testDuplicates(vec)
    }

    private fun testSearchAfterAdding(vec: VectorList<Int>, range: IntRange) {
        vec.addAll(range)

        for (num in range) {
            assertTrue(num in vec)
        }
    }

    private fun testSearchAfterRemovingByElement(vec: VectorList<Int>) {
        val first = 0
        val second = 5
        val third = 9

        vec.remove(first)
        vec.remove(second)
        vec.remove(third)

        assertFalse(first in vec)
        assertFalse(second in vec)
        assertFalse(third in vec)
    }

    private fun testSearchAfterRemovingByIndex(vec: VectorList<Int>) {
        val first = vec.removeAt(0)
        assertFalse(first in vec)

        val last = vec.removeAt(vec.lastIndex)
        assertFalse(last in vec)

        val mid = vec.removeAt(vec.size / 2)
        assertFalse(mid in vec)
    }

    private fun testDuplicates(vec: VectorList<Int>) {
        val value = 100

        vec.add(0, value)
        vec.add(vec.lastIndex, value)
        vec.add(vec.size / 2, value)

        vec.remove(value)
        assertTrue(value in vec)

        vec.remove(value)
        assertTrue(value in vec)

        vec.remove(value)
        assertFalse(value in vec)
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

                assertTrue{ vec.containsAll(sub) }
            }
        }
    }

    private fun testIfContainsOtherCollections() {
        val range = 1 .. 20
        val vec = range.toVectorList()

        assertTrue{ vec.containsAll((range step 2).toList()) }
        assertTrue{ vec.containsAll((range step 3).toHashSet()) }
        assertTrue{ vec.containsAll((range step 5).toSet()) }

        val set1 = hashSetOf(1, 20, 5, 9)
        val set2 = hashSetOf(-1, 1, 2, 3, 0)

        assertTrue{ vec.containsAll(set1) }
        assertFalse{ vec.containsAll(set2) }
    }

    @Test
    fun testIndexOf() {
        val range = (0 .. 20).asSequence()
        val vec = (range + range).toVectorList()

        val size = range.count()

        for (index in (0 until size).reversed()) {
            @Suppress("UnnecessaryVariable", "RedundantSuppression")
            val value = index

            val index1 = vec.indexOf(value)
            val index2 = vec.index(index + size, value)

            assertEquals(vec[index1], vec[index2])
            assertNotEquals(index1, index2)
        }

        assertEquals(-1, vec.indexOf(vec.first() - 1))
        assertEquals(-1, vec.indexOf(vec.last() + 1))
    }

    @Test
    fun testIndexOfWithPredicate() {

    }

    @Test
    fun testLastIndexOf() {
        val range = (0 .. 20).asSequence()
        val vec = (range + range).toVectorList()

        val size = range.count()

        for (index in size until size + size) {
            val value = index - size

            val index1 = vec.lastIndexOf(value)
            val index2 = vec.lastIndex(index - size, value)

            assertEquals(vec[index1], vec[index2])
            assertNotEquals(index1, index2)
        }

        assertEquals(-1, vec.lastIndexOf(vec.first() - 1))
        assertEquals(-1, vec.lastIndexOf(vec.last() + 1))
    }

    @Test
    fun testLastIndexOfWithPredicate() {

    }

    @Test
    fun testIndices() {

    }

    @Test
    fun testIndicesWithPredicate() {

    }

    @Test
    fun testFind() {

    }

    @Test
    fun testEquals() {
        val range = 0 .. 100

        val vec1 = range.toVectorList()
        val vec2 = (range step 2).toVectorList()
        val vec3 = (range step 4).toVectorList()

        this.testCopyAsEqual(vec1, vec2, vec3)
        this.testComparison(vec1, vec2, vec3)

        val vec1Dup = range.toVectorList()
        val vec2Dup = (range step 2).toVectorList()
        val vec3Dup = (range step 4).toVectorList()

        this.testDuplicatesAsEqual(vec1 to vec1Dup, vec2 to vec2Dup, vec3 to vec3Dup)
    }

    private fun testCopyAsEqual(vararg vecSet: VectorList<Int>) {
        for (vec in vecSet) {
            val copy1 = vec.toVectorList()
            val copy2 = vec.toVectorList()

            assertNotSame(vec, copy1)
            assertNotSame(copy1, copy2)
            assertNotSame(copy2, vec)

            assertEquals(vec, copy1)
            assertEquals(copy1, copy2)
            assertEquals(copy2, vec)
        }
    }

    private fun testComparison(vararg vecSet: VectorList<Int>) {
        for (index1 in vecSet.indices) {
            for (index2 in vecSet.indices) {
                this.testVecEquality(vecSet, index1, index2)
            }
        }
    }

    private fun testVecEquality(vecSet: Array<out VectorList<Int>>, index1: Int, index2: Int) {
        if (index1 == index2) {
            assertSame(vecSet[index1], vecSet[index2])
            assertEquals(vecSet[index1], vecSet[index2])
        }
        else {
            assertNotSame(vecSet[index1], vecSet[index2])
            assertNotEquals(vecSet[index1], vecSet[index2])
        }
    }

    private fun testDuplicatesAsEqual(vararg vecPairSet: Pair<VectorList<Int>, VectorList<Int>>) {
        for ((orig, dup) in vecPairSet) {
            assertEquals(orig, dup)
            assertNotSame(orig, dup)
        }
    }

    @Test
    fun testHashCode() {
        val vec1 = (1 .. 100 step 5).toVectorList()
        val vec2 = (1 .. 100 step 4).toVectorList()
        val vec3 = (1 .. 100 step 5).toVectorList()

        assertNotEquals(vec1, vec2)
        assertEquals(vec1, vec3)
        assertNotEquals(vec2, vec3)

        val hash1 = vec1.hashCode()
        val hash2 = vec2.hashCode()
        val hash3 = vec3.hashCode()

        assertNotEquals(hash1, hash2)
        assertEquals(hash1, hash3)
        assertNotEquals(hash2, hash3)
    }

    @Test
    fun testCompare() {

    }

    @Test
    fun testToString() {
        assertEquals("[]", vectorListOf<Int>().toString())
        assertEquals("[1000]", vectorListOf(1000).toString())
        assertEquals("[0, 4, 8, 12, 16, 20]", vectorListOf(0, 4, 8, 12, 16, 20).toString())
    }
}

class VectorIteratorTest {
    @Test
    fun testConstructor() {
        assertDoesNotThrow{ vectorListOf<Int>().iterator() }
        assertDoesNotThrow{ vectorListOf(1, 2, 3, 4, 5).iterator() }
    }

    @Test
    fun testHasNext() {
        this.testHasNextIfEmpty()
        this.testHasNextIfNonempty()
    }

    private fun testHasNextIfEmpty() {
        val iter = vectorListOf<Int>().iterator()

        assertFalse(iter.hasNext())
    }

    private fun testHasNextIfNonempty() {
        val max = 16
        val vec = (0 until max).toVectorList()
        val iter = vec.iterator()

        repeat(max) {
            assertTrue(iter.hasNext())
            assertDoesNotThrow{ iter.next() }
        }

        assertFalse(iter.hasNext())
    }

    @Test
    fun testNext() {
        assertFailsWith<NoSuchElementException>{ vectorListOf<Int>().iterator().next() }

        this.testNextIfNonempty()
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

    @Test
    fun testRemove() {
        val vec = (0 .. 100 step 5).toVectorList()
        val iter = vec.iterator()

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

        for (item in vec) {
            assertTrue(0 == item % 10)
        }

        assertEquals(initialSize - amountRemoved, vec.size)

        assertDoesNotThrow{ iter.remove() }
        assertFailsWith<IllegalStateException>{ iter.remove() }

        assertTrue(100 !in vec)
    }
}

class VectorListIteratorTest {
    @Test
    fun testConstructor() {
        val vec = vectorListOf(1, 2, 3, 4, 5, 6, 7, 8)

        for (index in 0 .. vec.size) {
            assertDoesNotThrow{ vec.listIterator(index) }
        }

        assertFailsWith<IndexOutOfBoundsException>{ vec.listIterator(-1) }
        assertFailsWith<IndexOutOfBoundsException>{ vec.listIterator(vec.size + 1) }

        assertDoesNotThrow{ vectorListOf<Int>().listIterator() }
    }

    @Test
    fun testPreviousIndex() {
        val vec = (1 .. 13).toVectorList()

        for (index in 0 .. vec.size) {
            val iter = vec.listIterator(index)

            assertEquals(index - 1, iter.previousIndex())
        }
    }

    @Test
    fun testNextIndex() {
        val vec = (1 .. 13).toVectorList()

        for (index in 0 .. vec.size) {
            val iter = vec.listIterator(index)

            assertEquals(index, iter.nextIndex())
        }
    }

    @Test
    fun testHasPrevious() {

    }

    @Test
    fun testHasNext() {

    }

    @Test
    fun testPrevious() {

    }

    @Test
    fun testNext() {

    }

    @Test
    fun testSet() {

    }

    @Test
    fun testRemove() {

    }

    @Test
    fun testAdd() {

    }
}

class VectorSliceListTest {
    @Test
    fun testConstructor() {
        val vec = (1 .. 10).toVectorList()
        assertDoesNotThrow{ vec.subList(0, vec.size) }

        this.testInRange(vec)
        this.testOutOfRange(vec)
    }

    private fun testInRange(vec: VectorList<Int>) {
        for (fromIndex in vec.indices) {
            for (toIndex in 0 .. vec.size) {
                this.testRange(vec, fromIndex, toIndex)
            }
        }
    }

    private fun testRange(vec: VectorList<Int>, fromIndex: Int, toIndex: Int) {
        if (fromIndex <= toIndex) {
            assertDoesNotThrow{ vec.subList(fromIndex, toIndex) }
        }
        else {
            assertFailsWith<IllegalArgumentException>{ vec.subList(fromIndex, toIndex) }
        }
    }

    private fun testOutOfRange(vec: VectorList<Int>) {
        assertFailsWith<IndexOutOfBoundsException>{ vec.subList(-2, -1) }
        assertFailsWith<IndexOutOfBoundsException>{ vec.subList(vec.size, vec.size + 1) }

        for (fromIndex in vec.indices) {
            assertFailsWith<IllegalArgumentException>{ vec.subList(fromIndex, -1) }
            assertFailsWith<IndexOutOfBoundsException>{ vec.subList(fromIndex, vec.size + 1) }
        }

        for (toIndex in 0 .. vec.size) {
            assertFailsWith<IndexOutOfBoundsException>{ vec.subList(-1, toIndex) }
            assertFailsWith<IllegalArgumentException>{ vec.subList(vec.size + 1, toIndex) }
        }
    }

    @Test
    fun testSize() {
        val vec = (1 .. 23).toVectorList()

        for (fromIndex in 0 until vec.size) {
            for (toIndex in fromIndex .. vec.size) {
                val sub = vec.subList(fromIndex, toIndex)

                assertEquals(toIndex - fromIndex, sub.size)
            }
        }
    }

    @Test
    fun testIsEmpty() {
        val vec = (1 .. 27).toVectorList()

        for (fromIndex in 0 until vec.size) {
            for (toIndex in fromIndex .. vec.size) {
                this.testIfSublistEmpty(vec, fromIndex, toIndex)
            }
        }
    }

    private fun testIfSublistEmpty(vec: VectorList<Int>, fromIndex: Int, toIndex: Int) {
        val sub = vec.subList(fromIndex, toIndex)

        if (fromIndex == toIndex) {
            assertTrue(sub.isEmpty())
        }
        else {
            assertFalse(sub.isEmpty())
        }
    }

    @Test
    fun testGet() {
        val size = 40
        val fromIndex = 10
        val toIndex = 31

        val vec = (0 until size).toVectorList()
        val sub = vec.subList(fromIndex, toIndex)

        for (index in sub.indices) {
            val vecItem = assertDoesNotThrow{ vec[index + fromIndex] }
            val subItem = assertDoesNotThrow{ sub[index] }

            assertEquals(vecItem, subItem)
        }

        for (index in vec.indices) {
            val vecItem = assertDoesNotThrow{ vec[index] }
            val subIndex = index - fromIndex

            if (index < fromIndex || index >= toIndex) {
                assertFailsWith<IndexOutOfBoundsException>{ sub[subIndex] }
            }
            else {
                val subItem = assertDoesNotThrow{ sub[subIndex] }

                assertEquals(vecItem, subItem)
            }
        }

        assertTrue(fromIndex - 1 in vec.indices)
        assertTrue(toIndex in vec.indices)

        assertFailsWith<IndexOutOfBoundsException>{ sub[-1] }
        assertFailsWith<IndexOutOfBoundsException>{ sub[sub.size] }
    }

    @Test
    fun testSet() {

    }

    @Test
    fun testAdd() {

    }

    @Test
    fun testAddWithIndexing() {

    }

    @Test
    fun testAddAll() {

    }

    @Test
    fun testAddAllWithIndexing() {

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
    fun testRetainAll() {

    }

    @Test
    fun testClear() {

    }

    @Test
    fun testContains() {

    }

    @Test
    fun testContainsAll() {

    }

    @Test
    fun testIndexOf() {

    }

    @Test
    fun testLastIndexOf() {

    }

    @Test
    fun testSubList() {

    }

    @Test
    fun testEquals() {
        this.testWholeVectorForEquality()
        this.testDifferentSubvectors()
        this.testEmptySubvectors()
    }

    private fun testWholeVectorForEquality() {
        val vec = vectorListOf(1, 2, 3, 4, 5, 6, 7, 8, 9)
        val sub = vec.subList(0, vec.size)

        assertEquals(vec, sub)
    }

    private fun testDifferentSubvectors() {
        val range = 1 .. 19
        val vec1 = range.toVectorList()
        val vec2 = range.toVectorList()

        assertEquals(vec1, vec2)

        for (fromIndex in 0 until vec1.size) {
            for (toIndex in fromIndex .. vec1.size) {
                val sub1 = vec1.subList(fromIndex, toIndex)
                val sub2 = vec2.subList(fromIndex, toIndex)

                assertEquals(sub1, sub2)
            }
        }
    }

    private fun testEmptySubvectors() {
        val vec = vectorListOf(1, 2, 3, 4, 5, 6, 7, 8, 9)

        val mid1 = vec.size / 4
        val mid2 = 3 * vec.size / 4

        val sub1 = vec.subList(mid1, mid1)
        val sub2 = vec.subList(mid2, mid2)

        assertEquals(sub1, sub2)
    }

    @Test
    fun testHashCode() {

    }

    @Test
    fun testCompare() {

    }

    @Test
    fun testToString() {

    }
}
