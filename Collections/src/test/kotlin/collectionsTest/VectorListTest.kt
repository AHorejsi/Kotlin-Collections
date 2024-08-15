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

        val size1 = 0
        val size2 = 3

        assertEquals(size1, vec1.size)
        assertEquals(size2, vec2.size)

        assertTrue(size2 <= vec2.capacity)
    }

    @Test
    fun testToVectorList() {
        val set = (1 .. 10).toHashSet()
        val seq = (10 downTo -10).asSequence()
        val range = -7 until 11 step 3
        val array = arrayOf(-1, 4, 0, -6, 8, -9)

        val vecFromSet = set.toVectorList()
        val vecFromSeq = seq.toVectorList()
        val vecFromRange = range.toVectorList()
        val vecFromArray = array.toVectorList()

        this.testSequenceEquality(vecFromSet.asSequence(), set.asSequence())
        this.testSequenceEquality(vecFromRange.asSequence(), range.asSequence())
        this.testSequenceEquality(vecFromSeq.asSequence(), seq)
        this.testSequenceEquality(vecFromArray.asSequence(), array.asSequence())

        assertTrue(set.size <= vecFromSet.capacity)
        assertTrue(seq.count() <= vecFromSeq.capacity)
        assertTrue(range.count() <= vecFromRange.capacity)
        assertTrue(array.size <= vecFromArray.size)
    }

    private fun testSequenceEquality(left: Sequence<*>, right: Sequence<*>) {
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

        vec.addAll(11 .. 1908)

        this.testSizeAfterAdd(vec)
        this.testSizeAfterRemove(vec)
    }

    private fun testSizeAfterAdd(vec: VectorList<Int>) {
        val oldSize = vec.size
        val amount = 500

        vec.addAll(1 .. amount)

        assertEquals(amount + oldSize, vec.size)
    }

    private fun testSizeAfterRemove(vec: VectorList<Int>) {
        val amount = 315
        val leftOver = vec.size - amount

        vec.removeFromBack(amount)

        assertEquals(leftOver, vec.size)
    }

    @Test
    fun testIsEmpty() {
        val vec = vectorListOf<Int>()
        assertTrue(vec.isEmpty())

        vec.add(1)
        assertFalse(vec.isEmpty())

        vec.add(0)
        assertFalse(vec.isEmpty())

        vec.removeLast()
        assertFalse(vec.isEmpty())

        vec.removeLast()
        assertTrue(vec.isEmpty())
    }

    @Test
    fun testCapacity() {
        this.testCapacityWithAdding()
        this.testCapacityWithRemoving()
    }

    private fun testCapacityWithAdding() {
        val initialCapacity = 51

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

    private fun testCapacityWithRemoving() {
        val cap1 = 30
        val cap2 = 31

        val vec1 = (1 .. cap1).toVectorList()
        val vec2 = (1 .. cap2).toVectorList()

        this.testCapacityAfterRemoving(vec1)
        this.testCapacityAfterRemoving(vec2)
    }

    private fun testCapacityAfterRemoving(vec: VectorList<Int>) {
        val capacityBeforeRemove = vec.capacity
        val full = vec.size == vec.capacity

        vec.removeFromBack((vec.size / 2) - 1)
        assertTrue((vec.capacity == capacityBeforeRemove && full) || capacityBeforeRemove > vec.capacity)

        vec.removeLast()
        assertTrue(capacityBeforeRemove > vec.capacity)
    }

    @Test
    fun testEnsureCapacity() {
        val initialCapacity = 20
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
        this.testWhenSizeEqualsCapacityBeforeAdding(vec)
    }

    private fun testAfterIncreasingSize(vec: VectorList<Int>) {
        val targetSize = 8

        vec.addAll(0 until targetSize)

        assertEquals(targetSize, vec.size)
        assertNotEquals(targetSize, vec.capacity)

        vec.trimToSize()

        assertEquals(targetSize, vec.size)
        assertTrue(targetSize <= vec.capacity)
    }

    private fun testWhenSizeEqualsCapacityBeforeAdding(vec: VectorList<Int>) {
        val items = 0.replicate(vec.capacity - vec.size)
        vec.addAll(items)

        val oldCapacity = vec.capacity
        vec.add(-100)
        assertNotEquals(oldCapacity, vec.capacity)
    }

    @Test
    fun testGet() {
        val vec = vectorListOf<Int>()

        this.testGetAfterAdding(vec)
        this.testGetAfterAddingAtIndex(vec)

        assertFailsWith<IndexOutOfBoundsException>{ vec[-1] }
        assertFailsWith<IndexOutOfBoundsException>{ vec[vec.size] }
        assertFailsWith<IndexOutOfBoundsException>{ vec[Int.MIN_VALUE] }
        assertFailsWith<IndexOutOfBoundsException>{ vec[Int.MAX_VALUE] }
    }

    private fun testGetAfterAdding(vec: VectorList<Int>) {
        val values = (0 .. 11).toList()

        vec.addAll(values)

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
        val size = 18
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
        assertDoesNotThrow{ vec.wrapGet(Int.MIN_VALUE) }
        assertDoesNotThrow{ vec.wrapGet(Int.MAX_VALUE) }
    }

    @Test
    fun testTryGet() {
        val vec = (0 until 10).toVectorList()

        for (index in vec.indices) {
            val result = vec.tryGet(index)

            assertTrue(result.isSuccess)
        }

        assertFailsWith<IndexOutOfBoundsException>{ vec.tryGet(-1).getOrThrow() }
        assertFailsWith<IndexOutOfBoundsException>{ vec.tryGet(vec.size).getOrThrow() }
        assertFailsWith<IndexOutOfBoundsException>{ vec.tryGet(Int.MIN_VALUE).getOrThrow() }
        assertFailsWith<IndexOutOfBoundsException>{ vec.tryGet(Int.MAX_VALUE).getOrThrow() }
    }

    @Test
    fun testSafeGet() {
        val vec = (0 until 15).toVectorList()

        for (index in vec.indices) {
            val option = vec.safeGet(index)

            assertTrue(option.isSome())
        }

        assertTrue(vec.safeGet(-1).isNone())
        assertTrue(vec.safeGet(vec.size).isNone())
        assertTrue(vec.safeGet(Int.MIN_VALUE).isNone())
        assertTrue(vec.safeGet(Int.MAX_VALUE).isNone())
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

        val otherItem = -targetSize

        assertFailsWith<IndexOutOfBoundsException>{ vec[-1] = otherItem }
        assertFailsWith<IndexOutOfBoundsException>{ vec[vec.size] = otherItem }
        assertFailsWith<IndexOutOfBoundsException>{ vec[Int.MIN_VALUE] = otherItem }
        assertFailsWith<IndexOutOfBoundsException>{ vec[Int.MAX_VALUE] = otherItem }

        assertFalse(otherItem in vec)
    }

    @Test
    fun testWrapSet() {
        val size = 10
        val vec = (0 until size).toVectorList()
        val index = 7

        this.testWrapSetAtIndex(vec, index, -1)
        this.testWrapSetAtIndex(vec, index - size, -2)
        this.testWrapSetAtIndex(vec, index + size, -3)
        this.testWrapSetOutOfBounds(vec, -size)
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
        assertDoesNotThrow{ vec.wrapSet(Int.MIN_VALUE, newItem) }
        assertDoesNotThrow{ vec.wrapSet(Int.MAX_VALUE, newItem) }

        assertEquals(newItem, vec.wrapGet(-1))
        assertEquals(newItem, vec.wrapGet(vec.size))
        assertEquals(newItem, vec.wrapGet(Int.MIN_VALUE))
        assertEquals(newItem, vec.wrapGet(Int.MAX_VALUE))

        assertTrue(vec.count(newItem) <= 4)
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
        val newItem = -10

        assertFailsWith<IndexOutOfBoundsException>{ vec.trySet(-1, newItem).getOrThrow() }
        assertFailsWith<IndexOutOfBoundsException>{ vec.trySet(vec.size, newItem).getOrThrow() }
        assertFailsWith<IndexOutOfBoundsException>{ vec.trySet(Int.MIN_VALUE, newItem).getOrThrow() }
        assertFailsWith<IndexOutOfBoundsException>{ vec.trySet(Int.MAX_VALUE, newItem).getOrThrow() }

        assertFalse(newItem in vec)
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

        assertTrue(vec.safeSet(-1, newItem).isNone())
        assertTrue(vec.safeSet(vec.size, newItem).isNone())
        assertTrue(vec.safeSet(Int.MIN_VALUE, newItem).isNone())
        assertTrue(vec.safeSet(Int.MAX_VALUE, newItem).isNone())

        assertFalse(newItem in vec)
    }

    @Test
    fun testAdd() {
        val initialCapacity = 21
        val vec = VectorList<Int>(initialCapacity)

        repeat(initialCapacity) {
            val preSize = vec.size
            val amount = 5

            val item = it

            repeat(amount) {
                assertTrue(vec.add(item))
            }

            assertEquals(item, vec[vec.lastIndex])
            assertEquals(vec.size, preSize + amount)
        }
    }

    @Test
    fun testAddWithIndexing() {
        val initialCapacity = 9
        val vec = VectorList<Int>(initialCapacity)

        this.testAfterInsertingAtEnd(vec)
        this.testAfterInsertingAtBeginning(vec, initialCapacity)
        this.testAfterInsertingAtMiddle(vec)

        val item = 88

        assertFailsWith<IndexOutOfBoundsException>{ vec.add(-1, item) }
        assertFailsWith<IndexOutOfBoundsException>{ vec.add(vec.size + 1, item) }

        assertFalse(item in vec)
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
        val vec = vectorListOf<Int>()

        val items = 0 until max
        val listToBeInserted = items.toList()
        val setToBeInserted = items.toHashSet()

        this.testCollectionToBeInserted(vec, listToBeInserted)
        assertEquals(listToBeInserted.size, vec.size)

        this.testCollectionToBeInserted(vec, setToBeInserted)
        assertEquals(listToBeInserted.size + setToBeInserted.size, vec.size)
    }

    private fun testCollectionToBeInserted(vec: VectorList<Int>, collection: Collection<Int>) {
        val initialSize = vec.size
        assertTrue(vec.addAll(collection))
        val newSize = vec.size

        val iter = collection.iterator()

        for (index in initialSize until newSize) {
            assertEquals(iter.next(), vec[index])
        }
    }

    @Test
    fun testAddAllWithIndexing() {
        val initialSize = 10
        val vec = vectorListOf<Int>()
        val items = (0 until initialSize).toList()

        assertTrue(vec.addAll(items))

        this.testAddAllAtEnd(vec)
        this.testAddAllAtBeginning(vec)
        this.testAddAllAtMiddle(vec)

        assertFailsWith<IndexOutOfBoundsException>{ vec.addAll(-1, items) }
        assertFailsWith<IndexOutOfBoundsException>{ vec.addAll(vec.size + 1, items ) }
    }

    private fun testAddAllAtBeginning(vec: VectorList<Int>) {
        val amount = 5
        val items = (0 until amount).toList()

        assertTrue(vec.addAll(0, items))

        val vecIter = vec.listIterator(0)
        val otherIter = items.iterator()

        while (otherIter.hasNext()) {
            assertEquals(vecIter.next(), otherIter.next())
        }
    }

    private fun testAddAllAtEnd(vec: VectorList<Int>) {
        val amount = 8
        val items = (0 until amount).toSet()

        assertTrue(vec.addAll(vec.size, items))

        val vecIter = vec.listIterator(vec.size - amount)
        val otherIter = items.iterator()

        while (otherIter.hasNext()) {
            assertEquals(vecIter.next(), otherIter.next())
        }
    }

    private fun testAddAllAtMiddle(vec: VectorList<Int>) {
        val amount = 4
        val items = (0 until amount).toHashSet()
        val midIndex = vec.size / 2

        assertTrue(vec.addAll(midIndex, items))

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
        assertFalse(7 in vec)
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

        vec.addAll(0 .. 50)

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
        val vec = vectorListOf<Int>()
        val range = 0 .. 100
        val divisor = 2

        vec.addAll(range)

        val toBeRetained = (range step divisor).toHashSet()
        assertTrue(vec.retainAll(toBeRetained))

        for (item in vec) {
            assertTrue(0 == item % divisor)
        }

        assertFalse(vec.retainAll(vec))

        val empty = listOf<Int>()

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
        assertEquals(0, vec.delete(divisibleBy2) + vec.delete(divisibleBy5) + vec.delete(divisibleBy10))
        assertEquals(vec.size, vec.delete(vec))
    }

    @Test
    fun testKeep() {
        val vec = vectorListOf<Int>()
        val range = 0 .. 100
        val divisor = 4

        vec.addAll(range)

        val toBeKept = (range step divisor).toSet()
        assertEquals(75, vec.keep(toBeKept))

        for (item in vec) {
            assertTrue(0 == item % divisor)
        }

        val empty = listOf<Int>()
        val size = vec.size

        assertEquals(0, vec.keep(vec))
        assertEquals(size, vec.keep(empty))
        assertTrue(vec.isEmpty())
    }

    @Test
    fun testRemoveFromBack() {
        val size = 501
        val half = size / 2
        val moreThanHalf = half + 1

        val vec = (1 .. size).toVectorList()

        assertFailsWith<IllegalArgumentException>{ vec.removeFromBack(-1) }

        assertEquals(0, vec.removeFromBack(0))
        assertEquals(size, vec.size)

        assertEquals(moreThanHalf, vec.removeFromBack(moreThanHalf))
        assertEquals(size - moreThanHalf, vec.size)

        assertTrue(half >= vec.removeFromBack(half))
        assertTrue(vec.isEmpty())
    }

    @Test
    fun testRemoveRange() {
        val vec = (0 until 74).toVectorList()

        this.testRemovingRange(vec, (vec.size / 5), (vec.size / 3))
        this.testRemovingRange(vec, (vec.size / 2), (3 * vec.size / 4))
    }

    private fun testRemovingRange(vec: VectorList<Int>, fromIndex: Int, toIndex: Int) {
        val newSize = vec.size - (toIndex - fromIndex)

        vec.removeRange(fromIndex, toIndex)

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

        val set1 = setOf(1, 20, 5, 9)
        val set2 = setOf(-1, 1, 2, 3, 0)

        assertTrue{ vec.containsAll(set1) }
        assertFalse{ vec.containsAll(set2) }
    }

    @Test
    fun testIndexOf() {
        val range = (0 .. 20).asSequence()
        val vec = (range + range).toVectorList()

        val size = range.count()

        for (index in 0 until size) {
            @Suppress("UnnecessaryVariable", "RedundantSuppression")
            val value = index

            val index1 = vec.indexOf(value)
            val index2 = vec.index(index + size, value)

            assertEquals(vec[index1], vec[index2])
            assertNotEquals(index1, index2)

            assertNotEquals(-1, index1)
            assertNotEquals(-1, index2)
        }

        assertEquals(-1, vec.indexOf(Int.MIN_VALUE))
        assertEquals(-1, vec.indexOf(Int.MAX_VALUE))
    }

    @Test
    fun testIndexWithPredicate() {
        val range = 0 .. 40
        val vec = range.toVectorList()
        val divisor = 4

        for (index in range) {
            val modResult = index % divisor

            val foundIndex = vec.index(index) { 0 == it % divisor }
            val intendedIndex = if (0 == modResult) index else index + divisor - modResult

            assertEquals(intendedIndex, foundIndex)
        }

        assertEquals(-1, vec.index(0) { it < 0 })
    }

    @Test
    fun testLastIndexOf() {
        val range = (0 .. 20).asSequence()
        val vec = (range + range).toVectorList()

        val size = range.count()

        for (index in 0 until size) {
            @Suppress("UnnecessaryVariable", "RedundantSuppression")
            val value = index

            val index1 = vec.lastIndexOf(value)
            val index2 = vec.lastIndex(index + size, value)

            assertEquals(vec[index1], vec[index2])
            assertNotEquals(index1, index2)

            assertNotEquals(-1, index1)
            assertNotEquals(-1, index2)
        }

        assertEquals(-1, vec.lastIndexOf(Int.MIN_VALUE))
        assertEquals(-1, vec.lastIndexOf(Int.MAX_VALUE))
    }

    @Test
    fun testLastIndexWithPredicate() {

    }

    @Test
    fun testIndices() {
        val vec = vectorListOf(2, 0, 1, 0, 1, 2, 2, 3)

        this.searchForIndicesFromBeginning(vec)
        this.searchForIndicesFromMiddle(vec)
    }

    private fun searchForIndicesFromBeginning(vec: VectorList<Int>) {
        val zero = vec.indices(0).toVectorList()
        val one = vec.indices(1).toVectorList()
        val two = vec.indices(2).toVectorList()
        val three = vec.indices(3).toVectorList()
        val four = vec.indices(4).toVectorList()

        assertEquals(vectorListOf(1, 3), zero)
        assertEquals(vectorListOf(2, 4), one)
        assertEquals(vectorListOf(0, 5, 6), two)
        assertEquals(vectorListOf(7), three)
        assertEquals(vectorListOf(), four)
    }

    private fun searchForIndicesFromMiddle(vec: VectorList<Int>) {
        val midIndex = vec.size / 2

        val zero = vec.indices(midIndex, 0).toVectorList()
        val one = vec.indices(midIndex, 1).toVectorList()
        val two = vec.indices(midIndex, 2).toVectorList()
        val three = vec.indices(midIndex, 3).toVectorList()
        val four = vec.indices(midIndex, 4).toVectorList()

        assertEquals(vectorListOf(), zero)
        assertEquals(vectorListOf(4), one)
        assertEquals(vectorListOf(5, 6), two)
        assertEquals(vectorListOf(7), three)
        assertEquals(vectorListOf(), four)
    }

    @Test
    fun testIndicesWithPredicate() {

    }

    @Test
    fun testFind() {

    }

    @Test
    fun testFindAmount() {

    }

    @Test
    fun testSwap() {
        val vec = vectorListOf("1", "2", "3", "4", "5")

        this.testSwappingSameElement(vec)
        this.testSwappingDifferentElement(vec)
    }

    private fun testSwappingSameElement(vec: VectorList<String>) {
        val index1 = 3
        val index2 = 3

        val old1 = vec[index1]
        val old2 = vec[index2]

        assertSame(vec[index1], vec[index2])
        assertSame(vec[index1], old1)
        assertSame(vec[index2], old2)
        assertSame(old1, old2)

        vec.swap(index1, index2)

        assertSame(vec[index1], vec[index2])
        assertSame(old1, vec[index1])
        assertSame(old2, vec[index2])
        assertSame(old1, old2)
    }

    private fun testSwappingDifferentElement(vec: VectorList<String>) {
        val firstIndex = 1
        val secondIndex = 4

        val firstOld = vec[firstIndex]
        val secondOld = vec[secondIndex]

        assertNotSame(vec[firstIndex], vec[secondIndex])
        assertSame(vec[firstIndex], firstOld)
        assertSame(vec[secondIndex], secondOld)
        assertNotSame(firstOld, secondOld)

        vec.swap(firstIndex, secondIndex)

        assertNotSame(vec[firstIndex], vec[secondIndex])
        assertSame(vec[firstIndex], secondOld)
        assertSame(vec[secondIndex], firstOld)
        assertNotSame(firstOld, secondOld)
    }

    @Test
    fun testIsSorted() {
        val reverseOrdering = reverseOrder<Int>()

        val vec = vectorListOf(7, -7, 8, 33, -100, 11, 5, -1011, 10000)
        val reversed = vec.reversed().toVectorList()
        val sorted = vec.sorted().toVectorList()
        val sortedReversed = sorted.reversed().toVectorList()

        assertFalse(vec.isSorted())
        assertFalse(reversed.isSorted(reverseOrdering))
        assertTrue(sorted.isSorted())
        assertTrue(sortedReversed.isSorted(reverseOrdering))
    }

    @Test
    fun testIsSortedUntil() {
        val reverseOrdering = reverseOrder<Int>()

        this.testIsSortedUntilOnFullySorted(reverseOrdering)
        this.testIsSortedUntilOnPartiallySorted(reverseOrdering)
    }

    private fun testIsSortedUntilOnFullySorted(reverse: Comparator<Int>) {
        val size = 10

        val vec = (1 .. size).toVectorList()
        val reversed = vec.reversed().toVectorList()

        assertEquals(size, vec.isSortedUntil())
        assertEquals(1, reversed.isSortedUntil())
        assertEquals(1, vec.isSortedUntil(reverse))
        assertEquals(size, reversed.isSortedUntil(reverse))
    }

    private fun testIsSortedUntilOnPartiallySorted(reverse: Comparator<Int>) {
        val max = 10
        val range = 1 .. max

        val forward = range.asSequence()
        val backward = range.reversed().asSequence()

        val forward2Backward = (forward + backward).toVectorList()
        val backward2Forward = (backward + forward).toVectorList()

        val midIndex = forward2Backward.size / 2

        forward2Backward.removeAt(midIndex)
        backward2Forward.removeAt(midIndex)

        assertEquals(max, forward2Backward.isSortedUntil())
        assertEquals(1, forward2Backward.isSortedUntil(reverse))
        assertEquals(1, backward2Forward.isSortedUntil())
        assertEquals(max, backward2Forward.isSortedUntil(reverse))
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
    fun testIsPermutationOf() {
        this.testReversedPermutation()
        this.testDifferentSizedPermutations()
        this.testArbitraryPermutations()
    }

    private fun testReversedPermutation() {
        val range = 1 .. 1000000

        val vec1 = range.toVectorList()
        val vec2 = range.reversed().toVectorList()

        assertTrue(vec1.isPermutationOf(vec2))
        assertTrue(vec2.isPermutationOf(vec1))
    }

    private fun testDifferentSizedPermutations() {
        val vec1 = vectorListOf(1, 2, 3, 4, 5)
        val vec2 = vectorListOf(1, 2, 4, 5)

        assertFalse(vec1.isPermutationOf(vec2))
    }

    private fun testArbitraryPermutations() {
        val vec1 = vectorListOf(1, 5, 3, 2, 4)
        val vec2 = vectorListOf(5, 4, 1, 3, 2)

        assertTrue(vec1.isPermutationOf(vec2))
        assertTrue(vec2.isPermutationOf(vec1))
    }

    @Test
    fun testNext() {

    }

    @Test
    fun testPrev() {

    }

    @Test
    fun testCompare() {

    }

    @Test
    fun testToString() {
        val vec1 = vectorListOf<Int>()
        val vec2 = vectorListOf(1000)
        val vec3 = vectorListOf(-1, 4, 0, -7, 16, -11)

        assertEquals("[]", vec1.toString())
        assertEquals("[1000]", vec2.toString())
        assertEquals("[-1, 4, 0, -7, 16, -11]", vec3.toString())

        this.testToStringOnLargeVector()
    }

    private fun testToStringOnLargeVector() {
        val range = 0 until 100000

        val vec = range.toVectorList().toString()
        val arr = range.toMutableList().toString()

        assertEquals(vec, arr)
    }
}

class VectorIteratorTest {
    @Test
    fun testConstructor() {
        assertDoesNotThrow{ vectorListOf<Int>().iterator() }
        assertDoesNotThrow{ vectorListOf(1, 2).iterator() }
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
        this.testConcurrentModification()
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

        repeat(vec.size / 2) {
            assertDoesNotThrow{ iter.next() }
        }

        vec.add(-1)

        assertFailsWith<ConcurrentModificationException>{ iter.hasNext() }
        assertFailsWith<ConcurrentModificationException>{ iter.next() }
        assertFailsWith<ConcurrentModificationException>{ iter.remove() }
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

        vec.removeLast()
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
            assertEquals(index - 1, iter.previousIndex())

            iter.previous()
            assertEquals(index - 2, iter.previousIndex())

            iter.next()
            assertEquals(index - 1, iter.previousIndex())

            iter.next()
            assertEquals(index, iter.previousIndex())
        }
    }

    @Test
    fun testNextIndex() {
        val vec = (1 .. 13).toVectorList()

        for (index in 1 .. vec.lastIndex) {
            val iter = vec.listIterator(index)
            assertEquals(index, iter.nextIndex())

            iter.next()
            assertEquals(index + 1, iter.nextIndex())

            iter.previous()
            assertEquals(index, iter.nextIndex())

            iter.previous()
            assertEquals(index - 1, iter.nextIndex())
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

        assertFalse(iter.hasPrevious())
    }

    private fun testAfterForHasPrevious(vec: VectorList<Int>) {
        for (index in 1 .. vec.size) {
            val iter = vec.listIterator(index)

            assertTrue(iter.hasPrevious())
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

        assertFalse(iter.hasNext())
    }

    private fun testBeforeForHasNext(vec: VectorList<Int>) {
        for (index in 0 .. vec.lastIndex) {
            val iter = vec.listIterator(index)

            assertTrue(iter.hasNext())
        }
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

class VectorSublistTest {
    @Test
    fun testConstructor() {
        val vec = (1 .. 10).toVectorList()

        this.testInRange(vec)
        this.testOutOfRange(vec)

        assertNotSame(vec, vec.subList(0, vec.size))
    }

    private fun testInRange(vec: VectorList<Int>) {
        for (fromIndex in 0 until vec.size) {
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
        val result = sub.isEmpty()

        if (fromIndex == toIndex) {
            assertTrue(result)
        }
        else {
            assertFalse(result)
        }
    }

    @Test
    fun testGet() {
        val size = 40
        val fromIndex = 10
        val toIndex = 31

        val vec = (0 until size).toVectorList()
        val sub = vec.subList(fromIndex, toIndex)

        this.testEqualityOfSublistGets(vec, sub, fromIndex)
        this.testEqualityOfListGets(vec, sub, fromIndex, toIndex)

        assertTrue(fromIndex - 1 in vec.indices)
        assertTrue(toIndex in vec.indices)

        assertFailsWith<IndexOutOfBoundsException>{ sub[-1] }
        assertFailsWith<IndexOutOfBoundsException>{ sub[sub.size] }
    }

    private fun testEqualityOfSublistGets(vec: VectorList<Int>, sub: MutableList<Int>, fromIndex: Int) {
        for (index in sub.indices) {
            val vecItem = assertDoesNotThrow{ vec[index + fromIndex] }
            val subItem = assertDoesNotThrow{ sub[index] }

            assertEquals(vecItem, subItem)
        }
    }

    private fun testEqualityOfListGets(vec: VectorList<Int>, sub: MutableList<Int>, fromIndex: Int, toIndex: Int) {
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
    }

    @Test
    fun testTryGet() {
        val size = 37
        val fromIndex = 14
        val toIndex = 27

        val vec = (0 until size).toVectorList()
        val sub = vec.subList(fromIndex, toIndex)

        for (index in vec.indices) {
            val vecRes = vec.tryGet(index)
            val subRes = sub.tryGet(index - fromIndex)

            assertDoesNotThrow{ vecRes.getOrThrow() }

            if (index < fromIndex || index >= toIndex) {
                assertFailsWith<IndexOutOfBoundsException>{ subRes.getOrThrow() }
            }
            else {
                assertDoesNotThrow{ subRes.getOrThrow() }
            }
        }
    }

    @Test
    fun testSafeGet() {

    }

    @Test
    fun testWrapGet() {

    }

    @Test
    fun testSet() {
        val size = 35
        val fromIndex = 10
        val toIndex = 33

        val vec = (1 .. size).toVectorList()
        val sub = vec.subList(fromIndex, toIndex)

        this.testSetAtIndex(vec, sub, 3, -6, fromIndex)
        this.testSetAtIndex(vec, sub, 11, -11, fromIndex)
        this.testSetAtIndex(vec, sub, 17, -18, fromIndex)
        this.testSetAtIndex(vec, sub, 0, -100, fromIndex)
        this.testSetAtIndex(vec, sub, 22, -2, fromIndex)

        assertFailsWith<IndexOutOfBoundsException>{ sub[-1] = Int.MIN_VALUE }
        assertFailsWith<IndexOutOfBoundsException>{ sub[sub.size] = Int.MIN_VALUE }
    }

    private fun testSetAtIndex(vec: VectorList<Int>, sub: MutableList<Int>, index: Int, item: Int, fromIndex: Int) {
        assertEquals(sub[index], vec[index + fromIndex])

        val old = assertDoesNotThrow{ sub.set(index, item) }

        assertNotEquals(old, sub[index])
        assertEquals(item, sub[index])

        assertEquals(sub[index], vec[index + fromIndex])
    }

    @Test
    fun testTrySet() {

    }

    @Test
    fun testSafeSet() {

    }

    @Test
    fun testWrapSet() {

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
        val vec = (0 until 20).toVectorList()

        assertEquals("[]", vec.subList(0, 0).toString())
        assertEquals("[13]", vec.subList(13, 14).toString())
        assertEquals("[2, 3, 4, 5]", vec.subList(2, 6).toString())
    }
}
