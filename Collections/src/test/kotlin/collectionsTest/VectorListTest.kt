package collectionsTest

import collections.*
import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.math.max
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

        this.testSequenceEquality(vecFromSet.iterator(), set.iterator())
        this.testSequenceEquality(vecFromIter.iterator(), iter.iterator())
        this.testSequenceEquality(vecFromSeq.iterator(), seq.iterator())
        this.testSequenceEquality(vecFromArray.iterator(), array.iterator())

        assertTrue(set.size <= vecFromSet.capacity)
        assertTrue(seq.count() <= vecFromSeq.capacity)
        assertTrue(iter.count() <= vecFromIter.capacity)
        assertTrue(array.size <= vecFromArray.capacity)
    }

    private fun testSequenceEquality(left: Iterator<*>, right: Iterator<*>) {
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
        val result = assertDoesNotThrow{ vec.isRandomAccess }

        assertTrue(result)
    }

    @Test
    fun testWithIndex() {
        val vec = (1 .. 16).toVectorList()

        for (index in vec.indices) {
            this.testWithIndexIteration(vec, index)
        }
    }

    private fun testWithIndexIteration(vec: VectorList<Int>, startIndex: Int) {
        val items = assertDoesNotThrow{ vec.withIndex(startIndex) }

        for ((index, elem) in items) {
            val item = assertDoesNotThrow{ vec[index - startIndex] }
            assertEquals(elem, item)
        }
    }

    @Test
    fun testAtLeast() {
        val count = 100

        val vec1 = 0.replicate(count - 1).toVectorList()
        val vec2 = 0.replicate(count).toVectorList()
        val vec3 = 0.replicate(count + 1).toVectorList()

        val result1 = assertDoesNotThrow{ vec1.atLeast(count) }
        val result2 = assertDoesNotThrow{ vec2.atLeast(count) }
        val result3 = assertDoesNotThrow{ vec3.atLeast(count) }

        assertFalse(result1)
        assertTrue(result2)
        assertTrue(result3)
    }

    @Test
    fun testAtMost() {
        val count = 100

        val vec1 = 0.replicate(count - 1).toVectorList()
        val vec2 = 0.replicate(count).toVectorList()
        val vec3 = 0.replicate(count + 1).toVectorList()

        val result1 = assertDoesNotThrow{ vec1.atMost(count) }
        val result2 = assertDoesNotThrow{ vec2.atMost(count) }
        val result3 = assertDoesNotThrow{ vec3.atMost(count) }

        assertTrue(result1)
        assertTrue(result2)
        assertFalse(result3)
    }

    @Test
    fun testExactly() {
        val count = 100

        val vec1 = 0.replicate(count - 1).toVectorList()
        val vec2 = 0.replicate(count).toVectorList()
        val vec3 = 0.replicate(count + 1).toVectorList()

        val result1 = assertDoesNotThrow{ vec1.exactly(count) }
        val result2 = assertDoesNotThrow{ vec2.exactly(count) }
        val result3 = assertDoesNotThrow{ vec3.exactly(count) }

        assertFalse(result1)
        assertTrue(result2)
        assertFalse(result3)
    }

    @Test
    fun testSize() {
        val vec = vectorListOf<Int>()

        this.testSizeAfterAdding(vec, 100)
        this.testSizeAfterAdding(vec, 200)
        this.testSizeAfterRemoving(vec, 150)
        this.testSizeAfterAdding(vec, 100)
    }

    private fun testSizeAfterAdding(vec: VectorList<Int>, amountToAdd: Int) {
        val initialSize = assertDoesNotThrow{ vec.size }

        vec.addAll(1 .. amountToAdd)

        val newSize = assertDoesNotThrow{ vec.size }

        assertEquals(newSize, initialSize + amountToAdd)
    }

    private fun testSizeAfterRemoving(vec: VectorList<Int>, amountToRemove: Int) {
        val initialSize = assertDoesNotThrow{ vec.size }

        vec.removeFromBack(amountToRemove)

        val newSize = assertDoesNotThrow{ vec.size }

        assertEquals(newSize, initialSize - amountToRemove)
    }

    @Test
    fun testIsEmpty() {
        val vec = vectorListOf<Int>()
        val onCreate = assertDoesNotThrow{ vec.isEmpty() }
        assertTrue(onCreate)

        vec.add(0)
        val onFirstAdd = assertDoesNotThrow{ vec.isEmpty() }
        assertFalse(onFirstAdd)

        vec.add(0)
        val onSecondAdd = assertDoesNotThrow{ vec.isEmpty() }
        assertFalse(onSecondAdd)

        vec.removeLast()
        val onFirstRemove = assertDoesNotThrow{ vec.isEmpty() }
        assertFalse(onFirstRemove)

        vec.removeLast()
        val onSecondRemove = assertDoesNotThrow{ vec.isEmpty() }
        assertTrue(onSecondRemove)
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
        val vec = vectorListOf<Int>()

        this.testGetAfterAdding(vec)
        this.testGetAfterAddingAtIndex(vec)

        @Suppress("KotlinConstantConditions")
        run {
            assertFailsWith<IndexOutOfBoundsException>{ vec[-1] }
            assertFailsWith<IndexOutOfBoundsException>{ vec[vec.size] }
            assertFailsWith<IndexOutOfBoundsException>{ vec[Int.MIN_VALUE] }
            assertFailsWith<IndexOutOfBoundsException>{ vec[Int.MAX_VALUE] }
        }
    }

    private fun testGetAfterAdding(vec: VectorList<Int>) {
        val orig = (0 .. 11).toList()
        vec.addAll(orig)

        for (index in vec.indices) {
            assertEquals(orig[index], vec[index])
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

        assertFailsWith<IllegalStateException>{ vectorListOf<Int>().wrapGet(0) }
    }

    @Test
    fun testTryGet() {
        val vec = (0 until 10).toVectorList()

        for (index in vec.indices) {
            assertDoesNotThrow{ vec.tryGet(index).getOrThrow() }
        }

        assertFailsWith<IndexOutOfBoundsException>{ vec.tryGet(-1).getOrThrow() }
        assertFailsWith<IndexOutOfBoundsException>{ vec.tryGet(vec.size).getOrThrow() }
        assertFailsWith<IndexOutOfBoundsException>{ vec.tryGet(Int.MIN_VALUE).getOrThrow() }
        assertFailsWith<IndexOutOfBoundsException>{ vec.tryGet(Int.MAX_VALUE).getOrThrow() }
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

        @Suppress("KotlinConstantConditions")
        run {
            assertFailsWith<IndexOutOfBoundsException>{ vec[-1] = otherItem }
            assertFailsWith<IndexOutOfBoundsException>{ vec[vec.size] = otherItem }
            assertFailsWith<IndexOutOfBoundsException>{ vec[Int.MIN_VALUE] = otherItem }
            assertFailsWith<IndexOutOfBoundsException>{ vec[Int.MAX_VALUE] = otherItem }
        }

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

        assertFailsWith<IllegalStateException>{ vectorListOf<Int>().wrapSet(0, 1000) }
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
    fun testAdd() {
        val count = 20
        val vec = vectorListOf<Int>()

        repeat(count) {
            val preSize = vec.size
            val amount = 500

            val item = it

            repeat(amount) {
                val change = assertDoesNotThrow{ vec.add(item) }

                assertTrue(change)
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
            assertDoesNotThrow{ vec.add(0, it) }

            assertEquals(it, vec[0])
        }
    }

    private fun testAfterInsertingAtEnd(vec: VectorList<Int>) {
        val endItem = -1
        assertDoesNotThrow{ vec.add(vec.size, endItem) }
        assertEquals(endItem, vec[vec.lastIndex])
    }

    private fun testAfterInsertingAtMiddle(vec: VectorList<Int>) {
        val midIndex = vec.size / 2
        val midItem = 123

        assertDoesNotThrow{ vec.add(midIndex, midItem) }

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
        this.testCollectionToBeInserted(vec, setToBeInserted)
        this.testCollectionToBeInserted(vec, vec)

        val empty = emptySet<Int>()
        val change = assertDoesNotThrow{ vec.addAll(empty) }
        assertFalse(change)
    }

    private fun testCollectionToBeInserted(vec: VectorList<Int>, collection: Collection<Int>) {
        val initialSize = vec.size
        val otherSize = collection.size

        val change = assertDoesNotThrow{ vec.addAll(collection) }
        assertTrue(change)
        assertEquals(initialSize + otherSize, vec.size)

        val iter = collection.iterator()

        repeat(otherSize) {
            assertEquals(iter.next(), vec[it])
        }
    }

    @Test
    fun testInsert() {
        val vec1 = (1 .. 10).toVectorList()
        val vec2 = (-10 .. -1).toVectorList()

        this.testInsertWithVector(vec1, vec2)
        this.testInsertWithVector(vec2, vec1)
        this.testInsertWithVector(vec1, vec1)
        this.testInsertWithVector(vec2, vec2)
    }

    private fun testInsertWithVector(receive: VectorList<Int>, source: VectorList<Int>) {
        val size = source.size
        val amountAdded = assertDoesNotThrow{ receive.insert(source) }

        assertEquals(size, amountAdded)
    }

    @Test
    fun testAddAllWithIndexing() {
        val initialSize = 10
        val vec = vectorListOf<Int>()
        val items = (0 until initialSize).toList()

        this.testInitialAddAll(vec, items)
        this.testAddAllWithEmpty(vec)
        this.testAddAllAtEnd(vec)
        this.testAddAllAtBeginning(vec)
        this.testAddAllAtMiddle(vec)

        assertFailsWith<IndexOutOfBoundsException>{ vec.addAll(-1, items) }
        assertFailsWith<IndexOutOfBoundsException>{ vec.addAll(vec.size + 1, items ) }
    }

    private fun testInitialAddAll(vec: VectorList<Int>, items: List<Int>) {
        val initialChange = assertDoesNotThrow{ vec.addAll(items) }

        assertTrue(initialChange)
    }

    private fun testAddAllWithEmpty(vec: VectorList<Int>) {
        val empty = emptyList<Int>()

        val changeWithEmpty = assertDoesNotThrow{ vec.addAll(0, empty) }

        assertFalse(changeWithEmpty)
    }

    private fun testAddAllAtBeginning(vec: VectorList<Int>) {
        val amount = 5
        val items = (0 until amount).toList()

        val change = assertDoesNotThrow{ vec.addAll(0, items) }
        assertTrue(change)

        val vecIter = vec.listIterator(0)
        val otherIter = items.iterator()

        while (otherIter.hasNext()) {
            assertEquals(vecIter.next(), otherIter.next())
        }
    }

    private fun testAddAllAtEnd(vec: VectorList<Int>) {
        val amount = 8
        val items = (0 until amount).toSet()

        val change = assertDoesNotThrow{ vec.addAll(vec.size, items) }
        assertTrue(change)

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

        val change = assertDoesNotThrow{ vec.addAll(midIndex, items) }
        assertTrue(change)

        val vecIter = vec.listIterator(midIndex)
        val otherIter = items.iterator()

        while (otherIter.hasNext()) {
            assertEquals(vecIter.next(), otherIter.next())
        }
    }

    @Test
    fun testResize() {
        val value = 0
        val amount = 100

        val vec = value.replicate(amount).toVectorList()

        this.testDecreasingResize(vec)
        this.testIncreasingResize(vec)
        this.testInvalidResize(vec)
    }

    private fun testIncreasingResize(vec: VectorList<Int>) {
        val initialSize = vec.size

        val value = 1
        val largerSize = 150

        assertDoesNotThrow{ vec.resize(largerSize) { value } }

        assertEquals(largerSize, vec.size)
        assertEquals(initialSize, vec.indexOf(value))
    }

    private fun testDecreasingResize(vec: VectorList<Int>) {
        val value = -1
        val smallerSize = 50

        assertDoesNotThrow{ vec.resize(smallerSize) { value } }

        assertEquals(smallerSize, vec.size)
        assertEquals(-1, vec.indexOf(value))
    }

    private fun testInvalidResize(vec: VectorList<Int>) {
        val initialSize = vec.size

        assertFailsWith<IllegalArgumentException>{ vec.resize(-1) { throw InternalError() } }

        assertEquals(initialSize, vec.size)
    }

    @Test
    fun testRemove() {
        val vec = VectorList<Int>(10)

        vec.addAll(0 until 13)
        vec.add(0)

        this.testRemoveWith(vec, 0, true)
        this.testRemoveWith(vec, 12, true)
        this.testRemoveWith(vec, 7, true)

        this.testRemoveWith(vec, -1, false)
        this.testRemoveWith(vec, 13, false)
        this.testRemoveWith(vec, 7, false)

        assertTrue(0 in vec)
        assertFalse(7 in vec)
    }

    private fun testRemoveWith(vec: VectorList<Int>, value: Int, success: Boolean) {
        val change = assertDoesNotThrow{ vec.remove(value) }

        assertEquals(success, change)
    }

    @Test
    fun testRemoveAll() {
        val vec = vectorListOf<Int>()

        vec.addAll(0 until 10)

        val fullyInRange = (2 until 7).toHashSet()
        val partiallyInRange = (-2 until 1).toList()
        val notInRange = (-10 until -1).toSet()

        this.testRemoveAllWith(vec, fullyInRange, true)
        this.testRemoveAllWith(vec, partiallyInRange, true)
        this.testRemoveAllWith(vec, notInRange, false)
    }

    private fun testRemoveAllWith(vec: VectorList<Int>, values: Collection<Int>, success: Boolean) {
        @Suppress("ConvertArgumentToSet")
        val change = assertDoesNotThrow{ vec.removeAll(values) }

        assertEquals(success, change)
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

        val removedFirst = assertDoesNotThrow{ vec.removeAt(0) }
        val removedLast = assertDoesNotThrow{ vec.removeAt(vec.lastIndex) }

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
        val removedMid1 = assertDoesNotThrow{ vec.removeAt(midIndex1) }

        val midIndex2 = vec.size / 4
        val mid2 = vec[midIndex2]
        val removedMid2 = assertDoesNotThrow{ vec.removeAt(midIndex2) }

        val midIndex3 = 3 * vec.size / 4
        val mid3 = vec[midIndex3]
        val removedMid3 = assertDoesNotThrow{ vec.removeAt(midIndex3) }

        assertEquals(mid1, removedMid1)
        assertEquals(mid2, removedMid2)
        assertEquals(mid3, removedMid3)

        assertEquals(startSize - 3, vec.size)

        assertFalse(mid1 in vec)
        assertFalse(mid2 in vec)
        assertFalse(mid3 in vec)
    }

    @Test
    fun testRemoveFirstOf() {
        this.testRemoveFirstByElement()
        this.testRemoveFirstByPredicate()
    }

    private fun testRemoveFirstByElement() {
        val max = 16
        val range = (1 .. max).asSequence()
        val vec = (range + range + range).toVectorList()

        val value = max / 2
        val index = vec.indexOf(value)

        val change1 = assertDoesNotThrow{ vec.removeFirstOf(value) }
        val newIndex = vec.indexOf(value)
        assertTrue(change1)
        assertTrue(index <= newIndex)

        val change2 = assertDoesNotThrow{ vec.removeFirstOf(value) }
        assertTrue(change2)
        assertTrue(newIndex <= vec.indexOf(value))

        val change3 = assertDoesNotThrow{ vec.removeFirstOf(value) }
        assertTrue(change3)
        assertEquals(-1, vec.indexOf(value))

        val change4 = assertDoesNotThrow{ vec.removeFirstOf(value) }
        assertFalse(change4)
        assertEquals(-1, vec.indexOf(value))
    }

    private fun testRemoveFirstByPredicate() {
        val max = 24
        val range = 1 .. max
        val vec = range.toVectorList()

        val divisor = max / 4
        val predicate = { num: Int -> 0 == num % divisor }

        do {
            val index = vec.index(0, predicate)

            val change = assertDoesNotThrow{ vec.removeFirstOf(predicate) }
            assertTrue(change)

            val nextIndex = vec.index(0, predicate)

            assertTrue(index <= nextIndex || -1 == nextIndex)
        } while (-1 != nextIndex)

        val change = assertDoesNotThrow{ vec.removeFirstOf(predicate) }
        assertFalse(change)
    }

    @Test
    fun testRemoveAllOf() {
        val vec = vectorListOf(1, 5, 1, 9, 5, 1, 9, 5, 2, 7, 3, 9, 5, 1, 4, 1, 1)
        val startSize = vec.size

        val zeroesRemoved = assertDoesNotThrow{ vec.removeAllOf(0) }
        assertFalse(0 in vec)

        val onesRemoved = assertDoesNotThrow{ vec.removeAllOf(1) }
        assertFalse(1 in vec)

        val threesRemoved = assertDoesNotThrow{ vec.removeAllOf(3) }
        assertFalse(3 in vec)

        val fivesRemoved = assertDoesNotThrow{ vec.removeAllOf(5) }
        assertFalse(5 in vec)

        val ninesRemoved = assertDoesNotThrow{ vec.removeAllOf(9) }
        assertFalse(9 in vec)

        assertEquals(0, zeroesRemoved)
        assertEquals(6, onesRemoved)
        assertEquals(1, threesRemoved)
        assertEquals(4, fivesRemoved)
        assertEquals(3, ninesRemoved)

        val newSize = vec.size
        val amountRemoved = zeroesRemoved + onesRemoved + threesRemoved + fivesRemoved + ninesRemoved

        assertEquals(startSize, newSize + amountRemoved)
    }

    @Test
    fun testRemoveAllOfWithPredicate() {
        val vec = (1 .. 50).toVectorList()
        val halfSize = vec.size / 2

        val evensRemoved = assertDoesNotThrow{ vec.removeAllOf{ 0 == it % 2 } }
        assertEquals(0, vec.count{ 0 == it % 2 })

        val oddsRemoved = assertDoesNotThrow{ vec.removeAllOf{ 1 == it % 2 } }
        assertEquals(0, vec.count{ 1 == it % 2 })

        assertEquals(halfSize, evensRemoved)
        assertEquals(halfSize, oddsRemoved)

        assertTrue(vec.isEmpty())
    }

    @Test
    fun testRemoveAmount() {
        this.testRemoveAmountByElement()
        this.testRemoveAmountByPredicate()
    }

    private fun testRemoveAmountByElement() {
        val range = (1 .. 20).asSequence()
        val vec = (range + range + range + range).toVectorList()

        val amount = 3
        val value = 5

        val initialCount = vec.count{ value == it }
        val removedCount = assertDoesNotThrow{ vec.removeAmount(amount, value) }
        val currentCount = vec.count{ value == it }

        assertEquals(initialCount, removedCount + currentCount)
    }

    private fun testRemoveAmountByPredicate() {
        val vec = (1 .. 20).toVectorList()

        val amount = 10
        val predicate = { item: Int -> 0 == item % 4 }

        val initialCount = vec.count(predicate)
        val removedCount = assertDoesNotThrow{ vec.removeAmount(amount, predicate) }
        val currentCount = vec.count(predicate)

        assertEquals(initialCount, removedCount + currentCount)
    }

    @Test
    fun testRetainAll() {
        val vec = vectorListOf<Int>()
        val range = 0 .. 100
        val divisor = 2

        vec.addAll(range)

        val toBeRetained = (range step divisor).toHashSet()

        val changeWithRandom = assertDoesNotThrow{ vec.retainAll(toBeRetained) }
        assertTrue(changeWithRandom)

        for (item in vec) {
            assertTrue(0 == item % divisor)
        }

        val changeWithSelf = assertDoesNotThrow{ vec.retainAll(vec) }
        assertFalse(changeWithSelf)

        val empty = listOf<Int>()

        val changeWithEmpty = assertDoesNotThrow{ vec.retainAll(empty) }
        assertTrue(changeWithEmpty)

        assertTrue(vec.isEmpty())
    }

    @Test
    fun testClear() {
        val vec = vectorListOf<Int>()
        assertTrue(vec.isEmpty())

        vec.addAll(0 until 10)
        assertFalse(vec.isEmpty())

        assertDoesNotThrow{ vec.clear() }
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

        val changeWhenDeleteFactorsOf10 = assertDoesNotThrow{ vec.delete(divisibleBy10) }
        val changeWhenDeleteFactorsOf5 = assertDoesNotThrow{ vec.delete(divisibleBy5) }
        val changeWhenDeleteFactorsOf2 = assertDoesNotThrow{ vec.delete(divisibleBy2) }
        val changeWhenDeleteFactorsAgain = assertDoesNotThrow{ vec.delete(divisibleBy2) + vec.delete(divisibleBy5) + vec.delete(divisibleBy10) }

        assertEquals(6, changeWhenDeleteFactorsOf10)
        assertEquals(5, changeWhenDeleteFactorsOf5)
        assertEquals(20, changeWhenDeleteFactorsOf2)
        assertEquals(0, changeWhenDeleteFactorsAgain)

        val sizeBeforeDeleteSelf = vec.size
        val changeWhenDeleteSelf = assertDoesNotThrow{ vec.delete(vec) }
        assertEquals(sizeBeforeDeleteSelf, changeWhenDeleteSelf)
    }

    @Test
    fun testKeep() {
        val range = 0 .. 100
        val vec = range.toVectorList()
        val divisor = 4

        val toBeKept = (range step divisor).toSet()

        val changeExpected = vec.size - toBeKept.size
        val changeWithRandom = assertDoesNotThrow{ vec.keep(toBeKept) }
        assertEquals(changeExpected, changeWithRandom)

        for (item in vec) {
            assertTrue(0 == item % divisor)
        }

        val changeWithSelf = assertDoesNotThrow{ vec.keep(vec) }
        assertEquals(0, changeWithSelf)

        val empty = emptyList<Int>()
        val size = vec.size

        val changeWithEmpty = assertDoesNotThrow{ vec.keep(empty) }
        assertEquals(size, changeWithEmpty)

        assertTrue(vec.isEmpty())
    }

    @Test
    fun testRemoveFromBack() {
        val size = 501
        val moreThanHalf = (size / 2) + 1

        val vec = (1 .. size).toVectorList()

        assertFailsWith<IllegalArgumentException>{ vec.removeFromBack(-1) }

        this.testRemoveFromBackWith(vec, 0)
        this.testRemoveFromBackWith(vec, moreThanHalf)
        this.testRemoveFromBackWith(vec, moreThanHalf)
    }

    private fun testRemoveFromBackWith(vec: VectorList<Int>, amount: Int) {
        val expectedSize = max(0, vec.size - amount)
        val expectedRemovedAmount = vec.size - expectedSize
        val amountRemoved = assertDoesNotThrow{ vec.removeFromBack(amount) }

        assertEquals(expectedSize, vec.size)
        assertEquals(expectedRemovedAmount, amountRemoved)
    }

    @Test
    fun testRemoveRange() {
        val vec = (0 until 100).toVectorList()

        this.testRemovingRange(vec, (vec.size / 5), (vec.size / 3))
        this.testRemovingRange(vec, (vec.size / 2), (3 * vec.size / 4))
    }

    private fun testRemovingRange(vec: VectorList<Int>, fromIndex: Int, toIndex: Int) {
        val rangeSize = toIndex - fromIndex
        val newSize = vec.size - rangeSize

        assertDoesNotThrow{ vec.removeRange(fromIndex, toIndex) }

        assertEquals(newSize, vec.size)
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
        val range = 1 .. 100

        val vec1 = (range step 5).toVectorList()
        val vec2 = (range step 4).toVectorList()
        val vec3 = (range step 5).toVectorList()

        assertNotEquals(vec1, vec2)
        assertEquals(vec1, vec3)
        assertNotEquals(vec2, vec3)

        val hash1 = assertDoesNotThrow{ vec1.hashCode() }
        val hash2 = assertDoesNotThrow{ vec2.hashCode() }
        val hash3 = assertDoesNotThrow{ vec3.hashCode() }

        assertNotEquals(hash1, hash2)
        assertEquals(hash1, hash3)
        assertNotEquals(hash2, hash3)
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

        this.testCompareOnSelf(vec1)
        this.testCompareOnSelf(vec2)
        this.testCompareOnSelf(vec3)

        val comparison12 = assertDoesNotThrow{ compare(vec1, vec2) }
        val comparison13 = assertDoesNotThrow{ compare(vec1, vec3) }
        val comparison23 = assertDoesNotThrow{ compare(vec2, vec3) }

        assertTrue(comparison12 < 0)
        assertTrue(comparison13 > 0)
        assertTrue(comparison23 > 0)
    }

    private fun testCompareOnSelf(vec: VectorList<Int>) {
        val comparison = assertDoesNotThrow{ compare(vec, vec) }

        assertEquals(0, comparison)
    }

    @Test
    fun testToString() {
        val vec1 = vectorListOf<Int>()
        val vec2 = vectorListOf(1000)
        val vec3 = vectorListOf(-1, 4, 0, -7, 16, -11)

        val str1 = assertDoesNotThrow{ vec1.toString() }
        val str2 = assertDoesNotThrow{ vec2.toString() }
        val str3 = assertDoesNotThrow{ vec3.toString() }

        assertEquals("[]", str1)
        assertEquals("[1000]", str2)
        assertEquals("[-1, 4, 0, -7, 16, -11]", str3)

        this.testToStringOnLargeVector()
    }

    private fun testToStringOnLargeVector() {
        val range = (0 until 1000000).reversed()

        val vec = range.toVectorList()
        val arr = range.toMutableList()

        val vecStr = assertDoesNotThrow{ vec.toString() }
        val arrStr = assertDoesNotThrow{ arr.toString() }

        assertEquals(vec, arr)
        assertEquals(vecStr, arrStr)
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
